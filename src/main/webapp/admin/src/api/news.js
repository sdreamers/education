import fetch from '@/libs/fetch';
// 配送路线相关API
export default {
    newsPagers(data) {
        return fetch({
            url: '/market/news/pages',
            method: 'get',
            params: data
        });
    },
    newsInsert(data) {
        return fetch({
            url: '/market/news/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    newsUpdate(data) {
        return fetch({
            url: '/market/news/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    newsDelete(data) {
        return fetch({
            url: '/market/news/delete',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
