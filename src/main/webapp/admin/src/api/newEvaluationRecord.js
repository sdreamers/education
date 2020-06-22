import fetch from '@/libs/fetch';

export default {
    pagers(data) {
        return fetch({
            url: '/order/evaluationRecord/pages',
            method: 'get',
            params: data
        })
    }
};
