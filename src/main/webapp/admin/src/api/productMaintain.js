import fetch from '@/libs/fetch';

export default {
    listProductsMenu(data) {
        return fetch({
            url: '/stock/product/listProductsMenu',
            method: 'get',
            params: data
        })
    },
    listProductCategoryMenu(data) {
        return fetch({
            url: '/stock/product/listProductCategoryMenu',
            method: 'get',
            params: data
        })
    },
    stockRecordPagers(data) {
        return fetch({
            url: '/stat/inAndOutStockCount/stockRecordPagers',
            method: 'get',
            params: data
        });
    },
    updateAllPurchasingCycle(data) {
        return fetch({
            url: '/stock/productSpecification/updateAllPurchasingCycle',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updatePurchasingCycle(data) {
        return fetch({
            url: '/stock/productSpecification/updatePurchasingCycle',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateThresholdValue(data) {
        return fetch({
            url: '/stock/productSpecification/updateThresholdValue',
            method: 'post',
            data: data,
            formData: true
        });
    },
    statProductSales(data) {
        return fetch({
            url: '/stat/statProductSales/list',
            method: 'get',
            params: data
        });
    },
    productSpecificationDelete(data) {
        return fetch({
            url: '/stock/productSpecification/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateStockListDisplayStatus(data) {
        return fetch({
            url: '/stock/productSpecification/updateStockListDisplayStatus',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateOnSaleStatus(data) {
        return fetch({
            url: '/stock/productSpecification/updateAvailable',
            method: 'post',
            data: data,
            formData: true
        });
    },
    priceNoteAdd(data) {
        return fetch({
            url: '/stock/priceNote/add',
            method: 'post',
            data: data
        });
    },
    updateBufferQuantity(data) {
        return fetch({
            url: '/stock/productSpecification/updateBufferQuantity',
            method: 'post',
            data: data,
            formData: true
        });
    },
    warehouseLocationInsert(data) {
        return fetch({
            url: '/stock/warehouseLocation/insert',
            method: 'post',
            data: data
        });
    },
    adjustStockForOutStock(data) {
        return fetch({
            url: '/stock/adjustStockNote/adjustStockForOutStock',
            method: 'post',
            data: data
        });
    },
    adjustStockForInStock(data) {
        return fetch({
            url: '/stock/adjustStockNote/adjustStockForInStock',
            method: 'post',
            data: data
        });
    },
    updatePromotionZone(data) {
        return fetch({
            url: '/stock/product/updatePromotionZone',
            method: 'post',
            data: data,
            formData: true
        });
    },
    listSpecificationPageInfo(data) {
        return fetch({
            url: '/stock/productSpecification/listSpecificationPageInfo',
            method: 'get',
            params: data
        });
    }
};
