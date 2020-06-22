import fetch from '@/libs/fetch';

export default {
    statMerchantOrder(data) {
        return fetch({
            url: 'stat/statMerchantOrder',
            method: 'get',
            params: data
        });
    }
}
