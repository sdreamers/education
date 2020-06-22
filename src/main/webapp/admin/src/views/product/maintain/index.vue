<template>
    <section class="view-content">
        <el-button @click="handleToggleSidebar">{{sidebarText}}</el-button>
        <el-row :gutter="20">
            <el-col :span="sidebarWidth[0]" v-show="isSidebarShow">
                <el-card shadow="none">
                    <div slot="header" class="card-title">
                        <i class="el-icon-menu"></i>
                        商品分类
                    </div>
                    <el-row>
                        <el-tree lazy :load="handleCategoryClickLazy" @node-click="handleCategoryClick" isLeaf="leaf" :data="treeData" :props="defaultProps" :default-expanded-keys="[1]" highlight-current style="max-height:700px;overflow:auto;"></el-tree>
                    </el-row>
                </el-card>
            </el-col>
            <el-col :span="sidebarWidth[1]">
                <el-card shadow="none">
                    <div slot="header" class="card-title">
                        <i class="el-icon-menu"></i>
                        {{G.pageTitle}}
                    </div>
                    <el-row class="list-con clearfix">
                        <el-row class="mb20" :gutter="20">
                            <!--<el-col :span="3">
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
                            </el-col>-->
                            <el-col :span="4">
                                <p style="font-size:12px;">关键字：</p>
                                <el-input v-model="searchString" placeholder="商品名称，规格名称，类型，品牌，产地"></el-input>
                            </el-col>
                            <el-col :span="6">
                                <p style="font-size:12px;">搜索</p>
                                <el-button type="primary" @click="handleSearch">搜索</el-button>
                                <el-button @click="handleClear">清除</el-button>
                            </el-col>
                        </el-row>
                        <el-table :data="tableData" border @row-click="handleRowClick" @sort-change='sortChange'>
                            <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" v-if="columnsSelected.includes(column.key)" :width="column.width">
                                <template slot-scope="scope">
                                    <span v-if="!column.custom">{{scope.row[column.key]}}</span>
                                    <section v-if="column.key === 'isSplit'" style="text-align:center">
                                        <el-select v-model="scope.row.isSplit" placeholder="请选择分组" disabled>
                                            <el-option v-for="(item,key) in splitList" :label="item.name" :value="item.id" :key="key"></el-option>
                                        </el-select>
                                    </section>
                                    <section v-if="column.key === 'promotionZone'" style="text-align:center">
                                        <el-select v-model="scope.row.promotionZone" placeholder="请选择分组" @change="handleChangePromition(scope.row)">
                                            <el-option v-for="(item,key) in promitionList" :label="item.name" :value="item.id" :key="key"></el-option>
                                        </el-select>
                                    </section>
                                    <section v-if="column.key === 'showAddQuantity'" style="text-align:center">
                                        <el-button v-if="scope.row.productSpecificationId !== null" type="primary" @click="handleAddQuantity(scope.row)">增加</el-button>
                                    </section>
                                    <section v-if="column.key === 'showDecreaseQuantity'" style="text-align:center">
                                        <el-button v-if="scope.row.productSpecificationId !== null" type="primary" @click="handleDecreaseQuantity(scope.row)">减少</el-button>
                                    </section>
                                    <section v-if="column.key === 'showWarehouseLocation'" style="text-align:center">
                                        <el-button type="primary" @click="handleAdjustStorehouse(scope.row)">设置</el-button>
                                    </section>
                                    <section v-if="column.key === 'showBufferQuantity'" style="text-align:center">
                                        <el-button type="primary" @click="handleBuffer(scope.row)">修改</el-button>
                                    </section>
                                    <section v-if="column.key === 'showPrice'" style="text-align:center">
                                        <el-button type="primary" @click="handlePrice(scope.row)">修改</el-button>
                                    </section>
                                    <section v-if="column.key === 'thresholdValue'" style="text-align:center">
                                        <el-input v-model="scope.row.thresholdValue" readonly></el-input>
                                    </section>
                                    <section v-if="column.key === 'purchasingCycle'" style="text-align:center">
                                        <el-input v-model="scope.row.purchasingCycle" readonly></el-input>
                                    </section>
                                    <section v-if="column.key === 'showBtnAvailable' && scope.row.productSpecificationId != null" style="text-align:center">
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
                                    <section v-if="column.key === 'stockRecord'">
                                        <el-button type="primary" @click="handleShowStockRecord(scope.row)">查看</el-button>
                                    </section>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作">
                                <template slot-scope="scope">
                                    <!-- <el-button
                                    type="text"
                                    @click.stop="handleShowProductSales(scope.row)">销量</el-button> -->
                                    <el-button
                                    type="text"
                                    v-if="G.permission['_stock/productSpecification/delete']"
                                    @click.stop="handleRemove(scope.row)">删除</el-button>
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
            </el-col>
        </el-row>
        <inventoryIncr
            v-if="dialogWarehousingAddVisible"
            :formWarehousing="formWarehousing"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogWarehousingAddVisible=false,listSpecificationPageInfo()"></inventoryIncr>
        <inventoryDesc
            v-if="dialogWarehousingDescVisible"
            :formWarehousing="formWarehousing"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogWarehousingDescVisible=false,listSpecificationPageInfo()"></inventoryDesc>
        <warehouseLocation
            v-if="dialogWarehouseLocationVisible"
            :formWarehouseLocation="formWarehouseLocation"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogWarehouseLocationVisible=false,listSpecificationPageInfo()"></warehouseLocation>
        <buffer
            v-if="dialogBufferVisible"
            :formBuffer="formBuffer"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogBufferVisible=false,listSpecificationPageInfo()"></buffer>
        <price
            v-if="dialogPriceVisible"
            :formPrice="formPrice"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogPriceVisible=false,listSpecificationPageInfo()"></price>
        <threshold
            v-if="dialogThresholdVisible"
            :formThreshold="formThreshold"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogThresholdVisible=false,listSpecificationPageInfo()"></threshold>
        <purchase
            v-if="dialogPurchasingCycleVisible"
            :formPurchasingCycle="formPurchasingCycle"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogPurchasingCycleVisible=false,listSpecificationPageInfo()"></purchase>
        <batchCycle
            v-if="dialogBatchCycleVisible"
            :formBatchCycle="formBatchCycle"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogBatchCycleVisible=false,listSpecificationPageInfo()"></batchCycle>
        <stockRecord
            v-if="dialogStockRecordVisible"
            :formStockRecord="formStockRecord"
            :warehouseId="warehouseId"
            :formLabelWidth="formLabelWidth"
            @close="dialogStockRecordVisible=false,listSpecificationPageInfo()"></stockRecord>

    </section>
