import fetch from '@/libs/fetch';

export default {
    pages(data) {
        return fetch({
            url: '/packet/pages',
            method: 'get',
            params: data
        });
    },
    progressPages(data) {
        return fetch({
            url: '/packet/progressPages',
            method: 'get',
            params: data
        });
    },
    overallProgress(data) {
        return fetch({
            url: '/packet/overallProgress',
            method: 'get',
            params: data
        });
    }
};
