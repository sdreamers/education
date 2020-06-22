import fetch from '@/libs/fetch';

export default {
    listSupplierAndSumNumPaging(data) {
        return fetch({
            url: '/stock/purchaseStockNoteDetailInfo/listSupplierAndSumNumPaging',
            method: 'post',
            data: data,
            formData: true
        });
    },
    getLastPurchaseInfo(data) {
        return fetch({
            url: '/stock/purchaseStockNoteDetailInfo/getLastPurchaseInfo',
            method: 'post',
            data: data,
            formData: true
        });
    },
    listSpecificationAndSumNumPaging(data) {
        return fetch({
            url: '/stock/purchaseStockNoteDetailInfo/listSpecificationAndSumNumPaging',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
