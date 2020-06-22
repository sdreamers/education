import fetch from '@/libs/fetch';

export default {
    insert(data) {
        return fetch({
            url: '/product/recommend/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    update(data) {
        return fetch({
            url: '/product/recommend/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: '/product/recommend/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    pagers(data) {
        return fetch({
            url: '/product/recommend/pages',
            method: 'get',
            params: data
        });
    },
    get(data) {
        return fetch({
            url: '/product/recommend/get',
            method: 'get',
            params: data
        });
    }
};
