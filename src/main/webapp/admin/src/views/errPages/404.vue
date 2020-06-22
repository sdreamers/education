<style lang="less">
    @import './404.less';
</style>

<template>
    <div class="error404">
        <div class="error404-body-con">
            <el-card shadow="none">
                <div class="error404-body-con-title">4<span><i class="el-icon-loading"></i></span>3</div>
                <p class="error404-body-con-message">没有权限</p>
                <div class="error404-btn-con">
                    <el-button @click="goHome" size="large" style="width: 200px;" type="text">返回首页</el-button>
                    <el-button @click="backPage" size="large" style="width: 200px;margin-left: 40px;" type="primary">返回上一页</el-button>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Error404',
    methods: {
        backPage() {
            this.$router.go(-1);
        },
        goHome() {
            this.$router.push({
                name: 'home'
            });
        }
    }
};
</script>
