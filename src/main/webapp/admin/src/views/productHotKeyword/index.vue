<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_product/productHotKeyword/insert']">添加记录</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading" @row-click="rowClick"  @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <img  v-if="column.key === 'previewImageURL'" style="width: 80px;height: 80px;" :src="scope.row.previewImageURL" />
                            <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="显示状态">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-switch
                                    v-model="scope.row.visibledFlag === '1'"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_product/productHotKeyword/update']"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_product/productHotKeyword/delete']"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page.sync="currentPage"
                    :page-sizes="[10, 20]"
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
            @close="dialogFormVisible=false,productHotKeywordPagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/productHotKeyword';

import edit from './edit';

const columns = [
    { key: 'sort', title: '排序号', sort: 'custom' },
    { key: 'keyword', title: '关键字' }
];
const form = {};
export default {
    components: {
        edit
    },
    data() {
        return {
            tableData: [],
            columns: columns,


            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            loading: false,

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
                this.productHotKeywordDelete(param).then(() => {
                    this.productHotKeywordPagers();
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
            this.productHotKeywordPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productHotKeywordPagers();
        },
        // 获取规格，或者显示隐藏
        rowClick(row, event, column) {
            const id = row.id;
            if (column.label === '显示状态') {
                row.visibledFlag = row.visibledFlag === '1' ? '0' : '1';
                const visibledFlag = row.visibledFlag;
                api.productHotKeywordUpdate({ id, visibledFlag }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.productHotKeywordPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
            // this.currentPageSpec = 1;
            // this.productBasicSpecificationPagers(id);
        },
        // 删除基本信息
        productHotKeywordDelete(data) {
            return api.productHotKeywordDelete(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
        // 获取基本信息列表
        productHotKeywordPagers() {
            if (!this.G.permission['_product/productHotKeyword/pages']) {
                return this.$notify.error('没有权限');
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
            api.productHotKeywordPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.productHotKeywordPagers();
        }
    },
    created() {
        this.productHotKeywordPagers();
    }
};
</script>
