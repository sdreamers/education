import fetch from '@/libs/fetch';

export default {
    updateAllPurchasingCycle(data) {
        return fetch({
            url: '/product/cityProductSpecification/updateAllPurchasingCycle',
            method: 'post',
            data: data,
            formData: true
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
    },

    updateSortNo(data) {
        return fetch({
            url: '/product/cityProductSpecification/updateSortNo',
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
    cityMasterSlaveInsert(data) {
        return fetch({
            url: '/product/cityMasterSlave/insertOrUpdate',
            method: 'post',
            params: data,
            formData: true
        });
    },
    getByMasterCityId(data) {
        return fetch({
            url: '/product/cityMasterSlave/getByMasterCityId',
            method: 'get',
            params: data
        });
    },
    productSpecificationDelete(data) {
        return fetch({
            url: '/product/cityProductSpecification/delete',
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
    updateOnDisplayStatus(data) {
        return fetch({
            url: '/product/cityProductSpecification/updateIsAppDisplay',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateOnSaleStatus(data) {
        return fetch({
            url: '/product/cityProductSpecification/updateOnSaleStatus',
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
            url: '/product/cityProductSpecification/updatePromotionZone',
            method: 'post',
            data: data,
            formData: true
        });
    },
    listSpecificationPageInfo(data) {
        return fetch({
            url: '/product/cityProductSpecification/pages',
            method: 'get',
            params: data
        });
    },
    copyNewCityProduct(data) {
        return fetch({
            url: '/product/cityMasterSlave/copyNewCityProduct',
            method: 'post',
            data: data,
            formData: true
        });
    },
    productListProduct(data) {
        return fetch({
            url: '/product/cityProductSpecification/listProduct',
            method: 'get',
            params: data
        });
    },
    productListProductBasicInfo(data) {
        if (!window.permission['_product/productBasicInfo/searchProduct']) {
            window.ELEMENT.Notification({
                message: '根据关键词获取相关商品名称',
                type: 'error',
                dangerouslyUseHTMLString: false,
                duration: 5000
            });
            return new Promise(res => {
                res({ code: 100, content: [] })
            })
        }
        return fetch({
            url: '/product/productBasicInfo/searchProduct',
            method: 'get',
            params: data
        });
    },
    listThresholdSpecificationPaging(data) {
        return fetch({
            url: '/product/cityProductSpecification/listThresholdSpecificationPaging',
            method: 'get',
            params: data
        });
    }
};
