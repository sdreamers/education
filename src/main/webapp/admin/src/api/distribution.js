import fetch from '@/libs/fetch';

export default {
    distributionScopeDelete(data) {
        return fetch({
            url: '/common/distribution/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    distributionScopeInsert(data) {
        return fetch({
            url: '/common/distribution/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    distributionScopeUpdate(data) {
        return fetch({
            url: '/common/distribution/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    distributionScopePagers(data) {
        return fetch({
            url: '/common/distribution/pages',
            method: 'get',
            params: data
        });
    },
    distributionScopeGet(data) {
        return fetch({
            url: '/common/distribution/get',
            method: 'get',
            params: data
        });
    }
};
