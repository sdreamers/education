import fetch from '@/libs/fetch';

export default {
    // 分页查询进货统计列表
    rechargeRulesPagers(data) {
        return fetch({
            url: '/order/rechargeRules/pages',
            method: 'get',
            params: data
        });
    },
    rechargeRulesUpdate(data) {
        return fetch({
            url: '/order/rechargeRules/updateRechargeRules',
            method: 'post',
            data: data,
            formData: true
        });
    },
    rechargeRulesInsert(data) {
        return fetch({
            url: '/order/rechargeRules/saveRechargeRules',
            method: 'post',
            data: data,
            formData: true
        });
    },
    rechargeRulesDelete(data) {
        return fetch({
            url: '/order/rechargeRules/deleterechargeRules',
            method: 'post',
            data: data,
            formData: true
        });
    }
};
