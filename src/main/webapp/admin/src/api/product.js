import fetch from '@/libs/fetch';

export default {
    listAllProduct(data) {
        return fetch({
            url: 'product/list',
            method: 'get',
            params: data
        });
    },

    delete(data) {
        return fetch({
            url: 'product',
            method: 'delete',
            params: data
        });
    },
    updateName(data) {
        return fetch({
            url: 'product/name',
            method: 'put',
            params: data
        });
    }
};
