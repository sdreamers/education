<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-form :inline="true" :model="searchForm"  class="demo-form-inline">
                    <el-form-item label="制单日期:" >
                        <el-date-picker
                            v-model="searchForm.createDate"
                            @change="handlePagers"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="制单开始日期"
                            end-placeholder="制单结束日期">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="handleClear">清空</el-button>
                    </el-form-item>
                    <el-form-item style="float: right; margin-left: 10px;">
                        <el-button type="success"
                                   round
                                   @click="handleExportPurchasePlan">
                            导出excel
                        </el-button>
                    </el-form-item>
                </el-form>

                <el-table :data="tableRecords" ref="table" border v-loading="loading">
                    <el-table-column align="center" width="80%">
                        <template slot-scope="scope">
                            <el-radio v-model="exportPlanId" :label="scope.row.id"> &nbsp;</el-radio>
                        </template>
                    </el-table-column>
                    <el-table-column v-for="(column,key) in columns"
                                     :prop="column.key"
                                     :label="column.title"
                                     :key="key">
                    </el-table-column>
                    <el-table-column label="查看详情" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleViewDetail(scope.row)">
                                详情
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[5, 10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <detail
            :purchasePlanNoteId="purchasePlanNoteId"
            :visible="detailVisible"
            @close="detailVisible=false" />
    </section>
</template>

<script>
    import purchasePlanNote from '@/api/purchasePlanNote';
    import util from '@/libs/util';
    import detail from './detail';

    const searchForm = {
        createDate: []
    };

    const columns = [
        { key: 'createTime', title: '制单时间' },
        { key: 'creatorName', title: '制单人' },
        { key: 'status', title: '状态' }
    ];

    const exportUrl = vars.URLApiBase + '/stock/purchasePlanNoteDetail/export';

    export default {

        components: { detail },

        data() {
            return {
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                tableRecords: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 15,
                totalSize: 0,

                /** 查看明细 */
                purchasePlanNoteId: '',
                detailVisible: false,

                /** 导出 */
                exportPlanId: '',

                /** 排序 */
                sortFiled: 'create_time',
                direction: 'desc'
            }
        },

        methods: {

            /**
             * 设置默认时间
             * */
            defaultDate() {
                const now = new Date();
                const year = now.getFullYear();
                let month = now.getMonth() + 1;
                let date = now.getDate();
                if (month < 10) {
                    month = `0${month}`;
                }
                if (date < 10) {
                    date = `0${date}`;
                }
                this.searchForm.createDate[0] = this.searchForm.createDate[1] = `${year}-${month}-${date}`;
            },

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.searchForm.createDate && this.searchForm.createDate.length === 2) {
                    param.createTimeBegin = this.searchForm.createDate[0];
                    param.createTimeEnd = this.searchForm.createDate[1]
                }
                if (this.sortFiled) {
                    param.sortBy = this.sortFiled;
                }
                if (this.direction) {
                    param.direction = this.direction;
                }
                purchasePlanNote.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.createTime = util.parseTime(new Date(item.createTime));
                        })
                    }
                    this.tableRecords = res.records;
                    this.totalSize = res.total;
                });
            },

            /**
             * 查看详情
             * */
            handleViewDetail(row) {
                this.purchasePlanNoteId = row.id;
                this.detailVisible = true;
            },

            /**
             * 导出采购计划
             * */
            handleExportPurchasePlan() {
                if (!this.exportPlanId) {
                    return this.$notify.error('请选择采购计划');
                }
                const baseParam = `purchasePlanNoteId=${this.exportPlanId}`;
                location.href = exportUrl + '?' + baseParam;
            }
        },

        created() {
            this.defaultDate();
            this.handlePagers();
        }
    }
</script>

<style scoped>

</style>
