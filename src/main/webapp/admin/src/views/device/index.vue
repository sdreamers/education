<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-select v-model="search.supplierId" filterable placeholder="请选择">
                        <el-option
                            v-for="item in suppliers"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="包名">
                    </el-input>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导入</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button>
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
                    <el-table-column label="操作" align="center" width="400%">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleViewDevice(scope.row)">查看设备
                            </el-button>
                            <el-button
                                type="text"
                                @click.stop="handleAppointSupplier(scope.row)">登记进度
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

        <schoolEdit
            :dialogVisible="merchantEditDialogVisible"
            :form="merchantEditForm"
            :type="merchantEditType"
            @close="merchantEditClose"/>

        <appointSupplier
            :dialogVisible="supplierDialogVisible"
            :form="supplierForm"
            @close="supplierDialogClose"/>

    </section>
</template>
<script>
    import schoolEdit from './schoolDetail'
    import appointSupplier from './supplier'
    import api from '@/api/packet';
    import supplierApi from '@/api/supplier'

    const merchantEditForm = {
        projectId: ''
    };

    const supplierForm = {
        projectId: '',
        oldSupplier: '',
        newSupplier: ''
    };

    const search = {
        supplierId: '',
        nameLike: ''
    };

    const columns = [
        {key: 'project', title: '包名'},
        {key: 'supplier', title: '供应商'}
    ];

    export default {
        components: {
            schoolEdit,
            appointSupplier
        },

        data() {
            return {
                tableData: [],
                columns: columns,
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                quotaYearArr: [],
                search: JSON.parse(JSON.stringify(search)),

                /* 添加商户 */
                merchantEditDialogVisible: false,
                merchantEditType: '',
                merchantEditForm: JSON.parse(JSON.stringify(merchantEditForm)),

                /* 指派供应商 */
                supplierDialogVisible: false,
                supplierForm: JSON.parse(JSON.stringify(supplierForm)),
                suppliers: []
            }
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
                this.tableData = [{project: 'A包-家具A', supplier: '成都更新供应商', status: '生产-完成', school: '一小'},
                    {project: 'B包-家具B', supplier: '成都家家新供应商', status: '生产-未完成', school: '二小'},
                    {project: 'C包-家具C', supplier: '成都大家好供应商', status: '安装-完成', school: '二小'}];
                /*const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                };
                if (this.search.nameLike) {
                    param.nameLike = this.this.search.nameLike;
                }
                if (this.search.supplierId) {
                    param.supplierId = this.this.search.supplierId;
                }
                api.pages(param).then(res => {
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })*/
            },

            handleViewDevice(row) {
                this.$router.push({ name: '/project/index', params: { id: 1 } });
            },

            merchantEditClose() {
                this.merchantEditDialogVisible = false;
                this.merchantEditForm = JSON.parse(JSON.stringify(merchantEditForm));
                this.handlePagers();
            },

            handleAppointSupplier(row) {
                this.supplierForm = {projectId: row.id, oldSupplier: row.supplierName};
                this.supplierDialogVisible = true;
            },

            supplierDialogClose() {
                this.supplierDialogVisible = false;
                this.supplierForm = JSON.parse(JSON.stringify(supplierForm));
            },

            handleExport() {
                const exportUrl = window.vars.URLApiBase + '/quota/export';
                return location.href = exportUrl;
            },

            loadSuppliers() {
                supplierApi.list().then(res => {
                    if (res.code === 100 && res.content) {
                        this.suppliers = res.content;
                    }
                })
            }
        },

        created() {
            this.loadSuppliers();
            this.handlePagers();
        }
    }
</script>
