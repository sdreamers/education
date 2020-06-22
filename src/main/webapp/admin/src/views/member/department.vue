<template>
        <el-dialog :close-on-click-modal="false" title="加入指定部门" :visible="true" @close="$emit('close')">
            <el-tree :data="treeData" :props="defaultProps" @node-click="handleDepartmentClick" default-expand-all highlight-current></el-tree>
            <div slot="footer" class="dialog-footer">
                <el-button @click="$emit('close')">取 消</el-button>
                <el-button type="primary" @click="handleSubmitDepartment" v-if="G.permission['_account/departmentUser/insert']">确 定</el-button>
            </div>
        </el-dialog>
</template>
<script>
import api from '@/api/member';
export default {
    props: ['userId', 'defaultProps', 'treeData'],
    data() {
        return {

            cityProps: {
                children: 'children',
                label: 'text'
            },
            cityList: [],
            cityChecked: [],

            dialogDepartment: ''

        };
    },
    methods: {
        // 加入指定部门，选中
        handleDepartmentClick(data) {
            this.dialogDepartment = data.id;
        },
        // 提交「加入指定部门」
        handleSubmitDepartment() {
            const param = {
                departmentId: this.dialogDepartment,
                userId: this.userId
            };
            if (!param.departmentId) {
                return this.$notify.error('未选择部门');
            }
            if (!param.userId) {
                return this.$notify.error('未选择人员');
            }
            api.departmentUserInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        }
    }
};
</script>
