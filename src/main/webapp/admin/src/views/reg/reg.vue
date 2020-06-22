<style lang="less">
    @import './reg.less';
</style>

<template>
    <div class="reg" @keydown.enter="login">
        <img src="https://images.dongpinyun.com/erp/login-bg.png" alt="">
        <div class="reg-con">
            <el-card :bordered="false">
                <div slot="header" class="clearfix">
                    <span>注册</span>
                </div>
                <div class="form-con">
                    <el-form ref="loginForm" :model="form" :rules="rules">
                        <el-form-item label="邮箱：" :label-width="formLabelWidth" prop="email">
                            <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100"></el-input>
                        </el-form-item>
                        <el-form-item label="手机：" :label-width="formLabelWidth" prop="telephone">
                            <el-input v-model="form.telephone" placeholder="请输入手机" maxlength="11"></el-input>
                        </el-form-item>
                        <el-form-item label="姓名：" :label-width="formLabelWidth" prop="nickname">
                            <el-input v-model="form.nickname" placeholder="请输入姓名" maxlength="16"></el-input>
                        </el-form-item>
                        <el-form-item label="密码：" :label-width="formLabelWidth" prop="pswd">
                            <el-input v-model="form.pswd" placeholder="请输入密码" maxlength="20"></el-input>
                        </el-form-item>
                        <el-form-item label="确认密码：" :label-width="formLabelWidth" prop="pswd2">
                            <el-input v-model="form.pswd2" placeholder="请输入确认密码" maxlength="20"></el-input>
                        </el-form-item>
                        <el-form-item label="省份：" :label-width="formLabelWidth" prop="provinceId">
                            <el-select v-model="form.provinceId" placeholder="请选择省份" @change="handleProvinceChange">
                                <el-option v-for="(item,key) in provinceList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="城市：" :label-width="formLabelWidth" prop="cityId">
                            <el-select v-model="form.cityId" placeholder="请选择城市">
                                <el-option v-for="(item,key) in cityList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="验证码：" :label-width="formLabelWidth" prop="vcode">
                            <el-input v-model="form.vcode" placeholder="请输入验证码" style="width:100px;vertical-align:middle" maxlength="4"></el-input>
                            <img style="cursor:pointer;height:28px;vertical-align:middle" :src="vcodeURL" @click="refreshVcode">
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="handleLogin" type="info" style="float:left">返回</el-button>
                            <el-button @click="handleSubmit" type="primary" style="float:right">提交</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
import api from '@/api/reg';
import apiCommon from '@/api/common';

const vcodeURL = vars.URLApiBase + '/common/openapi/getGifCode';
export default {
    data() {
        return {
            provinceList: [],
            cityList: [],
            form: {
                email: '',
                telephone: '',
                nickname: '',
                pswd: '',
                pswd2: '',
                provinceId: '',
                cityId: '',
                vcode: ''
            },
            vcodeURL: vcodeURL + '?t=' + new Date().getTime(),
            formLabelWidth: '120px',
            rules: {
                userName: [
                    { required: true, message: '账号不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        refreshVcode() {
            this.vcodeURL = vcodeURL + '?t=' + new Date().getTime();
        },
        handleLogin() {
            this.$router.push({ name: 'login' })
        },
        handleSubmit() {
            console.log(this.form);
            const param = this.form;
            for (const item in param) {
                if (param.hasOwnProperty(item)) {
                    const element = param[item];
                    if (element === '') {
                        return this.$notify.error('请完整填写注册信息');
                    }
                }
            }
            if (param.pswd !== param.pswd2) {
                return this.$notify.error('两次输入的密码不一致，请确认');
            }
            if (param.vcode.length !== 4) {
                return this.$notify.error('验证码为4位数');
            }

            if (this.loading) {
                return this.$notify.info('请稍候');
            }
            this.loading = true;
            setTimeout(() => {
                this.loading = false;
            }, 2000);

            api.reg(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    setTimeout(() => {
                        this.$router.push({ name: 'login' });
                    }, 233);
                } else {
                    this.refreshVcode();
                }
            })
        },
        handleProvinceChange() {
            this.form.cityId = '';
            this.getCity();
        },
        getCity() {
            const param = {
                provinceId: this.form.provinceId
            }
            apiCommon.availableAreasCities(param).then(res => {
                if (res.code === 100) {
                    this.cityList = res.content;
                }
            })
        },
        getProvince() {
            apiCommon.availableAreasProvinces({}).then(res => {
                if (res.code === 100) {
                    console.log(res);
                    this.provinceList = res.content;
                }
            })
        }
    },
    created() {
        this.getProvince();
    }
};
</script>

<style>

</style>
