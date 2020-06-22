import fetch from '@/libs/fetch';

export default {
    pagers(data) {
        return fetch({
            url: '/quota/pages',
            method: 'get',
            params: data
        });
    },

    updateQuota(data) {
        return fetch({
            url: '/quota/updateQuota',
            method: 'put',
            params: data
        });
    },

    quotaMap(data) {
        return fetch({
            url: '/quota/quotaMap',
            method: 'get',
            params: data
        });
    },

    allQuotaYear(data) {
        return fetch({
            url: '/quota/allQuotaYear',
            method: 'get',
            params: data
        });
    }
};
