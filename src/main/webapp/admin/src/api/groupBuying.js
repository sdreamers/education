import fetch from '@/libs/fetch';

export default {
    groupBuyingPagers(data) {
        return fetch({
            url: '/market/groupBuying/pages',
            method: 'get',
            params: data
        });
    },
    groupBuyingDelete(data) {
        return fetch({
            url: '/market/groupBuying?id=' + data.id,
            method: 'delete'
            // param: data
            // formData: true
        });
    },
    groupBuyingInsert(data) {
        return fetch({
            url: '/market/groupBuying',
            method: 'post',
            data: data,
            formData: true
        });
    },
    groupBuyingUpdate(data) {
        return fetch({
            url: '/market/groupBuying/',
            method: 'put',
            data: data,
            formData: true
        });
    },
    groupBuyingProductPagers(data) {
        return fetch({
            url: '/market/groupBuyingDetail/pages',
            method: 'get',
            params: data
        })
    },
    productListProduct(data) {
        return fetch({
            url: '/product/cityProductSpecification/listProduct',
            method: 'get',
            params: data
        });
    },
    productSpecificationSelectSpecification(data) {
        return fetch({
            url: '/product/cityProductSpecification/selectSpecification',
            method: 'get',
            params: data
        });
    },
    groupBuyingProductInsert(data) {
        return fetch({
            url: '/market/groupBuyingDetail',
            method: 'post',
            data: data
            // formData: true
        });
    },
    groupBuyingProductUpdate(data) {
        return fetch({
            url: '/market/groupBuyingDetail',
            method: 'put',
            data: data,
            formData: true
        });
    },
    groupBuyingProductDelete(data) {
        return fetch({
            url: '/market/groupBuyingDetail?id=' + data.id,
            method: 'delete'
            // data: data
            // formData: true
        });
    }
};
