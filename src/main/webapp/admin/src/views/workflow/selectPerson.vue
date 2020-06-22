<template>
<div>
    <el-dialog :close-on-click-modal="false" title="选择指定的人员" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-row :gutter="20">
            <el-col :span="6">
                <el-input v-model="departmentName" placeholder="通过部门筛选" readonly @focus="dialogDepartmentVisible=true"></el-input>
            </el-col>
            <el-button @click="clearDepartment">所有人员</el-button>
        </el-row>
        <el-row style="margin-top:20px">
            <el-table :data="tableData" border @row-click="rowClick">
                <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" :width="column.width">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pager-wrapper">
                <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-sizes="[5, 10, 15, 20]"
                :page-size="pageSize"
                layout="total, sizes, slot, prev, pager, next"
                :total="totalPage">
                    <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
            <el-row>
                已选中：
                <el-button type="primary" v-for="(item,key) in selectedUser" :key="key">{{item.nickName}}</el-button>
            </el-row>
        </el-row>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>

    <selectDepartment
        @treeClick="treeClick"
        v-if="dialogDepartmentVisible"
        @close="dialogDepartmentVisible=false"
        :treeData="treeData"></selectDepartment>
</div>
</template>
<script>
import api from '@/api/workflow';
import selectDepartment from './selectDepartment';
import Util from '@/libs/util';
const columns = [
    { key: 'nickName', title: '姓名', width: 100 },
    { key: 'telephone', title: '手机号', width: 100 },
    { key: 'email', title: '邮箱', width: 100 },
    { key: 'roleName', title: '角色' },
    { key: 'JobNumber', title: '工号', width: 100 }
]
export default {
    props: ['formNode'],
    components: {
        selectDepartment
    },
    data() {
        return {
            departmentName: '',
            departmentId: '',
            tableData: [],
            columns: columns,

            selectedUser: [],

            treeData: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            dialogDepartmentVisible: false
        };
    },
    methods: {
        clearDepartment() {
            this.departmentName = '';
            this.departmentId = '';
            this.currentPage = 1;
            this.getDepartmentUser();
        },
        treeClick(data) {
            this.departmentName = data.name;
            this.departmentId = data.id;
            this.dialogDepartmentVisible = false;
            this.currentPage = 1;
            this.getDepartmentUser();
        },
        rowClick(row, event, column) {
            console.log(row, event, column);
            this.selectedUser = this.selectedUser.filter(item => item.id !== row.id);
            this.selectedUser.push(row);
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.getDepartmentUser();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.getDepartmentUser();
        },
        // 提交表单
        handleSubmit() {
            this.$emit('submit', this.selectedUser);
        },
        getDepartmentListAll() {
            if (!this.G.permission['_account/departmentUser/pages']) {
                return this.$notify.error('没有权限');
            }
            api.departmentListAll().then(res => {
                if (res.code === 100) {
                    const data = JSON.parse(res.content);
                    this.treeData = Util.transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'pId',
                        childrenKey: 'children'
                    }, data);
                }
            })
        },
        getDepartmentUser() {
            const param = {
                departmentId: this.departmentId || undefined,
                page: this.currentPage,
                limit: this.pageSize
            }
            api.departmentUserPager(param).then(res => {
                // if (res.code === 100) {
                console.log(res.records);
                this.tableData = res.records;
                this.totalPage = res.total;
                // }
            })
        }
    },
    created() {
        this.getDepartmentListAll();
        this.getDepartmentUser();
    }
};
</script>
