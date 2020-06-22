import fetch from '@/libs/fetch';

export default {
    // 分页查询进货统计列表
    inStockDataCenterPagers(data) {
        return fetch({
            url: '/stat/inStockDataCenter/pages',
            method: 'get',
            params: data
        });
    },
    listSumStockDataCenter(data) {
        return fetch({
            url: '/stat/inStockDataCenter/listSumStockDataCenter',
            method: 'get',
            params: data
        })
    },
    listProduct(data) {
        return fetch({
            url: '/stock/product/searchProduct',
            method: 'get',
            params: data
        })
    },
    getDetails(data) {
        return fetch({
            url: '/stock/purchaseStockNote/getDetails',
            method: 'get',
            params: data
        })
    }
};
