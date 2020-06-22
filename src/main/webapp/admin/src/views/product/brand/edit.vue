<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="form.sortNo" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="品牌名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="状态" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.status" placeholder="请选择品牌">
                    <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="产品预览图" :label-width="formLabelWidth">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com/"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handlePreviewSuccess"
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
import apiCommon from '@/api/common';
import api from '@/api/product';
import { productBrandStatus } from '@/api/dict';
export default {
    props: ['formType', 'form'],
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

            // 状态列表
            statusList: [],

            // 七牛表单
            qiniuForm: {},


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
        // 图片上传到七牛云之前
        handleBeforeUpload() {
            const param = {
                fileName: new Date().getTime() + '_' + Math.random().toString(32).substring(2)
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
            this.$set(this.form, 'previewImageURL', fileName);
        },

        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        if (param.name.length > 20) {
                            this.$notify.error('品牌名称不能大于20个字符');
                            return;
                        }
                        api.productBrandUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        if (param.name.length > 20) {
                            this.$notify.error('品牌名称不能大于20个字符');
                            return;
                        }
                        api.productBrandInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
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
        },
        productBrandStatus() {
            productBrandStatus().then(res => {
                if (res.code === 100) {
                    const arr = [];
                    const obj = res.content;
                    for (const item in obj) {
                        if (obj.hasOwnProperty(item)) {
                            const ele = obj[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.statusList = arr;
                }
            });
        }
    },
    created() {
        this.productBrandStatus();
    }
};
</script>
