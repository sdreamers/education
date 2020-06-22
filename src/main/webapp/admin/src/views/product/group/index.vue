<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-select v-model="searchCategory" placeholder="请选择所在分类" @change="handleSearchCategory">
                                <el-option v-for="(item,key) in fatherList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <el-input v-model="searchInput" placeholder="搜索组名"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="productGroupPagers">搜索</el-button>
                            <el-button @click="clear">清除</el-button>
                        </el-col>
                        <el-col :span="12">
                            <el-button style="float: right;margin-left: 10px;" @click="$router.push({name: '/productCategory/index'})">取消</el-button>
                            <el-button style="float: right;margin-left: 10px;" @click="handleAdd">添加记录</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageurl'" style="width: 60px;height: 60px;" :src="scope.row.previewImageurl" />
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
            :fatherList="fatherList"
            :formType="formType"
            :form="form"
            @close="dialogFormVisible=false,productGroupPagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/product';
import apiCommon from '@/api/common';

import edit from './edit';
import common from '@/components/common';
import headerList from '@/config/headerList';

const columns = [
    {
        key: 'sortNo',
        title: '序号'
    },
    {
        key: 'previewImageurl',
        title: '预览图片'
    },
    {
        key: 'name',
        title: '商品分组名称'
    },
    {
        key: 'categoryName',
        title: '所在分类名称'
    }
];
const form = {
    sortNo: '',
    name: '',
    categoryId: '',
    previewImageurl: ''
};
export default {
    components: {
        edit,
        common
    },
    data() {
        return {
            headerList: headerList,

            searchCategory: '',
            searchInput: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: '',

            fatherList: []
        };
    },
    methods: {
        clear() {
            this.searchCategory = '';
            this.searchInput = '';
            this.productGroupPagers();
        },
        handleSearchCategory() {
            this.productGroupPagers();
        },
        handleCancel() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = false;
            this.$refs.ruleForm.resetFields();
        },
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.productCategoryBasic();
            this.dialogFormVisible = true;
        },
        // 编辑
        handleEdit(data) {
            this.formType = 'edit';
            this.form = JSON.parse(JSON.stringify(data));
            this.productCategoryBasic();
            this.dialogFormVisible = true;
        },
        // 删除
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
                this.productGroupDelete(param).then(res => {
                    window.LOG(res);
                    if (data.categoryId) {
                        this.productGroupPagers(data.categoryId);
                    } else {
                        this.productGroupPagers();
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
        // 一级品类每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.productGroupPagers();
        },
        // 一级品类翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productGroupPagers();
        },
        // 获取父级品类列表
        productCategoryBasic() {
            const param = {
                fatherId: 0
            };
            apiCommon.productCategoryBasic(param).then(res => {
                if (res.code === 100) {
                    this.fatherList = res.content;
                } else {
                    this.$notify.error('服务器似乎开了点小差');
                }
            });
        },
        // 修改二级品类单品的分组
        productGroupUpdate(data) {
            return api.productGroupUpdate(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 添加二级品类单品的分组
        productGroupInsert(data) {
            return api.productGroupInsert(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 删除二级品类单品的分组
        productGroupDelete(data) {
            return api.productGroupDelete(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        /**
         * 获取数据
         */
        productGroupPagers() {
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.searchCategory) {
                param.categoryId = this.searchCategory;
            }
            if (this.searchInput) {
                param.groupName = this.searchInput;
            }
            api.productGroupPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.productCategoryBasic();
        this.productGroupPagers();
    }
};
</script>
