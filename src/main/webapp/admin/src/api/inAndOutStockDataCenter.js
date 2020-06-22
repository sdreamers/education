import fetch from '@/libs/fetch';

export default {
    listSumStockDataCenter(data) {
        return fetch({
            url: '/inAndOutStockDataCenter/listSumStockDataCenter',
            method: 'get',
            params: data
        });
    },
    productListProduct(data) {
        return fetch({
            url: '/stock/product/searchProduct',
            method: 'get',
            params: data
        });
    },
    purchaseStockGetDetail(data) {
        return fetch({
            url: '/purchaseStock/getDetails',
            method: 'get',
            params: data
        });
    },
    inAndOutStockDataCenterPagers(data) {
        return fetch({
            url: '/inAndOutStockDataCenter/pagers',
            method: 'get',
            params: data
        });
    }
};
