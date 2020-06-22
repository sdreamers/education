import fetch from '@/libs/fetch';

export default {
    insert(data) {
        return fetch({
            url: '/stock/purchaseCart/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    pagers(data) {
        return fetch({
            url: '/stock/purchaseCart/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    update(data) {
        return fetch({
            url: '/stock/purchaseCart/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: '/stock/purchaseCart/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deleteAll(data) {
        return fetch({
            url: '/stock/purchaseCart/deleteAll',
            method: 'post',
            data: data,
            formData: true
        });
    }
}
