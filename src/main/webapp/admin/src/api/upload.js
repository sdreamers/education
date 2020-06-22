import fetch from '@/libs/fetch';

function getCsrf() {
    const keyValue = document.cookie.match('(^|;) ?csrfToken=([^;]*)(;|$)');
    return keyValue ? `?_csrf=${keyValue[2]}` : '';
}
export default {
    /**
     * 上传excel
     */
    upload(data) {
        return fetch({
            url: '/upload' + getCsrf(),
            method: 'post',
            data: data
        });
    }
};