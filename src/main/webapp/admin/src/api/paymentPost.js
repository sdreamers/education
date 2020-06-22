import fetch from '@/libs/fetch';

export default {
    post(data) {
        return fetch({
            url: '/order/paymentPost/post',
            method: 'post',
            params: data
        });
    }
};
