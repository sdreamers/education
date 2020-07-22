import fetch from '@/libs/fetch';
import axios from 'axios';
import Vue from 'vue';

export default {
    // 登录
    saveCookie(data) {
        return fetch({
            url: 'account/u/saveCookie',
            method: 'post',
            params: data
        });
    },
    // 所有权限
    permissionPagers(data) {
        return fetch({
            url: 'account/permission/pagers',
            method: 'get',
            params: data
        });
    },
    // 根据用户 id 获取用户的权限列表
    listPermissionsByUserId() {
        if (Vue.prototype.G.permission && Vue.prototype.G.permission.done) {
            return new Promise(res => {
                res(Vue.prototype.G.permission);
            });
        }
        const data = { timestamp: new Date().getTime() }
        return axios({
            url: window.vars.URLApiBase + '/user/permissions',
            method: 'get',
            params: data,
            withCredentials: true
        }).then(data => {
            const res = data.data;
            if (res.code === 100) {
                const obj = {};
                res.content.forEach(item => {
                    obj[item.url.replace('/', '_')] = true;
                });
                obj.done = true;
                Vue.prototype.G.permission = obj;
                window.permission = obj;
                // return new Promise(res => {
                return obj;
            } else {
                window.ELEMENT.Notification({
                    message: '权限信息获取异常，请重新登录后重试',
                    type: 'error'
                });
                return undefined;
            }
        }).catch(err => {
            console.log(err);
            window.LOG('权限信息获取异常，请重新登录后重试');
            window.ELEMENT.Notification({
                message: '权限信息获取异常，请重新登录后重试',
                type: 'error'
            });
        });
    },
    getSocketIOConfig() {
        return fetch({
            url: '/account/userMessage/getSocketIOConfig',
            method: 'get'
        })
    },
    getToken() {
        if (Vue.prototype.G.userToken) {
            return new Promise(res => {
                res(Vue.prototype.G.userToken);
            });
        }
        const data = { timestamp: new Date().getTime() }
        return axios({
            url: window.vars.URLApiBase + '/user/currentUser',
            method: 'get',
            params: data,
            withCredentials: true
        }).then(data => {
            const res = data.data;
            if (res.code === 100) {
                Vue.prototype.G.userToken = res;
                return res;
            } else {
                return undefined;
            }
        }).catch(res => {
            // unauthorized
            if (res.response && res.response.status === 401) {
                location.href = '#/login';
            } else {
                window.LOG('网络错误');
                window.ELEMENT.Notification({
                    message: '网络错误',
                    type: 'error'
                });
            }
        });
    }
};
