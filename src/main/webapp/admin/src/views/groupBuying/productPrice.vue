<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 团购" :visible="true" @close="$emit('close')">
        <el-form :model="formActivity" :rules="rules" ref="ruleForm">
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="formActivity.sortNo" type="text" auto-complete="off" maxlength="3"></el-input>
            </el-form-item>
            <el-form-item label="商品名称" :label-width="formLabelWidth" prop="cityProductName">
                <el-input v-model="formActivity.cityProductName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="商品规格" :label-width="formLabelWidth" prop="citySpecificationName">
                <el-input v-model="formActivity.citySpecificationName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="团购价格" :label-width="formLabelWidth" prop="price">
                <el-input v-model="formActivity.price" type="text" auto-complete="off" maxlength="11"></el-input>
            </el-form-item>
            <el-form-item label="起团数量" :label-width="formLabelWidth" prop="openUpNum">
                <el-input v-model="formActivity.openUpNum" type="text" auto-complete="off" maxlength="5"></el-input>
            </el-form-item>
            <el-form-item label="最大团购数量" :label-width="formLabelWidth" prop="maxPurchaseNum">
                <el-input v-model="formActivity.maxPurchaseNum" type="text" auto-complete="off" maxlength="5"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitActivity">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
import api from '@/api/groupBuying';
export default {
    props: ['dialogActivityVisible', 'formActivity', 'formLabelWidth'],
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        };
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) > 0) {
                callback();
            }
            callback(new Error('必须为大于0的正整数'));
        };
        const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        const formRequire = { required: true, message: '不能为空', trigger: 'blur' };
        return {
            rules: {
                sortNo: [formRequire, formIsNaN, formIsInt],
                cityProductName: [],
                citySpecificationName: [],
                price: [formRequire, formIsNaN],
                openUpNum: [formRequire, formIsNaN, formIsInt],
                addUpPurchaseNum: [formRequire, formIsNaN, formIsInt],
                maxPurchaseNum: [formRequire, formIsNaN, formIsInt]
            }
        }
    },
    methods: {

        // 修改团购商品的单价和库存
        handleSubmitActivity() {
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = this.formActivity;
                    api.groupBuyingProductUpdate(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.$emit('close');
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }
            })
        }
    }
};
</script>
