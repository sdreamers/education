import fetch from '@/libs/fetch';

export default {
    /**
     * login
     */
    reg(data) {
        return fetch({
            url: '/account/u/subRegister',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
