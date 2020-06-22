import fetch from '@/libs/fetch';
// 配送路线相关API
export default {
    merchantPagers(data) {
        return fetch({
            url: 'account/merchant/pages',
            method: 'get',
            params: data
        });
    },
    merchantAuthorize(data) {
        return fetch({
            url: '/account/merchant/authorize',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
