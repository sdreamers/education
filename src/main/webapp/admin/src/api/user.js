import fetch from '@/libs/fetch';

export default {

    pages(data) {
        return fetch({
            url: '/user/pages',
            method: 'get',
            params: data
        });
    },
    updatePswd(data) {
        return fetch({
            url: 'account/user/updatePswd',
            method: 'post',
            data,
            formData: true
        });
    },
    resetPassword(data) {
        return fetch({
            url: 'account/u/resetPassword',
            method: 'post',
            data,
            formData: true
        });
    }
};
