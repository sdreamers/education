import fetch from '@/libs/fetch';

export default {
    // 分页查询进货统计列表
    inAndOutStockCountPages(data) {
        return fetch({
            url: '/stat/inAndOutStockCount/pages',
            method: 'get',
            params: data
        });
    },
    // 统计进货总成本
    sumInAndOutStock(data) {
        return fetch({
            url: '/stat/inAndOutStockCount/sumInAndOutStock',
            method: 'get',
            params: data
        });
    }
};
