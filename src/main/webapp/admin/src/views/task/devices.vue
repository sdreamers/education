<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="设备名称">
                    </el-input>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

               <!--  <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleImport">导入</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button> -->
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
                    <el-table-column prop="produce" label="生产/采购" align="center">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.produce" @change="handleStatusChange(scope.row, 1)">
                                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column prop="produce" label="到货" align="center">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.arrival" @change="handleStatusChange(scope.row, 2)">
                                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column prop="produce" label="安装" align="center">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.install" @change="handleStatusChange(scope.row, 3)">
                                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
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
    import deviceApi from '@/api/device';

    const search = {
        type: '',
        nameLike: ''
    };

    const statusOptions = [{ label: '未完成', value: 0 }, { label: '已完成', value: 1 }];


    const columns = [
        { key: 'schoolName', title: '学校名称' },
        { key: 'name', title: '设备名称' },
        { key: 'num', title: '数量' },
        { key: 'unit', title: '单位' },
        { key: 'excludingTaxPrice', title: '不含税单价(元)' },
        { key: 'tax', title: '税金(元)' },
        { key: 'totalAmount', title: '含税总价(元)' }
    ];

    export default {

        data() {
            return {
                tableData: [],
                columns: columns,
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                search: JSON.parse(JSON.stringify(search)),
                statusOptions: statusOptions,
                packetId: ''
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
                if (!this.packetId) {
                    this.$notify.error('异常1');
                    return;
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    packetId: this.packetId
                };
                if (this.search.nameLike) {
                    param.nameLike = this.search.nameLike;
                }
                deviceApi.pages(param).then(res => {
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })
            },

            handleStatusChange(row, type) {
                const param = { id: row.id, type: type };
                if (type === 1) {
                    param.status = row.produce;
                } else if (type === 2) {
                    param.status = row.arrival;
                } else if (type === 3) {
                    param.status = row.install;
                } else {
                    this.$notify.error('参数异常');
                    return;
                }

                deviceApi.updateStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                    } else {
                        if (type === 1) {
                            row.produce = row.produce === 0 ? 1 : 0;
                        } else if (type === 2) {
                            row.arrival = row.arrival === 0 ? 1 : 0;
                        } else if (type === 3) {
                            row.install = row.install === 0 ? 1 : 0;
                        }
                    }
                })
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
                this.currentPage = 1;
                this.totalSize = 0;
                this.tableData = [];
                this.handlePagers();
            }
        }
    }
</script>
