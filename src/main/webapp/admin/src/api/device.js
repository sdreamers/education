import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/device/pages',
            method: 'get',
            params: data
        });
    },
    updateCompleteNum(data) {
        return fetch({
            url: '/device/completeNum',
            method: 'put',
            data: data,
            formData: true
        });
    }
};
