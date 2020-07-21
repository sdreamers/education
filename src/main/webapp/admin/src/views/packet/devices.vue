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
        nameLike: ''
    };

    const statusMap = { 0: '未完成', 1: '完成' };


    const columns = [
        { key: 'packetName', title: '包名' },
        { key: 'schoolName', title: '学校名' },
        { key: 'name', title: '设备名称' },
        { key: 'num', title: '数量' },
        { key: 'unit', title: '单位' },
        { key: 'excludingTaxPrice', title: '不含税单价(元)' },
        { key: 'tax', title: '税金(元)' },
        { key: 'produce', title: '生产/采购' },
        { key: 'arrival', title: '到货' },
        { key: 'install', title: '安装' }
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
                statusMap: statusMap,

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
                    this.$notify.error('缺少包ID');
                    return;
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    packetId: this.packetId
                };
                if (this.search.nameLike) {
                    //
                    param.nameLike = this.search.nameLike;
                }
                deviceApi.pages(param).then(res => {
                    this.tableData = res.records;
                    if (this.tableData && this.tableData.length > 0) {
                        this.tableData.forEach(item => {
                            item.produce = statusMap[item.produce];
                            item.arrival = statusMap[item.arrival];
                            item.install = statusMap[item.install];
                        });
                    }
                    this.totalSize = res.total;
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
                this.handlePagers();
            }
        }
    }
</script>
