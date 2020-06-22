import fetch from '@/libs/fetch';

export default {
    permissionUpdate(data) {
        return fetch({
            url: 'account/permissionAllocation/addPermission2Role',
            method: 'post',
            data: data,
            formData: true
        });
    },
    listPermissionByRoleIdOfZTree(data) {
        return fetch({
            url: 'account/permission/listPermissionByRoleIdOfZTree',
            method: 'get',
            params: data
        });
    },
    permissionAllocation(data) {
        return fetch({
            url: 'account/permissionAllocation/pages',
            method: 'get',
            params: data
        });
    },
    deletePermissionById(data) {
        return fetch({
            url: 'account/permission/deletePermissionById',
            method: 'post',
            data: data,
            formData: true
        });
    },
    addPermission(data) {
        return fetch({
            url: 'account/permission/addPermission',
            method: 'post',
            data: data,
            formData: true
        });
    },
    updateByPrimaryKeySelective(data) {
        return fetch({
            url: 'account/permission/updateByPrimaryKeySelective',
            method: 'post',
            data: data,
            formData: true
        });
    },
    listPermissionOfZTree(data) {
        return fetch({
            url: 'account/permission/listPermissionOfZTree',
            method: 'get',
            params: data
        });
    },
    selectByPrimaryKey(data) {
        return fetch({
            url: 'account/permission/selectByPrimaryKey',
            method: 'get',
            params: data
        });
    },
    permissionPagers(data) {
        return fetch({
            url: 'account/permission/pages',
            method: 'get',
            params: data
        });
    }
};
