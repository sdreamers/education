<style lang="less">
@import "./main.less";
</style>
<template>
    <el-container>
        <el-container>
            <sidebar></sidebar>
            <el-container>
                <el-header height="auto" :index="activeIndex" style="background-color:#278cff"><topbar></topbar></el-header>
                <visitedView></visitedView>
                <el-main>
                    <section name="slide-fade" v-if="$route.meta && $route.meta.noCache">
                        <router-view class="child-view"></router-view>
                    </section>
                    <section name="slide-fade" v-else>
                        <keep-alive>
                            <router-view class="child-view"></router-view>
                        </keep-alive>
                    </section>
                </el-main>
            </el-container>
        </el-container>
    </el-container>
</template>
<script>
// 侧边栏
import sidebar from '@/components/sidebar/sidebar.vue';
// 顶栏
import topbar from '@/components/topbar/topbar.vue';
import visitedView from '@/components/visitedViews/index.vue';
// 工具
import Util from '@/libs/util';
export default {
    components: {
        sidebar,
        topbar,
        visitedView
    },
    data() {
        return {
            currentView: ''
        };
    },
    watch: {
        // 每一次路由变化，会执行该方法
        $route: 'routeChange'
    },
    methods: {
        /**
         * 设置页面标题
         * @description 根据当前视图的name，获得视图标题
         */
        setPageTitle() {
            const name = this.$route.name;
            const title = Util.getViewTitle(this, name);
            this.activeIndex = name;
            document.title = title;
            if (title) {
                this.G.pageTitle = title.split('-')[0];
            }
        },
        /**
         * 路由变化
         * @param {Object} to 路由导航终点
         * @param {Object} from 路由导航起点
         * @description 路由变化时：1、如果两次路由为同一个视图，刷新页面；2、设置侧边栏；3、设置标题
         */
        routeChange() {
            this.setPageTitle();
        }
    },
    created() {
        this.setPageTitle();
    }
};
</script>
