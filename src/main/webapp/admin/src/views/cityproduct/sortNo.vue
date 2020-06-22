<template>
    <el-dialog :close-on-click-modal="false" title="优先级" :visible="true" @close="$emit('close')">
        <el-form :model="formSortNo" :rules="rules" ref="ruleForm">
            <el-form-item label="商品" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formSortNo.name" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="优先级" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="formSortNo.sortNo" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitSortNo">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/cityproduct';
export default {
    props: ['formSortNo', 'formLabelWidth', 'warehouseId'],
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
                sortNo: [
                    { required: true, message: '优先级不能空', trigger: 'blur' },
                    { validator: validateInt, trigger: 'blur' },
                    formIsNaN
                ]
            }
        }
    },
    methods: {
        handleSubmitSortNo() {
            const param = {
                productId: this.formSortNo.productId,
                sortNo: this.formSortNo.sortNo
            };
            if (!param.sortNo) {
                this.$notify.error('优先级不能为空');
                return;
            }
            if (parseFloat(param.sortNo) < 0) {
                this.$notify.error('优先级不能小于0');
                return;
            }
            if (parseFloat(param.sortNo) > 999999999) {
                this.$notify.error('优先级不能大于999999999');
                return;
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    api.updateSortNo(param).then(res => {
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
