import fetch from '@/libs/fetch';
import axios from 'axios';
import Vue from 'vue';

export default {
    // https://local.dongpinyun.cn:9090/manager/js/json/order_assignee_status.json
    orderAssigneeStatus() {
        return fetch({
            url: '/common/dict/orderAssigneeStatus',
            method: 'get'
        });
    },
    availableAreasStreets(data) {
        return fetch({
            url: 'common/availableAreas/streets',
            method: 'get',
            params: data
        });
    },
    availableAreasDistricts(data) {
        return fetch({
            url: 'common/availableAreas/districts',
            method: 'get',
            params: data
        });
    },
    availableAreasCities(data) {
        return fetch({
            url: 'common/availableAreas/cities',
            method: 'get',
            params: data
        });
    },
    availableAreasProvinces(data) {
        return fetch({
            url: 'common/availableAreas/provinces',
            method: 'get',
            params: data
        });
    },
    // 用户的省份
    userProvince() {
        return fetch({
            url: 'account/usercities/provinces',
            method: 'get'
        });
    },
    // 用户的城市
    userCity(data) {
        return fetch({
            url: 'account/usercities/cities',
            method: 'get',
            params: data,
            formData: true
        });
    },
    updateUserCities(data) {
        return fetch({
            url: 'account/usercities/updateUserCities',
            method: 'post',
            params: data,
            formData: true
        });
    },

    // 用户分组
    listMerchantGroup() {
        return fetch({
            url: 'account/merchantGroup/listMerchantGroup',
            method: 'get',
            formData: true
        });
    },

    userCityUserCitys(data) {
        return fetch({
            url: 'account/usercities/userCityies',
            method: 'get',
            params: data
        });
    },
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
    productArea() {
        if (!window.permission['_product/productArea/basic']) {
            window.ELEMENT.Notification({
                message: '没有获取商品地区列表权限',
                type: 'error'
            });
            return new Promise(res => {
                res({
                    code: 100,
                    content: []
                })
            })
        }
        return fetch({
            url: '/product/productArea/basic',
            method: 'get'
        });
    },
    productBrand() {
        return fetch({
            url: '/product/productBrand/basic',
            method: 'get'
        });
    },
    productCategoryBasic(data) {
        if (!window.permission['_product/productCategory/basic']) {
            window.ELEMENT.Notification({
                message: '没有获取商品分类列表权限',
                type: 'error'
            });
            return new Promise(res => {
                res({
                    code: 100,
                    content: []
                })
            })
        }
        return fetch({
            url: '/product/productCategory/basic',
            method: 'get',
            params: data
        });
    },
    productGroup(data) {
        return fetch({
            url: '/product/productGroup/listGroup',
            method: 'get',
            params: data
        });
    },
    getQiniuToken() {
        return fetch({
            url: '/common/openapi/uploadToken',
            method: 'get'
        });
    },
    getQiniuTokenTemp() {
        return fetch({
            url: '/common/openapi/tempUploadToken',
            method: 'get'
        });
    },
    countUserPlatformMsg() {
        return fetch({
            url: '/account/userMessage/countUserPlatformMsg',
            method: 'get'
        })
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
    },
    listProductMerchantType() {
        // if (Vue.prototype.G.userInfo) {
        //     return new Promise(res => {
        //         res(Vue.prototype.G.userInfo)
        //     });
        // }
        return fetch({
            url: 'common/dict/productMerchantType',
            method: 'get'
        })
    },
    getRechargeCardDict() {
        return fetch({
            url: 'common/dict/rechargeCard',
            method: 'get'
        })
    },
    getWarehouseByUserCurrentCityId() {
        return fetch({
            url: 'common/dict/warehouseByUserCurrentCity',
            method: 'get'
        })
    },
    getWorkFlowStatusDict() {
        return fetch({
            url: 'common/dict/workflowStatus',
            method: 'get'
        })
    },
    getEvaluationTypeDict() {
        return fetch({
            url: 'common/dict/evaluationType',
            method: 'get'
        })
    }
};
