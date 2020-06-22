import Vue from 'vue';
import VueRouter from 'vue-router';
import { routers } from './router';
import Util from '@/libs/util';
import api from '@/api/common';

Vue.use(VueRouter);

// 路由配置
const RouterConfig = {
    routes: routers
};

const router = new VueRouter(RouterConfig);

const whiteList = [
    'home',
    'index',
    'error_404',
    'help',
    'helpIndex',
    '_order/separateOrder',
    '_stat/monthlyStat',

    // // 后台没有设置权限，所以单独给白名单

    // 商品分组设置，
    '_productGroup/index',
    // 菜系和品牌分组
    '_productBrandOrMerchantTypeGroup/index',

    '_order/edit',
    '_order/editSelfPick',
    '_order/editBranchCompany',
    '_user/index',
    '_account/role/mypermission',
    '_account/user/index',
    '_account/user/updateSelf',
    '_account/user/updatePswd',
    '_new/index',
    '_cut/index',
    '_order/index',
    '_account/merchant/index'
];

function handleRouter(router) {
    whiteList.forEach(item => {
        router[item] = true;
    });
}

/**
 * 路由前置守卫
 */
router.beforeEach(async (to, from, next) => {
    // 停止自动扫描定时器
    clearInterval(window.autoScan);
    return next();
    // 每次路由跳转，都会验证当前是否登录，如果没登录，跳转登录页
    // 但是跳转登录页也算路由跳转操作，所以要判断跳转去的页面不是登录页，才跳转登陆页，避免死循环
    /* const hasCookie = !!Util.getCookie('dpy_manager');

    if (to.name === 'login' || to.name === 'reg') {
        return next();
    }
    await api.getToken().then(res => {
        if (res === undefined) {
            return next({
                name: 'login'
            });
        }
        if (res.code === 100) {
            // 将用户信息存到全局，方便随时调取
            Vue.prototype.G.userInfo = res.content;
        }
    })
    let toName = to.name;
    api.listPermissionsByUserId().then(res => {
        // 如果没登录，跳到登录页
        window.LOG(res);
        if (res === undefined) {
            return next({ name: 'login' });
        }
        if (toName === 'error_404') {
            return next();
        }
        handleRouter(res);
        console.log(toName);
        toName = toName.replace('/', '_');
        if (!res[toName] && toName !== 'index' && toName.indexOf('redirect') === -1) {
            window.LOG('404');
            next({ name: 'error_404' });
        } else if (!hasCookie) {
            window.LOG('no cookie');
            next({ name: 'login' });
        } else if (to.name === 'index') {
            window.LOG('index');
            next({ name: 'home' });
        } else {
            window.LOG('any');
            if (to.matched.length) {
                // 否则，愿意跳哪就跳哪
                next();
            } else {
                next({ name: 'error_404' });
            }
        }
    }).catch(err => {
        window.LOG('router error', err);
        window.ELEMENT.Notification({
            message: '路由错误',
            type: 'error',
            duration: 2 * 1000
        });
        next({ name: 'login' });
    });*/
});

export default router;
