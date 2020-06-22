<template>

    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 分组" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item v-show="form.categoryId !== 0" label="所在分类" :label-width="formLabelWidth" prop="categoryId" v-if="formType === 'add'">
                <el-select v-model="form.categoryId" placeholder="请选择所在分类">
                    <el-option v-for="(item,key) in fatherList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="form.sortNo" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="分组名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="预览图" :label-width="formLabelWidth">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com/"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handleAvatarSuccess"
                    :before-upload="handleBeforeUpload">
                    <img style="width: 150px;height: 150px;" v-if="form.previewImageurl" :src="form.previewImageurl || ''" class="avatar">
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
            rules: {
                sortNo: [
                    { required: true, message: '序号不能为空', trigger: 'blur' },
                    { validator: validateSortNo, trigger: 'blur' },
                    formIsNaN,
                    formIsInt
                ],
                name: [
                    { required: true, message: '分组名称不能为空', trigger: 'blur' }
                ],
                categoryId: [
                    { required: true, message: '必须选择所在分类', trigger: 'blur' }
                ]
            },

            qiniuForm: {},
            formLabelWidth: '120px'
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
            this.$set(this.form, 'previewImageurl', fileName);
        },
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        if (param.name.length > 20) {
                            this.$notify.error('分组名称不能大于20个字符');
                            return;
                        }
                        api.productGroupUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        if (param.name.length > 20) {
                            this.$notify.error('分组名称不能大于20个字符');
                            return;
                        }
                        api.productGroupInsert(param).then(res => {
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
