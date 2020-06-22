import fetch from '@/libs/fetch';

export default {
    pagers(data) {
        return fetch({
            url: '/product/newProductDemand/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateStatus(data) {
        return fetch({
            url: '/product/newProductDemand/updateStatus',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: '/product/newProductDemand/delete',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
