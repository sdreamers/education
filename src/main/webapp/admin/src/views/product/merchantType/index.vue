<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="merchantTypeName" placeholder="搜索组名"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                        <el-col :span="16">
                            <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_product/productMerchantType/insert']">添加菜系</el-button>
                            <el-button style="float: right;margin-left: 10px;" @click="$router.push({name:'/productBrandOrMerchantTypeGroup/index',params:{id: -2}})">分组设置</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border  @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageURL'" style="width: 80px;height: 80px;" :src="scope.row.previewImageURL" />
                                <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
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
                                v-if="G.permission['_product/productMerchantType/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productMerchantType/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column label="给菜系关联商品">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                @click.stop="handleAddMerchantTypeProduct(scope.row)">管理</el-button>
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
            @close="dialogFormVisible=false,productMerchantTypePagers()"></edit>

        <product
            v-if="productMechantVisible"
            :merchantTypeId="merchantTypeId"
            @close="handleMerchantClose"></product>
    </section>
</template>
<script>
import api from '@/api/product';
import apiCommon from '@/api/common';

import edit from './edit';
import product from './product';
import common from '@/components/common';
import headerList from '@/config/headerList';

const columns = [
    {
        key: 'sortNo',
        title: '序号',
        sort: 'custom'
    },
    {
        key: 'previewImageURL',
        title: '预览图'
    },
    {
        key: 'name',
        title: '菜系类型'
    }
];
const form = {
    sortNo: '',
    name: ''
};
export default {
    components: {
        edit,
        product,
        common
    },
    data() {
        return {
            headerList: headerList,


            merchantTypeName: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            // 品牌列表
            productGroupList: [],

            merchantTypeId: '',

            dialogFormVisible: false,
            productMechantVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: ''
        };
    },
    methods: {
        // 关闭菜系关联弹层时
        handleMerchantClose() {
            this.productMechantVisible = false;
            this.currentPageProductMerchant = 1;
        },
        // 点击管理菜系关联
        handleAddMerchantTypeProduct(data) {
            this.merchantTypeId = data.id;
            this.productMechantVisible = true;
        },
        // 修改分组
        handleGroupChange(data) {
            const param = {
                groupId: data.groupId,
                id: data.id
            };
            api.productMerchantTypeUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.productMerchantTypePagers();
                } else {
                    this.$notify.warning(res.message || '异常错误');
                }
            });
        },
        // 搜索
        handleSearch() {
            this.currentPage = 1;
            this.productMerchantTypePagers();
        },
        // 清除搜索
        handleClear() {
            this.merchantTypeName = '';
            this.currentPage = 1;
            this.productMerchantTypePagers();
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
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.productMerchantTypeDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.productMerchantTypePagers();
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
            this.productMerchantTypePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productMerchantTypePagers();
        },
        // 获取品牌
        productGroup() {
            const param = {
                categoryId: -2
            };
            apiCommon.productGroup(param).then(res => {
                res.unshift({ id: 0, name: '请选择' });
                this.productGroupList = res;
            });
        },
        // 获取数据
        productMerchantTypePagers() {
            if (!this.G.permission['_product/productMerchantType/pages']) {
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
            if (this.merchantTypeName) {
                param.merchantTypeName = this.merchantTypeName;
            }
            api.productMerchantTypePagers(param).then(res => {
                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.productMerchantTypePagers();
        }
    },
    created() {
        this.productGroup();
        this.productMerchantTypePagers();
    }
};
</script>
