import fetch from '@/libs/fetch';

export default {
    priceNoteAdd(data) {
        return fetch({
            url: '/stock/priceNote/add',
            method: 'post',
            data: data
            // formData: true
        });
    },

    selectSpecification(data) {
        return fetch({
            url: '/product/cityProductSpecification/selectSpecification',
            method: 'get',
            params: data
        });
    },
    productListProduct(data) {
        return fetch({
            url: '/product/cityProductSpecification/listProduct',
            method: 'get',
            params: data
        });
    },
    priceNotePagers(data) {
        return fetch({
            url: '/stock/priceNote/pages',
            method: 'get',
            params: data
        });
    },
    audit(data) {
        return fetch({
            url: '/stock/priceNote/audit',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
