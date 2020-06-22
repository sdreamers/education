<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_stock/supplier/insert']">添加供应商</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20">
                        <el-form :inline="true">
                            <el-form-item label="供应商">
                                <el-input v-model="search.supplierName" placeholder="供应商名称"></el-input>
                            </el-form-item>
                            <el-form-item label="税号">
                                <el-input v-model="search.taxNo" placeholder="税号"></el-input>
                            </el-form-item>
                            <el-form-item label="联系电话">
                                <el-input v-model="search.phone" placeholder="联系电话"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="handleSearch">搜索</el-button>
                                <el-button @click="clear">清除</el-button>
                            </el-form-item>
                        </el-form>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading" @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageurl'" style="width: 60px;height: 60px;" :src="scope.row.previewImageurl" />
                                <span v-if="column.key !== 'previewImageurl'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_stock/supplier/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_stock/supplier/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
                                <el-button
                                    type="text"
                                    v-if="G.permission['_stock/supplier/get']"
                                    @click.stop="handleViewPurchaseDetail(scope.row)">查看</el-button>
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
            @close="dialogFormVisible=false,supplierPagers()"
            :form="form"
            :formType="formType"
            :formLabelWidth="formLabelWidth"></edit>
        <purchase-detail
            :visible="purchaseDetailForm.visible"
            :supplierId="purchaseDetailForm.supplierId"></purchase-detail>
    </section>
</template>
<script>
import api from '@/api/supplier';

import edit from './edit';
import purchaseDetail from './purchaseDetail';

const columns = [
    { key: 'id', title: 'ID', sort: 'custom' },
    { key: 'supplierName', title: '供应商名称' },
    { key: 'linkman', title: '联系人' },
    { key: 'bank', title: '开户行' },
    { key: 'bankCardNo', title: '银行卡号' },
    { key: 'address', title: '地址' },
    { key: 'phone', title: '联系电话' }
];
const form = {
    supplierName: '',
    taxNo: '',
    bank: '',
    bankCardNo: '',
    linkman: '',
    phone: '',
    address: ''
};

const purchaseDetailForm = {
    visible: false,
    supplierId: ''
}

export default {
    components: {
        edit,
        purchaseDetail
    },
    data() {
        return {
            search: {
                supplierName: '',
                taxNo: '',
                phone: ''
            },

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formType: '',

            purchaseDetailForm: JSON.parse(JSON.stringify(purchaseDetailForm))
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.supplierPagers();
        },
        // 清除搜索
        clear() {
            this.search.supplierName = '';
            this.search.taxNo = '';
            this.search.phone = '';
            this.currentPage = 1;
            this.supplierPagers();
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
            this.supplierGet(data);
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
                api.supplierDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.supplierPagers();
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
            this.supplierPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.supplierPagers();
        },
        // 获取供应商信息
        supplierGet(data) {
            const param = {
                id: data.id
            };
            api.supplierGet(param).then(res => {
                if (res.code === 100) {
                    this.form = res.content;
                }
            });
        },
        // 获取数据
        supplierPagers() {
            if (!this.G.permission['_stock/supplier/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            param.supplierName = this.search.supplierName ? this.search.supplierName : undefined;
            param.taxNo = this.search.taxNo ? this.search.taxNo : undefined;
            param.phone = this.search.phone ? this.search.phone : undefined;
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.supplierPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.supplierPagers();
        },

        /**
         * 查看供应商的采购历史
         * */
        handleViewPurchaseDetail(row) {
            this.purchaseDetailForm.supplierId = row.id;
            this.purchaseDetailForm.visible = true;
        }
    },
    created() {
        this.supplierPagers();
    }
};
</script>
