import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/school/pages',
            method: 'get',
            params: data
        });
    },
    progressPages(data) {
        return fetch({
            url: '/school/progressPages',
            method: 'get',
            params: data
        });
    }
};
