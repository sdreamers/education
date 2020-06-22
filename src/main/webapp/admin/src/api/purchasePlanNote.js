import fetch from '@/libs/fetch';

export default {
    generatePurchasePlan(data) {
        return fetch({
            url: '/stock/purchasePlanNote/generatePurchasePlan',
            method: 'post',
            data: data
        });
    },
    pagers(data) {
        return fetch({
            url: '/stock/purchasePlanNote/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    detailPagers(data) {
        return fetch({
            url: '/stock/purchasePlanNoteDetail/pages',
            method: 'post',
            data: data,
            formData: true
        });
    }
}
