<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 规格" :visible="true" @close="$emit('close')">
        <el-form :model="formSpec" :rules="rules" ref="ruleFormSpec">
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="formSpec.sortNo" type="text" auto-complete="off" maxlength="9"></el-input>
            </el-form-item>
            <el-form-item label="规格编号" :label-width="formLabelWidth" prop="productSpecificationNo">
                <el-input v-model="formSpec.productSpecificationNo" type="text" auto-complete="on" name="productSpecificationNo" maxlength="15"></el-input>
            </el-form-item>
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="specificationName">
                <el-input v-model="formSpec.specificationName" type="text" auto-complete="off" maxlength="30"></el-input>
            </el-form-item>
            <el-form-item label="重量（KG）" :label-width="formLabelWidth" prop="weight">
                <el-input v-model="formSpec.weight" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="单位" :label-width="formLabelWidth" prop="unitId">
                <el-select v-model="formSpec.unitId" placeholder="请选择单位">
                    <el-option v-for="(item,key) in unitList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="关系数" :label-width="formLabelWidth" prop="relationNum" v-if="formSpec.isSplit">
                <el-input v-model="formSpec.relationNum" type="text" auto-complete="off" min="1" max="5" :disabled="formType === 'edit'"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitSpec">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/product';
export default {
    props: ['formSpec', 'formType', 'formLabelWidth', 'unitList', 'productBasicInfoId'],
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        }
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('必须为大于等于0的正整数'));
        };
        const validateNumber = (rule, value, callback) => {
            if (!isNaN(Number(value))) {
                callback();
            }
            callback(new Error('必须为大于等于0的正整数'));
        };
        const formIsNaN = { validator: validateInt, trigger: 'blur' }
        const formIsInt = { validator: validateNaN, trigger: 'blur' }
        const noEmpty = { required: true, message: '不能为空', trigger: 'blur' };
        return {
            rules: {
                sortNo: [noEmpty, formIsNaN, formIsInt],
                productSpecificationNo: [noEmpty],
                specificationName: [noEmpty],
                weight: [noEmpty, validateNumber],
                unitId: [noEmpty],
                relationNum: [noEmpty, formIsNaN]
            }
        };
    },
    methods: {
        // 提交规格
        handleSubmitSpec() {
            const formType = this.formType;
            this.$refs.ruleFormSpec.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        this.unitList.forEach(item => {
                            if (item.id === this.formSpec.unitId) {
                                this.formSpec.unit = item.name;
                            }
                        });
                        const param = this.formSpec;
                        param.relationNum = this.formSpec.relationNum || 0;

                        if (param.weight === '') {
                            return this.$notify.error('重量输入不合法');
                        } else if (isNaN(param.weight)) {
                            return this.$notify.error('重量必须为数字');
                        } else if (parseFloat(param.weight).toFixed(2) <= 0) {
                            return this.$notify.error('重量必须为正数');
                        } else if (parseFloat(param.weight).toFixed(2) > 999999999.99) {
                            return this.$notify.error('重量不能大于999999999.99');
                        }

                        // if (param.weight )
                        api.productBasicSpecificationUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close', { productBasicInfoId: param.productBasicInfoId });
                            }
                        });
                    } else if (formType === 'add') {
                        this.unitList.forEach(item => {
                            if (item.id === this.formSpec.unitId) {
                                this.formSpec.unit = item.name;
                            }
                        });
                        const param = this.formSpec;
                        if (param.weight === '') {
                            return this.$notify.error('重量输入不合法');
                        } else if (isNaN(param.weight)) {
                            return this.$notify.error('重量必须为数字');
                        } else if (parseFloat(param.weight).toFixed(2) <= 0.00) {
                            return this.$notify.error('重量必须为正数');
                        } else if (parseFloat(param.weight).toFixed(2) > 999999999.99) {
                            return this.$notify.error('重量不能大于999999999.99');
                        }
                        param.productBasicInfoId = this.productBasicInfoId;
                        api.productBasicSpecificationInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close', { productBasicInfoId: param.productBasicInfoId });
                            }
                        });
                    }
                } else {
                    this.$notify({
                        message: '请按要求填写表单',
                        type: 'warning'
                    });
                    return false;
                }
            });
        }
    }
};
</script>
