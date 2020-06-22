<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改" :visible="true" @close="$emit('close')" width="600px">
        <el-form :model="form">
            <el-form-item label="生效城市" :label-width="formLabelWidth" prop="cityId">
                <el-select v-model="form.cityId" placeholder="请选择生效城市" @change="cityChange">
                    <el-option v-for="(item,key) in cityMap" :label="item" :value="key" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="生效范围" :label-width="formLabelWidth" prop="level">
                <el-select v-model="form.level" style="vertical-align:top" placeholder="请选择生效范围" @change="typeChange">
                    <el-option v-for="(item,key) in relationScope" :label="item" :value="key" :key="key"></el-option>
                </el-select>
                <el-select v-model="form.relationId" style="vertical-align:top" placeholder="请选择生效范围" v-show="form.level !== 'city' && form.level !== ''">
                    <el-option v-for="(item,key) in productMerchantTypeMap" :label="item" :value="key" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="价格关系" :label-width="formLabelWidth">
                <el-radio-group v-model="form.computeMode_text" size="small" @change="change">
                    <el-radio-button :label="item" v-for="(item,key) in relationMap" :key="key"></el-radio-button>
                </el-radio-group>
                <el-row>
                    <el-input :value="cityMap[form.cityId]" readonly style="width:100px"></el-input>
                    =
                    <el-select v-model="form.relationCityId" placeholder="请选择对比城市" style="width:100px">
                        <el-option v-for="(item,key) in cityMap_filterd" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                    <el-input :value="form.computeModeText" readonly style="width:100px"></el-input>
                    <!-- {{form.computeModeText}} -->
                    <el-input v-model="form.value" style="width:100px" maxlength="12" @input="form.value = form.value.toString().match(/^\d+(?:\.\d{0,3})?/)"></el-input>
                </el-row>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/productRelation';
// import util from '@/libs/util';

const relationMap = {
    add: '加',
    sub: '减',
    multiply: '乘',
    divide: '除'
}

const relationMapText = {
    加: 'add',
    减: 'sub',
    乘: 'multiply',
    除: 'divide'
}

const relationText = {
    加: '+',
    减: '-',
    乘: 'x',
    除: '÷'
}

export default {
    props: ['form', 'formType', 'cityMap', 'productMerchantTypeMap', 'relationScope'],
    data() {
        return {
            formLabelWidth: '120px',

            relationMap,
            relationMapText
        };
    },
    methods: {
        handleValueInput(data) {
            data = data || 0;
            const value = data.toString().match(/^\d+(?:\.\d{0,3})?/)[0];
            this.$set(this.form, 'value', value)
            console.log(value);
            this.form.value = value;
        },
        cityChange(data) {
            this.form.relationCityId = '';
            const cityMap = JSON.parse(JSON.stringify(this.cityMap));
            delete cityMap[data];
            this.cityMap_filterd = cityMap;
        },
        change(data) {
            this.form.computeModeText = relationText[data]
            this.form.computeMode = relationMapText[data]
            this.form = JSON.parse(JSON.stringify(this.form))
        },
        typeChange(data) {
            console.log(data);
            if (data === 'rootCategory') {
                this.form.relationId = '';
            }
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.form.computeMode = relationMapText[this.form.computeMode_text]
            const form = this.form;
            const param = {
                id: form.id,
                cityId: form.cityId,
                level: form.level,
                relationCityId: form.relationCityId,
                computeMode: form.computeMode,
                value: form.value || '',
                relationId: form.relationId
                // operatorId: this.G.userInfo.id,
                // operatorName: this.G.userInfo.nickname,
                // operatorTime: util.formatTime(new Date())
            }
            if (typeof param.value === 'object') {
                param.value = param.value[0];
            }
            for (const item in param) {
                if (param.hasOwnProperty(item)) {
                    if (!param[item] && item !== 'relationId' && item !== 'id') {
                        return this.$notify.error('输入不完整');
                    }
                }
            }
            if (param.level === 'rootCategory' && param.relationId === '') {
                return this.$notify.error('输入不完整');
            } else if (param.level === 'city') {
                param.relationId = '0';
            }
            if (isNaN(param.value) || Number(param.value) <= 0) {
                return this.$notify.error('关系值必须为大于0的数字');
            }
            if (param.value !== Number(param.value).toString()) {
                return this.$notify.error('请确认输入是否正确');
            }
            if (/^(\d{1,8}|\d{1,8}\.\d{1,3})$/.test(param.value) === false) {
                return this.$notify.error('只能输入8位整数，3位小数');
            }
            if (formType === 'edit') {
                api.priceRelationUpdate(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.$emit('close');
                    }
                });
            } else if (formType === 'add') {
                api.priceRelationInsert(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.$emit('close');
                    }
                });
            }
        }
    },
    created() {
        if (this.form.value) {
            this.form.value = this.form.value.toString();
        }
        this.form.computeModeText = relationText[this.form.computeMode_text];
        this.cityMap_filterd = JSON.parse(JSON.stringify(this.cityMap));
    }
};
</script>
