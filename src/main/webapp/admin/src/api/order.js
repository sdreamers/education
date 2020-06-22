import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/order/pages',
            method: 'get',
            params: data
        });
    },

    insert(data) {
        return fetch({
            url: '/order',
            method: 'post',
            data: data
        });
    },
    update(data) {
        return fetch({
            url: '/order',
            method: 'put',
            data: data
        });
    },
    delete(data) {
        return fetch({
            url: '/order',
            method: 'delete',
            params: data
        });
    },
    separatePages(data) {
        return fetch({
            url: '/order/separatePages',
            method: 'get',
            params: data
        });
    },
    updateAmount(data) {
        return fetch({
            url: '/order/amount',
            method: 'put',
            params: data
        });
    },
    deleteOrderProductSnapshot(data) {
        return fetch({
            url: '/order/orderProductSnapshot',
            method: 'delete',
            params: data
        });
    },
    updateOrderProductSnapshotRemark(data) {
        return fetch({
            url: '/order/orderProductSnapshotRemark',
            method: 'put',
            params: data
        });
    },
    updateOrderRemark(data) {
        return fetch({
            url: '/order/orderRemark',
            method: 'put',
            params: data
        });
    },
    updateOrderNo(data) {
        return fetch({
            url: '/order/orderNo',
            method: 'put',
            params: data
        });
    },
    getByOrderNo(data) {
        return fetch({
            url: '/order/getByOrderNo',
            method: 'get',
            params: data
        });
    }
};
