<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                    <el-form-item label="商品名称:">
                        <el-autocomplete
                            v-model="searchForm.productName"
                            :fetch-suggestions="querySearchAsync"
                            placeholder="请输入商品关键字.."
                            @select="handleSelect"
                            style="width: 250px"
                            @input="handleProductNameChange"
                            :trigger-on-focus = 'false'
                        ></el-autocomplete>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="handleClear">清空</el-button>
                    </el-form-item>
                    <el-form-item style="float: right; margin-left: 10px;">
                        <el-button v-if="G.permission['_statistics/startStatPurchasingPlan']"
                                   type="success"
                                   round
                                   @click="handleStatPurchasingPlan">
                            立即执行采购计划
                        </el-button>

                        <el-button v-if="G.permission['_product/cityProductSpecification/exportThresholdSpecification']"
                                   style="margin-left: 20px"
                                   type="success"
                                   round
                                   @click="handleExport">
                            导出excel
                        </el-button>
                    </el-form-item>
                </el-form>

                <el-table :data="tableRecords" @row-click="handleRowClick" border v-loading="loading" :row-class-name="tableRowClassName" @sort-change="handleSortChange">
                    <el-table-column label="商品名称" prop="productName" width="150"/>
                    <el-table-column label="商品规格" prop="specificationName" width="140"/>
                    <el-table-column label="库存数量" prop="quantity" sortable="custom" width="100"/>
                    <el-table-column label="预警值" prop="thresholdValue" sortable="custom" width="100">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-input v-model="scope.row.thresholdValue" @click.stop="handleUpdateThresholdValue(scope.row)" readonly></el-input>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="采购周期" prop="purchasingCycle" sortable="custom" width="100">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-input v-model="scope.row.purchasingCycle" readonly></el-input>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="采购周期平均销量" prop="averageCyclicalSales" sortable="custom" width="150"/>
                    <el-table-column label="销量权重" prop="salesNum" sortable="custom" width="100"/>
                    <el-table-column label="利润权重" prop="profit" sortable="custom" width="100"/>
                    <el-table-column label="推荐采购量" prop="recommendation" sortable="custom" width="110"/>
                    <el-table-column align="center">
                        <template slot-scope="scope">
                            <el-button type="text" @click.stop="handleAddShoppingCart(scope.row)">加入购物车</el-button>
                            <el-button type="text" @click.stop="handleShowProductSales(scope.row)">销量</el-button>
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
                        <span class="el-pagination__total refresh" @click="handleThresholdPagers(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <!--更新预警-->
        <el-dialog :close-on-click-modal="false" title="更新预警值" width="30%" :visible.sync="thresholdDialog.visible">
            <el-form :model="thresholdDialog" >
                <el-form-item label="商品名称" :label-width="thresholdDialog.labelWidth">
                    <el-input v-model="thresholdDialog.productName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="规格名称" :label-width="thresholdDialog.labelWidth">
                    <el-input v-model="thresholdDialog.specificationName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="预警值" :label-width="thresholdDialog.labelWidth">
                    <el-input v-model="thresholdDialog.thresholdValue" type="text" auto-complete="off" maxlength="8"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelThreshold">取 消</el-button>
                <el-button type="primary" @click="handleSubmitThreshold">确 定</el-button>
            </div>
        </el-dialog>

        <!--更新采购周期-->
        <el-dialog :close-on-click-modal="false" title="更新采购周期" width="30%" :visible.sync="purchaseDialog.visible">
            <el-form :model="purchaseDialog" >
                <el-form-item label="商品名称" :label-width="purchaseDialog.labelWidth">
                    <el-input v-model="purchaseDialog.productName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="规格名称" :label-width="thresholdDialog.labelWidth">
                    <el-input v-model="purchaseDialog.specificationName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="采购周期" :label-width="thresholdDialog.labelWidth">
                    <el-input v-model="purchaseDialog.purchasingCycle" type="text" auto-complete="off" maxlength="8"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelPurchasingCycle">取 消</el-button>
                <el-button type="primary" @click="handleSubmitPurchasingCycle">确 定</el-button>
            </div>
        </el-dialog>

        <!--查看销量-->
        <el-dialog :close-on-click-modal="false" title="查看商品规格的销量趋势图" :visible.sync="salesDialog.visible" class="dialogw80">
            <el-row>
                <el-col :span="4">
                    <el-select v-model="salesDialog.dateType" placeholder="请选择分组" @change="handleDateTypeChange">
                        <el-option v-for="(item,key) in dateType" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6">
                    <el-date-picker
                        v-model="salesDialog.dateStart"
                        :type="salesDialog.pickerType"
                        :value-format="salesDialog.pickerFormat"
                        align="right"
                        unlink-panels
                        placeholder="开始日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="6">
                    <el-date-picker
                        v-model="salesDialog.dateEnd"
                        :type="salesDialog.pickerType"
                        :value-format="salesDialog.pickerFormat"
                        align="right"
                        unlink-panels
                        placeholder="结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="4" :offset="4">
                    <el-button type="primary" @click="handleSalesSearch">查询</el-button>
                    <el-button type="primary" @click="handleResetDate">重置日期</el-button>
                </el-col>
            </el-row>
            <chart-bar :series="salesDialog.chartSeries" :xaxis="salesDialog.chartXAxis" :title="salesDialog.chartTitle"></chart-bar>
        </el-dialog>

    </section>
