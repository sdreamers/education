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
        { key: 'schoolName', title: '学校名' },
        { key: 'packetName', title: '包名' },
        { key: 'name', title: '设备名称' },
        { key: 'no', title: '设备编号' },
        { key: 'specification', title: '规格' },
        { key: 'model', title: '型号' },
        { key: 'brand', title: '品牌/制造商' },
        { key: 'num', title: '数量' },
        { key: 'unit', title: '单位' },
        { key: 'excludingTaxPrice', title: '不含税单价(元)' },
        { key: 'tax', title: '税金(元)' },
        { key: 'totalAmount', title: '含税总价(元)' },
        { key: 'produceNum', title: '生产/采购' },
        { key: 'arrivalNum', title: '到货' },
        { key: 'installNum', title: '安装' }
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

                schoolId: ''
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
                if (!this.schoolId) {
                    this.$notify.error('缺少学校ID');
                    return;
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    schoolId: this.schoolId
                };
                if (this.packetId) {
                    param.packetId = this.packetId;
                }
                if (this.search.nameLike) {
                    param.nameLike = this.search.nameLike;
                }
                deviceApi.pages(param).then(res => {
                    this.tableData = res.records;
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
            if (this.$route.params.schoolId) {
                this.schoolId = this.$route.params.schoolId;
                this.packetId = this.$route.params.packetId;
                this.currentPage = 1;
                this.totalSize = 0;
                this.tableData = [];
                this.handlePagers();
            }
        }
    }
</script>
