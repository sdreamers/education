import fetch from '@/libs/fetch';

export default {
    list(data) {
        return fetch({
            url: '/quotaYear/list',
            method: 'get',
            params: data
        });
    },

    get(data) {
        return fetch({
            url: '/quotaYear',
            method: 'get',
            params: data
        });
    },

    initNewQuotaYear(data) {
        return fetch({
            url: '/quotaYear/initNewQuotaYear',
            method: 'post',
            data: data,
            formData: true
        });
    }

};
