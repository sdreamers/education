import fetch from '@/libs/fetch';

export default {

    updateSelf(data) {
        return fetch({
            url: 'account/user/updateSelf',
            method: 'post',
            data,
            formData: true
        });
    },
    saveQuotaYearCookie(data) {
        return fetch({
            url: 'user/quotaYearCookie',
            method: 'post',
            data,
            formData: true
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
    findByTelephone(data) {
        return fetch({
            url: 'account/u/findByTelephone',
            method: 'post',
            data,
            formData: true
        });
    },
    findByEmail(data) {
        return fetch({
            url: 'account/u/findByEmail',
            method: 'post',
            data,
            formData: true
        });
    },
    checkPasswordAndSendCaptcha(data) {
        return fetch({
            url: 'account/u/checkPasswordAndSendCaptcha',
            method: 'post',
            data,
            formData: true
        });
    },
    getResetPasswordMsgCode(data) {
        return fetch({
            url: 'account/u/getResetPasswordMsgCode',
            method: 'post',
            data,
            formData: true
        });
    },
    needSendCode(data) {
        return fetch({
            url: 'account/u/needSendCode',
            method: 'get',
            params: data
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
