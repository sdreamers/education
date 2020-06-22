import fetch from '@/libs/fetch';

export default {
    pagers(data) {
        return fetch({
            url: '/order/rechargeCard/pages',
            method: 'get',
            params: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: '/order/rechargeCard/delete',
            method: 'post',
            params: data,
            formData: true
        });
    },
    updateDisableFlag(data) {
        return fetch({
            url: '/order/rechargeCard/updateDislabeFlag',
            method: 'post',
            params: data,
            formData: true
        });
    },
    batchInsert(data) {
        return fetch({
            url: '/order/rechargeCard/batchInsert',
            method: 'post',
            params: data,
            formData: true
        });
    }
};