</template>
<script>
import api from '@/api/productMaintain';
import Util from '@/libs/util';
import warehouseApi from '@/api/warehouse';
import inventoryIncr from './inventoryIncr';
import inventoryDesc from './inventoryDesc';
import warehouseLocation from './warehouseLocation';
import buffer from './buffer';
import price from './price';
import threshold from './threshold';
import purchase from './purchase';
import batchCycle from './batchCycle';
// import sales from './sales';
import stockRecord from './stockRecord';

const columns = [
    { key: 'productSpecificationNo', title: '编号', sort: 'custom' },
    { key: 'name', title: '商品名称', sort: 'custom' },
    { key: 'specificationName', title: '规格名称' },
    { key: 'categoryName', title: '品类名称', sort: 'custom' },
    { key: 'weight', title: '重量（公斤）' },
    { key: 'unit', title: '单位' },
    { key: 'showAddQuantity', title: '增加库存', hide: !window.permission['_stock/adjustStockNote/adjustStockForInStock'] },
    { key: 'showDecreaseQuantity', title: '减少库存', hide: !window.permission['_stock/adjustStockNote/adjustStockForOutStock'] },
    { key: 'quantity', title: '库存' },
    { key: 'relationNum', title: '关系数', hide: !window.permission['_stock/productSpecification/updateRelation'] }
].filter(item => item.hide !== true);
const splitList = [
    { id: '1', name: '是' },
    { id: '0', name: '否' }
];
const searchSplitList = [
    { id: '', name: '全部' },
    { id: '1', name: '是' },
    { id: '0', name: '否' }
];
const onStockList = [
    { id: '', name: '全部' },
    { id: '0', name: '未上架' },
    { id: '1', name: '已上架' }
];
const promitionList = [
    { id: 'hot_sale', name: '热销产品' },
    { id: 'new_product', name: '新品上架' },
    { id: '', name: '无' }
];
const appDisplayList = [
    { id: '1', name: '显示' },
    { id: '0', name: '隐藏' },
    { id: '', name: '全部' }
];
const form = {
    name: ''
};
export default {
    components: {
        inventoryIncr,
        inventoryDesc,
        warehouseLocation,
        buffer,
        price,
        threshold,
        purchase,
        batchCycle,
        // batchPrice,
        // sales,
        stockRecord
    },
    data() {
        return {
            sidebarText: '显示商品分类',
            isSidebarShow: false,
            sidebarWidth: [6, 24],
            originTree: [],
            treeData: [],
            defaultProps: {
                children: 'children',
                label: 'name'
                // isLeaf: 'leaf'
            },
            productFatherCategoryId: '',
            productSpecificationId: '',
            productId: '',
            productBrandId: '',
            productCategoryId: '',
            merchantTypeId: '',
            productFCategoryId: '',
            sortBy: '',
            direction: '',

            searchString: '',

            onStock: '',
            onStockList: onStockList,

            promotionZone: '',

            isAppDisplay: '',
            appDisplayList: appDisplayList,

            isSplit: '',

            splitList: splitList,
            promitionList: promitionList,
            searchSplitList: searchSplitList,

            pickerType: 'date',
            pickerFormat: 'yyyy-MM-dd',

            tableData: [],
            columns: columns.map(item => {
                item.name = item.title;
                item.id = item.key;
                return item;
            }),
            columnsSelected: columns.map(item => item.key),

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            dialogWarehousingAddVisible: false,
            dialogWarehousingDescVisible: false,
            dialogWarehouseLocationVisible: false,
            dialogBufferVisible: false,
            dialogPriceVisible: false,
            dialogThresholdVisible: false,
            dialogPurchasingCycleVisible: false,
            dialogSalesVisible: false,
            dialogBatchCycleVisible: false,
            dialogStockRecordVisible: false,
            formLabelWidth: '120px',
            formWarehousing: JSON.parse(JSON.stringify(form)),
            formWarehouseLocation: {},
            formBuffer: {},
            formPrice: {},
            formThreshold: {},
            formPurchasingCycle: {},
            formBatchCycle: {},
            formBatchPrice: {},
            formSales: {
                dateType: 0
            },
            formStockRecord: {
                dateStart: '',
                dateEnd: ''
            },
            formType: ''
        };
    },
    methods: {
        handleToggleSidebar() {
            console.log(1);
            if (this.isSidebarShow) {
                this.sidebarWidth = [6, 24];
                this.sidebarText = '显示商品分类';
            } else {
                this.sidebarWidth = [6, 18];
                this.sidebarText = '隐藏商品分类';
            }
            this.isSidebarShow = !this.isSidebarShow;
        },
        initData() {
            this.productSpecificationId = '';
            this.productId = '';
            this.productFatherCategoryId = '';
            this.productBrandId = '';
            this.productCategoryId = '';
            this.merchantTypeId = '';
            this.productFCategoryId = '';
        },
        setData(node) {
            if (node.productSpecificationId) {
                this.productSpecificationId = node.productSpecificationId;
            }
            if (node.productId) {
                this.productId = node.productId;
            }
            if (node.productFatherCategoryId) {
                this.productFatherCategoryId = node.productFatherCategoryId;
            }
            if (node.productBrandId) {
                this.productBrandId = node.productBrandId;
            }
            if (node.productCategoryId) {
                this.productCategoryId = node.productCategoryId;
            }
            if (node.merchantTypeId) {
                this.merchantTypeId = node.merchantTypeId;
            }
            if (node.productFCategoryId) {
                this.productFCategoryId = node.productFCategoryId;
            }
        },
        handleCategoryClick(data) {
            this.initData();
            this.setData(data);
            this.listSpecificationPageInfo();
        },
        handleCategoryClickLazy(data, resolve) {
            this.initData();
            const node = data.data;
            this.setData(node);
            if (node.loadFlag) {
                this.listProductsMenu({
                    productCategoryId: node.productCategoryId,
                    productBrandId: node.productBrandId,
                    merchantTypeId: node.merchantTypeId,
                    warehouseId: this.$route.params.id
                }, resolve, node);
            } else {
                if (node.children) {
                    resolve(node.children)
                } else {
                    resolve([])
                }
            }
        },
        listProductsMenu(param, resolve, originData) {
            if (!this.G.permission['_stock/product/listProductsMenu']) {
                return this.$notify.error('没有获取商品详情菜单权限')
            }
            api.listProductsMenu(param).then(res => {
                console.log(res);
                if (res.code === 100) {
                    originData.loadFlag = false;
                    // resolve(res.content);
                    // resolve([])
                    const data = Util.transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'pId',
                        childrenKey: 'children'
                    }, res.content);
                    resolve(data)
                }
            })
        },
        handleColumnChange(data) {
            window.LOG(data);
            const columnKeys = this.columnsSelected.join(',');
            localStorage.setItem('columnKeysShow_productMaintain', columnKeys);
        },
        checkColumnShow() {
            const columnKeys = localStorage.getItem('columnKeysShow_productMaintain');
            window.LOG(columnKeys);
            if (columnKeys) {
                this.columnsSelected = columnKeys.split(',');
            }
        },
        handleSearch() {
            this.currentPage = 1;
            this.listSpecificationPageInfo();
        },
        // 清除搜索
        handleClear() {
            this.searchString = '';
            this.onStock = '';
            this.promotionZone = '';
            this.isAppDisplay = '';
            this.isSplit = '';
            this.currentPage = 1;
            this.productFatherCategoryId = '';
            this.listSpecificationPageInfo();
        },
        handleBatchCycle() {
            this.dialogBatchCycleVisible = true;
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
        updateOnSaleStatus(row) {
            const param = {
                productId: row.productId,
                productSpecificationId: row.productSpecificationId,
                available: row.available === 0 ? 1 : 0
            };
            api.updateOnSaleStatus(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.listSpecificationPageInfo();
                }
            });
        },
        handleRowClick(row, event, column) {
            if (column.label === '上下架') {
                this.updateOnSaleStatus(row);
            } else if (column.label === '显示库存表') {
                this.updateStockListDisplayStatus(row);
            } else if (column.label === '预警值') {
                this.handleThresholdValue(row);
            } else if (column.label === '采购周期（天）') {
                this.handlePurchasingCycle(row);
            }
        },
        // handleShowProductSales(data) {
        //     this.formSales = Object.assign(this.formSales, data);
        //     this.dialogSalesVisible = true;
        // },
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
            this.formPurchasingCycle = JSON.parse(JSON.stringify(data));
            this.dialogPurchasingCycleVisible = true;
        },
        handleThresholdValue(data) {
            window.LOG(data);
            this.formThreshold = JSON.parse(JSON.stringify(data));
            this.dialogThresholdVisible = true;
        },
        handleShowStockRecord(data) {
            this.formStockRecord = JSON.parse(JSON.stringify(data));
            this.dialogStockRecordVisible = true;
        },
        handlePrice(data) {
            this.formPrice = JSON.parse(JSON.stringify(data));
            this.dialogPriceVisible = true;
        },
        // 点击修改缓冲值
        handleBuffer(data) {
            this.formBuffer = JSON.parse(JSON.stringify(data));
            this.dialogBufferVisible = true;
        },
        // 点击仓位设置
        handleAdjustStorehouse(data) {
            this.formWarehouseLocation = JSON.parse(JSON.stringify(data));
            this.dialogWarehouseLocationVisible = true;
        },
        // 点击减少库存
        handleDecreaseQuantity(data) {
            this.formWarehousing = JSON.parse(JSON.stringify(data));
            this.formWarehousing.type = 'out_inventory_loss';
            this.dialogWarehousingDescVisible = true;
        },
        // 点击增加库存
        handleAddQuantity(data) {
            this.formWarehousing = JSON.parse(JSON.stringify(data));
            this.formWarehousing.type = 'in_inventory_profit';
            this.dialogWarehousingAddVisible = true;
        },
        // 更改促销导购状态
        handleChangePromition(data) {
            const param = {
                warehouseId: this.$route.params.id,
                productId: data.productId,
                promotionZone: data.promotionZone
            };
            api.updatePromotionZone(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.listSpecificationPageInfo();
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
            window.LOG(param);
        },
        // 获取数据
        listSpecificationPageInfo() {
            if (!this.G.permission['_stock/productSpecification/listSpecificationPageInfo']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize,
                warehouseId: this.$route.params.id
            };
            param.searchString = this.searchString ? this.searchString : undefined;
            param.onStock = this.onStock ? this.onStock : undefined;
            param.promotionZone = this.promotionZone ? this.promotionZone : undefined;
            param.isAppDisplay = this.isAppDisplay ? this.isAppDisplay : undefined;
            param.isSplit = this.isSplit ? this.isSplit : undefined;
            param.productFatherCategoryId = this.productFatherCategoryId ? this.productFatherCategoryId : undefined;
            param.productSpecificationId = this.productSpecificationId ? this.productSpecificationId : undefined;
            param.productId = this.productId ? this.productId : undefined;
            param.productBrandId = this.productBrandId ? this.productBrandId : undefined;
            param.productCategoryId = this.productCategoryId ? this.productCategoryId : undefined;
            param.merchantTypeId = this.merchantTypeId ? this.merchantTypeId : undefined;
            param.productFatherCategoryId = this.productFCategoryId ? this.productFCategoryId : undefined;
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.listSpecificationPageInfo(param).then(res => {
                window.LOG(res);

                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.No = item.categoryNo + '-' + item.categorySecondNo + '-' + item.productNo + '-' + item.productSpecificationNo;

                        item.lowestPrice = !item.productAvailable ? '待上架' : item.lowestPrice;

                        item.showAvailable = item.productSpecificationId != null ? item.available === 0 ? '待上架' : '已上架' : '';

                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.error('无数据');
                }
                this.totalPage = res.total;
            });
        },
        listProductCategoryMenu() {
            if (!this.G.permission['_stock/product/listProductCategoryMenu']) {
                return this.$notify.error('没有获取商品分类菜单栏权限')
            }
            api.listProductCategoryMenu().then(res => {
                if (res.code === 100) {
                    const data = res.content;
                    this.originTree = data;
                    this.treeData = Util.transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'pId',
                        childrenKey: 'children'
                    }, data);
                }
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.listSpecificationPageInfo();
        },

        /**
         * 判断当前城市与传过来的仓库ID是否匹配
         * */
        checkWarehouseAndCity() {
            return warehouseApi.getCityRelatedWarehouseList().then(res => {
                if (res.code === 100) {
                    const mate = res.content.some(item => Number(item.id) === Number(this.$route.params.id));
                    if (!mate) {
                        this.$router.push({ name: '/warehouse/index' });
                        return new Promise((resolve, reject) => reject());
                    }
                }
                return new Promise(resolve => resolve());
            });
        }
    },
    created() {
        // this.$route.params.id = this.$route.params.id;
        Promise.all([
            this.checkWarehouseAndCity()
        ]).then(() => {
            this.checkColumnShow();
            this.listSpecificationPageInfo();
            this.listProductCategoryMenu();
        });
    }
};
</script>
