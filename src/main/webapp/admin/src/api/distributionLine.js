import fetch from '@/libs/fetch';
// 配送路线相关API
export default {
    pagers(data) {
        return fetch({
            url: 'account/distributionLine/pages',
            method: 'get',
            params: data
        });
    },
    insert(data) {
        return fetch({
            url: 'account/distributionLine/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    update(data) {
        return fetch({
            url: 'account/distributionLine/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: 'account/distributionLine/delete',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
