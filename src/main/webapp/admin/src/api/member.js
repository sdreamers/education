import fetch from '@/libs/fetch';

export default {
    departmentDelete(data) {
        return fetch({
            url: 'account/department/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    departmentInsert(data) {
        return fetch({
            url: 'account/department/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    departmentUpdate(data) {
        return fetch({
            url: 'account/department/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    memberDeleteUserById(data) {
        return fetch({
            url: 'account/member/deleteUserById',
            method: 'post',
            data: data,
            formData: true
        });
    },
    departmentUserDelete(data) {
        return fetch({
            url: 'account/departmentUser/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    departmentUserInsert(data) {
        return fetch({
            url: 'account/departmentUser/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    forbidUserById(data) {
        return fetch({
            url: 'account/member/forbidUserById',
            method: 'post',
            data: data,
            formData: true
        });
    },
    departmentListAll(data) {
        return fetch({
            url: 'account/department/listAll',
            method: 'get',
            params: data
        });
    },
    memberPagers(data) {
        return fetch({
            url: 'account/member/pages',
            method: 'get',
            params: data
        });
    },
    memberOnline() {
        return fetch({
            url: '/user/online',
            method: 'get'
        });
    },
    memberChangeSessionStatus(data) {
        return fetch({
            url: 'account/member/changeSessionStatus',
            method: 'post',
            data: data,
            formData: true
        });
    },
    activityDelete(data) {
        return fetch({
            url: '/market/activity/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    activityInsert(data) {
        return fetch({
            url: '/market/activity/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    activityUpdate(data) {
        return fetch({
            url: '/market/activity/update',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
