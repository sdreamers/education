<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="用户名">
                    </el-input>
                </el-col>
                <el-col :span="3">
                    <el-input v-model="search.accountLike" placeholder="账号">
                    </el-input>
                </el-col>

                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleInsertUser">新增</el-button>
            </el-row>
            
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center">
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="120%"
                                         :label="subColumn.title" align="center" :key="subKey">
                            <template slot-scope="scope">
                                <span>{{scope.row[subColumn.key]}}</span>
                            </template>
                        </el-table-column>
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleEditUser(scope.row)">编辑
                            </el-button>

                            <el-button
                                type="text"
                                v-if="scope.row.status === 1"
                                @click.stop="handleForbiddenUser(scope.row)">禁用
                            </el-button>

                            <el-button
                                type="text"
                                v-if="scope.row.status === 0"
                                @click.stop="handleStartUseUser(scope.row)">启用
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i
                            class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <edit
            :dialogVisible='editDialogVisible'
            :form='editForm'
            @close='handleCloseEditDialog'/>
    </section>
</template>

<script>

import userApi from '@/api/user'
import edit from './edit';


const search = {
    nameLike: '',
    accountLike: ''
}

const columns = [
        { key: 'name', title: '用户名' },
        { key: 'account', title: '账号' },
        { key: 'telephone', title: '手机号码' },
        { key: 'statusStr', title: '状态' },
        { key: 'natureStr', title: '角色' },
        { key: 'supplierName', title: '对应供应商' }]

const statusMap = { 0: '失效', 1: '正常' }

const natureMap = { 1: '管理员', 2: '系统管理员', 3: '供应商' }

export default {
    components: {
        edit
    },

    data() {
        return {
            search: JSON.parse(JSON.stringify(search)),

            tableData: [],
            columns: columns,
            currentPage: 1,
            totalSize: 0,
            pageSize: 10,
            loading: false,

            editForm: {},
            editDialogVisible: false
        };
    },
    methods: {

        handleSizeChange(size) {
            this.pageSize = size;
            this.handlePagers();
        },

        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.handlePagers();
        },

        handleSearch() {
            this.handlePagers();
        },
        handleClear() {
            this.search = JSON.parse(JSON.stringify(search));
        },
        handlePagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            if (this.search.nameLike) {
                param.nameLike = this.search.nameLike;
            }
            if (this.search.accountLike) {
                param.accountLike = this.search.accountLike;
            }
            userApi.pages(param).then(res => {
                this.tableData = res.records;
                if (this.tableData && this.tableData.length > 0) {
                    for (const data of this.tableData) {
                        data.natureStr = natureMap[data.nature];
                        data.statusStr = statusMap[data.status];
                    }
                }
                this.totalSize = res.total;
            });
        },
        // 编辑用户
        handleEditUser(row) {
            this.editForm = { id: row.id, name: row.name, account: row.account, telephone: row.telephone, nature: row.nature, supplierId: row.supplierId };
            this.editDialogVisible = true;
        },

        // 新增用户
        handleInsertUser() {
            this.editForm = {};
            this.editDialogVisible = true;
        },

        handleCloseEditDialog() {
            this.editForm = {};
            this.editDialogVisible = false;
            this.handlePagers();
        },

        handleForbiddenUser(row) {
            this.$confirm(`是否禁用用户：${row.name}`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                userApi.updateStatus({ id: row.id, status: 0 }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('禁用成功');
                        this.handlePagers();
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消禁用'
                });
            });
        },

        handleStartUseUser(row) {
            this.$confirm(`是否启用用户：${row.name}`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                userApi.updateStatus({ id: row.id, status: 1 }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('启用成功');
                        this.handlePagers();
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消启用'
                });
            });
        }
    },

    created() {
        this.handlePagers();
    }

};
</script>
