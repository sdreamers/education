import fetch from '@/libs/fetch';

export default {
    listAll() {
        return fetch({
            url: 'account/newevaluationconfig/listAll',
            method: 'get'
        })
    },
    delete(data) {
        return fetch({
            url: 'account/newevaluationconfig/delete',
            method: 'post',
            params: data,
            formData: true
        })
    },
    insert(data) {
        return fetch({
            url: 'account/newevaluationconfig/insert',
            method: 'post',
            params: data,
            formData: true
        })
    },
    update(data) {
        return fetch({
            url: 'account/newevaluationconfig/update',
            method: 'post',
            params: data,
            formData: true
        })
    }
};
