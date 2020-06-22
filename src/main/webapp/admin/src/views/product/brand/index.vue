<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="searchInput" placeholder="搜索"></el-input>
                    </el-col>
                    <el-col :span="6">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="14">
                        <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_product/productBrand/insert']">添加记录</el-button>
                        <el-button style="float: right;margin-left: 10px;" @click="$router.push({name:'/productBrandOrMerchantTypeGroup/index',params:{id: -1}})">分组设置</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading" @row-click="rowClick"  @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <img  v-if="column.key === 'previewImageURL'" style="width: 80px;height: 80px;" :src="scope.row.previewImageURL" />
                            <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="禁用状态" v-if="G.permission['_product/productBrand/updateStatus']">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-switch
                                    v-model="scope.row.status === '1'"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="所属分组">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-select @change="handleGroupChange(scope.row)" v-model="scope.row.groupId" placeholder="请选择分组">
                                    <el-option v-for="(item,key) in productGroupList" :label="item.name" :value="item.id" :key="key"></el-option>
                                </el-select>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleEdit(scope.row)"
                            v-if="G.permission['_product/productBrand/update']">编辑</el-button>
                            <el-button
                            type="text"
                            @click.stop="handleRemove(scope.row)"
                            v-if="G.permission['_product/productBrand/delete']">删除</el-button>
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
            @close="dialogFormVisible=false,productBrandPagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/product';
import apiCommon from '@/api/common';
import common from '@/components/common';
import headerList from '@/config/headerList';

import edit from './edit';

const columns = [
    { key: 'sortNo', title: '序号', sort: 'custom' },
    { key: 'previewImageURL', title: '品牌图片' },
    { key: 'name', title: '品牌名称', sort: 'custom' }
];
const form = {};
export default {
    components: {
        edit,
        common
    },
    data() {
        return {
            headerList: headerList,


            searchInput: '',

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
            formType: '',
            // 品牌列表
            productGroupList: [],
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
            api.productBrandUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.productBrandPagers();
                } else {
                    this.$notify.warning(res.message || '异常错误');
                }
            });
        },
        // 点击搜索按钮
        handleSearch() {
            this.currentPage = 1;
            this.productBrandPagers();
        },
        // 清除搜索条件
        handleClear() {
            this.searchInput = '';
            this.currentPage = 1;
            this.productBrandPagers();
        },
        // 关闭基本信息弹层
        handleCancel() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = false;
            this.$refs.ruleForm.resetFields();
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
                const param = {
                    id: data.id
                };
                this.productBrandDelete(param).then(() => {
                    this.productBrandPagers();
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
            this.productBrandPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productBrandPagers();
        },
        // 获取规格，或者显示隐藏
        rowClick(row, event, column) {
            const id = row.id;
            this.productBasicInfoId = id;
            if (column.label === '禁用状态') {
                // row.status = row.status === '1' ? '0' : '1';
                const status = row.status;
                const sortNo = row.sortNo;
                api.productBrandUpdateStatus({ id, sortNo, status }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.productBrandPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
            // this.currentPageSpec = 1;
            // this.productBasicSpecificationPagers(id);
        },
        // 修改规格
        productBasicSpecificationUpdate(data) {
            return api.productBasicSpecificationUpdate(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 添加规格
        productBasicSpecificationInsert(data) {
            return api.productBasicSpecificationInsert(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 删除基本信息
        productBrandDelete(data) {
            return api.productBrandDelete(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 获取品牌
        productGroup() {
            const param = {
                categoryId: -1
            };
            apiCommon.productGroup(param).then(res => {
                res.unshift({ id: 0, name: '请选择' });
                this.productGroupList = res;
            });
        },
        // 获取基本信息列表
        productBrandPagers() {
            console.log(this.G.permission['_product/productBrand/pages']);
            if (!this.G.permission['_product/productBrand/pages']) {
                return this.$notify.error('您没有获取该数据的权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.searchInput) {
                param.brandName = this.searchInput;
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.productBrandPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.productBrandPagers();
        }
    },
    created() {
        this.productGroup();
        this.productBrandPagers();
    }
};
</script>
