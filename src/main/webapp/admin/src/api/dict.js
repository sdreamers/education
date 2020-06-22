import fetch from '@/libs/fetch';

export const deliverymanStatusMap = () => fetch({
    url: 'common/dict/deliverymanStatus',
    method: 'get'
});

export const salesmanStatusMap = () => fetch({
    url: 'common/dict/salesmanStatus',
    method: 'get'
});

export const deliverymanSex = () => fetch({
    url: 'common/dict/deliverymanSex',
    method: 'get'
});

export const salesmanRole = () => fetch({
    url: 'common/dict/salesmanRole',
    method: 'get'
});

export const salesmanSex = () => fetch({
    url: 'common/dict/salesmanSex',
    method: 'get'
});

export const getOrderStatusMap = () => fetch({
    url: 'common/dict/orderStatus',
    method: 'get'
});

export const merchantStatusMap = () => fetch({
    url: 'common/dict/merchantStatus',
    method: 'get'
});

export const getWarehouseMap = () => fetch({
    url: 'common/dict/warehouse',
    method: 'get'
});

export const warehouseByUserCurrentCity = () => fetch({
    url: 'common/dict/warehouseByUserCurrentCity',
    method: 'get'
});

export const getPaymentType = () => fetch({
    url: 'common/dict/paymentType',
    method: 'get'
});

export const purchasePaymentMethod = () => fetch({
    url: 'common/dict/purchasePaymentMethod',
    method: 'get'
});
export const bannerExpiryDaysFlagMap = () => fetch({
    url: 'common/dict/bannerExpiryDaysFlag',
    method: 'get'
});

export const bannerProductCategoryTypeMap = () => fetch({
    url: 'common/dict/bannerProductCategoryType',
    method: 'get'
});

export const bannerStatusMap = () => fetch({
    url: 'common/dict/bannerStatus',
    method: 'get'
});

export const subStockNoteType = () => fetch({
    url: 'common/dict/subStockNoteType',
    method: 'get'
});

export const stockNoteType = () => fetch({
    url: 'common/dict/stockNoteType',
    method: 'get'
});

export const getDistributionLine = () => fetch({
    url: 'common/dict/distributionLine',
    method: 'get'
});

export const getPayMethodMap = () => fetch({
    url: 'common/dict/paymethod',
    method: 'get'
});

export const getPayMethodPostMap = () => fetch({
    url: 'common/dict/paymethodPostMethod',
    method: 'get'
});

export const getOrderSource = () => fetch({
    url: 'common/dict/orderSource',
    method: 'get'
});

export const getOrderFlag = () => fetch({
    url: 'common/dict/orderFlag',
    method: 'get'
});

export const getCompany = () => fetch({
    url: 'common/dict/company',
    method: 'get'
});

export const refundType = () => fetch({
    url: 'common/dict/refundType',
    method: 'get'
});

export const orderRefundType = () => fetch({
    url: 'common/dict/orderRefundType',
    method: 'get'
});

export const returnCause = () => fetch({
    url: 'common/dict/returnCause',
    method: 'get'
});

export const getPriceStatus = () => fetch({
    url: 'common/dict/priceStatus',
    method: 'get'
});

export const productBrandStatus = () => fetch({
    url: 'common/dict/productBrandStatus',
    method: 'get'
});

export const orderRefundStatus = () => fetch({
    url: 'common/dict/orderRefundStatus',
    method: 'get'
});

export const regions = () => fetch({
    url: 'common/dict/region',
    method: 'get'
});

export const workflowCodes = () => fetch({
    url: 'common/dict/workflowCodes',
    method: 'get'
})

export const roles = () => fetch({
    url: 'common/dict/roles',
    method: 'get'
})

export const workflowHandlerTypes = () => fetch({
    url: 'common/dict/workflowHandlerTypes',
    method: 'get'
})

export const workflowEvents = () => fetch({
    url: 'common/dict/workflowEvents',
    method: 'get'
})

export const orderType = () => fetch({
    url: 'common/dict/orderType',
    method: 'get'
})