</template>

<script>
    import cityProduct from '@/api/cityproduct';
    import purchaseCart from '@/api/purchaseCart';
    import statistics from '@/api/statistics';
    import util from '@/libs/util';
    import chartBar from '@/components/charts/chartBar.vue';

    const exportUrl = vars.URLApiBase + '/product/cityProductSpecification/exportThresholdSpecification';

    const columns = [
        { key: 'productName', title: '商品名称', width: '200%' },
        { key: 'specificationName', title: '商品规格', width: '200%' },
        { key: 'quantity', title: '库存数量', sort: 'custom' },
        { key: 'thresholdValue', title: '预警值', sort: 'custom' },
        { key: 'purchasingCycle', title: '采购周期', sort: 'custom' },
        { key: 'averageCyclicalSales', title: '采购周期平均销量', sort: 'custom' },
        { key: 'salesNum', title: '销量权重', sort: 'custom' },
        { key: 'profit', title: '利润权重', sort: 'custom' },
        { key: 'recommendation', title: '推荐采购量', sort: 'custom' }
    ];

    const searchForm = {
        productName: '',
        productId: ''
    };

    const thresholdDialog = {
        visible: false,
        labelWidth: '120px',
        productName: '',
        specificationName: '',
        thresholdValue: '',
        specificationId: ''
    };

    const purchaseDialog = {
        visible: false,
        labelWidth: '120px',
        productName: '',
        specificationName: '',
        purchasingCycle: '',
        specificationId: ''
    };

    const dateType = [
        { id: 0, name: '按日期' },
        { id: 1, name: '按月份' },
        { id: 2, name: '按年份' }
    ];

    const dateTypeCheck = {
        0: '日期', 1: '月份', 2: '年份'
    };

    const date_ = new Date();
    const year = date_.getFullYear();
    const month = date_.getMonth();

    const salesDialog = {
        visible: false,
        dateType: 0,
        dateStart: util.formatTime(new Date(year, month - 1, 1)).split(' ')[0],
        dateEnd: util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0],
        chartSeries: [],
        chartXAxis: [],
        chartTitle: '',
        pickerType: 'date',
        pickerFormat: 'yyyy-MM-dd',
        specificationId: ''
    };

    export default {

        components: {
            chartBar
        },

        data() {
            return {
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                tableRecords: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 15,
                totalSize: 0,

                sortBy: '',
                direction: '',

                thresholdDialog: JSON.parse(JSON.stringify(thresholdDialog)),
                purchaseDialog: JSON.parse(JSON.stringify(purchaseDialog)),


                dateType: dateType,
                salesDialog: JSON.parse(JSON.stringify(salesDialog)),
                dateTypeCheck: dateTypeCheck
            }
        },

        methods: {
            /**
             * 商品搜索
             * */
            querySearchAsync(productName, cb) {
                const param = { name: productName };
                cityProduct.productListProduct(param).then(res => {
                    if (res && res.code === 100) {
                        const productArr = [];
                        res.content.forEach(product => {
                            productArr.push({ value: product.name, id: product.id });
                        });
                        cb(productArr);
                    } else {
                        return this.$notify.error(res.message);
                    }
                })
            },

            handleSelect(item) {
                this.searchForm.productId = item.id;
            },

            handleProductNameChange() {
                if (!this.searchForm.productName) {
                    this.searchForm.productId = '';
                }
            },

            handleSearch() {
                this.currentPage = 1;
                this.handleThresholdPagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handleThresholdPagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handleThresholdPagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handleThresholdPagers();
            },

            handleThresholdPagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.searchForm.productId) {
                    param.productId = this.searchForm.productId;
                }
                if (this.sortBy) {
                    param.sortBy = this.sortBy
                }
                if (this.direction) {
                    param.direction = this.direction;
                }
                cityProduct.listThresholdSpecificationPaging(param).then(res => {
                    this.tableRecords = res.records;
                    this.totalSize = res.total;
                })
            },

            /**
             * 加入购物车
             * */
            handleAddShoppingCart(row) {
                this.$prompt('采购数量', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^\d{1,10}$/
                }).then(({ value }) => {
                    if (value.length > 10) {
                        return this.$notify.error('不得大于10位');
                    }
                    if (!value) {
                        return this.$notify.error('请输入采购数量');
                    }
                    if (isNaN(value)) {
                        return this.$notify.error('请输入正确的采购数量');
                    }
                    const param = {
                        specificationId: row.specificationId,
                        productId: row.productId,
                        productName: row.productName,
                        specificationName: row.specificationName,
                        productBasicSpecificationId: row.productBasicSpecificationId,
                        productBasicInfoId: row.productBasicInfoId,
                        amount: value
                    };

                    purchaseCart.insert(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('采购成功');
                            this.handleThresholdPagers();
                        }
                    })
                }).catch(() => {
                    console.log('取消');
                });
            },

            /**
             * 为存在于购物车的规格添加背景颜色
             * */
            tableRowClassName({ row }) {
                return row.inPurchaseCart ? 'warning-row' : '';
            },

            /**
             * 立即执行采购计划
             * */
            handleStatPurchasingPlan() {
                const param = { t: new Date().getTime() }
                statistics.startStatPurchasingPlan(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('执行成功');
                        this.handleThresholdPagers();
                    }
                });
            },

            handleExport() {
                let paramUrl = '';
                if (this.searchForm.productId) {
                    paramUrl += `productId=${this.searchForm.productId}`
                }
                location.href = exportUrl + '?' + paramUrl;
            },

            /**
             * 列表排序
             * */
            handleSortChange(column) {
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.handleThresholdPagers();
            },

            /**
             * 点击行
             * */
            handleRowClick(row, event, column) {
                if (column.label === '预警值') {
                    this.handleUpdateThresholdValue(row);
                } else if (column.label === '采购周期') {
                    this.handleUpdatePurchasingCycle(row);
                }
            },

            /** 打开更新预警值dialog */
            handleUpdateThresholdValue(row) {
                this.thresholdDialog.productName = row.productName;
                this.thresholdDialog.specificationName = row.specificationName;
                this.thresholdDialog.thresholdValue = row.thresholdValue;
                this.thresholdDialog.specificationId = row.specificationId;
                this.thresholdDialog.visible = true;
            },

            handleCancelThreshold() {
                this.thresholdDialog = JSON.parse(JSON.stringify(thresholdDialog));
            },

            handleSubmitThreshold() {
                const thresholdValue = this.thresholdDialog.thresholdValue;
                if (!thresholdValue || isNaN(thresholdValue)) {
                    return this.$notify.error('请输入正确的预警值');
                }
                if (Number(thresholdValue) % 1 !== 0 || Number(thresholdValue) <= 0) {
                    return this.$notify.error('预警值应为正整数');
                }
                const param = {
                    specificationId: this.thresholdDialog.specificationId,
                    thresholdValue: this.thresholdDialog.thresholdValue
                }
                cityProduct.updateThresholdValue(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('更新预警值成功');
                        this.thresholdDialog = JSON.parse(JSON.stringify(thresholdDialog));
                        this.handleThresholdPagers();
                    }
                })
            },

            /**
             * 更新采购周期
             * */
            handleUpdatePurchasingCycle(row) {
                this.purchaseDialog.productName = row.productName;
                this.purchaseDialog.specificationName = row.specificationName;
                this.purchaseDialog.purchasingCycle = row.purchasingCycle;
                this.purchaseDialog.specificationId = row.specificationId;
                this.purchaseDialog.visible = true;
            },

            handleCancelPurchasingCycle() {
                this.purchaseDialog = JSON.parse(JSON.stringify(purchaseDialog));
            },

            handleSubmitPurchasingCycle() {
                const purchasingCycle = this.purchaseDialog.purchasingCycle;
                if (!purchasingCycle || isNaN(purchasingCycle)) {
                    return this.$notify.error('请输入正确的采购周期');
                }
                if (Number(purchasingCycle) % 1 !== 0 || Number(purchasingCycle) <= 0) {
                    return this.$notify.error('采购周期应为正整数');
                }
                const param = {
                    specificationId: this.purchaseDialog.specificationId,
                    purchasingCycle: this.purchaseDialog.purchasingCycle
                }
                cityProduct.updatePurchasingCycle(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('更新采购周期成功');
                        this.purchaseDialog = JSON.parse(JSON.stringify(purchaseDialog));
                        this.handleThresholdPagers();
                    }
                })
            },


            /**
             * 查看销量相关方法
             * */
            handleShowProductSales(row) {
                this.salesDialog.specificationId = row.specificationId;
                this.salesDialog.visible = true;
                this.handleSalesSearch();
            },

            handleResetDate() {
                this.salesDialog.dateType = 0;
                this.handleDateTypeChange();
                this.salesDialog.dateStart = util.formatTime(new Date(year, month, 1)).split(' ')[0];
                this.salesDialog.dateEnd = util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0];
                this.handleSalesSearch();
            },

            handleDateTypeChange() {
                if (this.salesDialog.dateType === 0) {
                    this.salesDialog.pickerType = 'date';
                    this.salesDialog.pickerFormat = 'yyyy-MM-dd';
                } else if (this.salesDialog.dateType === 1) {
                    this.salesDialog.pickerType = 'month';
                    this.salesDialog.pickerFormat = 'yyyy-MM';
                } else if (this.salesDialog.dateType === 2) {
                    this.salesDialog.pickerType = 'year';
                    this.salesDialog.pickerFormat = 'yyyy';
                }
                this.salesDialog.dateStart = '';
                this.salesDialog.dateEnd = '';
                if (this.salesDialog.dateType === 0) {
                    this.salesDialog.chartTitle = '每日销售量趋势图';
                } else if (this.salesDialog.dateType === 1) {
                    this.salesDialog.chartTitle = '每月销售量趋势图';
                } else if (this.salesDialog.dateType === 2) {
                    this.salesDialog.chartTitle = '每年销售量趋势图';
                }
            },

            handleSalesSearch() {
                const param = {
                    dateType: this.salesDialog.dateType,
                    productSpecificationId: this.salesDialog.specificationId,
                    deliveryDateBegin: this.salesDialog.dateStart,
                    deliveryDateEnd: this.salesDialog.dateEnd
                };
                if (!param.deliveryDateBegin || !param.deliveryDateEnd) {
                    return this.$notify.error('请选择日期');
                }
                if (!util.compareDateString(param.deliveryDateBegin, param.deliveryDateEnd)) {
                    return this.$notify.error(`开始${this.dateTypeCheck[param.dateType]}不能大于结束${this.dateTypeCheck[param.dateType]}`);
                }
                cityProduct.statProductSales(param).then(res => {
                    const arr = [];
                    const xaxis = [];
                    const data = res.content;
                    for (const item in data) {
                        if (data.hasOwnProperty(item)) {
                            const ele = data[item];
                            arr.push(ele);
                            xaxis.push(item);
                        }
                    }
                    if (this.salesDialog.dateType === 0) {
                        this.salesDialog.chartTitle = '每日销售量趋势图';
                    } else if (this.salesDialog.dateType === 1) {
                        this.salesDialog.chartTitle = '每月销售量趋势图';
                    } else if (this.salesDialog.dateType === 2) {
                        this.salesDialog.chartTitle = '每年销售量趋势图';
                    }
                    this.salesDialog.chartXAxis = xaxis;
                    this.salesDialog.chartSeries = arr;
                });
            }
        },

        created() {
            this.handleThresholdPagers();
        }

    }
</script>

<style>
    .el-table .warning-row {
        background: oldlace;
    }

    .el-table .success-row {
        background: #f0f9eb;
    }
</style>
