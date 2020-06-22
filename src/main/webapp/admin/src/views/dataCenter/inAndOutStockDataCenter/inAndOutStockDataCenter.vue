<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button type="primary" style="float: right;margin-left: 10px;" v-if="G.permission['_inAndOutStockDataCenter/getExcel']" @click.stop="inAndOutStockDataCenterPagers(true)">导出Excel</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-form :inline="true" :model="search">
                    <el-row>
                        <!-- <el-col :span="4"> -->
                            <el-form-item label="冻库">
                                <el-select v-model="search.warehouseId" placeholder="选择冻库">
                                    <el-option v-for="(item,key) in warehouseList" :label="item.name" :value="item.id" :key="key"></el-option>
                                </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="商品">
                            <el-select
                                v-model="search.productBasicInfoId"
                                filterable
                                remote
                                reserve-keyword
                                placeholder="商品名称（输入筛选）"
                                :disabled="search.warehouseId === ''"
                                :remote-method="handleProductSearch"
                                @change="handleProductSearchSelect">
                                <el-option
                                v-for="item in productList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.productBasicInfoId">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="规格">
                            <el-select
                                v-model="search.productBasicSpecificationId"
                                :disabled="search.warehouseId === ''"
                                placeholder="选择规格">
                                <el-option
                                v-for="item in specList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.productBasicSpecificationId">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="供应商">
                            <el-select
                                v-model="search.supplierId"
                                filterable
                                remote
                                reserve-keyword
                                placeholder="供应商（输入筛选）"
                                :remote-method="handleMerchantSearch">
                                <el-option
                                v-for="item in merchantList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="操作类型">
                            <el-select
                                v-model="search.type"
                                placeholder="操作类型">
                                <el-option
                                v-for="item in typeList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="单据子类型">
                            <el-select
                                v-model="search.subType"
                                placeholder="单据子类型">
                                <el-option
                                v-for="item in subTypeList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col> -->
                    <!-- </el-row>
                    <el-row class="mb20" :gutter="20"> -->
                        <!-- <el-col :span="4"> -->
                            <el-form-item label="支付方式">
                            <el-select
                                v-model="search.purchasePaymentMethod"
                                placeholder="支付方式">
                                <el-option
                                v-for="item in payMethodList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item label="采购备注">
                            <el-input v-model="search.comment" placeholder="采购备注"></el-input>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="8"> -->
                            <el-form-item label="制单日期">
                            <el-date-picker
                                v-model="search.date"
                                type="datetimerange"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :default-time="['00:00:00','23:59:59']"
                                align="right"
                                @change="inAndOutStockDataCenterPagers"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="制单开始日期"
                                end-placeholder="制单结束日期">
                            </el-date-picker>
                            </el-form-item>
                        <!-- </el-col>
                        <el-col :span="4"> -->
                            <el-form-item>
                                <el-button type="primary" @click="handleSearch">搜索</el-button>
                                <el-button @click="clear">清除</el-button>
                            </el-form-item>
                        <!-- </el-col> -->
                    </el-row>
                </el-form>
                <el-row class="mb20">
                    <el-col :span="8">
                        合计数量：{{totalData.sumNum}}
                    </el-col>
                    <el-col :span="8">
                        合计成本总价：{{totalData.sumCost}}
                    </el-col>
                    <el-col :span="8">
                        合计运费：{{totalData.sumShippingPrice}}
                    </el-col>
                </el-row>
                <el-table :data="tableData" border @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort" :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key === 'type'">{{scope.row[column.key] | dictFilter(typeMap)}}</span>
                            <span v-if="column.key === 'subType'">{{scope.row[column.key] | dictFilter(subTypeMap)}}</span>
                            <!-- <span v-if="column.key === 'createDateTime'">{{scope.row[column.key] | filterDateTime}}</span> -->
                            <span v-if="column.key === 'purchasePaymentMethod'">{{scope.row[column.key] | dictFilter(payMethodMap)}}</span>
                            <span v-else v-html="scope.row[column.key]"></span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleView(scope.row)">查看</el-button>
                        </template>
                    </el-table-column>
                </el-table>
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
        <viewDetail
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,inAndOutStockDataCenterPagers()"
            :stockNoteId="stockNoteId"></viewDetail>
    </section>
