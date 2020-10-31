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
                        <template slot-scope="scope">
                            <template v-if="column.key !== 'name'">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                            <template v-else>
                                <el-popover trigger="hover" placement="top" width="50%">
                                    <p>技术参数: {{ scope.row.technicalParamter }}</p>
                                    <div slot="reference" class="name-wrapper">
                                        <span>{{ scope.row[column.key] }}</span>
                                    </div>
                                </el-popover>
                            </template>
                        </template>
                    </el-table-column>
                    <el-table-column prop="produce" label="生产/采购" align="center">
                        <template slot-scope="scope">
                            <el-input v-model="scope.row.produceNum" @change="handleStatusChange(scope.row, 1)" placeholder="生产/采购">
                            </el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="produce" label="到货" align="center">
                        <template slot-scope="scope">
                            <el-input v-model="scope.row.arrivalNum" @change="handleStatusChange(scope.row, 2)" placeholder="到货">
                            </el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="produce" label="安装" align="center">
                        <template slot-scope="scope">
                            <el-input v-model="scope.row.installNum" @change="handleStatusChange(scope.row, 3)" placeholder="安装">
                            </el-input>
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
        { key: 'no', title: '设备编号' },
        { key: 'specification', title: '规格' },
        { key: 'model', title: '型号' },
        { key: 'brand', title: '品牌/制造商' },
        { key: 'num', title: '数量' },
        { key: 'unit', title: '单位' },
        { key: 'includingTaxPrice', title: '含税单价(元)' },
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
                    param.completeNum = row.produceNum;
                } else if (type === 2) {
                    param.completeNum = row.arrivalNum;
                } else if (type === 3) {
                    param.completeNum = row.installNum;
                } else {
                    this.$notify.error('参数异常');
                    return;
                }

                if (Number.isNaN(Number.parseInt(param.completeNum))) {
                    this.$notify.error('请输入正整数');
                    return;
                }

                if (Number.parseInt(param.completeNum) < 0) {
                    this.$notify.error('请输入正整数');
                    return;
                }

                if (Number.parseInt(param.completeNum) > Number.parseInt(row.num)) {
                    this.$notify.error('输入的数量不能大于设备总数');
                    return;
                }

                deviceApi.updateCompleteNum(param).then(res => {
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
