<template>
    <el-dialog :close-on-click-modal="false" title="修改额度" :visible="dialogVisible" width="20%" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item v-for="(product,key) in form.products" :label="product.name" label-width="100px" :key="key">
                <el-input v-model="product.amount" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import api from '@/api/merchantproductquota';
    import util from '@/libs/util';

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
                const param = {
                    merchantId: this.form.merchantId,
                    quotaYear: util.getCookie('quotaYear'),
                    type: this.form.type
                };
                const products = {};
                for (const product of this.form.products) {
                    if (product.amount) {
                        products[product.id] = product.amount;
                    }
                }
                param.quotaMapStr = JSON.stringify(products);
                api.updateQuota(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('添加成功');
                        this.$emit('close');
                    }
                });
            }
        }
    };
</script>
