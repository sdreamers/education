<template>
    <el-dialog :close-on-click-modal="false" :title="form.title" :visible="dialogVisible" width="25%" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="旧值" label-width="40px">
                <el-input v-model="form.oldValue" type="text" readonly auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="新值" label-width="40px">
                <el-input v-model="form.newValue" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
    import order from '@/api/order';

    export default {
        props: ['dialogVisible', 'formLabelWidth', 'form'],
        data() {
            return {
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }]
                }
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                const param = {};
                if (this.form.type === '1') {
                    param.oldOrderNo = this.form.orderNo;
                    param.newOrderNo = this.form.newValue;
                    order.updateOrderNo(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('修改成功');
                            this.$emit('close');
                        }
                    });
                } else if (this.form.type === '2') {
                    param.orderNo = this.form.orderNo;
                    param.remark = this.form.newValue;
                    order.updateOrderRemark(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('修改成功');
                            this.$emit('close');
                        }
                    });
                }
            }
        }
    };
</script>
