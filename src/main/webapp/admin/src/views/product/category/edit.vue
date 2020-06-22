<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="form.sortNo" type="text" auto-complete="off" maxlength="11"></el-input>
            </el-form-item>
            <el-form-item v-show="form.fatherId !== 0" label="父级品类" :label-width="formLabelWidth">
                <el-select v-model="form.fatherId" placeholder="请选择父级品类">
                    <el-option v-for="(item,key) in fatherList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="品类名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="预览图" :label-width="formLabelWidth">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com/"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handleAvatarSuccess"
                    :before-upload="handleBeforeUpload">
                    <img style="width: 150px;height: 150px;" v-if="form.previewImageURL" :src="form.previewImageURL || ''" class="avatar">
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
import api from '@/api/product';
import apiCommon from '@/api/common';
export default {
    props: ['form', 'fatherList', 'formType'],
    data() {
        const validateSortNo = function(rule, value, callback) {
            if (value.toString().length > 3) {
                callback(new Error('不得超过3位'));
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
                sortNo: [
                    { required: true, message: '序号不能为空', trigger: 'blur' },
                    { validator: validateSortNo, trigger: 'blur' },
                    formIsNaN,
                    formIsInt
                ],
                categoryNo: [
                    { required: true, message: '编号不能为空', trigger: 'blur' }
                ],
                name: [
                    { required: true, message: '品类名称不能为空', trigger: 'blur' }
                ]
            },

            qiniuForm: {}
        };
    },
    methods: {

        // 图片上传到七牛云之前
        handleBeforeUpload() {
            const params = {
                // fileName: uuid.v1() + '-' + file.name
                fileName: new Date().getTime() + '_' + Math.random().toString(32).substring(2)
            };
            return apiCommon.getQiniuToken()
                .then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.qiniuForm = {
                            key: params.fileName,
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
        // 图片上传到七牛云成功
        handleAvatarSuccess(data) {
            const fileName = this.qiniuForm.domain + data.key;
            this.$set(this.form, 'previewImageURL', fileName);
        },
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        if (this.formBak.fatherId !== param.fatherId) {
                            param.groupId = 0;
                        }
                        api.productCategoryUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                if (param.fatherId) {
                                    this.$emit('close', {
                                        id: param.fatherId
                                    });
                                } else {
                                    this.$emit('close');
                                }
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.productCategoryInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                if (param.fatherId) {
                                    this.$emit('close', {
                                        id: param.fatherId
                                    });
                                } else {
                                    this.$emit('close');
                                }
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
    },
    mounted() {
        this.formBak = JSON.parse(JSON.stringify(this.form));
    }
};
</script>
