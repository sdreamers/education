import fetch from '@/libs/fetch';

export default {
    messagePagers(data) {
        return fetch({
            url: 'account/message/pages',
            method: 'get',
            params: data
        });
    },
    messageInsert(data) {
        return fetch({
            url: 'account/message/insertByMulticase',
            method: 'post',
            data: data,
            formData: true
        });
    },
    messageDelete(data) {
        return fetch({
            url: 'account/message/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    messageUpdate(data) {
        return fetch({
            url: 'account/message/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    messageGet(data) {
        return fetch({
            url: 'account/message/get',
            method: 'get',
            params: data
        })
    },
    getDepartmentUserTree(data) {
        return fetch({
            url: 'account/departmentUser/getDepartmentUserTree',
            method: 'get',
            params: data
        });
    }
};
