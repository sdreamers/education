import fetch from '@/libs/fetch';

export default {
    selectRoleByUserId(data) {
        return fetch({
            url: 'account/roleAllocation/selectRoleByUserId',
            method: 'get',
            params: data
        });
    },
    roleAllocationPagers(data) {
        return fetch({
            url: 'account/roleAllocation/pages',
            method: 'get',
            params: data
        });
    },
    roleUpdate(data) {
        return fetch({
            url: 'account/roleAllocation/addRole2User',
            method: 'post',
            data: data,
            formData: true
        });
    },
    rolePagers(data) {
        return fetch({
            url: 'account/role/pages',
            method: 'get',
            params: data
        });
    },
    roleDelete(data) {
        return fetch({
            url: 'account/role/deleteRoleById',
            method: 'post',
            data: data,
            formData: true
        });
    },
    roleInsert(data) {
        return fetch({
            url: 'account/role/addRole',
            method: 'post',
            data: data,
            formData: true
        });
    },
    getPermissionTree() {
        return fetch({
            url: 'account/role/getPermissionTree',
            method: 'post'
        })
    }
};
