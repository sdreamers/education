import fetch from '@/libs/fetch';

export default {
    /**
     * login
     */
    login(data) {
        return fetch({
            url: '/authentication/login',
            method: 'post',
            data: data,
            formData: true
        });
    },
    out() {
        return fetch({
            url: '/authentication/logout',
            method: 'post',
            formData: true
        });
    },
    getLoginMsgCode(data) {
        return fetch({
            url: '/u/getLoginMsgCode',
            method: 'post',
            data: data,
            formData: true
        })
    },
    checkMsgCode(data) {
        return fetch({
            url: '/u/checkMsgCode',
            method: 'post',
            data: data,
            formData: true
        })
    }
};
