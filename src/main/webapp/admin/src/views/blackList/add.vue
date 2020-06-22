<template>
    <el-dialog :close-on-click-modal="false"  title="添加商户至黑名单" :visible="visible" @close="handleClose" width="30%">
        <el-form :model="form" label-position="top" :rules="rules" ref="ruleForm">
            <el-form-item label="商户注册号码 ：">
                <el-autocomplete
                    v-model="form.telephone"
                    :fetch-suggestions="handleSearchMerchant"
                    placeholder="商户注册手机号码"
                    @select="handleSelect"
                    :trigger-on-focus = 'false'
                ></el-autocomplete>
            </el-form-item>
            <el-form-item label="备注 ：">
                <el-input v-model="form.remark" type="textarea" :rows="4" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleClose">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
    import merchant from '@/api/merchant';
    import blackList from '@/api/blackList';

    const form = {
        telephone: '',
        merchantId: '',
        remark: ''
    };

    export default {

        props: ['visible'],

        data() {
            return {
                form: JSON.parse(JSON.stringify(form)),

                rules: {
                    telephone: [
                        { required: true, message: '不能为空', trigger: 'blur' }
                    ]
                }
            }
        },

        methods: {
            handleSearchMerchant(telephone, cb) {
                if (!this.form.telephone || this.form.telephone.length < 4) {
                    return cb([]);
                }
                const param = {
                    name: this.form.telephone
                };
                merchant.listByKeyword(param).then(res => {
                    if (res.code === 100 && res.content) {
                        const content = JSON.parse(res.content).map(item => ({ id: item.id, value: item.telephone }));
                        cb(content);
                    } else {
                        cb([]);
                    }
                });
            },

            handleSelect(item) {
                this.form.merchantId = item.id;
            },

            handleSubmit() {
                if (!this.form.telephone) {
                    return this.$notify.error('请输入手机号码');
                }
                if (!this.form.merchantId) {
                    return this.$notify.error('请先选择商户');
                }
                const param = {
                    merchantId: this.form.merchantId,
                    merchantTelephone: this.form.telephone,
                    remark: this.form.remark
                };
                blackList.insert(param).then(res => {
                    if (res.code === 100) {
                        this.handleClose();
                        this.$notify.success('操作成功');
                    }
                });
            },

            handleClose() {
                this.form = JSON.parse(JSON.stringify(form));
                this.$emit('close');
            }
        }
    }
</script>

<style scoped>

</style>
