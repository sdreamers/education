<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="search.name" placeholder="搜索关键字"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="16">
                        <el-button style="float: right;margin-left: 10px;" @click="handleAdd">添加记录</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border @cell-click="cellClick">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <img  v-if="column.key === 'productPreviewImageURL'" style="width: 60px;height: 60px;" :src="scope.row.productPreviewImageURL" />
                            <span v-if="column.key !== 'productPreviewImageURL'">{{scope.row[column.key]}}</span>
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
                    <el-table-column label="固定显示">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-switch
                                    v-model="scope.row.isFixed === '1'"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[2, 5, 10, 20]"param.eventName = this.eventName;
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPage">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
        <!-- 添加\修改记录 -->
        <edit
            ref="edit"
            v-if="dialogFormVisible"
            @close="dialogFormVisible = false,pagers()"
            :formType="formType"
            :formLabelWidth="formLabelWidth"
            :form="form"
            :rules="rules"
            :eventName="eventName"></edit>
    </section>
</template>
<script>
    import api from '@/api/recommend';
    // import Util from '@/libs/util';
    import headerList from '@/config/recommend';
    import common from '@/components/common';
    import edit from './edit';

    const columns = [
        { key: 'sortNo', title: '序号', width: '100px' },
        { key: 'name', title: '商品名称', width: '200px' },
        { key: 'specificationName', title: '规格名称', width: '300px' },
        { key: 'productPreviewImageURL', title: '商品预览图' }
        // { key: 'isFixed', title: '固定显示', width: '100px' }
    ];
    const form = {
        cityProductId: '',
        cityProductSpecificationId: '',
        sortNo: ''
    };
    export default {
        components: {
            common,
            edit
        },
        data() {
            const validateNaN = (rule, value, callback) => {
                if (isNaN(value)) {
                    callback(new Error('请输入数字'));
                }
                callback();
            };
            const validateInt = (rule, value, callback) => {
                if (Number.isInteger(Number(value)) && Number(value) > 0 && value.indexOf('.') !== 1) {
                    callback();
                }
                callback(new Error('序号必须为正整数'));
            };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                headerList: headerList,
                search: {
                    name: ''
                },

                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                product: {},

                activityId: '',
                activityCode: '',

                // 添加\修改表单
                dialogFormVisible: false,
                // 活动列表
                dialogProductVisible: false,
                // 添加商品
                dialogProductAddVisible: false,
                formLabelWidth: '120px',
                form: JSON.parse(JSON.stringify(form)),
                formType: '',
                rules: {
                    cityProductId: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    sortNo: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt],
                    cityProductSpecificationId: [{ required: true, message: '不能为空', trigger: 'blur' }]
                },
                eventName: 'new_product'
            };
        },
        methods: {
            // 搜索
            handleSearch() {
                this.currentPage = 1;
                this.pagers();
            },
            // 清除搜索
            handleClear() {
                this.search.name = '';
                this.currentPage = 1;
                this.pagers();
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
                }
                api.get(param).then(res => {
                    if (res.code === 100) {
                        this.form = JSON.parse(JSON.stringify(res.content));
                        this.$refs.edit.handleAddingSearch(data.name);
                        this.$refs.edit.handleAddingSearchSelect(this.form.cityProductId);
                    } else {
                        this.$notify.success(res.message);
                    }
                });
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
                    api.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.pagers();
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
            cellClick(row, column) {
                if (column.label === '固定显示') {
                    const param = {
                        id: row.id
                    };
                    param.isFixed = row.isFixed === '1' ? '0' : '1';
                    api.update(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.pagers();
                        }
                    });
                }
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.pagers();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.pagers();
            },
            // 获取数据
            pagers() {
                const param = {
                    eventName: 'new_product',
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.search.name) {
                    param.keyword = this.search.name;
                }
                api.pagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records;
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
            }
        },
        created() {
            this.pagers();
        }
    };
</script>
