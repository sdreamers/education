import fetch from '@/libs/fetch';

export default {

    insertMerchant(data) {
        return fetch({
            url: '/merchant',
            method: 'post',
            data: data,
            formData: true
        });
    },

    updateMerchant(data) {
        return fetch({
            url: '/merchant',
            method: 'put',
            data: data,
            formData: true
        });
    },

    insertProduct(data) {
        return fetch({
            url: '/product',
            method: 'post',
            data: data,
            formData: true
        });
    },

    search(data) {
        return fetch({
            url: '/merchant/search',
            method: 'get',
            params: data
        });
    },

    get(data) {
        return fetch({
            url: '/merchant',
            method: 'get',
            params: data
        });
    },

    list(data) {
        return fetch({
            url: '/merchant/list',
            method: 'get',
            params: data
        });
    }
};
