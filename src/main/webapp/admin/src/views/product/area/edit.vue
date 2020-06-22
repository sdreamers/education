<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 产地" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="form.sortNo" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="产地" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off" maxlength="20"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/product';
export default {
    props: ['form', 'formType'],
    data() {
        const validateSortNo = function(rule, value, callback) {
            if (value.toString().length > 10) {
                callback(new Error('不得超过10位'));
            } else {
                callback();
            }
        };
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        };
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('序号必须为大于等于0的正整数'));
        };
        const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        return {
            formLabelWidth: '120px',

            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                sortNo: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: validateSortNo, trigger: 'blur' },
                    formIsNaN,
                    formIsInt
                ]
            }
        };
    },
    methods: {
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        api.productAreaUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.productAreaInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
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
