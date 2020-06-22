import fetch from '@/libs/fetch';

export default {
    getMap() {
        return fetch({
            url: 'common/dict/rechargeOrderMap',
            method: 'get'
        });
    },
    pagers(data) {
        return fetch({
            url: '/order/rechargeOrder/pages',
            method: 'get',
            params: data
        });
    },
    insert(data) {
        return fetch({
            url: '/order/rechargeOrder/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: '/order/rechargeOrder/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    getAudit(data) {
        return fetch({
            url: '/order/rechargeOrder/getAudit',
            method: 'get',
            params: data
        });
    },
    reject(data) {
        return fetch({
            url: '/order/rechargeOrder/reject',
            method: 'post',
            data: data,
            formData: true
        });
    },
    pass(data) {
        return fetch({
            url: '/order/rechargeOrder/pass',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
