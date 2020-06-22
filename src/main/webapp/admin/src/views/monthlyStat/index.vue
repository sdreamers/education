<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="5">
                    <el-date-picker
                        v-model="search.dateArr"
                        @change="handlePagers"
                        type="daterange"
                        align="right"
                        unlink-panels
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading" >
                    <el-table-column prop="merchantName" label="单位名称" width="200%" align="center"/>
                    <el-table-column prop="remark" label="摘要" width="200%" align="center"/>
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center" >
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="120%"
                                         :label="subColumn.title" align="center" :key="subKey">
                            <template slot-scope="scope">
                                <span>{{scope.row[subColumn.key]}}</span>
                            </template>
                        </el-table-column>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                v-if="scope.row.merchantName !== '合计'"
                                @click.stop="handleExportStatement(scope.row)">导出结算单</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[30]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
    </section>
</template>
<script>
    import api from '@/api/monthlyStat';
    import util from '@/libs/util';
    import quotaYear from '@/api/quotaYear';


    const search = {
        dateArr: [util.getCookie('quotaYear') + '-01-01', util.getCookie('quotaYear') + '-12-31']
    };

    let quotaYearStartDate = null;
    let quotaYearEndDate = null;

    export default {
        data() {
            return {
                tableData: [],
                columns: [],
                currentPage: 1,
                totalSize: 0,
                pageSize: 30,
                loading: false,
                search: JSON.parse(JSON.stringify(search)),

                pickerOptions: {
                    disabledDate(time) {
                        if (!quotaYearEndDate) {
                            return time.getTime() < new Date(quotaYearStartDate);
                        } else {
                            return time.getTime() < new Date(quotaYearStartDate) || time.getTime() > new Date(quotaYearEndDate);
                        }
                    }
                }
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
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.startDate = this.search.dateArr[0];
                    param.endDate = this.search.dateArr[1];
                }
                api.statMerchantOrder(param).then(res => {
                    this.tableData = res.records;
                    this.columns = res.columns;
                    this.totalSize = res.total;
                })
            },

            handleGetQuotaYear() {
                quotaYear.get({ year: util.getCookie('quotaYear') }).then(res => {
                    if (res.code === 100 && res.content) {
                        quotaYearStartDate = res.content.startDate;
                        if (res.content.endDate) {
                            quotaYearEndDate = res.content.endDate;
                        }
                    }
                })
            },

            handleExport() {
                const param = {};
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.startDate = this.search.dateArr[0];
                    param.endDate = this.search.dateArr[1];
                }
                const exportUrl = window.vars.URLApiBase + '/stat/export';
                if (param) {
                    location.href = exportUrl + '?' + util.qs(param);
                } else {
                    location.href = exportUrl;
                }
            },

            handleExportStatement(row) {
                const param = {
                    merchantId: row.merchantId
                };
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.startDate = this.search.dateArr[0];
                    param.endDate = this.search.dateArr[1];
                }
                const exportUrl = window.vars.URLApiBase + '/stat/exportStatement';
                return location.href = exportUrl + '?' + util.qs(param);
            }

        },

        created() {
            this.handleGetQuotaYear();
            this.handlePagers();
        }
    }
</script>
