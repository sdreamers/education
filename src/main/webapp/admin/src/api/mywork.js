import fetch from '@/libs/fetch';

export default {
    countByWorks() {
        return fetch({
            url: 'account/myWork/countByWorks',
            method: 'post'
        });
    }
};
