<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="充值金额（元）" :label-width="formLabelWidth" prop="payablePrice">
                <el-input v-model="form.payablePrice" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="赠送金额（元）" :label-width="formLabelWidth" prop="giftAmount">
                <el-input v-model="form.giftAmount" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handlePreviewSuccess"
                    :before-upload="handleBeforeUpload">
                    <img style="width: 150px;height: 150px;" v-if="form.previewImageUrl" :src="form.previewImageUrl || ''" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/rechargeRules';
import apiCommon from '@/api/common';
export default {
    props: ['form', 'formType'],
    data() {
        return {
            formLabelWidth: '120px',

            qiniuForm: {},

            rules: {
                key: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        // 图片上传到七牛云之前
        handleBeforeUpload(file) {
            const param = {
                fileName: file.name
            };
            return apiCommon.getQiniuToken()
                .then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.qiniuForm = {
                            key: param.fileName,
                            token: content.uploadToken,
                            domain: content.domain
                        };
                    } else {
                        this.$notify.error('图片上传失败： token 获取失败');
                    }
                })
                .catch(err => {
                    this.$notify.error('图片上传失败： token 获取失败');
                    window.LOG('图片上传失败： token 获取失败', err);
                });
        },
        // 预览图片上传到七牛云成功
        handlePreviewSuccess(data) {
            const fileName = this.qiniuForm.domain + data.key;
            this.$set(this.form, 'previewImageUrl', fileName);
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            const form = JSON.parse(JSON.stringify(this.form));
            if (!form.payablePrice) {
                return this.$notify.error('请输入充值金额');
            }
            if (isNaN(form.payablePrice) || Number(form.payablePrice) < 0) {
                return this.$notify.error('请输入正确的充值金额');
            }
            if (!/^\d{1,9}(\.\d{1,2})?$/.test(form.payablePrice)) {
                return this.$notify.error('充值金额最大值为999999999.99，可保留两位小数');
            }
            form.payablePrice = Number(form.payablePrice);
            if (!form.giftAmount) {
                return this.$notify.error('请输入赠送金额');
            }
            if (isNaN(form.giftAmount) || Number(form.giftAmount) < 0) {
                return this.$notify.error('请输入正确的赠送金额');
            }
            if (Math.floor(Number(form.giftAmount)) < Number(form.giftAmount)) {
                return this.$notify.error('赠送金额必须为正整数');
            }
            if (!/^\d{1,9}$/.test(form.giftAmount)) {
                return this.$notify.error('赠送金额最大值为999999999');
            }
            form.giftAmount = Number(form.giftAmount);
            if (!form.previewImageUrl) {
                return this.$notify.error('请上传图片');
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = form;
                        api.rechargeRulesUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = form;
                        api.rechargeRulesInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
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
