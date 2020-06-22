import fetch from '@/libs/fetch';

export default {
    countUserPlatformMsg(data) {
        return fetch({
            url: '/account/userMessage/countUserPlatformMsg',
            method: 'get',
            data: data
        })
    },
    markedRead(data) {
        return fetch({
            url: '/account/userMessage/markedRead',
            method: 'post',
            data: data,
            formData: true
        })
    },
    userMessagePagers(data) {
        return fetch({
            url: 'account/userMessage/pages',
            method: 'get',
            params: data
        });
    }
};
