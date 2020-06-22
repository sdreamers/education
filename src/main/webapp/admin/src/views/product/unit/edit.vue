<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 商品单位" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="商品单位" :label-width="formLabelWidth" prop="unitName">
                <el-input v-model="form.unitName" type="text" auto-complete="off"></el-input>
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
    props: ['form', 'formLabelWidth', 'rules', 'formType'],
    methods: {

        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        api.productUnitUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.productUnitInsert(param).then(res => {
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
