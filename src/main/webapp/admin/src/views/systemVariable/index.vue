<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_common/systemVariable/insert']">添加环境变量</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="key" placeholder="根据 KEY 搜索"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_common/systemVariable/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_common/systemVariable/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
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
            </el-row>
        </el-card>

        <edit
            v-if="dialogFormVisible"
            :formType="formType"
            :form="form"
            @close="dialogFormVisible=false,systemVariablePagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/systemVariable';

import edit from './edit';

const columns = [
    { key: 'id', title: 'ID' },
    { key: 'key', title: 'key' },
    { key: 'value', title: 'value' },
    { key: 'scope', title: '作用域' },
    { key: 'remark', title: '备注' }
];
const form = {
    key: '',
    value: '',
    remark: '',
    scope: []
};
export default {
    components: {
        edit
    },
    data() {
        return {
            key: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 20,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: ''
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.systemVariablePagers();
        },
        // 清除搜索
        handleClear() {
            this.key = '';
            this.currentPage = 1;
            this.systemVariablePagers();
        },
        // 点击添加按钮
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.formType = 'edit';
            const param = {
                id: data.id
            };
            if (!this.G.permission['_common/systemVariable/get']) {
                return this.$notify.error('没有权限');
            }
            api.systemVariableGet(param).then(res => {
                if (res.code === 100) {
                    this.form = JSON.parse(JSON.stringify(res.content));
                    this.dialogFormVisible = true;
                }
            })
        },
        // 点击删除按钮
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.systemVariableDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.systemVariablePagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }).catch(err => {
                window.LOG(err);
                this.$notify({
                    type: 'info',
                    message: '取消'
                });
            });
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.systemVariablePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.systemVariablePagers();
        },
        // 获取数据
        systemVariablePagers() {
            if (!this.G.permission['_common/systemVariable/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            if (this.key) {
                param.key = this.key;
            }
            api.systemVariablePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.systemVariablePagers();
    }
};
</script>
