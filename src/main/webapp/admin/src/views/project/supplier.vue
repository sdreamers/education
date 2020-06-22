<template>
    <el-dialog :close-on-click-modal="false" title="指派供应商" :visible="dialogVisible" width="30%" @close="$emit('close')">
        <el-form :model="form" label-position="left" ref="ruleForm" label-width="90px">
            <el-form-item label="当前供应商">
                <el-input
                    v-model="form.oldSupplier"
                    :disabled="true">
                </el-input>
            </el-form-item>
            <el-form-item label="指派供应商">
                <el-input v-model="form.newSupplier" placeholder="请输入供应商"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import api from '@/api/merchant';

    export default {
        props: ['dialogVisible', 'form'],
        data() {
            return {
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }]
                },

                supplierForm: {}
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                const param = {
                    name: this.form.name,
                    memberNumber: this.form.memberNumber
                };
                if (this.type === 'add') {
                    api.insertMerchant(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('添加成功');
                            this.$emit('close');
                        }
                    });
                } else {
                    param.id = this.form.id;
                    api.updateMerchant(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('添加成功');
                            this.$emit('close');
                        }
                    });
                }
            }
        }
    };
</script>
