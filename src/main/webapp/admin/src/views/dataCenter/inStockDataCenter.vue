<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button type="primary" style="float:right;" @click="handleExport">导出 Excel</el-button>
            </div>
            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-form-item label="商品名称">
                    <el-select
                        v-model="searchForm.productBasicInfoId"
                        filterable
                        remote
                        reserve-keyword
                        :clearable="true"
                        placeholder="请输入商品名称"
                        :disabled="!searchForm.warehouseId"
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
                    <el-select v-model="searchForm.productBasicSpecificationId" placeholder="所有" :disabled="!searchForm.productBasicInfoId">
                        <el-option
                            v-for="(item,key) in specificationList"
                            :label="item.name"
                            :value="item.productBasicSpecificationId"
                            :key="item.productBasicSpecificationId">
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
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort" :width="column.width">
                    </el-table-column>
                    <el-table-column label="查看">
                        <template slot-scope="scope">
                            <el-button @click="handleGetDetails(scope.row)">详情</el-button>
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

        <el-dialog :close-on-click-modal="false" title="查看商品入库信息" :visible.sync="dialogDetailVisible" class="dialogw80">
            <el-row>
                <el-col :span="4"><b>总吨数：</b>{{formDetail.totalWeight}}</el-col>
            </el-row>
            <el-table :data="formDetail.purchaseStockNoteDetailInfoVOList" border>
                <el-table-column v-for="(column,key) in columnsDetail" :prop="column.key" :label="column.title" :key="key">
                </el-table-column>
            </el-table>
            <el-row>
                <a target="_blank" :href="item" v-for="item in formDetail.imageurls"><img :src="item" alt="" style="width: 100px;height:100px;display:inline-block;vertical-align:middle"></a>
                <img v-if="formDetail.imageurls.length === 0" src="/manager/images/refundDefaultImg.png" alt="" style="width: 100px;height:100px;display:inline-block;vertical-align:middle">
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogDetailVisible=false">关闭</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>
    import api from '@/api/inStockDataCenter';
    import Util from '@/libs/util';
    import { purchasePaymentMethod, warehouseByUserCurrentCity } from '@/api/dict';
    import supplierAPI from '@/api/supplier';

    const columns = [
        { key: 'id', title: 'ID' },
        { key: 'createDateTime', title: '单据日期' },
        { key: 'productBasicInfoId', title: '商品ID' },
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '商品规格' },
        { key: 'unit', title: '单位' },
        { key: 'num', title: '数量' },
        { key: 'weight', title: '重量(KG)' },
        { key: 'purchasePaymentMethod', title: '支付方式' }
    ];
    const columnsDetail = [
        { key: 'index', title: '序号' },
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'num', title: '入库数量' },
        { key: 'comment', title: '备注' }
    ]

    const searchForm = {
        supplierId: '',
        warehouseId: '',
        productBasicInfoId: '',
        productBasicSpecificationId: '',
        purchasePaymentMethod: '',
        comment: '',
        dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59'],
        auditDateArr: []
    };

    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                formDetail: {
                    warehouseName: '',
                    shippingPrice: '',
                    supplierName: '',
                    purchasePaymentMethod: '',
                    totalWeight: '',

                    purchaseStockNoteDetailInfoVOList: [],

                    auditRecord: '',

                    imageurls: ''
                },
                tableDataDetail: [],
                columnsDetail: columnsDetail,

                currentPage: 1,
                totalPage: 0,
                pageSize: 10,

                loading: false,
                searchForm: {
                    supplierId: '',
                    warehouseId: 1,
                    productBasicInfoId: '',
                    productBasicSpecificationId: '',
                    purchasePaymentMethod: '',
                    comment: '',
                    dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59'],
                    auditDateArr: []
                },

                sumNum: 0,
                sumCost: 0,
                sumShippingPrice: 0,

                productList: [],
                specificationList: [],
                supplierList: [],
                warehouseList: {},
                paymentMethodMap: {},

                dialogDetailVisible: false,

                typeMap: { 1: '采购入库', 2: '盘盈', 3: '盘亏', 4: '销售出库', 5: '退货', 6: '系统自动退单', 7: '损耗' }
            };
        },
        methods: {
            handleSubmitPrice() {
                console.log('1');
            },
            handleGetDetails(data) {
                const param = { id: data.purchaserId };
                api.getDetails(param).then(res => {
                    if (res.code === 100) {
                        this.formDetail = res.content;
                        this.formDetail.purchasePaymentMethod = this.paymentMethodMap[res.content.purchasePaymentMethod];
                        if (this.formDetail.imageurls) {
                            this.formDetail.imageurls = res.content.imageurls.split(',');
                        }
                        this.formDetail.purchaseStockNoteDetailInfoVOList = res.content.purchaseStockNoteDetailInfoVOList.map((item, index) => {
                            item.index = index + 1;
                            item.cost = parseFloat(item.cost).toFixed(2);
                            item.price = (parseFloat(item.cost) * parseFloat(item.num)).toFixed(2)
                            return item;
                        })
                        this.dialogDetailVisible = true;

                        console.log(this.formDetail);
                    }
                    // this.warehouseName = '';
                    // this.shippingPrice = '';
                    // this.supplierName = '';
                    // this.purchasePaymentMethod = '';
                    // this.totalWeight = '';

                    // this.purchaseStockNoteDetailInfoVOList = [];

                    // this.auditRecord = '';

                    // this.imageurls = '';
                })
            },
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
            handleExport() {
                const param = {};

                if (this.searchForm.productBasicInfoId) {
                    param.productBasicInfoId = this.searchForm.productBasicInfoId;
                }
                if (this.searchForm.productBasicSpecificationId) {
                    param.productBasicSpecificationId = this.searchForm.productBasicSpecificationId;
                }
                if (this.searchForm.warehouseId) {
                    param.warehouseId = this.searchForm.warehouseId;
                }
                if (this.searchForm.supplierId) {
                    param.supplierId = this.searchForm.supplierId;
                }
                if (this.searchForm.purchasePaymentMethod) {
                    param.purchasePaymentMethod = this.searchForm.purchasePaymentMethod;
                }
                if (this.searchForm.comment) {
                    param.comment = this.searchForm.comment;
                }
                if (this.searchForm.dateArr) {
                    if (Util.isTimeLimitExceeded(new Date(this.searchForm.dateArr[0]), new Date(this.searchForm.dateArr[1]))) {
                        this.$notify.error('导出的时间范围不能大于30天');
                        return;
                    }
                    param.createDateTimeStart = this.searchForm.dateArr[0];
                    param.createDateTimeEnd = this.searchForm.dateArr[1];
                }
                if (this.searchForm.auditDateArr) {
                    if (Util.isTimeLimitExceeded(new Date(this.searchForm.auditDateArr[0]), new Date(this.searchForm.auditDateArr[1]))) {
                        this.$notify.error('导出的时间范围不能大于30天');
                        return;
                    }
                    param.auditStartTime = this.searchForm.auditDateArr[0];
                    param.auditEndTime = this.searchForm.auditDateArr[1];
                }
                if (!param.createDateTimeStart && !param.auditStartTime) {
                    this.$notify.info('请选择导出时间范围');
                    return;
                }
                return location.href = window.vars.URLApiBase + '/stat/inStockDataCenter/getExcel?' + Util.qs(param);
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
                if (this.searchForm.supplierId) {
                    param.supplierId = this.searchForm.supplierId;
                }
                if (this.searchForm.purchasePaymentMethod) {
                    param.purchasePaymentMethod = this.searchForm.purchasePaymentMethod;
                }
                this.listSumStockDataCenter(param);
                const paymentMethodMap = this.paymentMethodMap;
                api.inStockDataCenterPagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            if (item.warehousingTime) {
                                item.warehousingTime = item.warehousingTime.substring(0, 19);
                            }
                            item.createDateTime = Util.parseTimeStr(item.createDateTime);
                            item.purchasePaymentMethod = paymentMethodMap[item.purchasePaymentMethod];
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
            },
            listSumStockDataCenter(data) {
                api.listSumStockDataCenter(data).then(res => {
                    console.log(res);
                    this.sumNum = res.sumNum;
                    this.sumCost = res.sumCost;
                    this.sumShippingPrice = res.sumShippingPrice
                })
            },
            warehouseByUserCurrentCity() {
                warehouseByUserCurrentCity().then(res => {
                    this.warehouseList = res.content;
                });
            },
            purchasePaymentMethod() {
                return purchasePaymentMethod().then(res => {
                    this.paymentMethodMap = res.content;
                })
            },
            handleProductSelect(data) {
                console.log(data);
                const product = this.productList.filter(item => item.productBasicInfoId === data)[0];
                if (product) {
                    this.specificationList = product.productSpecificationList;
                } else {
                    this.searchForm.productBasicSpecificationId = '';
                    this.specificationList = [];
                }
            },
            handleMerchantSearch(data) {
                const param = {
                    name: data
                };
                supplierAPI.supplierListByKeyword(param).then(res => {
                    if (res.code === 100) {
                        this.supplierList = res.content;
                    }
                });
            },
            handleKeywordSearch(data) {
                this.searchForm.productBasicSpecificationId = '';
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

        async created() {
            await this.purchasePaymentMethod();
            this.pagersData();
            this.warehouseByUserCurrentCity();
        }

    };
</script>
