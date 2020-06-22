<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="groupName" placeholder="搜索"></el-input>
                        </el-col>
                        <el-col :span="6">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                        <el-col :span="14">
                            <el-button style="float: right;margin-left: 10px;" @click="handleAdd">添加记录</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageurl'" style="width: 80px;height: 80px;" :src="scope.row.previewImageurl" />
                                <span v-if="column.key !== 'previewImageurl'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
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
            :form="form"
            :formType="formType"
            @close="dialogFormVisible=false,brandOrMerchantTypeGroupPagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/product';

import edit from './edit';
import common from '@/components/common';
import headerList from '@/config/headerList';

const columns = [
    { key: 'sortNo', title: '序号' },
    { key: 'previewImageurl', title: '预览图片' },
    { key: 'name', title: '商品分组名称' }
];
const form = {};
export default {
    components: {
        edit,
        common
    },
    data() {
        form.categoryId = this.$route.params.id;
        return {
            headerList: headerList,


            groupName: '',

            tableData: [],
            columns: columns,


            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: '',

            // 单位列表
            unitList: [],
            splitList: [
                { id: '0', name: '否' },
                { id: '1', name: '是' }
            ]
        };
    },
    methods: {
        handleGroupChange(data) {
            const param = {
                groupId: data.groupId,
                id: data.id
            };
            api.productGroupUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.brandOrMerchantTypeGroupPagers();
                } else {
                    this.$notify.warning(res.message || '异常错误');
                }
            });
        },
        // 点击搜索按钮
        handleSearch() {
            this.currentPage = 1;
            this.brandOrMerchantTypeGroupPagers();
        },
        // 清除搜索条件
        handleClear() {
            this.groupName = '';
            this.currentPage = 1;
            this.brandOrMerchantTypeGroupPagers();
        },
        // 添加基本信息
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑基本信息按钮
        handleEdit(data) {
            this.formType = 'edit';
            const formData = JSON.parse(JSON.stringify(data));
            this.form = JSON.parse(JSON.stringify(formData));
            this.dialogFormVisible = true;
        },
        // 删除基本信息
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                api.productGroupDelete({
                    id: data.id
                }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.brandOrMerchantTypeGroupPagers();
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
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.brandOrMerchantTypeGroupPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.brandOrMerchantTypeGroupPagers();
        },
        // 获取基本信息列表
        brandOrMerchantTypeGroupPagers() {
            const param = {
                page: 1,
                limit: 5,
                categoryId: this.$route.params.id
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.groupName) {
                param.groupName = this.groupName;
            }
            api.brandOrMerchantTypeGroupPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.id = this.$route.params.id;
        this.brandOrMerchantTypeGroupPagers();
    }
};
</script>
