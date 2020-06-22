<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 供应商" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="供应商名称" :label-width="formLabelWidth" prop="supplierName">
                        <el-input v-model="form.supplierName" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="税号" :label-width="formLabelWidth" prop="taxNo">
                        <el-input v-model="form.taxNo" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="开户银行" :label-width="formLabelWidth" prop="bank">
                        <el-input v-model="form.bank" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="银行卡号" :label-width="formLabelWidth" prop="bankCardNo">
                        <el-input v-model="form.bankCardNo" type="text" auto-complete="off" maxlength="20"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系人" :label-width="formLabelWidth" prop="linkman">
                        <el-input v-model="form.linkman" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" :label-width="formLabelWidth" prop="phone">
                        <el-input v-model="form.phone" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
                <el-col>
                    <el-form-item label="联系地址" :label-width="formLabelWidth" prop="address">
                        <el-input v-model="form.address" type="text" auto-complete="off"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/supplier';
export default {
    props: ['form', 'formLabelWidth', 'formType'],
    data() {
        return {
            rules: {
                supplierName: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                taxNo: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                bank: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                bankCardNo: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                linkman: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                address: [
                    { required: true, message: '不能为空', trigger: 'blur' }
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
                        api.supplierUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.supplierInsert(param).then(res => {
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
