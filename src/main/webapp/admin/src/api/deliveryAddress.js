import fetch from '@/libs/fetch';

export default {
    deliveryAddressUpdateDistributionLine(data) {
        return fetch({
            url: 'account/deliveryAddress/updateDistributionLine',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressRemoveDistributionLine(data) {
        return fetch({
            url: 'account/deliveryAddress/removeDistributionLine',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressUpdate(data) {
        return fetch({
            url: 'account/deliveryAddress/update',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressPagers(data) {
        return fetch({
            url: 'account/deliveryAddress/pages',
            method: 'get',
            params: data
        });
    },
    deliveryAddressDelete(data) {
        return fetch({
            url: 'account/deliveryAddress/delete',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressInsert(data) {
        return fetch({
            url: 'account/deliveryAddress/insert',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressRegionPagers(data) {
        return fetch({
            url: 'account/deliveryAddress/region/pages',
            method: 'get',
            params: data
        });
    },
    deliveryAddressUpdateRegion(data) {
        return fetch({
            url: 'account/deliveryAddress/updateRegion',
            method: 'post',
            data: data,
            formData: true
        });
    },
    deliveryAddressRegionDelete(data) {
        return fetch({
            url: 'account/deliveryAddress/removeRegion',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
