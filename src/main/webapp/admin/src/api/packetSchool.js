import fetch from '@/libs/fetch';

export default {
    listYears(data) {
        return fetch({
            url: '/packetSchool/listYears',
            method: 'get',
            params: data
        });
    }
};
