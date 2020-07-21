<style lang="less">
    @import "./topbar.less";
</style>
<template>
    <div class="topbar">
        <el-row type="flex" class="row-bg">
            <el-col :span="24">
                <div class="grid-content">
                    <span style="color:#fff;line-height:50px;display:inline-block;vertical-align:middle">教育管理系统</span>
                    <!--<breadcrumb></breadcrumb>-->
                </div>
            </el-col>
            <el-col :span="2" class="top-item">
                <div class="grid-content">
                    <el-menu
                        background-color="#278cff"
                        text-color="#fff"
                        mode="horizontal">
                        <el-submenu index="myCenter">
                            <template slot="title">{{nickname || '个人中心'}}</template>
                            <!-- <el-menu-item index="myMSG" @click="dialogMsgVisible=true">我的消息</el-menu-item> -->
                            <router-link :to="{name: '/user/updatePswd'}">
                                <el-menu-item index="/user/updatePswd">修改密码</el-menu-item>
                            </router-link>
                            <el-menu-item index="logout" @click="logout">安全退出</el-menu-item>
                        </el-submenu>
                    </el-menu>
                </div>
            </el-col>
        </el-row>
    </div>
</template>merchantArr.push
<script>

    import Util from '@/libs/util';
    import api from '@/api/common';
    import login from '@/api/login';
    import user from '@/api/user';

    import breadcrumb from '@/components/breadcrumb/index'

    export default {
        components: {
            breadcrumb
        },
        watch: {
            // 每一次路由变化，会执行该方法
            $route: 'routeChange'
        },
        data() {
            const permission = this.G.permission;
            return {
                siteURL: location.protocol + '//' + location.host + '/manager',
                index: '',
                dialogFormVisible: false,
                formLabelWidth: '60px',
                form: {
                    province: '',
                    city: ''
                },
                cityName: '',
                provinceList: [],
                cityList: [],
                permission: permission,
                dialogMsgVisible: false,
                isRedDotShowWork: false,
                isRedDotShowMsg: false,
                userId: '',
                nickname: '',

                quotaYear: '',
                quotaYearArr: [],

                quotaYearForm: {}
            };
        },
        methods: {
            routeChange() {
                const name = this.$route.name;
                this.index = name;
            },
            logout() {
                login.out();
                // axios.get('/account/customer/merchant/logOut');
                this.$router.push({ name: 'login' });
            },

            handleProvinceChange(data) {
                this.form.province = data;
                this.getCity();
            },
            getCity() {
                const param = {
                    provinceId: this.form.province
                };
                return api.userCity(param).then(res => {
                    if (res.code === 100) {
                        this.cityList = res.content;
                        if (res.content.length) {
                            this.form.city = res.content[0].id;
                        } else {
                            this.form.city = '';
                        }
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                    return res;
                });
            },
            getProvince() {
                return api.userProvince().then(res => {
                    if (res.code === 100) {
                        this.provinceList = res.content;
                        if (res.content.length) {
                            this.form.province = res.content[0].id;
                            this.getCity();
                        } else {
                            this.form.province = '';
                        }
                    }
                    return res;
                });
            },
            handleSubmit() {
                const param = {
                    quotaYear: this.form.quotaYear
                };
                if (!param.quotaYear) {
                    return alert('请选择年份');
                }
                user.saveQuotaYearCookie(param).then(res => {
                    if (res.code === 100) {
                        Util.setCookie('quotaYear', param.quotaYear, 30);
                        this.quotaYear = param.quotaYear;
                        location.reload();
                    }
                });
            },
            checkCityCookie() {
                const cityId = 1;
                const cityName = '三沙市';
                const provinceId = 1;
                this.cityName = cityName;
                Util.setCookie('cityName', cityName, 30);
                Util.setCookie('cityId', cityId, 30);
                Util.setCookie('provinceId', provinceId, 30);
                this.quotaYear = Util.getCookie('quotaYear');
            },
            getToken() {
                return api.getToken().then(res => {
                    if (res.code === 100) {
                        const data = res.content;
                        this.userId = data.id;
                        window.userinfo = data;
                        this.nickname = data.name;
                    }
                })
            },

            handleInitQuotaYear() {
                const param = {
                    startDate: this.quotaYearForm.startDate,
                    quotaYear: this.quotaYearForm.quotaYear
                };
                if (!param.startDate) {
                    return this.$notify.error('请选择开始日期');
                }
                if (!param.quotaYear) {
                    return this.$notify.error('请输入年份');
                }
                quotaYear.initNewQuotaYear(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('新建成功');
                        this.handleCloseQuotaYearForm();
                    }
                });
            }
        },
        async created() {
            this.routeChange();
            await this.getToken();
        }
    };
</script>

