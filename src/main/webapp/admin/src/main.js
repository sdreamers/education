import Vue from 'vue';
import App from './App';
import router from './router';
import store from './store'
// element的默认样式偏大，全局设置为mini
import ElementUI from 'element-ui';

Vue.use(ElementUI);
Vue.prototype.$ELEMENT = { size: 'mini' };

window.DebugMode = /__DEBUG_MODE__/.test(location.href);
window.LOG = function(data) {
    if (!window.DebugMode) {
        return false;
    }
    console.log(data);
};

Vue.config.productionTip = false;

Vue.prototype.G = {
    pageTitle: '冻品云',
    permission: {},
    userToken: undefined
};

// 字符串取出左右空格
String.prototype.trim = function() {
    return this.replace(/(^\s*)|(\s*$)/g, '');
};
// 读取cookie
window.getCookie = function(name) {
    const reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
    const arr = document.cookie.match(reg);
    if (arr) {
        return decodeURI(arr[2]);
    } else {
        return null;
    }
}

/* eslint-disable no-new */
Vue.filter('dictFilter', (val, map) => {
    if (!val) return '';
    return map[val];
});
new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
});
