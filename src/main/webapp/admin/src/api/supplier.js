import fetch from '@/libs/fetch';

export default {
    list(data) {
        return fetch({
            url: '/supplier/list',
            method: 'get',
            params: data
        });
    }
};
