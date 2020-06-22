<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-form-item label="供应商">
                    <el-select
                        v-model="searchForm.supplierId"
                        filterable
                        remote
                        reserve-keyword
                        :clearable="true"
                        placeholder="请输入供应商关键字"
                        :remote-method="handleKeywordSearch">
                        <el-option
                            v-for="item in supplierList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="当前冻库">
                    <el-select v-model="searchForm.warehouseId" placeholder="所有">
                        <el-option
                            v-for="(item,key) in warehouseList"
                            :label="item"
                            :value="key"
                            :key="key">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="制表日期">
                    <el-date-picker
                        v-model="searchForm.dateArr"
                        type="datetimerange"
                        align="right"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00','23:59:59']">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="最后审核日期">
                    <el-date-picker
                        v-model="searchForm.auditDateArr"
                        type="datetimerange"
                        align="right"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00','23:59:59']">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button @click="handleClear">清空</el-button>
                </el-form-item>
            </el-form>

            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading" @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort" :width="column.width">
                    </el-table-column>
                </el-table>
                <el-row class="mt20">
                    <el-col :span="8"><span style="font-size:14px;">数量合计: {{sumNum}}</span></el-col>
                    <el-col :span="16"><span style="font-size:14px;">进货总成本（含运费）合计: {{sumCost}} 元</span></el-col>
                </el-row>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[2, 5, 10, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPage">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
    </section>
</template>

<script>
    import api from '@/api/dataCenter';
    import supplierAPI from '@/api/supplier';
    import { warehouseByUserCurrentCity } from '@/api/dict';

    const columns = [
        { key: 'supplierName', title: '供应商' },
        { key: 'sumNum', title: '进货数量', sort: 'custom' },
        { key: 'sumCost', title: '进货总成本价(含运费)', sort: 'custom' },
        { key: 'avgCost', title: '均成本价(含运费)', sort: 'custom' },
        { key: 'sumPrice', title: '进货总成本价(不含运费)', sort: 'custom', width: 180 },
        { key: 'avgPrice', title: '均成本价(不含运费)' },
        { key: 'purchaseCount', title: '进货次数', sort: 'custom' }
    ];

    const searchForm = {
        supplierId: '',
        warehouseId: '',
        dateArr: [],
        auditDateArr: []
    };

    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                loading: false,
                sortBy: '',
                direction: '',
                searchForm: {
                    supplierId: '',
                    warehouseId: '',
                    dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59'],
                    auditDateArr: []
                },

                supplierList: {},
                warehouseList: {},

                sumNum: 0,
                sumCost: 0
            };
        },
        methods: {
            handleSearch() {
                this.currentPage = 1;
                this.pagersData(true);
            },
            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.pagersData(true);
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.currentPage = 1;
                this.pagersData(false);
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.pagersData(false);
            },
            // 获取数据
            pagersData(isInitPage) {
                const param = {
                    page: 1,
                    limit: this.pageSize
                };
                if (!isInitPage) {
                    param.page = this.currentPage;
                    param.limit = this.pageSize;
                }

                if (this.searchForm.supplierId) {
                    param.supplierId = this.searchForm.supplierId;
                }
                if (this.searchForm.warehouseId) {
                    param.warehouseId = this.searchForm.warehouseId;
                }
                if (this.searchForm.dateArr) {
                    param.createDateTimeStart = this.searchForm.dateArr[0];
                    param.createDateTimeEnd = this.searchForm.dateArr[1];
                }
                if (this.searchForm.auditDateArr) {
                    param.auditStartTime = this.searchForm.auditDateArr[0];
                    param.auditEndTime = this.searchForm.auditDateArr[1];
                }
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                api.inAndOutStockCountPages(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            console.log(item.warehousingTime)
                            if (item.warehousingTime) {
                                item.warehousingTime = item.warehousingTime.substring(0, 19);
                            }
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
                api.sumInAndOutStock(param).then(res => {
                    this.sumNum = res.content.sumTotalNum;
                    this.sumCost = res.content.sumCostIncludeShippingPrice;
                });
            },
            warehouseByUserCurrentCity() {
                warehouseByUserCurrentCity().then(res => {
                    this.warehouseList = res.content;
                });
            },
            handleKeywordSearch(data) {
                const param = {
                    name: data
                };
                supplierAPI.supplierListByKeyword(param).then(res => {
                    if (res.code === 100) {
                        this.supplierList = res.content;
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            sortChange(column) {
                console.log(column + '-' + column.prop + '-' + column.order);
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.pagersData(true);
            }
        },

        created() {
            this.pagersData();
            this.warehouseByUserCurrentCity();
        }

    };
</script>

<style scoped>

</style>
