<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="areaName" placeholder="产地"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                        <el-col :span="16">
                            <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_product/productArea/insert']">添加产地</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading"  @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productArea/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productArea/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pager-wrapper">
                        <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[2, 5, 10, 20]"
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
            @close="dialogFormVisible=false,productAreaPagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/product';

import edit from './edit';
import common from '@/components/common';
import headerList from '@/config/headerList';

const columns = [
    {
        key: 'sortNo',
        title: '序号',
        sort: 'custom'
    },
    {
        key: 'name',
        title: '产地名称'
    }
];
const form = {
    sortNo: '',
    name: ''
};
export default {
    components: {
        edit,
        common
    },
    data() {
        return {
            headerList: headerList,


            areaName: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
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
            this.productAreaPagers();
        },
        // 清除搜索
        handleClear() {
            this.areaName = '';
            this.currentPage = 1;
            this.productAreaPagers();
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
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
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
                api.productAreaDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.productAreaPagers();
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
            this.productAreaPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productAreaPagers();
        },
        // 获取数据
        productAreaPagers() {
            if (!this.G.permission['_product/productArea/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            if (this.areaName) {
                param.areaName = this.areaName;
            }
            api.productAreaPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.productAreaPagers();
        }
    },
    created() {
        this.productAreaPagers();
    }
};
</script>
