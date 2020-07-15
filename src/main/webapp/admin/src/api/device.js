import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/device/pages',
            method: 'get',
            params: data
        });
    },
    updateStatus(data) {
        return fetch({
            url: '/device/status',
            method: 'put',
            data: data,
            formData: true
        });
    }
};
