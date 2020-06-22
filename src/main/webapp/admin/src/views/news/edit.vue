<template>
    <el-dialog :close-on-click-modal="false" title="填写新闻信息" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="新闻序号" :label-width="formLabelWidth" prop="sortNo">
                    <el-input v-model="form.sortNo" type="text" auto-complete="off" maxlength="4"></el-input>
                </el-form-item>
                <el-form-item label="新闻标题" :label-width="formLabelWidth" prop="title">
                    <el-input v-model="form.title" type="text" auto-complete="off" maxlength="255"></el-input>
                </el-form-item>
                <el-form-item label="点击图片跳转至URL" :label-width="formLabelWidth" prop="redirectionUrl">
                    <el-input v-model="form.redirectionUrl" type="text" auto-complete="off" maxlength="255"></el-input>
                </el-form-item>
                <el-form-item label="新闻内容" :label-width="formLabelWidth" prop="cityId">
                    <el-button @click="codeHandler">切换输入方式</el-button>
                    <quill-editor
                        v-if="toggleInputType"
                        v-model="form.content"
                        ref="qqq"
                        :options="editorOption">
                    </quill-editor>
                    <textarea v-if="!toggleInputType" name="" id="" style="width: 100%;height:300px;outline:0" v-model="form.content"></textarea>
                </el-form-item>
                <el-form-item label="新闻预览图" :label-width="formLabelWidth">
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        :show-file-list="false"
                        :data="qiniuForm"
                        :on-success="handlePreviewSuccess"
                        :before-upload="handleBeforeUpload"
                        :on-error="alertErrMsg">
                        <img style="width: 150px;height: 150px;" v-if="form.previewImageUrl"
                            :src="form.previewImageUrl || ''" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
                <el-form-item label="新闻是否在APP首页轮播展示" :label-width="formLabelWidth" prop="isShowIndex">
                    <el-select v-model="form.isShowIndex" placeholder="新闻是否在APP首页轮播展示">
                        <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="新闻是否在APP启用" :label-width="formLabelWidth" prop="enableFlag">
                    <el-select v-model="form.enableFlag" placeholder="新闻是否在APP启用">
                        <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-upload
                    class="avatar-uploader"
                    ref="upload"
                    style="display:none"
                    action="https://upload-z2.qiniup.com/"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handleImgSuccess"
                    :before-upload="handleBeforeUpload"
                    :on-error="alertErrMsg">
                    <i class="el-icon-plus avatar-uploader-icon" id="imgInput"></i>
                </el-upload>
            <!-- <el-form-item label="退货原因代码" :label-width="formLabelWidth" prop="returnCauseNo">
                <el-input v-model="form.returnCauseNo" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="退货原因描述" :label-width="formLabelWidth" prop="returnCauseDescription">
                <el-input v-model="form.returnCauseDescription" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="状态" :label-width="formLabelWidth" prop="isNeedImage">
                <el-select v-model="form.isNeedImage" placeholder="请选择品牌">
                    <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item> -->
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/news';
import apiCommon from '@/api/common';
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

import { quillEditor } from 'vue-quill-editor'
export default {
    props: ['formType', 'form'],
    components: {
        quillEditor
    },
    data() {
        return {
            formLabelWidth: '120px',

            // 状态列表
            statusList: [{ id: 'N', name: '否' }, { id: 'Y', name: '是' }],

            provinceList: [],
            cityList: [],

            // 七牛表单
            qiniuForm: {},

            editorOption: {
                // modules: {
                    // toolbar: [['bold'], [{ color: [] }], [{ align: [] }], [{ size: ['small', false, 'large', 'huge'] }]]
                // },
                placeholder: '输入内容'
            },
            toggleInputType: true,

            rules: {
                sortNo: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                title: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                isShowIndex: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                enableFlag: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        imgHandler() {
            this.addRange = this.$refs.qqq.quill.getSelection()
            const fileInput = document.getElementById('imgInput')
            fileInput.click() // 加一个触发事件
        },
        codeHandler() {
            console.log(123);
            this.toggleInputType = !this.toggleInputType;
        },
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
            this.$set(this.form, 'previewImageUrl', fileName);
        },
        handleImgSuccess(data) {
            let fileName = this.qiniuForm.domain + data.key;
            this.addRange = this.$refs.qqq.quill.getSelection()
            fileName = fileName.indexOf('http') !== -1 ? fileName : 'https:' + fileName
            this.$refs.qqq.quill.insertEmbed(this.addRange !== null ? this.addRange.index : 0, 'image', fileName)
        },
        alertErrMsg(err, file) {
            this.$notify.warning(file.name + ',上传失败，请重试');
        },
        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = JSON.parse(JSON.stringify(this.form));
                    console.log(param);
                    if (formType === 'edit') {
                        api.newsUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        api.newsInsert(param).then(res => {
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
        }
    },
    mounted() {
        console.log(this.form);
        if (this.form.contentUrl) {
            axios.get(this.form.contentUrl).then(res => {
                // console.log(res.data);
                const data = res.data;
                const div = document.createElement('div');
                div.innerHTML = data;
                const html = div.querySelector('.app-news-container').innerHTML;

                this.form.content = html;
            })
        }
        const that = this;
        setTimeout(() => {
            that.$refs.qqq.quill.getModule('toolbar').addHandler('image', this.imgHandler)
            // that.$refs.qqq.quill.getModule('toolbar').addHandler('code-block', this.codeHandler)
            console.log(that.$refs.qqq);
        }, 666);
    }
};
</script>