</template>
<script>
import api from '@/api/inAndOutStockDataCenter';
import { warehouseByUserCurrentCity, purchasePaymentMethod, stockNoteType, subStockNoteType } from '@/api/dict';
import Util from '@/libs/util';

import viewDetail from '@/views/dataCenter/inAndOutStockDataCenter/view';

const filterDateTime = function(data) {
    let createDateTime = ' ';
    if (data) {
        const date = new Date();
        date.setYear(parseInt(data.substring(0, 4)));
        date.setMonth(parseInt(data.substring(4, 6)) - 1, parseInt(data.substring(6, 8)));
        date.setHours(parseInt(data.substring(8, 10)));
        date.setMinutes(parseInt(data.substring(10, 12)));
        date.setSeconds(parseInt(data.substring(12, 14)));
        createDateTime = date.customFormat('#YYYY#-#MM#-#DD# #hhh#:#mm#:#ss#');
    }
    return createDateTime;
};

const columns = [
    { key: 'id', title: 'ID', sort: 'custom' },
    { key: 'type', title: '单据类型' },
    { key: 'subType', title: '单据子类型', width: '150px' },
    { key: 'createDateTime', title: '单据日期', width: '140px' },
    { key: 'productId', title: '商品ID' },
    { key: 'supplierName', title: '供应商' },
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '商品规格' },
    { key: 'unit', title: '单位' },
    { key: 'price', title: '线上单价' },
    { key: 'cost', title: '成本单价(含运费)' },
    { key: 'num', title: '数量' },
    { key: 'thisCost', title: '成本总价' },
    { key: 'thisShippingPrice', title: '运费(元)' },
    { key: 'purchasePaymentMethod', title: '支付方式' }
];
export default {
    components: {
        viewDetail
    },
    data() {
        return {
            search: {
                // 冻库
                warehouseId: '',
                // 商品
                productBasicInfoId: '',
                // 规格
                productBasicSpecificationId: '',
                // 操作类型
                type: '',
                // 单据子类型
                subType: '',
                // 支付方式
                purchasePaymentMethod: '',
                // 备注
                comment: '',
                // 供应商
                supplierId: '',
                // 制单日期
                date: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59']
            },

            // 暂存单据 id
            stockNoteId: '',

            typeMap: {},
            subTypeMap: {},
            payMethodMap: {},

            warehouseList: [],
            productList: [],
            merchantList: [],
            specList: [],
            typeList: [],
            subTypeList: [],
            payMethodList: [],

            totalData: {},

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            dialogFormVisible: false,
            formLabelWidth: '120px'
        };
    },
    filters: {
        filterType(data) {
            return typeMap[data];
        },
        filterSubtype(data) {
            return subTypeMap[data];
        },
        filterPaymethod(data) {
            return payMethodMap[data];
        }
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.inAndOutStockDataCenterPagers();
        },
        // 清除搜索
        clear() {
            this.search.warehouseId = '';
            this.search.productBasicInfoId = '';
            this.search.productBasicSpecificationId = '';
            this.search.type = '';
            this.search.subType = '';
            this.search.purchasePaymentMethod = '';
            this.search.comment = '';
            this.search.supplierId = '';
            this.search.date = [];
            this.currentPage = 1;
            this.inAndOutStockDataCenterPagers();
        },
        // 选中商品搜索下拉菜单时，获取商品规格
        handleProductSearchSelect(data) {
            this.specList = this.productList.filter(item => item.productBasicInfoId = data)[0].productSpecificationVOList;
        },
        // 输入商品时执行搜索
        handleProductSearch(data) {
            const param = {
                warehouseId: this.search.warehouseId,
                name: data
            };
            api.productListProduct(param).then(res => {
                if (res.code === 100) {
                    this.productList = res.content;
                }
            });
        },
        // 输入供应商时执行搜索
        handleMerchantSearch(data) {
            window.LOG(data);
            const param = {
                name: data
            };
            api.supplierListByKeyword(param).then(res => {
                if (res.code === 100) {
                    this.merchantList = res.content;
                }
            });
        },
        // 取消表单
        handleView(data) {
            this.stockNoteId = data.stockNoteId;
            this.dialogFormVisible = true;
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.inAndOutStockDataCenterPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.inAndOutStockDataCenterPagers();
        },
        // 获取数据
        inAndOutStockDataCenterPagers(excel) {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };

            param.warehouseId = this.search.warehouseId ? this.search.warehouseId : undefined;
            param.productBasicInfoId = this.search.productBasicInfoId ? this.search.productBasicInfoId : undefined;
            param.productBasicSpecificationId = this.search.productBasicSpecificationId ? this.search.productBasicSpecificationId : undefined;
            param.type = this.search.type ? this.search.type : undefined;
            param.subType = this.search.subType ? this.search.subType : undefined;
            param.purchasePaymentMethod = this.search.purchasePaymentMethod ? this.search.purchasePaymentMethod : undefined;
            param.comment = this.search.comment ? this.search.comment : undefined;
            param.supplierId = this.search.supplierId ? this.search.supplierId : undefined;
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            if (this.search.date.length) {
                param.createDateTimeStart = this.search.date[0];
                param.createDateTimeEnd = this.search.date[1];
            }

            if (excel === true) {
                this.getExcel(param);
            }
            api.listSumStockDataCenter(param).then(res => {
                this.totalData = res;
            });
            api.inAndOutStockDataCenterPagers(param).then(res => {
                window.LOG(res);
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.createDateTime = filterDateTime(item.createDateTime);
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.info('无数据');
                }
                this.totalPage = res.total;
            });
        },
        getExcel(param) {
            if (new Date(param.createDateTimeEnd).getTime() - new Date(param.createDateTimeStart).getTime() > 15552000000) {
                return this.$notify.error('导出时间范围不能大于半年');
            }
            for (const item in param) {
                if (param.hasOwnProperty(item)) {
                    if (param[item] === undefined) {
                        param[item] = '';
                    }
                }
            }
            return location.href = window.vars.URLApiBase + '/inAndOutStockDataCenter/getExcel?' + Util.qs(param);
        },
        // 根据用户城市获取冻库
        warehouseByUserCurrentCity() {
            return warehouseByUserCurrentCity().then(res => {
                if (res.code === 100) {
                    this.warehouseMap = res.content;
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.warehouseList = arr;
                }
            });
        },
        // 支付方式
        purchasePaymentMethod() {
            return purchasePaymentMethod().then(res => {
                window.LOG(res);
                if (res.code === 100) {
                    this.payMethodMap = res.content;
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.payMethodList = arr;
                }
            });
        },
        // 单据子类型
        subStockNoteType() {
            return subStockNoteType().then(res => {
                if (res.code === 100) {
                    this.subTypeMap = res.content;
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.subTypeList = arr;
                }
            });
        },
        // 操作类型
        stockNoteType() {
            return stockNoteType().then(res => {
                if (res.code === 100) {
                    this.typeMap = res.content;
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.typeList = arr;
                }
            });
        },

        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.inAndOutStockDataCenterPagers();
        }
    },
    created() {
        Promise.all([this.warehouseByUserCurrentCity(), this.purchasePaymentMethod(), this.subStockNoteType(), this.stockNoteType()]).then(() => {
            this.inAndOutStockDataCenterPagers();
        }).catch(err => {
            this.$notify.error('获取数据字典异常:' + err);
        });
    }
};
</script>
