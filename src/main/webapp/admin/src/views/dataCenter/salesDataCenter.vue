<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button type="primary" style="float: right;margin-left: 10px;" v-if="G.permission['_salesDataCenter/getExcel']" @click.stop="pagersData(true,true)">导出Excel</el-button>
            </div>
            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-form-item label="商品名称:">
                    <el-autocomplete
                        v-model="searchForm.productName"
                        :fetch-suggestions="handleProductSearch"
                        placeholder="请输入商品关键字.."
                        @select="handleProductSearchSelect"
                        @input="handleProductNameChange"
                        :trigger-on-focus = 'false'
                        :disabled="searchForm.warehouseId ===''"
                    ></el-autocomplete>
                </el-form-item>

                <el-form-item label="商品规格:">
                    <el-select v-model="searchForm.specificationId" placeholder="请选择" :disabled="searchForm.warehouseId ===''">
                        <el-option
                            v-for="(item,key) in specificationList"
                            :label="item.name"
                            :value="item.id"
                            :key="key">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="订单状态:">
                    <el-popover
                        ref="popover"
                        placement="bottom"
                        width="400"
                        trigger="click">
                        <el-checkbox-group v-model="searchForm.orderStatus">
                            <el-checkbox v-for="(label,key) in orderStatusList"
                                         :label="key" :key="key">{{label}}</el-checkbox>
                        </el-checkbox-group>
                    </el-popover>
                    <el-input v-model="searchForm.orderStatusText" v-popover:popover placeholder="请选择订单状态" :disabled="true"></el-input>
                </el-form-item>

                <el-form-item label="商户信息:">
                    <el-input v-model="searchForm.keyword" placeholder="输入手机号或商户名称"></el-input>
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
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width" :sortable="column.sort">
                        <template slot-scope="scope">
                            <span v-if="column.key === 'appointDeliveryStartTime'">{{ scope.row[column.key] | paresTime }}</span>
                            <span v-else-if="column.key === 'appointDeliveryEndTime'">{{ scope.row[column.key] | paresTime }}</span>
                            <span v-else-if="column.key === 'deliveryTime'">{{ scope.row[column.key] | paresTime }}</span>
                            <span v-else-if="column.key === 'orderTime'">{{ scope.row[column.key] | paresTime }}</span>
                            <span v-else-if="column.key === 'orderSource'">{{ scope.row[column.key] | dictFilter(orderSourceList) }}</span>
                            <span v-else-if="column.key === 'orderStatus'">{{ scope.row[column.key] | dictFilter(orderStatusList) }}</span>
                            <span v-else>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleOpen(scope.row.orderNo)">查看
                            </el-button>
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

        <el-dialog :close-on-click-modal="false" title="订单详情信息" :visible.sync="dialogFormVisible" class="dialogw60">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>订单号</p>
                    <el-input disabled :value="formInfo.orderNo" placeholder="订单号"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>订单状态</p>
                    <el-input disabled :value="formInfo.orderStatus | dictFilter(orderStatusList)" placeholder="订单状态"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>下单时间</p>
                    <el-input disabled :value="formInfo.orderTime | dateFilter" placeholder="下单时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>支付时间</p>
                    <el-input disabled :value="formInfo.payTime | dateFilter" placeholder="支付时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>店铺名称</p>
                    <el-input disabled :value="formInfo.consigneeMerchantName" placeholder="店铺名称"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>收货电话</p>
                    <el-input disabled :value="formInfo.consigneeTelephone" placeholder="收货电话"></el-input>
                </el-col>
            </el-row>
            <el-row>
                <el-table :data="formInfo.orderProductSnapshotList" border v-loading="loading" :summary-method="getSummaries" show-summary>
                    <el-table-column v-for="(column,key) in columnsOrderInfo" :prop="column.key" :label="column.title" :key="key">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleClose">关闭</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>
    import api from '@/api/salesDataCenter';
    import { warehouseByUserCurrentCity, getPayMethodMap, getOrderStatusMap, getOrderSource, orderType } from '@/api/dict';
    import Form from '@/libs/form';
    import Util from '@/libs/util';

    const columns = [
        {
            key: 'orderTime',
            title: '单据日期',
            sort: 'custom'
        },
        {
            key: 'merchantTelephone',
            title: '手机号'
        },
        {
            key: 'consigneeMerchantName',
            title: '商户名称'

        },
        {
            key: 'productName',
            title: '商品名称'
        },
        {
            key: 'specificationName',
            title: '商品规格'
        },
        {
            key: 'purchasedNum',
            title: '数量'
        },
        {
            key: 'orderStatus',
            title: '订单状态'
        }
    ];

    const columnsOrderInfo = [
        { key: 'index', title: '序列号' },
        { key: 'productName', title: '商品名' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'purchasedNum', title: '购买数量' },
        { key: 'unit', title: '单位' }
    ];

    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,
                sortBy: '',
                direction: '',
                loading: false,

                searchForm: {
                    warehouseId: 1,
                    productName: '',
                    productId: '',
                    specificationId: '',
                    orderStatus: [],
                    appointDateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#'), new Date().customFormat('#YYYY#-#MM#-#DD#')],
                    deliveryDateArr: [],
                    keyword: '',
                    orderSource: '',
                    categoryFatherId: '',
                    categoryId: '',
                    orderStatusText: ''
                },
                productList: [],
                specificationList: [],

                warehouseList: {},
                payMethodList: {},
                orderStatusList: {},
                orderSourceList: {},
                productCategoryList: {},
                subCategoryList: {},
                orderTypeMap: {},
                sumNum: 0,
                sumCost: 0,
                sumSaleAmount: 0,

                dialogFormVisible: false,
                formInfo: {
                    orderProductSnapshotList: []
                },
                columnsOrderInfo: columnsOrderInfo
            };
        },
        methods: {
            handleSearch() {
                this.currentPage = 1;
                this.pagersData(true);
            },
            handleClear() {
                Form.initForm(this.searchForm);
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
            pagersData(isInitPage, isExport) {
                const param = {
                    page: 1,
                    limit: this.pageSize
                };
                if (!isInitPage) {
                    param.page = this.currentPage;
                    param.limit = this.pageSize;
                }
                if (this.searchForm.warehouseId) {
                    param.warehouseId = this.searchForm.warehouseId;
                }
                if (this.searchForm.productId) {
                    param.productId = this.searchForm.productId;
                }
                if (this.searchForm.specificationId) {
                    param.specificationId = this.searchForm.specificationId;
                }
                if (this.searchForm.keyword) {
                    param.keyword = this.searchForm.keyword;
                }
                if (this.searchForm.categoryFatherId) {
                    param.categoryFatherId = this.searchForm.categoryFatherId;
                }
                if (this.searchForm.categoryId) {
                    param.categoryId = this.searchForm.categoryId;
                }
                if (this.searchForm.orderStatus) {
                    param.orderStatus = this.searchForm.orderStatus.toString();
                }
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                param.type = this.searchForm.type;
                if (isExport === true) {
                    console.log('isExport')
                    for (const item in param) {
                        if (param.hasOwnProperty(item)) {
                            if (param[item] === undefined) {
                                param[item] = '';
                            }
                        }
                    }
                    return location.href = window.vars.URLApiBase + '/stat/salesDataCenter/getExcel?' + Util.qs(param);
                }
                api.pagers(param).then(res => {
                    console.log(res.records)
                    if (res.records === null) {
                        this.tableData = [];
                        this.totalPage = 0;
                        return;
                    }
                    res.records.map(item => {
                        if (item.type === '1') {
                            item.consigneeName = '';
                            item.consigneeMerchantName = '自提订单';
                        }
                        return item;
                    })
                    this.tableData = res.records;
                    this.totalPage = res.total;
                });
                api.sumPagers(param).then(res => {
                    this.sumNum = res.sumPurchasedNum;
                    this.sumCost = res.sumCost;
                    this.sumSaleAmount = res.sumPrice;
                });
            },
            warehouseByUserCurrentCity() {
                warehouseByUserCurrentCity().then(res => {
                    res.content[''] = '所有';
                    this.warehouseList = res.content;
                })
            },
            getPayMethodMap() {
                getPayMethodMap().then(res => {
                    this.payMethodList = res.content;
                })
            },
            getOrderStatusMap() {
                getOrderStatusMap().then(res => {
                    this.orderStatusList = res.content;
                })
            },
            getOrderSource() {
                getOrderSource().then(res => {
                    res.content[''] = '所有';
                    this.orderSourceList = res.content;
                })
            },
            // 获取字典
            getProductCategoryList() {
                const param = {
                    fatherId: 0
                };
                api.productCategory(param).then(res => {
                    if (res.code === 100) {
                        res.content.unshift({ id: '', name: '所有' });
                        this.productCategoryList = res.content;
                    } else {
                        // this.$notify.error(res.message);
                    }
                });
            },
            // 选中商品搜索下拉菜单时，获取商品规格
            handleProductSearchSelect(data) {
                this.searchForm.productId = data.id;
                const param = { productId: this.searchForm.productId };
                api.selectCitySpecification(param).then(res => {
                    if (res && res.code === 100) {
                        this.specificationList = res.content.map(item => ({ id: item.id, name: item.specificationName }));
                        this.specificationList.unshift({ id: '', name: '请选择规格' });
                    }
                });
            },
            // 输入商品时执行搜索
            handleProductSearch(data, cb) {
                if (!data) {
                    return;
                }
                const param = {
                    warehouseId: this.searchForm.warehouseId,
                    name: data
                };
                api.listCityProduct(param).then(res => {
                    if (res.code === 100) {
                        this.productList = res.content;
                        const productList = [];
                        for (const item of res.content) {
                            productList.push({ id: item.id, value: item.name });
                        }
                        cb(productList);
                    }
                });
            },

            handleProductNameChange() {
                this.searchForm.productId = '';
                this.searchForm.specificationId = '';
                this.specificationList = [{ id: '', name: '全部' }];
            },

            handleCategorySearchSelect(data) {
                const param = {
                    fatherId: data
                };
                api.productCategory(param).then(res => {
                    if (res.code === 100) {
                        res.content.unshift({ id: '', name: '所有' });
                        this.subCategoryList = res.content;
                    } else {
                        // this.$notify.error(res.message);
                    }
                });
            },
            // 打开订单详情
            handleOpen(orderNo) {
                const param = {
                    orderNo: orderNo,
                    page: 1,
                    limit: 1
                };
                api.getOrderInfo(param).then(res => {
                    const orderInfo = res.records[0];
                    if (orderInfo.type === '1') {
                        orderInfo.consigneeName = '';
                    }
                    orderInfo.orderProductSnapshotList.map((item, index) => {
                        item.index = ++index;
                        item.totalPrice = parseFloat(item.specificationUnitPrice * item.purchasedNum).toFixed(2);
                        return item;
                    });
                    this.formInfo = orderInfo;
                    this.dialogFormVisible = true;
                });
            },
            getSummaries(param) {
                const { columns, data } = param;
                const sums = [];

                columns.forEach((column, index) => {
                    // 这里写死合计位置，修改表头注意
                    if (index === 0) {
                        sums[index] = '合计';
                    } else if (index === 3) {
                        const numArr = data.map(item => Number(item.purchasedNum));
                        if (!numArr.every(value => isNaN(value))) {
                            sums[index] = numArr.reduce((prev, curr) => {
                                const value = Number(curr);
                                if (!isNaN(value)) {
                                    return prev + curr;
                                } else {
                                    return prev;
                                }
                            }, 0);
                        } else {
                            sums[index] = 0;
                        }
                    } else if (index === 6) {
                        const priceArr = data.map(item => Number(item.totalPrice));
                        if (!priceArr.every(value => isNaN(value))) {
                            const totalPrice = priceArr.reduce((prev, curr) => {
                                const value = Number(curr);
                                if (!isNaN(value)) {
                                    return prev + curr;
                                } else {
                                    return prev;
                                }
                            }, 0);
                            sums[index] = totalPrice ? parseFloat(totalPrice).toFixed(2) : totalPrice;
                        } else {
                            sums[index] = 0;
                        }
                    }
                });
                return sums;
            },
            // 关闭订单详情
            handleClose() {
                Form.initForm(this.formInfo);
                this.dialogFormVisible = false;
            },
            orderType() {
                orderType().then(res => {
                    if (res.code === 100) {
                        const map = res.content;
                        map[''] = '请选择';
                        this.orderTypeMap = map;
                    }
                })
            },
            sortChange(column) {
                console.log(column + '-' + column.prop + '-' + column.order);
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.pagersData();
            }
        },
        created() {
            this.getProductCategoryList();
            this.warehouseByUserCurrentCity();
            this.getPayMethodMap();
            this.getOrderStatusMap();
            this.getOrderSource();
            this.pagersData();
            this.orderType()
        },
        filters: {
            paresTime: function(val) {
                return Util.parseTimeStr(val);
            },
            dateFilter(data) {
                return Util.formatTime(new Date(data));
            }
        },
        watch: {
            'searchForm.orderStatus': function(curVal) {
                const arr = [];
                for (let i = 0; i < curVal.length; i++) {
                    arr.push(this.orderStatusList[curVal[i]]);
                }
                this.searchForm.orderStatusText = arr.toString();
            }
        }

    };
</script>

<style scoped>

</style>
