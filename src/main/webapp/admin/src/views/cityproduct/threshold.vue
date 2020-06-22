<template>
    <el-dialog :close-on-click-modal="false" title="预警值" :visible="true" @close="$emit('close')">
        <el-form :model="formThreshold" :rules="rules" ref="ruleForm">
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formThreshold.specificationName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="预警值" :label-width="formLabelWidth" prop="thresholdValue">
                <el-input v-model="formThreshold.thresholdValue" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitThreshold">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/cityproduct';
export default {
    props: ['formThreshold', 'formLabelWidth', 'warehouseId'],
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
                thresholdValue: [
                    { required: true, message: '预警值不能空', trigger: 'blur' },
                    { validator: validateInt, trigger: 'blur' },
                    formIsNaN
                ]
            }
        }
    },
    methods: {
        handleSubmitThreshold() {
            const param = {
                specificationId: this.formThreshold.productSpecificationId,
                thresholdValue: this.formThreshold.thresholdValue
            };
            if (!param.thresholdValue) {
                this.$notify.error('预警值不能为空');
                return;
            }
            if (parseFloat(param.thresholdValue) < 0) {
                this.$notify.error('预警值不能小于0');
                return;
            }
            if (parseFloat(param.thresholdValue) > 999999999) {
                this.$notify.error('预警值不能大于999999999');
                return;
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    api.updateThresholdValue(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.$emit('close');
                        } else {
                            // this.$notify.error(res.message || '未知修改订单错误');
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
