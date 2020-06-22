<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <p style="font-size:12px;">关键字：</p>
                        <el-input v-model="searchString" placeholder="商品名称，规格名称，类型，品牌，产地"></el-input>
                    </el-col>
                    <el-col :span="2">
                        <p style="font-size:12px;">上架状态：</p>
                        <el-select v-model="onStock" placeholder="上架状态" @change="handleSearch">
                            <el-option v-for="(item,key) in onStockList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px;">促销导购：</p>
                        <el-select v-model="promotionZone" placeholder="促销导购" @change="handleSearch">
                            <el-option v-for="(item,key) in promitionList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <p style="font-size:12px;">供应商：</p>
                        <el-select v-model="suppliers" style="width: 100%"  multiple collapse-tags placeholder="供应商">
                            <el-option v-for="(item,key) in supplierList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px;">APP 显示状态：</p>
                        <el-select v-model="isAppDisplay" placeholder="APP 显示状态" @change="handleSearch">
                            <el-option v-for="(item,key) in appDisplayList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="2">
                        <p style="font-size:12px;">是否拆卖：</p>
                        <el-select v-model="isSplit" placeholder="是否拆卖" @change="handleSearch">
                            <el-option v-for="(item,key) in splitList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        <p style="font-size:12px;visibility:hidden;opacity:0">搜索</p>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                        <el-button  v-if="G.permission['_product/cityProductSpecification/exportExcel']"
                                    @click="listSpecificationPageInfo(true)">导出Excel</el-button>
                    </el-col>
                </el-row>
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <p style="font-size:12px;">展示的列：</p>
                        <el-select
                            v-model="columnsSelected"
                            @change="handleColumnChange"
                            multiple
                            collapse-tags
                            placeholder="选择展示的列">
                            <el-option
                                v-for="item in columns"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        <p style="font-size:12px;">库存范围：</p>
                        <el-col :span="11" style="padding: 0">
                            <el-input v-model="inventoryMin" placeholder="最小"></el-input>
                        </el-col>
                        <el-col :span="2" style="text-align:center;padding: 0">-</el-col>
                        <el-col :span="11" style="padding: 0">
                            <el-input v-model="inventoryMax" placeholder="最大"></el-input>
                        </el-col>
                    </el-col>
                    <el-col :span="12">
                        <p style="font-size:12px;">其它设置：</p>
                        <el-button type="primary" @click="clearForm('formBackupCity'),dialogBackupCityVisible = true,getProvince(),getByMasterCityId()"  v-if="G.permission['_product/cityMasterSlave/insertOrUpdate']">设置城市主备</el-button>
                        <el-button type="primary" @click="clearForm('latestForm'),dialogLatestVisible = true,getProvince()" v-if="G.permission['_product/cityMasterSlave/copyNewCityProduct']">获取新商品</el-button>
                        <el-button type="primary" @click="handleBatchCycle" v-if="G.permission['_product/cityProductSpecification/updateAllPurchasingCycle']">采购周期批量调整</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading" @row-click="handleRowClick" @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" v-if="columnsSelected.includes(column.key)" :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="!column.custom">{{scope.row[column.key]}}</span>
                            <section v-if="column.key === 'isSplit'" style="text-align:center">
                                <el-select v-model="scope.row.isSplit" placeholder="请选择分组" disabled>
                                    <el-option v-for="(item,key) in splitList" :label="item.name" :value="item.id" :key="key"></el-option>
                                </el-select>
                            </section>
                            <section v-if="column.key === 'promotionZone' && G.permission['_product/cityProductSpecification/updatePromotionZone']" style="text-align:center">
                                <el-select v-model="scope.row.promotionZoneList" placeholder="请选择分组" @change="handleChangePromition(scope.row)" multiple collapse-tags>
                                    <el-option v-for="item in promitionList" :label="item.name" :value="item.id" :key="item.id"></el-option>
                                </el-select>
                            </section>
                            <!-- <section v-if="column.key === 'showBufferQuantity'" style="text-align:center">
                                <el-button type="primary" @click="handleBuffer(scope.row)">修改</el-button>
                            </section> -->
                            <section v-if="column.key === 'showPrice'" style="text-align:center">
                                <el-button type="primary" @click="handlePrice(scope.row)">修改</el-button>
                            </section>
                            <section v-if="column.key === 'thresholdValue'" style="text-align:center">
                                <el-input v-model="scope.row.thresholdValue" readonly></el-input>
                            </section>
                            <section v-if="column.key === 'purchasingCycle'" style="text-align:center">
                                <el-input v-model="scope.row.purchasingCycle" readonly></el-input>
                            </section>
                            <section v-if="column.key === 'available'" style="text-align:center">
                                <el-switch
                                    v-model="scope.row.available === 1"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                            <section v-if="column.key === 'showSpecification' && scope.row.productSpecificationId != null" style="text-align:center">
                                <el-switch
                                    v-model="scope.row.isAppDisplay === 1"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                            <section v-if="column.key === 'showStockList' && scope.row.productSpecificationId != null" style="text-align:center">
                                <el-switch
                                    v-model="scope.row.isStockListDisplay === '1'"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120%" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleShowProductSales(scope.row)">销量</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleShowSupplierDetail(scope.row)">供应详情</el-button>
                            <!-- <el-button
                                type="text"
                                @click.stop="handleRemove(scope.row)">删除</el-button> -->
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

        <el-dialog :close-on-click-modal="false" title="获取最新商品" :visible.sync="dialogLatestVisible">
            <el-form :model="latestForm">
                <el-form-item label="省份" :label-width="formLabelWidth" prop="name">
                <el-select v-model="latestForm.provinceId" placeholder="省份" @change="getCity">
                    <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
                </el-form-item>
                <el-form-item label="城市" :label-width="formLabelWidth" prop="name">
                    <el-select v-model="latestForm.cityId" placeholder="城市" @change="latestFormCityChange">
                        <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <p style="margin-left:120px;" v-show="latestForm.isRatioShow">是否设置{{cityName}}的价格为{{latestForm.cityName}}的{{latestForm.priceRatio}}倍</p>
                <el-form-item label="价格倍数" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="latestForm.priceRatio" type="text" auto-complete="off" @input="latestForm.isRatioShow = true"></el-input>
                </el-form-item>
                <el-form-item label="成本倍数" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="latestForm.costRatio" type="text" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogLatestVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitLatest">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="选择备城" :visible.sync="dialogBackupCityVisible">
            <el-form :model="formBackupCity">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="8">
                        <el-form-item label="省份列表" label-width="80px">
                            <el-select v-model="formBackupCity.provinceId" placeholder="省份列表" @change="getCity">
                                <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="城市列表" label-width="80px">
                            <el-select v-model="formBackupCity.cityId" placeholder="城市列表" >
                                <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="rowClickBackupCity()">添加备城</el-button>
                    </el-col>
                </el-row>
            </el-form>
            <el-row style="margin-top:20px">
                <p>已选择：</p>
                <el-button type="primary" v-for="(item,key) in formBackupCity.selected" :key="key" @click="handleClickBackupCity(item)">{{item.slaveCityName}}</el-button>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogBackupCityVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitBackupCity">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="修改售价" :visible.sync="dialogPriceVisible">
            <el-form :model="formPrice" >
                <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="formPrice.specificationName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="当前售价" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="formPrice.price" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
                <el-form-item label="新售价" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="formPrice.newPrice" type="text" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogPriceVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitPrice">确 定</el-button>
            </div>
        </el-dialog>



        <el-dialog :close-on-click-modal="false" title="查看商品规格的销量趋势图" :visible.sync="dialogSalesVisible" class="dialogw80">
            <el-row>
                <el-col :span="4">
                    <el-select v-model="formSales.dateType" placeholder="请选择分组" @change="handleDateTypeChange">
                        <el-option v-for="(item,key) in dateType" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6">
                    <el-date-picker
                        v-model="formSales.dateStart"
                        :type="pickerType"
                        :value-format="pickerFormat"
                        align="right"
                        unlink-panels
                        placeholder="开始日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="6">
                    <el-date-picker
                        v-model="formSales.dateEnd"
                        :type="pickerType"
                        :value-format="pickerFormat"
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
            <chart-bar :series="chartSeries" :xaxis="chartXAxis" :title="chartTitle"></chart-bar>
        </el-dialog>
        <threshold
            v-if="dialogThresholdVisible"
            :formThreshold="formThreshold"
            :formLabelWidth="formLabelWidth"
            @close="dialogThresholdVisible=false,listSpecificationPageInfo()"></threshold>
        <sortNo
            v-if="dialogSortNoVisible"
            :formSortNo="formSortNo"
            :formLabelWidth="formLabelWidth"
            @close="dialogSortNoVisible=false,listSpecificationPageInfo()"></sortNo>
        <purchase
            v-if="dialogPurchasingCycleVisible"
            :formPurchasingCycle="formPurchasingCycle"
            :formLabelWidth="formLabelWidth"
            @close="dialogPurchasingCycleVisible=false,listSpecificationPageInfo()"></purchase>
        <batchCycle
            v-if="dialogBatchCycleVisible"
            :formBatchCycle="formBatchCycle"
            :formLabelWidth="formLabelWidth"
            @close="dialogBatchCycleVisible=false,listSpecificationPageInfo()"></batchCycle>
        <supplier-detail
            :visible="supplierDialog.visible"
            :citySpecificationId="supplierDialog.citySpecificationId"
            :cityProductId="supplierDialog.cityProductId"
            :isSplit="supplierDialog.isSplit"></supplier-detail>
    </section>
