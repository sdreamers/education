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

                <el-button style="float: right;margin-right: 30px;" type="primary" v-if="G.userInfo.insider" @click="handleExportProgress">导出进度</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center">
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="80%"
                                         :label="subColumn.title" align="center" :key="subKey">
                            <template slot-scope="scope">
                                <span>{{scope.row[subColumn.key]}}</span>
                            </template>
                        </el-table-column>
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleViewSchool(scope.row)">查看学校进度
                            </el-button>

                            <el-button
                                type="text"
                                @click.stop="handleEditMerchant(scope.row)">查看任务明细
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

    </section>
</template>
<script>
    import supplierApi from '@/api/supplier'
    import packetApi from '@/api/packet';

    const merchantEditForm = {
        packetId: ''
    };

    const supplierForm = {
        projectId: '',
        oldSupplier: '',
        newSupplier: ''
    };

    const search = {
        supplierId: '',
        nameLike: '',
        inProgressStatus: ''
    };

    const columns = [
        { key: 'name', title: '包名' },
        { key: 'supplierName', title: '供应商' },
        { key: 'unStart', title: '生产/采购(未完成)', columns: [{ key: 'unStartDeviceNumProgress', title: '数量进度' }, { key: 'unStartDeviceAmountProgress', title: '金额进度' }] },
        { key: 'produce', title: '生产/采购(完成)', columns: [{ key: 'produceDeviceNumProgress', title: '数量进度' }, { key: 'produceDeviceAmountProgress', title: '金额进度' }] },
        { key: 'arrival', title: '到货(完成)', columns: [{ key: 'arrivalDeviceNumProgress', title: '数量进度' }, { key: 'arrivalDeviceAmountProgress', title: '金额进度' }] },
        { key: 'install', title: '整体进度', columns: [{ key: 'installDeviceNumProgress', title: '数量进度' }, { key: 'installDeviceAmountProgress', title: '金额进度' }] }];

    const deviceStatuses = [{ label: '生产/采购', value: 1 }, { label: '到货', value: 2 }, { label: '安装', value: 3 }]


    export default {
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
                deviceStatuses: deviceStatuses,
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
                packetApi.progressPages(param).then(res => {
                    this.tableData = res.records;
                    if (this.tableData && this.tableData.length > 0) {
                        for (const data of this.tableData) {
                            data.unStartDeviceNumProgress = data.unStartDeviceNumProgress ? data.unStartDeviceNumProgress + '%' : '0%';
                            data.unStartDeviceAmountProgress = data.unStartDeviceAmountProgress ? data.unStartDeviceAmountProgress + '%' : '0%';
                            data.produceDeviceNumProgress = data.produceDeviceNumProgress ? data.produceDeviceNumProgress + '%' : '0%';
                            data.produceDeviceAmountProgress = data.produceDeviceAmountProgress ? data.produceDeviceAmountProgress + '%' : '0%';
                            data.arrivalDeviceNumProgress = data.arrivalDeviceNumProgress ? data.arrivalDeviceNumProgress + '%' : '0%';
                            data.arrivalDeviceAmountProgress = data.arrivalDeviceAmountProgress ? data.arrivalDeviceAmountProgress + '%' : '0%';
                            data.installDeviceNumProgress = data.installDeviceNumProgress ? data.installDeviceNumProgress + '%' : '0%';
                            data.installDeviceAmountProgress = data.installDeviceAmountProgress ? data.installDeviceAmountProgress + '%' : '0%';
                        }
                    }
                    this.totalSize = res.total;
                });
            },

            handleViewSchool(row) {
                this.$router.push({ name: '/packet/school', params: { packetId: row.id, packetName: row.name, type: row.type, title: `${row.name}-学校进度` } });
            },

            handleEditMerchant(row) {
                this.$router.push({ name: '/packet/devices', params: { packetId: row.id, type: row.type, title: `${row.name}-供货明细` } });
            },

            merchantEditClose() {
                this.merchantEditDialogVisible = false;
                this.merchantEditForm = JSON.parse(JSON.stringify(merchantEditForm));
                this.handlePagers();
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
            },

            handleExportProgress() {
                let exportUrl = window.vars.URLApiBase + '/packet/exportProgress?a=1';
                if (this.search.nameLike) {
                    exportUrl += `&nameLike=${this.search.nameLike}`;
                }
                if (this.search.supplierId) {
                    exportUrl += `&supplierId=${this.search.supplierId}`;
                }
                return location.href = exportUrl;
            }
        },

        created() {
            this.loadSuppliers();
            this.handlePagers();
        }
    }
</script>
