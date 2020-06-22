import fetch from '@/libs/fetch';

export default {
    pagers(data) {
        return fetch({
            url: 'account/merchantGroup/pages',
            method: 'post',
            data: data,
            formData: true
        });
    },
    delete(data) {
        return fetch({
            url: 'account/merchantGroup/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    update(data) {
        return fetch({
            url: 'account/merchantGroup/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    insert(data) {
        return fetch({
            url: 'account/merchantGroup/insert',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
