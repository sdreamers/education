import fetch from '@/libs/fetch';

export default {
    paymentGetExcel(data) {
        return fetch({
            url: '/order/payment/getExcel',
            method: 'get',
            params: data
        });
    },
    paymentDelete(data) {
        return fetch({
            url: '/payment/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    paymentInsert(data) {
        return fetch({
            url: '/payment/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    paymentUpdate(data) {
        return fetch({
            url: '/payment/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    paymentPagers(data) {
        return fetch({
            url: '/order/payment/pages',
            method: 'get',
            params: data
        });
    },
    paymentPostPagers(data) {
        return fetch({
            url: '/order/paymentPost/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    orderDetail(data) {
        return fetch({
            url: '/order/payment/orderDetail',
            method: 'get',
            params: data
        });
    }
};
