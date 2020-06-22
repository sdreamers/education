import fetch from '@/libs/fetch';

export default {
    merchantRedPacketPagers(data) {
        return fetch({
            url: 'account/merchantRedPacket/pages',
            method: 'get',
            params: data
        });
    },
    falseDeletion(data) {
        return fetch({
            url: 'account/merchantRedPacket/falseDeletion',
            method: 'post',
            data: data,
            formData: true
        });
    },
    merchantRedPacketSystemSendPagers(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/pages',
            method: 'get',
            params: data
        });
    },
    merchantRedPacketSystemSendInsert(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    merchantRedPacketSystemSendUpdate(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    getMerchant(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/getMerchant',
            method: 'get',
            params: data
        });
    },
    merchantListByKeyword(data) {
        return fetch({
            url: 'account/merchant/listByKeyword',
            method: 'get',
            params: data
        })
    },
    updateRejected(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/updateRejected',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updatePassed(data) {
        return fetch({
            url: 'account/merchantRedPacketSystemSend/updatePassed',
            method: 'post',
            data: data,
            formData: true
        });
    },
    redPacketPagers(data) {
        return fetch({
            url: 'market/redPacket/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    redPacketInsert(data) {
        return fetch({
            url: 'market/redPacket/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    redPacketUpdate(data) {
        return fetch({
            url: 'market/redPacket/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    redPacketDelete(data) {
        return fetch({
            url: 'market/redPacket/delete',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
