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

            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
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

                packetId: '',
                packetName: ''
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

            handleViewDevice(row) {
                this.$router.push({ name: '/school/devices', params: { schoolId: row.id, packetId: this.packetId, type: row.type, title: `${this.packetName}-${row.name}-供货明细` } });
            },

            handleExport() {
                const exportUrl = window.vars.URLApiBase + '/quota/export';
                return location.href = exportUrl;
            }
        },

        activated() {
            if (this.$store.state.tagsView.visitedViews) {
                for (const view of this.$store.state.tagsView.visitedViews) {
                    if (view.fullPath === this.$route.name && this.$route.params.title) {
                        view.title = this.$route.params.title;
                    }
                }
            }
            if (this.$route.params.packetId) {
                this.packetId = this.$route.params.packetId;
                this.packetName = this.$route.params.packetName;
                this.currentPage = 1;
                this.totalSize = 0;
                this.tableData = [];
                this.handlePagers();
            }
        }
    }
</script>
