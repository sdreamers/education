<style lang="less">
  @import "./sidebar.less";
</style>

<template>
    <el-aside id="aside" width="220px" style="background-color: #1d73d0" height="auto">
        <div id="sidebar" class="sidebar" style="margin-top: 15%">
            <el-menu
                :default-active="index"
                :default-openeds="defaultOpeneds"
                class="el-menu-vertical-demo"
                active-text-color="#ffd04b"
                background-color="#1d73d0"
                text-color="#fff">

                <router-link :to="{name: '/project/index'}"><el-menu-item  index="/project/index"><img class="menu-icon" :src="iconYonghu" /><span class="sidebar-title">项目进度</span></el-menu-item></router-link>
                <router-link :to="{name: '/task/index'}"><el-menu-item  index="/task/index"><img class="menu-icon" :src="iconYonghu" /><span class="sidebar-title">设备明细</span></el-menu-item></router-link>

            </el-menu>
        </div>
    </el-aside>
</template>
<script>

import iconTongji from '@/assets/images/icon-tongji.png'
import iconDingdan from '@/assets/images/icon-dingdan.png'
import iconShouhou from '@/assets/images/icon-shouhou.png'
import iconXinxi from '@/assets/images/icon-xinxi.png'
import iconXitong from '@/assets/images/icon-xitong.png'
import iconYingxiao from '@/assets/images/icon-yingxiao.png'
import iconYonghu from '@/assets/images/icon-yonghu.png'
import iconShangpin from '@/assets/images/icon-shangpin.png'
import iconArrow from '@/assets/images/icon-arrow.png'
import logo1 from '@/assets/images/logo1.png'
import logo2 from '@/assets/images/logo2.png'

export default {
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
            userId: '',
            nickname: '',

            sidebarAll: true,

            defaultOpeneds: ['dataCenter', 'productCenter', 'productOffline', 'orderCenter', 'userCenter'],

            iconTongji,
            iconDingdan,
            iconShouhou,
            iconXinxi,
            iconXitong,
            iconYingxiao,
            iconYonghu,
            iconShangpin,
            iconArrow,
            logo1,
            logo2
        };
    },
    methods: {
        routeChange() {
            const name = this.$route.name;
            this.index = name;
        }
    },
    created() {
        this.routeChange();
    }
}
</script>
