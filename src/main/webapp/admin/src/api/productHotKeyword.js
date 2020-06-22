import fetch from '@/libs/fetch';

export default {
    productHotKeywordPagers(data) {
        return fetch({
            url: 'product/productHotKeyword/pages',
            method: 'get',
            params: data
        });
    },
    productHotKeywordInsert(data) {
        return fetch({
            url: 'product/productHotKeyword/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    productHotKeywordUpdate(data) {
        return fetch({
            url: 'product/productHotKeyword/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    productHotKeywordDelete(data) {
        return fetch({
            url: 'product/productHotKeyword/delete',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
