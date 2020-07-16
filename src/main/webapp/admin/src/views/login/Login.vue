<style lang="less">
    @import './login.less';
</style>

<template>
    <div class="login">
        <!--<img :src="bg">-->
        <div class="bg" :style="'backgroundImage: url(' + bg + ')'"></div>
        <img class="wall" :src="wall"/>
        <img class="roof" :src="roof"/>
        <img class="cloud" :src="cloud"/>
        <img class="cloud-line" :src="cloudLine"/>
        <img class="stone" :src="stone"/>
        <!--<img src="https://images.dongpinyun.com/erp/login-bg.png" alt="">-->
        <!--<img src="https://images.dongpinyun.com/erp/login-assets.png" alt="" class="float-assets">-->
        <div class="login-con">
            <el-card :bordered="false">
                <!-- <div slot="header" class="clearfix"> -->
                    <!--<p style="color: #999;font-size:18px">欢迎登录油料管理系统</p>-->
                <!-- </div> -->
                <div slot="header" class="login-con-header">
                    <span>系统登录</span>
                </div>
                <div class="form-con">
                    <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px">
                        <el-form-item prop="account" label="账号：">
                            <el-input v-model="form.account" placeholder="请输入账号" @keyup.enter.native="login">
                                <!-- <template slot="prepend"><i class="el-icon-message"></i></template> -->
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="password" label="密码：">
                            <el-input type="password" v-model="form.password" placeholder="请输入密码" @keyup.enter.native="login">
                                <!-- <template slot="prepend"><i class="el-icon-goods"></i></template> -->
                            </el-input>
                        </el-form-item>
                        <!--<el-form-item>-->
                            <el-button @click="login" class="loginBtn" type="primary" style="width:100%;line-height:30px;background-color:#31b2ff;font-size:16px;">登录</el-button>
                        <!--</el-form-item>-->
                        <!--<el-form-item style="text-align:center">-->
                            <!-- <el-checkbox v-model="form.rememberMe">记住我</el-checkbox> -->
                            <!--<router-link :to="{name: 'reg'}"><el-button type="text" style="color:#999">我要注册</el-button></router-link>-->
                            <!--|-->
                            <!--<el-button type="text" style="color:#999" @click.stop="handleResetPassword">忘记密码</el-button>-->
                        <!-- </el-form-item>
                        <el-form-item> -->
                        <!--</el-form-item>-->
                    </el-form>
                </div>
            </el-card>
        </div>
        <el-dialog :close-on-click-modal="false" title="请输入手机验证码" :visible.sync="dialogFormVisible">
            <p>登录需要验证身份，验证码已发送至：{{receiveNumber}}，请稍等</p>
            <el-form ref="form" :model="form">
                <el-form-item>
                    <el-input v-model="form.vcode" placeholder="请输入短信验证码" @keyup.enter.native="handleSubmit"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="重置密码" @close="handleCancelReset" center :visible.sync="resetForm.visible" width="25%">
            <p>重置密码需要验证手机号，验证码将发送至：{{resetForm.telephone}}</p>
            <el-form ref="form" :model="resetForm">
                <el-form-item>
                    <el-input v-model="resetForm.msgCode" style="width: 70%" placeholder="请输入短信验证码" @keyup.enter.native="handleSubmitReset"></el-input>
                    <el-button :type="resetForm.btnType" :disabled="resetForm.btnDisabled" @click.stop="handleSendMsgCode" round>{{resetForm.btnMessage}}</el-button>
                </el-form-item>
                <el-form-item>
                    <el-input type="password" v-model="resetForm.password" style="width: 70%" placeholder="请输入新密码" @keyup.enter.native="handleSubmitReset"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input type="password" v-model="resetForm.checkPassword" style="width: 70%" placeholder="请输入确认新密码" @keyup.enter.native="handleSubmitReset"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelReset">取 消</el-button>
                <el-button type="primary" @click="handleSubmitReset">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import Util from '@/libs/util';
    import api from '@/api/login';
    import apiCommon from '@/api/common';
    import user from '@/api/user';

    import bg from '@/assets/images/login/bg.png';
    import cloud from '@/assets/images/login/cloud.png';
    import cloudLine from '@/assets/images/login/cloud-line.png';
    import roof from '@/assets/images/login/roof.png';
    import stone from '@/assets/images/login/stone.png';
    import wall from '@/assets/images/login/wall.png';

    const resetForm = {
        telephone: '',
        btnMessage: '获取验证码',
        btnType: 'primary',
        btnDisabled: false,
        email: '',
        msgCode: '',
        password: '',
        checkPassword: '',
        visible: false,
        id: ''
    };

    export default {
        data() {
            return {
                bg,
                cloud,
                cloudLine,
                roof,
                stone,
                wall,

                siteURL: window.vars.URLApiBase,
                sendCode: '',
                form: {
                    userName: '',
                    password: '',
                    rememberMe: true,
                    vcode: ''
                },
                rules: {
                    userName: [
                        { required: true, message: '账号不能为空', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '密码不能为空', trigger: 'blur' }
                    ]
                },
                dialogFormVisible: false,
                receiveNumber: '',

                resetForm: JSON.parse(JSON.stringify(resetForm))
            };
        },
        methods: {
            handleSubmit() {
                if (this.form.vcode && this.receiveNumber) {
                    const param = {
                        email: this.form.userName,
                        password: md5(this.form.userName + '#' + this.form.password),
                        captcha: this.form.vcode,
                        rememberMe: this.form.rememberMe
                    };
                    api.login(param).then(res => {
                        if (res.code === 100) {
                            const userInfo = {};
                            userInfo.name = this.form.userName;
                            const userInfoStr = JSON.stringify(userInfo);
                            Util.setCookie('dpy_manager', userInfoStr);
                            Util.delCookie('cityId');
                            Util.delCookie('cityName');
                            if (res.content) {
                                this.showNumberAndSend(res.content);
                            } else {
                                apiCommon.listPermissionsByUserId().then(() => {
                                    this.$router.push({ name: '/account/user/index' });
                                })
                            }
                        }
                    }).catch(err => {
                        this.$notify.error(err.message || '未知错误');
                    });
                } else {
                    this.$notify.error('手机号或者验证码未输入');
                }
            },
            showNumberAndSend(telephone) {
                this.receiveNumber = telephone;
                this.dialogFormVisible = true;
                const param = {
                    telephone
                };
                api.getLoginMsgCode(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '发送成功');
                    }
                })
            },
            login() {
                this.$refs.loginForm.validate(valid => {
                    if (valid) {
                        const param = {
                            account: this.form.account,
                            password: md5(this.form.password)
                        };
                        if (this.sendCode === 'true') {
                            user.checkPasswordAndSendCaptcha(param).then(res => {
                                if (res.code === 100) {
                                    this.receiveNumber = res.content.telephone;
                                    this.dialogFormVisible = true;
                                }
                            });
                        } else {
                            const param = {
                                account: this.form.account,
                                password: md5(this.form.password),
                                rememberMe: this.form.rememberMe
                            };
                            api.login(param).then(res => {
                                if (res.code === 100) {
                                    const userInfo = {};
                                    userInfo.name = this.form.userName;
                                    const userInfoStr = JSON.stringify(userInfo);
                                    Util.setCookie('education_user', userInfoStr);
                                    if (res.content) {
                                        this.showNumberAndSend(res.content);
                                    } else {
                                        /* apiCommon.listPermissionsByUserId().then(() => {
                                            this.$router.push({ name: '/account/merchant/index' });
                                        }) */
                                        this.$router.push({ name: '/task/index' })
                                    }
                                }
                            }).catch(err => {
                                this.$notify.error(err.message || '未知错误');
                            });
                        }
                    }
                });
            },

            /**
             * 重置密码
             * */
            handleResetPassword() {
                if (!this.form.userName) {
                    return this.$notify.error('请输入登陆邮箱账号');
                }
                user.findByEmail({ email: this.form.userName }).then(res => {
                    if (res.code === 100) {
                        if (res.content == null) {
                            return this.$notify.error('邮箱账号不存在');
                        } else {
                            this.resetForm.telephone = res.content.telephone;
                            this.resetForm.id = res.content.id;
                            this.resetForm.email = this.form.userName;
                            this.resetForm.visible = true;
                        }
                    }
                });
            },

            handleSendMsgCode() {
                user.getResetPasswordMsgCode({ telephone: this.resetForm.telephone }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('验证码已发送');
                        let second = 60;
                        const timer = setInterval(() => {
                            second = second - 1;
                            if (second > 0) {
                                this.resetForm.btnType = 'info';
                                this.resetForm.btnMessage = `${second}秒后重发`;
                                this.resetForm.btnDisabled = true;
                            } else {
                                clearInterval(timer);
                                this.resetForm.btnType = 'primary';
                                this.resetForm.btnMessage = '获取验证码';
                                this.resetForm.btnDisabled = false;
                            }
                        }, 1000);
                    }
                });
            },

            handleCancelReset() {
                this.resetForm = JSON.parse(JSON.stringify(resetForm));
            },

            handleSubmitReset() {
                if (this.resetForm.msgCode === '') {
                    return this.$notify.error('请输入验证码');
                }
                if (this.resetForm.password === '') {
                    return this.$notify.error('请输入新密码');
                }
                if (this.resetForm.checkPassword === '') {
                    return this.$notify.error('请输入确认新密码');
                }
                if (this.resetForm.password !== this.resetForm.checkPassword) {
                    return this.$notify.error('新密码与确认新密码不一致');
                }
                const param = {
                    id: this.resetForm.id,
                    email: this.resetForm.email,
                    telephone: this.resetForm.telephone,
                    password: this.resetForm.password,
                    msgCode: this.resetForm.msgCode
                };
                user.resetPassword(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('重置密码成功');
                        this.resetForm = JSON.parse(JSON.stringify(resetForm));
                        location.reload();
                    }
                });
            }
        },
        created() {
            Vue.prototype.G.permission = undefined;
            window.permission = undefined;
        }
    };
</script>

<style>

</style>
