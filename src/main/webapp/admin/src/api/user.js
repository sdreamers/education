import fetch from '@/libs/fetch';

export default {

    pages(data) {
        return fetch({
            url: 'user/pages',
            method: 'get',
            params: data
        });
    },
    insert(data) {
        return fetch({
            url: 'user',
            method: 'post',
            data,
            formData: true
        });
    },
    update(data) {
        return fetch({
            url: 'user',
            method: 'put',
            data,
            formData: true
        });
    },
    updatePassword(data) {
        return fetch({
            url: 'user/password',
            method: 'put',
            data,
            formData: true
        });
    },
    updateStatus(data) {
        return fetch({
            url: 'user/status',
            method: 'put',
            data,
            formData: true
        });
    }
};
