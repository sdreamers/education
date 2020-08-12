<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="学校名称">
                    </el-input>
                </el-col>

                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" v-if="G.userInfo.insider" @click="handleExportProgress">导出进度</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" v-if="G.userInfo.insider" @click="handleExportDevices">导出设备明细</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading" @select="select"
                          @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50"></el-table-column>
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center">
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="100%"
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
                                @click.stop="handleViewPacket(scope.row)">查看包进度
                            </el-button>

                            <el-button
                                type="text"
                                @click.stop="handleViewDevice(scope.row)">查看任务明细
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
    import schoolApi from '@/api/school';

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
        { key: 'name', title: '学校名称' },
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
                schoolApi.progressPages(param).then(res => {
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

            handleViewPacket(row) {
                this.$router.push({ name: '/school/packet', params: { schoolId: row.id, schoolName: row.name, type: row.type, title: `${row.name}-包进度` } });
            },

            handleViewDevice(row) {
                this.$router.push({ name: '/school/devices', params: { schoolId: row.id, type: row.type, title: `${row.name}-供货明细` } });
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

            select(selection, row) {
                this.$refs.table.clearSelection();
                if (selection.length === 0) {
                    return;
                }
                this.$refs.table.toggleRowSelection(row, true);
            },

            handleSelectionChange(row) {
                this.selections = row;
            },

            handleExportDevices() {
                if (!this.selections || this.selections.length !== 1) {
                    this.$notify.error('请选择一条记录导出');
                    return;
                }
                const row = this.selections[0];
                const exportUrl = window.vars.URLApiBase + '/device/exportSchoolDevice?schoolId=' + row.id;
                return location.href = exportUrl;
            },

            handleExportProgress() {
                let exportUrl = window.vars.URLApiBase + '/school/exportProgress?a=1';
                if (this.search.nameLike) {
                    exportUrl += `&nameLike=${this.search.nameLike}`;
                }
                return location.href = exportUrl;
            }
        },

        created() {
            this.handlePagers();
        }
    }
</script>
