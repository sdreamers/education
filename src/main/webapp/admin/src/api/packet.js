import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/packet/pages',
            method: 'get',
            params: data
        });
    },
    progressPages(data) {
        return fetch({
            url: '/packet/progressPages',
            method: 'get',
            params: data
        });
    },
    updatePurchasingCycle(data) {
        return fetch({
            url: '/product/cityProductSpecification/updatePurchasingCycle',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateThresholdValue(data) {
        return fetch({
            url: '/product/cityProductSpecification/updateThresholdValue',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
