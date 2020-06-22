<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
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
                <el-form-item label="商品名称">
                    <el-select
                        v-model="searchForm.productBasicInfoId"
                        :disabled="searchForm.warehouseId === ''"
                        filterable
                        remote
                        reserve-keyword
                        :clearable="true"
                        placeholder="请输入商品名称"
                        @change="handleProductSelect"
                        :remote-method="handleKeywordSearch">
                        <el-option
                            v-for="item in productList"
                            :key="item.productBasicInfoId"
                            :label="item.name"
                            :value="item.productBasicInfoId">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="规格名称">
                    <el-select
                        v-model="searchForm.productBasicSpecificationId"
                        :disabled="searchForm.productBasicInfoId === ''"
                        placeholder="所有">
                        <el-option
                            v-for="(item,key) in specificationList"
                            :label="item.name"
                            :value="item.productBasicSpecificationId"
                            :key="item.productBasicSpecificationId">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="操作类型">
                    <el-select v-model="searchForm.subType" placeholder="所有">
                        <el-option
                            v-for="(item,key) in typeMap"
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
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button @click="handleClear">清空</el-button>
                </el-form-item>
            </el-form>

            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort">
                    </el-table-column>
                </el-table>
                <div>合计数量：{{totalNum}}</div>
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
    import api from '@/api/adjustStockDataCenter';
    import Util from '@/libs/util';
    import { warehouseByUserCurrentCity } from '@/api/dict';

    const columns = [
        { key: 'id', title: 'ID' },
        { key: 'type', title: '类型' },
        { key: 'createDateTime', title: '单据日期' },
        { key: 'productBasicInfoId', title: '商品ID' },
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '商品规格' },
        { key: 'unit', title: '单位' },
        { key: 'num', title: '数量' }
    ];

    const searchForm = {
        productBasicInfoId: '',
        productBasicSpecificationId: '',
        supplierId: '',
        warehouseId: '',
        dateArr: []
    };

    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 10,

                loading: false,
                searchForm: {
                    productBasicInfoId: '',
                    productBasicSpecificationId: '',
                    supplierId: '',
                    warehouseId: '',
                    dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59']
                },

                productList: [],
                specificationList: [],
                warehouseList: {},

                totalNum: 0,

                typeMap: { 2: '盘亏', 1: '盘盈' }
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

                if (this.searchForm.productBasicInfoId) {
                    param.productBasicInfoId = this.searchForm.productBasicInfoId;
                }
                if (this.searchForm.productBasicSpecificationId) {
                    param.productBasicSpecificationId = this.searchForm.productBasicSpecificationId;
                }
                if (this.searchForm.warehouseId) {
                    param.warehouseId = this.searchForm.warehouseId;
                }
                if (this.searchForm.subType) {
                    param.type = this.searchForm.subType;
                }
                if (this.searchForm.dateArr) {
                    param.createDateTimeStart = this.searchForm.dateArr[0];
                    param.createDateTimeEnd = this.searchForm.dateArr[1];
                }
                api.adjustStockDataCenterPagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            item.type = this.typeMap[item.type];
                            item.createDateTime = Util.parseTimeStr(item.createDateTime);
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
                api.listSumStockDataCenter(param).then(res => {
                    this.totalNum = res.num;
                })
            },
            warehouseByUserCurrentCity() {
                warehouseByUserCurrentCity().then(res => {
                    this.warehouseList = res.content;
                });
            },
            handleProductSelect(data) {
                this.searchForm.productBasicSpecificationId = '';
                if (data === '') {
                    return;
                }
                console.log(1);
                const list = this.productList.filter(item => item.productBasicInfoId === data)[0].productSpecificationList;
                console.log(list);
                this.specificationList = list;
            },
            handleKeywordSearch(data) {
                console.log(data);
                if (!data) {
                    return this.productList = [];
                }
                const param = {
                    name: data,
                    warehouseId: this.searchForm.warehouseId
                };
                api.listProduct(param).then(res => {
                    if (res.code === 100) {
                        this.productList = res.content;
                    }
                });
            }
        },

        created() {
            this.pagersData();
            this.warehouseByUserCurrentCity();
        }

    };
</script>