import fetch from '@/libs/fetch';

export default {
    priceRelationPagers(data) {
        return fetch({
            url: 'product/priceRelation/listByPage',
            method: 'get',
            params: data
        });
    },
    priceRelationInsert(data) {
        return fetch({
            url: 'product/priceRelation/insertPriceRelation',
            method: 'post',
            data: data
            // formData: true
        });
    },
    priceRelationUpdate(data) {
        return fetch({
            url: 'product/priceRelation/updatePriceRelation',
            method: 'post',
            data: data
            // formData: true
        });
    },
    priceRelationDelete(data) {
        return fetch({
            url: 'product/priceRelation/deletePriceRelation',
            method: 'post',
            data: data,
            formData: true
        });
    },
    productCategoryPagers(data) {
        return fetch({
            url: '/product/productCategory/pages',
            method: 'get',
            params: data
        });
    },
    priceRelationExecute(data) {
        return fetch({
            url: '/product/priceRelation/immediateExecutionPriceRelation',
            method: 'post',
            params: data,
            formData: true
        });
    }
};
