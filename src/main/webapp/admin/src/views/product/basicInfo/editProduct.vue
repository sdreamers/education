<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 商品" :visible="true" top="5vh" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="商品名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="商品简介" :label-width="formLabelWidth" prop="description">
                <el-input v-model="form.description" type="text" maxlength="25" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
                <el-input v-model="form.remark" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="一级品类" :label-width="formLabelWidth" prop="categoryFatherId">
                <el-select v-model="form.categoryFatherId" placeholder="请选择一级品类"
                           @change="handleformCategoryFatherChange">
                    <el-option v-for="(item,key) in fatherList" :label="item.name" :value="item.id"
                               :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="二级品类" :label-width="formLabelWidth" prop="categoryId">
                <el-select v-model="form.categoryId" placeholder="请选择二级品类">
                    <el-option v-for="(item,key) in categoryList" :label="item.name" :value="item.id"
                               :key="key"></el-option>
                </el-select>
            </el-form-item>
            <!--<el-form-item label="产品预览图" :label-width="formLabelWidth">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com/"
                    :show-file-list="false"
                    :data="qiniuForm"
                    :on-success="handlePreviewSuccess"
                    :before-upload="handleBeforeUpload"
                    :on-error="alertErrMsg">
                    <img style="width: 150px;height: 150px;" v-if="form.productPreviewImageURL"
                         :src="form.productPreviewImageURL || ''" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>-->
            <!--<el-form-item label="产品详情图" :label-width="formLabelWidth">
                <el-upload
                    class="avatar-uploader"
                    action="https://upload-z2.qiniup.com/"
                    list-type="picture-card"
                    :data="qiniuForm"
                    :on-success="handleDetailSuccess"
                    :file-list="form.productDetailImageURLs"
                    :on-remove="handleDetailRemove"
                    :before-upload="checkUpload_detailImg"
                    :on-error="alertErrMsg">
                    <i class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>-->
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
        props: ['form', 'formLabelWidth', 'brandList', 'areaList', 'fatherList', 'splitList', 'formType', 'productMerchantTypeList'],
        data() {
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
                callback(new Error('保质期必须为大于等于0的正整数'));
            };
            const formRequire = { required: true, message: '不能为空', trigger: 'blur' };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                // 七牛表单
                qiniuForm: {},
                categoryList: [],
                isCategoryInit: false,
                rules: {
                    name: [formRequire],
                    factoryName: [formRequire],
                    qualityGuaranteePeriod: [formRequire, formIsNaN, formIsInt],
                    categoryFatherId: [formRequire],
                    categoryId: [formRequire]
                }
            };
        },
        methods: {
            // 图片上传到七牛云之前
            handleBeforeUpload() {
                const param = {
                    // fileName: uuid.v1() + '-' + file.name
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

            getUploadToken: function() {
                const param = {
                    // fileName: uuid.v1() + '-' + file.name
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
            // 图片上传到七牛云之前
            checkUpload_detailImg(file) {
                if (this.form.productDetailImageURLs.length > 5) {
                    this.$notify.warning('【产品详情图】最多上传6张');
                    return false
                }
                return this.getUploadToken(file);
            },
            checkUpload_reportImg(file) {
                if (this.form.inspectionReportImageURLs.length > 5) {
                    this.$notify.warning('【检疫报告图】最多上传6张');
                    return false
                }
                return this.getUploadToken(file);
            },
            // 预览图片上传到七牛云成功
            handlePreviewSuccess(data) {
                const fileName = this.qiniuForm.domain + data.key;
                this.$set(this.form, 'productPreviewImageURL', fileName);
            },
            // 详情图片上传到七牛云成功
            handleDetailSuccess(data) {
                const fileName = this.qiniuForm.domain + data.key;
                let productDetailImageURLs = this.form.productDetailImageURLs;
                if (!productDetailImageURLs) {
                    productDetailImageURLs = [];
                }
                productDetailImageURLs.push({ name: fileName, url: fileName });
                this.$set(this.form, 'productDetailImageURLs', productDetailImageURLs);
            },
            // 检疫图片上传到七牛云成功
            handleInspectionSuccess(data) {
                const fileName = this.qiniuForm.domain + data.key;
                let inspectionReportImageURLs = this.form.inspectionReportImageURLs;
                if (!inspectionReportImageURLs) {
                    inspectionReportImageURLs = [];
                }
                inspectionReportImageURLs.push({ name: fileName, url: fileName });
                this.$set(this.form, 'inspectionReportImageURLs', inspectionReportImageURLs);
            },
            // 移除详情图
            handleDetailRemove(data) {
                const url = data.url;
                const productDetailImageURLs = this.form.productDetailImageURLs.filter(item => item.url !== url);
                this.$set(this.form, 'productDetailImageURLs', productDetailImageURLs);
            },
            // 移除检疫图
            handleInspectionRemove(data) {
                const url = data.url;
                const inspectionReportImageURLs = this.form.inspectionReportImageURLs.filter(item => item.url !== url);
                this.$set(this.form, 'inspectionReportImageURLs', inspectionReportImageURLs);
            },
            // 一级品类选中后，获取二级品类
            handleformCategoryFatherChange(id) {
                if (this.isCategoryInit) {
                    this.form.categoryId = '';
                } else {
                    if (this.formType !== 'edit') {
                        this.form.categoryId = '';
                    }
                    this.isCategoryInit = true;
                }
                const param = {
                    fatherId: id
                };
                apiCommon.productCategoryBasic(param).then(res => {
                    if (res.code === 100) {
                        this.categoryList = res.content;
                    }
                });
            },
            // 提交基本信息
            handleSubmit() {
                const formType = this.formType;
                console.log('formType', formType);
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        if (formType === 'edit') {
                            const param = JSON.parse(JSON.stringify(this.form));
                            if (param.saveMethod.length > 20) {
                                this.$notify.error('存储方式不能大于20个字符');
                                return;
                            }
                            if (param.remark.length > 255) {
                                this.$notify.error('备注不能大于255个字符');
                                return;
                            }
                            param.inspectionReportImageURLs = param.inspectionReportImageURLs || [];
                            param.productDetailImageURLs = param.productDetailImageURLs || [];
                            param.inspectionReportImageURLs = param.inspectionReportImageURLs.map(item => item.url).join(',');
                            param.productDetailImageURLs = param.productDetailImageURLs.map(item => item.url).join(',');
                            api.productBasicInfoUpdate(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.$emit('close');
                                }
                            });
                        } else if (formType === 'add') {
                            const param = JSON.parse(JSON.stringify(this.form));
                            console.log(param);
                            if (param.saveMethod.length > 20) {
                                this.$notify.error('存储方式不能大于20个字符');
                                return;
                            }
                            if (param.remark.length > 255) {
                                this.$notify.error('备注不能大于255个字符');
                                return;
                            }
                            param.inspectionReportImageURLs = param.inspectionReportImageURLs || [];
                            param.productDetailImageURLs = param.productDetailImageURLs || [];
                            param.inspectionReportImageURLs = param.inspectionReportImageURLs.map(item => item.url).join(',');
                            param.productDetailImageURLs = param.productDetailImageURLs.map(item => item.url).join(',');
                            api.productBasicInfoInsert(param).then(res => {
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
            },
            alertErrMsg(err, file) {
                this.$notify.warning(file.name + ',上传失败，请重试');
            }
        },
        created() {
            if (this.formType === 'edit') {
                this.handleformCategoryFatherChange(this.form.categoryFatherId);
            }
        }
    };
</script>
