<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm" class="clearfix">
            <el-col :span="12">
                <el-form-item label="展示位置" :label-width="formLabelWidth" prop="showLocation">
                    <el-select v-model="form.showLocation" placeholder="展示位置">
                        <el-option v-for="(item,key) in bannerShowLocations" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="排序号" :label-width="formLabelWidth" prop="sortNo">
                    <el-input v-model="form.sortNo" type="text" auto-complete="off" maxlength="10"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="展示省份" :label-width="formLabelWidth" prop="provinceId">
                    <el-select v-model="form.provinceId" placeholder="展示省份" @change="availableAreasCities">
                        <el-option v-for="(item,key) in provinceList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="展示城市" :label-width="formLabelWidth" prop="cityId">
                    <el-select v-model="form.cityId" placeholder="展示城市">
                        <el-option v-for="(item,key) in cityList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="状态" :label-width="formLabelWidth" prop="status">
                    <el-select v-model="form.status" placeholder="状态">
                        <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="点击广告跳转至(URL)" :label-width="formLabelWidth" prop="detailUrl">
                    <el-input v-model="form.detailUrl" type="text" auto-complete="off" maxlength="255"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="24">
                <el-form-item label="缩略图" :label-width="formLabelWidth">
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        :show-file-list="false"
                        :data="qiniuForm"
                        :on-success="handlePreviewSuccess"
                        :before-upload="handleBeforeUpload"
                        :on-error="alertErrMsg">
                        <img style="width: 150px;height: 150px;" v-if="form.imageUrl"
                            :src="form.imageUrl || ''" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="图片宽度" :label-width="formLabelWidth" prop="width">
                    <el-input v-model="form.width" type="text" auto-complete="off" maxlength="8"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="图片高度" :label-width="formLabelWidth" prop="height">
                    <el-input v-model="form.height" type="text" auto-complete="off" maxlength="8"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="24">
                <el-form-item label="有效期" :label-width="formLabelWidth">
                    <el-row>
                        <el-radio v-model="form.expiryDaysFlag" label="0">自定义时间</el-radio>
                        <el-radio v-model="form.expiryDaysFlag" label="1">永久有效</el-radio>
                    </el-row>

                    <el-date-picker
                        v-show="form.expiryDaysFlag === '0'"
                        v-model="form.date"
                        type="datetimerange"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00','23:59:59']"
                        align="right"
                        unlink-panels
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </el-form-item>
            </el-col>
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
import api from '@/api/banner';
import apiCommon from '@/api/common';
import Util from '@/libs/util';

export default {
    props: ['formType', 'form'],
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value) || !/^[0-9]\d*$/.test(value)) {
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
            statusList: [{ id: '0', name: '否' }, { id: '1', name: '是' }],

            provinceList: [],
            cityList: [],

            // 七牛表单
            qiniuForm: {},

            bannerShowLocations: { APP_INDEX: 'APP-首页', APP_POP: 'APP-弹出' },


            rules: {

                showLocation: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                sortNo: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    formIsNaN, formIsInt
                ],
                provinceId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                cityId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                status: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                width: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    formIsNaN, formIsInt
                ],
                height: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    formIsNaN, formIsInt
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
            this.$set(this.form, 'imageUrl', fileName);
        },
        alertErrMsg(err, file) {
            this.$notify.warning(file.name + ',上传失败，请重试');
        },
        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            console.log(formType);
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = JSON.parse(JSON.stringify(this.form));
                    console.log(param);
                    if (!param.expiryDaysFlag) {
                        return this.$notify.error('请选择有效期');
                    }
                    if (param.expiryDaysFlag === '0') {
                        if (!param.date || param.date.length !== 2) {
                            return this.$notify.error('请选择时间');
                        }
                        param.beginTime = Util.formatTime(new Date(param.date[0]));
                        param.endTime = Util.formatTime(new Date(param.date[1]));
                    } else if (param.expiryDaysFlag === '1') {
                        param.beginTime = '';
                        param.endTime = '';
                    }
                    param.productCategoryId = '';
                    param.bannerTime = '';
                    if (formType === 'edit') {
                        api.bannerUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        api.bannerInsert(param).then(res => {
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
        getProvince() {
            apiCommon.userProvince().then(res => {
                if (res.code === 100) {
                    this.provinceList = res.content;
                }
            })
        },
        availableAreasCities(data) {
            if (data) {
                this.form.cityId = '';
            }
            const param = {
                provinceId: this.form.provinceId
            }
            apiCommon.availableAreasCities(param).then(res => {
                if (res.code === 100) {
                    this.cityList = res.content;
                }
            })
        }
    },
    created() {
        this.getProvince();
        if (this.form.provinceId) {
            this.availableAreasCities();
        }
    }
};
</script>
