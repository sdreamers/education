import fetch from '@/libs/fetch';

export default {
    orderPagers(data) {
        return fetch({
            url: '/order/order/pages',
            method: 'get',
            params: data
        });
    },
    deliverymanDelete(data) {
        return fetch({
            url: 'account/deliveryman/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliverymanInsert(data) {
        return fetch({
            url: 'account/deliveryman/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliverymanUpdate(data) {
        return fetch({
            url: 'account/deliveryman/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliverymanPagers(data) {
        return fetch({
            url: 'account/deliveryman/pages',
            method: 'get',
            params: data
        });
    }
};