</template>
<script>
    import api from '@/api/cityproduct';
    import Util from '@/libs/util';
    import apiCommon from '@/api/common';
    import chartBar from '@/components/charts/chartBar.vue';
    import Form from '@/libs/form';
    import threshold from './threshold';
    import purchase from './purchase';
    import batchCycle from './batchCycle';
    import supplierDetail from './supplierDetail';
    import supplier from '@/api/supplier';
    import sortNo from './sortNo';

    const columns = [
        { key: 'sortNo', title: '优先级', sort: 'custom', width: '90' },
        { key: 'productSpecificationNo', title: '编号', sort: 'custom', width: '80' },
        { key: 'name', title: '商品名称', sort: 'custom', width: '100' },
        { key: 'specificationName', title: '规格名称', width: '100' },
        { key: 'brandName', title: '品牌名称', sort: 'custom', width: '100' },
        { key: 'categoryName', title: '品类名称', sort: 'custom', width: '100' },
        { key: 'totalStock', title: '总库存', sort: 'custom', width: '90' },
        { key: 'areaName', title: '产地名称', sort: 'custom', width: '100' },
        { key: 'lowestPrice', title: '规格最低价格（元）', sort: 'custom', width: '160' },
        { key: 'isSplit', title: '是否拆卖', custom: true, width: '100' },
        { key: 'promotionZone', title: '促销导购', custom: true, width: '180' },
        { key: 'weight', title: '重量（公斤）', width: '100' },
        { key: 'unit', title: '单位', width: '45' },
        { key: 'viewCost', title: '成本价（元）', width: '100' },
        { key: 'price', title: '售价（元）', width: '90' },
        { key: 'quantity', title: '库存', width: '60' },
        { key: 'thresholdValue', title: '预警值', custom: true, hide: !window.permission['_product/cityProductSpecification/updateThresholdValue'] },
        { key: 'bufferQuantity', title: '缓冲值', width: '60' },
        { key: 'showPrice', title: '修改售价', hide: !window.permission['_stock/priceNote/add'] },
        { key: 'relationNum', title: '关系数', width: '60' },
        { key: 'purchasingCycle', title: '采购周期（天）', custom: true, hide: !window.permission['_product/cityProductSpecification/updatePurchasingCycle'], width: '110' },
        { key: 'showAvailable', title: '上下架状态', width: '90' },
        { key: 'available', title: '上下架', custom: true, hide: !window.permission['_product/cityProductSpecification/updateOnSaleStatus'] },
        { key: 'showSpecification', title: '显示规格', hide: !window.permission['_product/cityProductSpecification/updateIsAppDisplay'] },
        { key: 'favorite', title: '收藏量', width: '90' },
        { key: 'sale', title: '销售量', sort: 'custom', width: '90' }
    ];

    const splitList = [
        { id: '', name: '全部' },
        { id: '1', name: '是' },
        { id: '0', name: '否' }
    ];
    const onStockList = [
        { id: '', name: '全部' },
        { id: '0', name: '待上架' },
        { id: '1', name: '已上架' }
    ];
    const promitionList = [
        // { id: '', name: '全部' },
        { id: 'hot_sale', name: '热销产品' },
        { id: 'new_product', name: '新品上架' },
        { id: 'price_cut', name: '最近降价' }
    ];
    const appDisplayList = [
        { id: '', name: '全部' },
        { id: '1', name: '显示' },
        { id: '0', name: '隐藏' }
    ];
    const wareHousesingListAdd = [
        { id: 'in_inventory_profit', name: '盘盈' }
    ];
    const wareHousesingListDesc = [
        { id: 'out_inventory_loss', name: '盘亏' }
    ];
    const form = {
        name: ''
    };
    const dateType = [
        { id: 0, name: '按日期' },
        { id: 1, name: '按月份' },
        { id: 2, name: '按年份' }
    ];
    const dateTypeCheck = {
        0: '日期', 1: '月份', 2: '年份'
    };
    const compareWarehouseList = [
        { id: 5, name: '儋州冻品云仓库' },
        { id: 6, name: '三亚冻品云仓库' }
    ];
    const date_ = new Date();
    const year = date_.getFullYear();
    const month = date_.getMonth();

    const supplierDialog = {
        citySpecificationId: '',
        cityProductId: '',
        isSplit: '',
        visible: false
    };

    export default {
        components: {
            chartBar,
            threshold,
            sortNo,
            purchase,
            batchCycle,
            supplierDetail
        },
        data() {
            return {
                cityName: '',

                searchString: '',
                sortBy: '',
                direction: '',

                onStock: '',
                onStockList: onStockList,

                promotionZone: '',

                suppliers: [],
                supplierList: [],

                isAppDisplay: '',
                appDisplayList: appDisplayList,

                inventoryMin: '',
                inventoryMax: '',

                isSplit: '',

                splitList: splitList,
                promitionList: promitionList,
                wareHousesingListAdd: wareHousesingListAdd,
                wareHousesingListDesc: wareHousesingListDesc,

                compareWarehouseList: compareWarehouseList,

                dateType: dateType,
                dateTypeCheck: dateTypeCheck,
                pickerType: 'date',
                pickerFormat: 'yyyy-MM-dd',

                tableData: [],
                columns: columns.map(item => {
                    item.name = item.title;
                    item.id = item.key;
                    return item;
                }).filter(item => !item.hide),
                columnsSelected: columns.map(item => item.key),
                cityArr: [],
                provinceArr: [],
                chartSeries: [],
                chartXAxis: [],
                chartTitle: '',

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,
                dialogBatchCycleVisible: false,
                loading: false,
                dialogBackupCityVisible: false,
                dialogWarehousingAddVisible: false,
                dialogWarehousingDescVisible: false,
                dialogWarehouseLocationVisible: false,
                dialogBufferVisible: false,
                dialogPriceVisible: false,
                dialogThresholdVisible: false,
                dialogSortNoVisible: false,
                dialogPurchasingCycleVisible: false,
                dialogSalesVisible: false,
                dialogLatestVisible: false,
                formLabelWidth: '120px',
                formWarehousing: JSON.parse(JSON.stringify(form)),
                formWarehouseLocation: {},
                formBuffer: {},
                formPrice: {},
                formThreshold: {},
                formSortNo: {},
                formPurchasingCycle: {},
                formBackupCity: {
                    selected: [],
                    provinceId: '',
                    cityId: ''
                },
                latestForm: {
                    warehouseArr: [],
                    provinceId: '',
                    cityId: '',
                    isRatioShow: false,
                    cityName: '',
                    priceRatio: '',
                    costRatio: ''
                },
                formBatchCycle: {},
                formSales: {
                    dateType: 0,
                    dateStart: Util.formatTime(new Date(year, month - 1, 1)).split(' ')[0],
                    dateEnd: Util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0]
                },
                formType: '',
                rules: {
                    unitName: [
                        { required: true, message: '序号不能为空', trigger: 'blur' }
                    ]
                },

                supplierDialog: JSON.parse(JSON.stringify(supplierDialog))

            };
        },
        methods: {
            latestFormCityChange() {
                this.latestForm.cityName = this.cityArr.find(item => item.id === this.latestForm.cityId).name;
            },
            clearForm(name) {
                Form.initForm(this[name]);
            },
            handleColumnChange(data) {
                window.LOG(data);
                const columnKeys = this.columnsSelected.join(',');
                localStorage.setItem('columnKeysShow_cityProduct', columnKeys);
            },
            checkColumnShow() {
                const columnKeys = localStorage.getItem('columnKeysShow_cityProduct');
                window.LOG(columnKeys);
                if (columnKeys) {
                    this.columnsSelected = columnKeys.split(',');
                }
            },
            handleSearch() {
                this.currentPage = 1;
                this.listSpecificationPageInfo();
            },
            handleBatchCycle() {
                console.log('handleBatchCycle')
                this.dialogBatchCycleVisible = true;
            },
            // 清除搜索
            handleClear() {
                this.searchString = '';
                this.onStock = '';
                this.promotionZone = '';
                this.isAppDisplay = '';
                this.isSplit = '';
                this.currentPage = 1;
                this.inventoryMin = '';
                this.inventoryMax = '';
                this.suppliers = [];
                this.listSpecificationPageInfo();
            },
            handleResetDate() {
                this.formSales.dateType = 0;
                this.handleDateTypeChange();
                this.formSales.dateStart = Util.formatTime(new Date(year, month, 1)).split(' ')[0];
                this.formSales.dateEnd = Util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0];
                this.handleSalesSearch();
            },
            handleDateTypeChange() {
                if (this.formSales.dateType === 0) {
                    this.pickerType = 'date';
                    this.pickerFormat = 'yyyy-MM-dd';
                } else if (this.formSales.dateType === 1) {
                    this.pickerType = 'month';
                    this.pickerFormat = 'yyyy-MM';
                } else if (this.formSales.dateType === 2) {
                    this.pickerType = 'year';
                    this.pickerFormat = 'yyyy';
                }
                this.formSales.dateStart = '';
                this.formSales.dateEnd = '';
                if (this.formSales.dateType === 0) {
                    this.chartTitle = '每日销售量趋势图';
                } else if (this.formSales.dateType === 1) {
                    this.chartTitle = '每月销售量趋势图';
                } else if (this.formSales.dateType === 2) {
                    this.chartTitle = '每年销售量趋势图';
                }
            },
            handleSalesSearch() {
                const param = {
                    dateType: this.formSales.dateType,
                    productSpecificationId: this.formSales.productSpecificationId,
                    deliveryDateBegin: this.formSales.dateStart,
                    deliveryDateEnd: this.formSales.dateEnd
                };
                if (!param.deliveryDateBegin || !param.deliveryDateEnd) {
                    return this.$notify.error('请选择日期');
                }
                if (!Util.compareDateString(param.deliveryDateBegin, param.deliveryDateEnd)) {
                    return this.$notify.error(`开始${this.dateTypeCheck[param.dateType]}不能大于结束${this.dateTypeCheck[param.dateType]}`);
                }
                api.statProductSales(param).then(res => {
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
                    if (this.formSales.dateType === 0) {
                        this.chartTitle = '每日销售量趋势图';
                    } else if (this.formSales.dateType === 1) {
                        this.chartTitle = '每月销售量趋势图';
                    } else if (this.formSales.dateType === 2) {
                        this.chartTitle = '每年销售量趋势图';
                    }
                    this.chartXAxis = xaxis;
                    this.chartSeries = arr;
                });
            },
            // 获取最新商品
            handleSubmitLatest() {
                const param = {
                    slaveCityId: this.latestForm.cityId,
                    priceRatio: this.latestForm.priceRatio,
                    costRatio: this.latestForm.costRatio
                };
                if (!param.slaveCityId) {
                    this.$notify.error('请选择城市');
                    return;
                }
                if (isNaN(param.priceRatio)) {
                    return this.$notify.error('价格倍数请输入数字');
                }
                if (isNaN(param.costRatio)) {
                    return this.$notify.error('成本倍数请输入数字');
                }
                if (param.priceRatio <= 0) {
                    this.$notify.error('价格倍数不能小于或等于0');
                    return;
                }
                if (param.costRatio <= 0) {
                    this.$notify.error('成本倍数不能小于或等于0');
                    return;
                }
                window.LOG(param);
                api.copyNewCityProduct(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogLatestVisible = false;
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            updateStockListDisplayStatus(row) {
                const param = {
                    productSpecificationId: row.productSpecificationId,
                    isStockListDisplay: row.isStockListDisplay === '0' ? '1' : '0'
                };
                api.updateStockListDisplayStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.listSpecificationPageInfo();
                    }
                });
            },
            updateOnDisplayStatus(row) {
                const param = {
                    productId: row.productId,
                    productSpecificationId: row.productSpecificationId,
                    isAppDisplay: row.isAppDisplay === 0 ? 1 : 0
                };
                api.updateOnDisplayStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.listSpecificationPageInfo();
                    }
                });
            },
            updateOnSaleStatus(row) {
                const param = {
                    productId: row.productId,
                    productSpecificationId: row.productSpecificationId,
                    available: row.available === 0 ? 1 : 0
                };
                if ((row.price <= 0 || row.price === null) && row.available === 0) {
                    this.$alert('请先设置售价');
                    return;
                }
                api.updateOnSaleStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.listSpecificationPageInfo();
                    }
                });
            },
            handleRowClick(row, event, column) {
                console.log('handleRowClick')
                if (column.label === '上下架') {
                    this.updateOnSaleStatus(row);
                } else if (column.label === '显示规格') {
                    this.updateOnDisplayStatus(row);
                } else if (column.label === '显示库存表') {
                    this.updateStockListDisplayStatus(row);
                } else if (column.label === '预警值') {
                    this.handleThresholdValue(row);
                } else if (column.label === '采购周期（天）') {
                    this.handlePurchasingCycle(row);
                } else if (column.label === '优先级') {
                    this.handleSortNo(row);
                }
            },
            handleSubmitPurchasingCycle() {
                const param = {
                    specificationId: this.formPurchasingCycle.productSpecificationId,
                    purchasingCycle: this.formPurchasingCycle.purchasingCycle
                };
                api.updatePurchasingCycle(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogPurchasingCycleVisible = false;
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleSubmitPrice() {
                if (this.formPrice.newPrice === '') {
                    return this.$notify.error('售价不能为空');
                } else if (isNaN(this.formPrice.newPrice)) {
                    return this.$notify.error('售价必须为数字');
                } else if (parseFloat(this.formPrice.newPrice) > 999999999.9) {
                    return this.$notify.error('售价不能大于999999999.9');
                } else if (parseFloat(this.formPrice.newPrice) < 0) {
                    return this.$notify.error('售价不能为负数');
                } else if (this.formPrice.newPrice.toString().split('.').length > 1 && this.formPrice.newPrice.toString().split('.')[1].length > 1) {
                    return this.$notify.error('售价最多只能保留一位小数点');
                }
                const param = {
                    productList: [{
                        newPrice: this.formPrice.newPrice,
                        oldPrice: this.formPrice.price,
                        productSpecificationId: this.formPrice.productSpecificationId
                    }]
                };
                api.priceNoteAdd(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogPriceVisible = false;
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleSubmitBuffer() {
                const param = {
                    specificationId: this.formBuffer.productSpecificationId,
                    cityId: Util.getCookie('cityId'),
                    bufferQuantity: this.formBuffer.bufferQuantity
                };
                api.updateBufferQuantity(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogBufferVisible = false;
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleSubmitWarehouseLocation() {
                const data = this.formWarehouseLocation;
                const param = {
                    productId: data.productId,
                    productName: data.name,
                    productSpecificationId: data.productSpecificationId,
                    specificationName: data.specificationName,
                    warehouseLocationMaster: data.warehouseLocationMaster,
                    warehouseLocationSlave: data.warehouseLocationSlave
                };
                // window.LOG(this.formWarehouseLocation, param);
                api.warehouseLocationInsert(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogWarehouseLocationVisible = false;
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            // 提交表单
            handleSubmitWarehousing(type) {
                const param = {
                    subType: this.formWarehousing.type,
                    productList: [{
                        productBasicInfoId: this.formWarehousing.productBasicInfoId,
                        productBasicSpecificationId: this.formWarehousing.productBasicSpecificationId,
                        productSpecificationId: this.formWarehousing.productSpecificationId,
                        num: this.formWarehousing.num,
                        comment: this.formWarehousing.comment
                        // cost: this.formWarehousing.cost
                    }]
                };
                if (type === 'In') {
                    param.type = '1';
                } else {
                    param.type = '2';
                }
                api['adjustStockFor' + type + 'Stock'](param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogWarehousingAddVisible = false;
                        this.dialogWarehousingDescVisible = false;
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleShowProductSales(data) {
                this.formSales = Object.assign(this.formSales, data);
                this.dialogSalesVisible = true;
                this.handleSalesSearch();
            },
            // 点击删除按钮
            handleRemove(data) {
                window.LOG(data);
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        productSpecificationId: data.productSpecificationId
                    };
                    api.productSpecificationDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.listSpecificationPageInfo();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(err => {
                    window.LOG(err);
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.listSpecificationPageInfo();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.listSpecificationPageInfo();
            },
            handlePurchasingCycle(data) {
                window.LOG(data);
                this.formPurchasingCycle = data;
                this.dialogPurchasingCycleVisible = true;
            },
            handleThresholdValue(data) {
                this.formThreshold = JSON.parse(JSON.stringify(data));
                this.dialogThresholdVisible = true;
            },
            handleSortNo(data) {
                this.formSortNo = JSON.parse(JSON.stringify(data));
                this.dialogSortNoVisible = true;
            },
            handlePrice(data) {
                this.formPrice = data;
                this.dialogPriceVisible = true;
            },
            handlePromotionRemove(data) {
                console.log(data);
            },
            // 更改促销导购状态
            handleChangePromition(data) {
                console.log(data.promotionZoneList.join(','));
                const param = {
                    productId: data.productId,
                    promotionZone: data.promotionZoneList.join(','),
                    specificationId: data.productSpecificationId,
                    cityId: Util.getCookie('cityId')
                };
                api.updatePromotionZone(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.listSpecificationPageInfo();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
                // window.LOG(param);
            },
            // 主备城市修改
            handleSubmitBackupCity() {
                const param = {
                    masterCityId: Util.getCookie('cityId'),
                    slaveCityIds: this.formBackupCity.selected.map(item => item.slaveCityId || item.id).join(',')
                };

                api.cityMasterSlaveInsert(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogBackupCityVisible = false;
                    }
                });
            },
            // 获取省份列表
            getProvince() {
                return apiCommon.userProvince().then(res => {
                    if (res.code === 100) {
                        this.provinceArr = res.content;
                    }
                });
            },
            getCity(id) {
                // this.formBatchPrice.cityId = '';
                this.latestForm.cityId = '';
                this.formBackupCity.cityId = '';

                if (!this.formBackupCity.provinceId && !id) {
                    return;
                }
                const param = {
                    provinceId: this.formBackupCity.provinceId || id
                };
                return apiCommon.userCity(param).then(res => {
                    if (res.code === 100) {
                        this.cityArr = res.content.filter(item => {
                            const res = item.id !== parseInt(Util.getCookie('cityId'));
                            return res;
                        });
                        this.formBackupCity.cityId = '';
                    }
                });
            },
            rowClickBackupCity() {
                let cityName = '';
                this.cityArr.forEach(item => {
                    if (item.id === this.formBackupCity.cityId) {
                        cityName = item.name;
                    }
                });
                if (cityName === '') {
                    return this.$notify.error('城市获取错误');
                }
                let has = false;
                this.formBackupCity.selected.forEach(item => {
                    if (cityName === item.slaveCityName) {
                        has = true;
                    }
                });
                if (!has) {
                    const slaveCityNameList = {
                        slaveCityName: cityName,
                        slaveCityId: this.formBackupCity.cityId
                    };
                    this.formBackupCity.selected.push(slaveCityNameList);
                }
            },
            handleClickBackupCity(data) {
                this.formBackupCity.selected = this.formBackupCity.selected.filter(item => item.slaveCityId !== data.slaveCityId);
            },
            getByMasterCityId() {
                const param = {
                    masterCityId: Util.getCookie('cityId')
                };
                api.getByMasterCityId(param).then(res => {
                    if (res.code === 100) {
                        if (res.content != null) {
                            this.formBackupCity.selected = res.content;
                        }
                    }
                });
            },
            // 获取数据
            listSpecificationPageInfo(excel) {
                if (!this.G.permission['_product/cityProductSpecification/pages']) {
                    return this.$notify.error('没有列表权限');
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                param.cityId = Util.getCookie('cityId');
                param.searchString = this.searchString ? this.searchString : undefined;
                param.onStockSelect = this.onStock ? this.onStock : undefined;
                param.promotionZone = this.promotionZone ? this.promotionZone : undefined;
                param.isAppDisplay = this.isAppDisplay ? this.isAppDisplay : undefined;
                param.isSplit = this.isSplit ? this.isSplit : undefined;
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                param.inventoryMin = this.inventoryMin ? this.inventoryMin : undefined;
                param.inventoryMax = this.inventoryMax ? this.inventoryMax : undefined;
                if (this.suppliers.length > 0) {
                    param.supplierIds = this.suppliers.join(',');
                }
                if (param.inventoryMin && param.inventoryMax) {
                    if (isNaN(param.inventoryMin) || isNaN(param.inventoryMax)) {
                        return this.$notify.error('库存范围必须为数字');
                    } else if (Number(param.inventoryMin) > Number(param.inventoryMax)) {
                        return this.$notify.error('库存下限不得大于上限');
                    }
                } else if (param.inventoryMin && !param.inventoryMax) {
                    if (isNaN(param.inventoryMin)) {
                        return this.$notify.error('库存范围必须为数字');
                    }
                } else if (!param.inventoryMin && param.inventoryMax) {
                    if (isNaN(param.inventoryMax)) {
                        return this.$notify.error('库存范围必须为数字');
                    }
                }
                if (excel === true) {
                    this.getExcel(param);
                }
                api.listSpecificationPageInfo(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            item.lowestPrice = !item.productAvailable ? '待上架' : item.lowestPrice;

                            item.showAvailable = item.productSpecificationId != null ? item.available === 0 ? '待上架' : '已上架' : '';

                            item.viewCost = Util.precisionMath(item.cost);
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
            },
            sortChange(column) {
                console.log(column + '-' + column.prop + '-' + column.order);
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.listSpecificationPageInfo();
            },
            getExcel(param) {
                for (const item in param) {
                    if (param.hasOwnProperty(item)) {
                        if (param[item] === undefined) {
                            param[item] = '';
                        }
                    }
                }
                return location.href = window.vars.URLApiBase + '/product/cityProductSpecification/exportExcel?' + Util.qs(param);
            },
            handleListSupplier() {
                supplier.getSupplier().then(res => {
                    if (res.code === 100 && res.content) {
                        this.supplierList = res.content;
                    }
                });
            },

            handleShowSupplierDetail(row) {
                this.supplierDialog.citySpecificationId = row.productSpecificationId;
                this.supplierDialog.cityProductId = row.productId;
                this.supplierDialog.isSplit = row.isSplit;
                this.supplierDialog.visible = true;
            }
        },
        created() {
            this.handleListSupplier();
            this.checkColumnShow();
            this.listSpecificationPageInfo();
            this.cityName = Util.getCookie('cityName');
        }
    };
</script>
