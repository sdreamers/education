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

                <el-col :span="3">
                    <el-select v-model="search.type" @change="handleTypeChange">
                        <el-option v-for="item in types" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-col>

                <el-col :span="3">
                    <el-select v-model="search.inProgressStatus">
                        <el-option v-for="item in inProgressStatuses" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-col>

                <!--<el-col :span="6">
                    <el-date-picker
                        v-model="search.dateArr"
                        @change="handlePagers"
                        type="daterange"
                        align="right"
                        unlink-panels
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
                        range-separator="至"
                        start-placeholder="项目完成开始日期"
                        end-placeholder="项目完成结束日期">
                    </el-date-picker>
                </el-col>-->
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
                                @click.stop="handleEditMerchant(scope.row)">查看学校明细
                            </el-button>

                            <el-button
                                type="text"
                                @click.stop="handleAppointSupplier(scope.row)">指派供应商
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
    import supplierApi from '@/api/supplier'
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
        nameLike: '',
        type: 1,
        inProgressStatus: ''
    };

    const columns = [{ key: 'name', title: '包名' }, { key: 'supplierName', title: '供应商' }, {
        key: 'deviceNumProgress',
        title: '任务进度'
    }, { key: 'deviceAmountProgress', title: '金额进度' }];

    const types = [{ label: '普通', value: 1 }, { label: '信息化', value: 2 }];

    const normalStatus = [{ label: '生产中', value: 1 }, { label: '到货中', value: 2 }, { label: '安装中', value: 3 }, { label: '已完成', value: 7 }]
    const informationStatus = [{ label: '到货中', value: 4 }, { label: '安装中', value: 5 }, { label: '调试中', value: 6 }, { label: '已完成', value: 7 }]


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

                suppliers: [],
                types: types,
                normalStatus: normalStatus,
                informationStatus: informationStatus,
                inProgressStatuses: [],

                /* 添加商户 */
                merchantEditDialogVisible: false,
                merchantEditType: '',
                merchantEditForm: JSON.parse(JSON.stringify(merchantEditForm)),

                /* 指派供应商 */
                supplierDialogVisible: false,
                supplierForm: JSON.parse(JSON.stringify(supplierForm))
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
                if (this.search.supplierId) {
                    param.supplierId = this.search.supplierId;
                }
                if (this.search.nameLike) {
                    param.nameLike = this.search.nameLike;
                }
                if (this.search.inProgressStatus) {
                    param.inProgressStatus = this.search.inProgressStatus;
                }
                packetApi.progressPages(param).then(res => {
                    this.tableData = res.records;
                    this.totalSize = res.total;
                });
            },

            handleTypeChange() {
                if (this.search.type === 1) {
                    this.inProgressStatuses = normalStatus;
                    this.search.inProgressStatus = 1;
                } else if (this.search.type === 2) {
                    this.inProgressStatuses = informationStatus;
                    this.search.inProgressStatus = 4;
                }
            },

            handleEditMerchant(row) {
                this.merchantEditForm = { projectId: row.id };
                this.merchantEditDialogVisible = true;
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

            handleExport() {
                const exportUrl = window.vars.URLApiBase + '/quota/export';
                return location.href = exportUrl;
            },

            loadSuppliers() {
                supplierApi.list().then(res => {
                    if (res.code === 100 && res.content) {
                        this.suppliers = res.content;
                    }
                    this.suppliers.unshift({ id: '', name: '全部供应商' });
                })
            }
        },

        created() {
            this.loadSuppliers();
            this.handleTypeChange();
            this.handlePagers();
        }
    }
</script>
