<template>
    <el-dialog :close-on-click-modal="false" title="控制可访问城市" :visible="true" @close="$emit('close')">
        <el-tree
        :data="cityList"
        :props="cityProps"
        ref="cityTree"
        show-checkbox
        node-key="id"
        default-expand-all
        highlight-current
        :default-checked-keys="cityChecked"></el-tree>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitCity" v-if="G.permission['_account/usercities/updateUserCities']">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import apiCommon from '@/api/common';
import Util from '@/libs/util';
export default {
    props: ['userId'],
    data() {
        return {

            cityProps: {
                children: 'children',
                label: 'text'
            },
            cityList: [],
            cityChecked: []
        };
    },
    methods: {

        // 提交可访问城市
        handleSubmitCity() {
            const param = {
                userId: this.userId,
                ids: this.$refs.cityTree.getCheckedKeys().filter(item => item).join(',')
            };
            apiCommon.updateUserCities(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        },
        // 获取用户的可访问城市
        userCity(userId) {
            const param = {
                userId
            };
            apiCommon.userCityUserCitys(param).then(res => {
                this.cityList = res;
                const checked = Util.getTreeCheckedNodes({
                    checkedKey: 'checked',
                    childrenKey: 'children'
                }, res, 1, []).map(item => item.id);
                window.LOG(checked);
                this.cityChecked = checked;
            });
        }
    },
    created() {
        const userId = this.userId;
        if (!userId) {
            return this.$notify.error('id 异常');
        }
        this.userCity(userId);
    }
};
</script>
