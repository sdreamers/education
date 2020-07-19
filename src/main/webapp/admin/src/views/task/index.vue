<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-select v-model="search.supplierId" filterable placeholder="请选择供应商">
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

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleImport">导入</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" ref="table" border v-loading="loading" @select="select"
                          @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50"></el-table-column>
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
                                @click.stop="handleViewDevice(scope.row)">查看供货明细
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

        <importExcel
            :dialogVisible="importDialogVisible"
            @close="importDialogClose"
            @importSuccess="importSuccess"/>

    </section>
</template>
<script>
    import schoolEdit from './schoolDetail'
    import appointSupplier from './supplier'
    import supplierApi from '@/api/supplier'
    import importExcel from './importExcel';
    import packetApi from '@/api/packet';

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
        { key: 'name', title: '包名' },
        { key: 'supplierName', title: '供应商' }
    ];

    export default {
        components: {
            schoolEdit,
            appointSupplier,
            importExcel
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
                suppliers: [],

                /* 导入 */
                importDialogVisible: false,

                selections: []
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
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.search.nameLike) {
                    param.nameLike = this.search.nameLike;
                }
                if (this.search.supplierId) {
                    param.supplierId = this.search.supplierId;
                }
                packetApi.pages(param).then(res => {
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })
            },

            handleViewDevice(row) {
                this.$router.push({ name: '/task/devices', params: { packetId: row.id, type: row.type } });
            },

            merchantEditClose() {
                this.merchantEditDialogVisible = false;
                this.merchantEditForm = JSON.parse(JSON.stringify(merchantEditForm));
                this.handlePagers();
            },

            handleAppointSupplier(row) {
                this.supplierForm = { projectId: row.id, oldSupplier: row.supplierName };
                this.supplierDialogVisible = true;
            },

            supplierDialogClose() {
                this.supplierDialogVisible = false;
                this.supplierForm = JSON.parse(JSON.stringify(supplierForm));
            },

            importDialogClose() {
                this.importDialogVisible = false;
            },

            importSuccess() {
                this.importDialogVisible = false;
                this.handlePagers();
            },

            handleExport() {
                if (!this.selections || this.selections.length !== 1) {
                    this.$notify.error('请选择一条记录导出');
                    return;
                }
                const row = this.selections[0];
                const exportUrl = window.vars.URLApiBase + '/device/export?packetId=' + row.id;
                return location.href = exportUrl;
            },

            handleImport() {
                this.importDialogVisible = true;
            },

            loadSuppliers() {
                supplierApi.list().then(res => {
                    if (res.code === 100 && res.content) {
                        this.suppliers = res.content;
                    }
                    this.suppliers.unshift({ id: '', name: '全部供应商' });
                })
            },

            select(selection, row) {
                this.$refs.table.clearSelection();
                if (selection.length === 0) {
                    return;
                }
                this.$refs.table.toggleRowSelection(row, true);
            },

            handleSelectionChange(row) {
                this.selections = row;
            }
        },

        created() {
            this.loadSuppliers();
            this.handlePagers();
        }
    }
</script>
