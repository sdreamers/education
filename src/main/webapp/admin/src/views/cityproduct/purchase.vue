<template>
    <el-dialog :close-on-click-modal="false" title="修改周期" :visible="true" @close="$emit('close')">
        <el-form :model="formPurchasingCycle" :rules="rules" ref="ruleForm">
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formPurchasingCycle.specificationName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="采购周期" :label-width="formLabelWidth" prop="purchasingCycle">
                <el-input v-model="formPurchasingCycle.purchasingCycle" type="text" auto-complete="off" maxlength="6"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitPurchasingCycle">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/cityproduct';
export default {
    props: ['formPurchasingCycle', 'formLabelWidth', 'warehouseId'],
    data() {
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('请输入整数'));
        };
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        }
        const formIsNaN = { validator: validateNaN, trigger: 'blur' }
        return {
            rules: {
                purchasingCycle: [
                    { required: true, message: '采购周期不能空', trigger: 'blur' },
                    { validator: validateInt, trigger: 'blur' },
                    formIsNaN
                ]
            }
        }
    },
    methods: {
        handleSubmitPurchasingCycle() {
            console.log('handleSubmitPurchasingCycle')
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = {
                        specificationId: this.formPurchasingCycle.productSpecificationId,
                        purchasingCycle: this.formPurchasingCycle.purchasingCycle
                    };
                    api.updatePurchasingCycle(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.$emit('close');
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
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
