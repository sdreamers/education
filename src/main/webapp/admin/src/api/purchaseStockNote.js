import fetch from '@/libs/fetch';

export default {
    purchaseStockNoteUpdate(data) {
        return fetch({
            url: '/stock/purchaseStockNote/update',
            method: 'post',
            data: data
        });
    },
    purchaseStockNoteInsert(data) {
        return fetch({
            url: '/stock/purchaseStockNote/insert',
            method: 'post',
            data: data
        });
    },
    selectSpecificationInfoByBasicId(data) {
        if (!window.permission['_stock/productSpecification/getByBasicId']) {
            window.ELEMENT.Notification({
                message: '根据商品基本信息查询规格信息',
                type: 'error',
                dangerouslyUseHTMLString: false,
                duration: 5000
            });
            return new Promise(res => {
                res({
                    code: 100,
                    content: []
                })
            })
        }
        return fetch({
            url: '/stock/productSpecification/getByBasicId',
            method: 'get',
            params: data
        });
    },
    selectSpecification(data) {
        if (!window.permission['_product/productBasicSpecification/selectSpecification']) {
            window.ELEMENT.Notification({
                message: '没有根据商品id查询规格权限',
                type: 'error',
                dangerouslyUseHTMLString: false,
                duration: 5000
            });
            return new Promise(res => {
                res({
                    code: 100,
                    content: []
                })
            })
        }
        return fetch({
            url: '/product/productBasicSpecification/selectSpecification',
            method: 'get',
            params: data
        });
    },
    listProductBasicInfo(data) {
        if (!window.permission['_product/productBasicInfo/searchProduct']) {
            window.ELEMENT.Notification({
                message: '根据关键词获取相关商品名称',
                type: 'error',
                dangerouslyUseHTMLString: false,
                duration: 5000
            });
            return new Promise(res => {
                res({
                    code: 100,
                    content: []
                })
            })
        }
        return fetch({
            url: '/product/productBasicInfo/searchProduct',
            method: 'get',
            params: data
        });
    },
    updatePassed(data) {
        return fetch({
            url: '/stock/purchaseStockNote/pass',
            method: 'post',
            data: data,
            formData: true
        });
    },

    updateRejected(data) {
        return fetch({
            url: '/stock/purchaseStockNote/reject',
            method: 'post',
            data: data,
            formData: true
        });
    },

    purchaseStockNoteGetAudit(data) {
        return fetch({
            url: '/stock/purchaseStockNote/getAudit',
            method: 'get',
            params: data
        });
    },

    purchaseStockNoteGet(data) {
        return fetch({
            url: '/stock/purchaseStockNote/get',
            method: 'get',
            params: data
        });
    },
    getDetails(data) {
        return fetch({
            url: '/stock/purchaseStockNote/getDetails',
            method: 'get',
            params: data
        });
    },
    getSupplier(data) {
        return fetch({
            url: '/stock/supplier/listAllSupplier',
            method: 'get',
            params: data
        });
    },
    purchaseStockNotePagers(data) {
        return fetch({
            url: '/stock/purchaseStockNote/pages',
            method: 'get',
            params: data
        });
    },
    listPrint(data) {
        return fetch({
            url: '/stock/purchaseStockNote/listPrint',
            method: 'get',
            params: data
        });
    },
    listOutPrint(data) {
        return fetch({
            url: '/stock/purchaseStockNote/listOutPrint',
            method: 'get',
            params: data
        });
    },
    getOrderUpdateInfo(data) {
        return fetch({
            url: '/order/order/getOrderUpdateInfo',
            method: 'get',
            params: data
        })
    },
    getOrderInfoNotContainRefund(data) {
        return fetch({
            url: '/order/order/getOrderInfoNotContainRefund',
            method: 'get',
            params: data
        })
    }
};
