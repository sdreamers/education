<template>
    <section class="view-content">
        <el-card shadow="none">
                <common :list="headerList"></common>
                <!-- <i class="el-icon-menu"></i> -->
                <!-- {{G.pageTitle}} -->

            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <p style="font-size:14px;">关键字</p>
                            <el-input v-model="searchInput" placeholder="关键字：商品名称，品牌，产地，商品名称中文拼音首字母(区分多音字，请多次尝试)"></el-input>
                        </el-col>
                        <el-col :span="8">
                            <p style="font-size:14px;">录入日期</p>
                            <el-date-picker
                                v-model="searchDate"
                                type="daterange"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :default-time="['00:00:00','23:59:59']"
                                @change="productBasicInfoPagers()"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="录入开始日期"
                                end-placeholder="录入结束日期">
                            </el-date-picker>
                        </el-col>
                        <el-col :span="4">
                            <p style="font-size:14px;">显示状态</p>
                            <el-select v-model="isDisplay" placeholder="显示状态" @change="productBasicInfoPagers()">
                                <el-option
                                v-for="item in isDisplayArr"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <p style="font-size:14px;">搜索</p>
                            <div style="width:150px">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                            </div>
                        </el-col>
                        <el-col :span="4">
                            <p style="font-size:14px;">添加</p>
                            <el-button @click="handleAdd" v-if="G.permission['_product/productBasicInfo/insert']">添加记录</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading" @row-click="rowClick" @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'productPreviewImageurl'" style="width: 60px;height: 60px;" :src="scope.row.productPreviewImageURL" />
                                <span v-if="column.key !== 'productPreviewImageurl'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="显示/隐藏">
                            <template slot-scope="scope">
                                <section style="text-align:center">
                                    <el-switch
                                        v-model="scope.row.isDisplay === '1'"
                                        active-color="#13ce66"
                                        inactive-color="#ff4949">
                                    </el-switch>
                                </section>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productBasicInfo/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productBasicInfo/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column label="添加规格">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productBasicSpecification/insert']"
                                @click.stop="handleAddSpec(scope.row)">添加规格</el-button>
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

                    <el-table :data="tableDataSpec" border v-loading="loadingSpec">
                        <el-table-column v-for="(column,key) in columnsSpec" :prop="column.key" :label="column.title" :key="key">
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productBasicSpecification/update']"
                                @click.stop="handleEditSpec(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_product/productBasicSpecification/delete']"
                                @click.stop="handleRemoveSpec(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pager-wrapper">
                        <el-pagination
                        @size-change="handleSizeChangeSpec"
                        @current-change="handleCurrentChangeSpec"
                        :current-page.sync="currentPageSpec"
                        :page-sizes="[2, 5, 10, 20]"
                        :page-size="pageSizeSpec"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPageSpec">
                            <span class="el-pagination__total refresh" @click="handleCurrentChangeSpec(currentPageSpec)"><i class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
            </el-row>
        </el-card>
        <editProduct
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,productBasicInfoPagers(1)"
            :brandList="brandList"
            :areaList="areaList"
            :fatherList="fatherList"
            :splitList="splitList"
            :formType="formType"
            :productMerchantTypeList="productMerchantTypeList"
            :rules="rules"
            :formLabelWidth="formLabelWidth"
            :form="form"></editProduct>
        <editSpecification
            v-if="dialogFormVisibleSpec"
            :formSpec="formSpec"
            :rules="rules"
            :formLabelWidth="formLabelWidth"
            :unitList="unitList"
            :formType="formType"
            :productBasicInfoId="productBasicInfoId"
            @close="handleSpecClose"></editSpecification>
            <similarProduct
            v-if="dialogSimilarProductVisible"
            :productId="similarProductId"
            @close="dialogSimilarProductVisible=false"></similarProduct>
    </section>
</template>
<script>
import api from '@/api/product';
import apiCommon from '@/api/common';
import common from '@/components/common';
import headerList from '@/config/headerList';
import similarProduct from './similarProduct';

import editProduct from './editProduct';
import editSpecification from './editSpecification';

const columns = [
  // { key: 'productNo', title: '编号' },
  { key: 'createDateTime', title: '录入时间' },
  { key: 'name', title: '商品名称' },
  { key: 'categoryName', title: '品类' }
];
const columnsSpec = [
  { key: 'sortNo', title: '序号' },
  { key: 'productSpecificationNo', title: '编号' },
  { key: 'specificationName', title: '商品规格' },
  { key: 'weight', title: '重量（KG）' },
  { key: 'unit', title: '单位' }
];
const formSpec = {
    sortNo: '',
    productSpecificationNo: '',
    specificationName: '',
    weight: '',
    unitId: ''
};
const form = {
    name: '',
    productNo: '',
    factoryName: '',
    saveMethod: '',
    qualityGuaranteePeriod: '',
    remark: '',
    brandId: '',
    areaId: '',
    categoryFatherId: '',
    categoryId: '',
    isSplit: '',
    searchKey: '',
    productPreviewImageURL: '',
    productDetailImageURLs: [],
    inspectionReportImageURLs: [],
    productMerchantTypeIds: []
};
const pickerOptions = {
    shortcuts: [{
        text: '最近一周',
        onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
        }
    }, {
        text: '最近一个月',
        onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
        }
    }, {
        text: '最近三个月',
        onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
        }
    }]
};
export default {
    components: {
        editProduct,
        editSpecification,
        common,
        similarProduct
    },
    data() {
        return {
            headerList: headerList,


            pickerOptions: pickerOptions,
            searchInput: '',
            searchDate: [],
            isDisplayArr: [
                    { value: '', label: '全部' },
                    { value: '0', label: '隐藏' },
                    { value: '1', label: '显示' }
            ],
            isDisplay: '',

            tableData: [],
            columns: columns,

            tableDataSpec: [],
            columnsSpec: columnsSpec,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            currentPageSpec: 1,
            totalPageSpec: 0,
            pageSizeSpec: 5,

            loading: false,
            loadingSpec: false,

            dialogFormVisible: false,
            dialogFormVisibleSpec: false,
            dialogSimilarProductVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formSpec: JSON.parse(JSON.stringify(formSpec)),
            formType: '',
            rules: {
                name: [
                        { required: true, message: '不能为空', trigger: 'blur' }
                ],
                qualityGuaranteePeriod: [
                        { required: true, message: '有效期不能为空且只能为数字', trigger: 'blur' }
                ]
            },

            productBasicInfoId: '',

                // 一级品类列表
            fatherList: [],
                // 产地列表
            areaList: [],
                // 品牌列表
            brandList: [],
                // 单位列表
            unitList: [],
                // 菜系专区
            productMerchantTypeList: [],
            splitList: [
                    { id: '0', name: '否' },
                    { id: '1', name: '是' }
            ],
            similarProductId: ''
        };
    },
    methods: {
        handleSpecClose(data) {
            this.dialogFormVisibleSpec = false;
            this.productBasicInfoPagers(1);
            console.log(data);
            if (data && data.productBasicInfoId) {
                this.specificationID = data.productBasicInfoId;
                this.productBasicSpecificationPagers();
            }
        },
            // 点击搜索按钮
        handleSearch() {
            this.currentPage = 1;
            this.productBasicInfoPagers();
        },
            // 清除搜索条件
        handleClear() {
            this.searchInput = '';
            this.currentPage = 1;
            this.searchDate = [];
            this.isDisplay = '';
            this.productBasicInfoPagers();
        },
            // 添加基本信息
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.productCategoryBasic();
            this.productArea();
            this.productBrand();
            this.productMerchantType();
            this.dialogFormVisible = true;
        },
            // 添加规格
        handleAddSpec(data) {
            window.LOG(data);
            this.productBasicInfoId = data.id;
            this.formType = 'add';
            if (data.isSplit === '1') {
                formSpec.isSplit = true;
            } else {
                formSpec.isSplit = false;
            }
            this.formSpec = JSON.parse(JSON.stringify(formSpec));
            this.productUnitListForSelect();
            this.dialogFormVisibleSpec = true;
        },
            // 点击编辑基本信息按钮
        handleEdit(data) {
            this.formType = 'edit';
            this.productArea();
            this.productBrand();
            const formData = JSON.parse(JSON.stringify(data));
            if (formData.areaName === null) {
                formData.areaId = '';
            }
            if (formData.brandName === null) {
                formData.brandId = '';
            }
            if (formData.productDetailImageURLs === '') {
                formData.productDetailImageURLs = [];
            } else {
                formData.productDetailImageURLs = formData.productDetailImageURLs.split(',').map(item => ({ name: item, url: item }));
            }
            if (formData.inspectionReportImageURLs === '') {
                formData.inspectionReportImageURLs = [];
            } else {
                formData.inspectionReportImageURLs = formData.inspectionReportImageURLs.split(',').map(item => ({ name: item, url: item }));
            }
            this.form = JSON.parse(JSON.stringify(formData));
            this.productCategoryBasic();
            this.productMerchantType();
            this.dialogFormVisible = true;
        },
            // 点击编辑规格按钮
        handleEditSpec(data) {
            this.formType = 'edit';
            if (data.relationNum) {
                data.isSplit = true;
            } else {
                data.isSplit = false;
            }
            this.productUnitListForSelect().then(() => {
                let has = false;
                this.unitList.forEach(item => {
                    if (item.id === data.unitId) {
                        has = true;
                    }
                })
                if (!has) {
                    data.unitId = '';
                }
                this.formSpec = JSON.parse(JSON.stringify(data));
                if (this.formSpec.weight !== undefined) {
                    this.formSpec.weight = this.formSpec.weight.toString();
                }
                this.dialogFormVisibleSpec = true;
            });
        },
        handleSimilarProduct(data) {
            console.log('handleSimilarProduct-->data', data);
            this.similarProductId = data.id;
            this.dialogSimilarProductVisible = true;
        },
            // 删除规格
        handleRemoveSpec(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.productBasicSpecificationDelete(param).then(res => {
                    window.LOG(res);
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.specificationID = data.productBasicInfoId;
                        this.productBasicSpecificationPagers();
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
            // 删除基本信息
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                this.productBasicInfoDelete(param).then(() => {
                    this.productBasicInfoPagers();
                });
            }).catch(err => {
                window.LOG(err);
                this.$notify({
                    type: 'info',
                    message: '取消'
                });
            });
        },
            // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.productBasicInfoPagers(1);
        },
            // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.productBasicInfoPagers(1);
        },
            // 规格页码改变
        handleSizeChangeSpec(pageSize) {
            this.pageSizeSpec = pageSize;
            this.productBasicSpecificationPagers();
        },
            // 规格翻页
        handleCurrentChangeSpec(currentPage) {
            this.currentPageSpec = currentPage;
            this.productBasicSpecificationPagers();
        },
            // 获取规格，或者显示隐藏
        rowClick(row, event, column) {
            const id = row.id;
            this.productBasicInfoId = id;
            if (column.label === '显示/隐藏') {
                row.isDisplay = row.isDisplay === '1' ? '0' : '1';
                const isDisplay = row.isDisplay;
                const productBasicInfoId = id;
                api.productBasicInfoUpdateProductBasicInfoDisplayStatus({ productBasicInfoId, isDisplay }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.productBasicInfoPagers();
                    } else {
                            // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
            this.currentPageSpec = 1;

            this.specificationID = id;
            this.productBasicSpecificationPagers();
        },
            // 获取父级品类列表
        productCategoryBasic() {
            const param = {
                fatherId: 0
            };
            apiCommon.productCategoryBasic(param).then(res => {
                if (res.code === 100) {
                    this.fatherList = res.content;
                } else {
                    this.$notify.error('服务器似乎开了点小差');
                }
            });
        },

        // 获取父级品类列表
        productMerchantType() {
            apiCommon.listProductMerchantType().then(res => {
                if (res.code === 100) {
                    this.productMerchantTypeList = res.content;
                } else {
                    this.$notify.error('服务器似乎开了点小差');
                }
            });
        },

            // 删除基本信息
        productBasicInfoDelete(data) {
            return api.productBasicInfoDelete(data).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message);
                } else {
                    this.$notify.warning(res.message || '发生未知错误');
                }
                return res;
            });
        },
            // 获取单位
        productUnitListForSelect() {
            return api.productUnitListForSelect().then(res => {
                if (res.code === 100) {
                    this.unitList = res.content;
                } else {
                        // this.$notify.error(res.message || '未知错误');
                }
            });
        },
            // 获取产地
        productArea() {
            apiCommon.productArea().then(res => {
                if (res.code === 100) {
                    this.areaList = res.content;
                } else {
                    this.$notify.error('服务器似乎开了点小差');
                }
            });
        },
            // 获取品牌
        productBrand() {
            apiCommon.productBrand().then(res => {
                if (res.code === 100) {
                    this.brandList = res.content;
                } else {
                    this.$notify.error('服务器似乎开了点小差');
                }
            });
        },
            // 获取规格
        productBasicSpecificationPagers() {
            if (!this.G.permission['_productBasicSpecification/pagers']) {
                return this.$notify.error('没有获取规格权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPageSpec;
            param.limit = this.pageSizeSpec;
            if (this.specificationID) {
                param.productBasicInfoId = this.specificationID;
            }
            this.loadingSpec = true;
            console.log(param);
            api.productBasicSpecificationPagers(param).then(res => {
                window.LOG(res);

                this.loadingSpec = false;
                this.tableDataSpec = res.records;
                this.totalPageSpec = res.total;
            });
        },
            // 获取基本信息列表
        productBasicInfoPagers(initPage) {
            if (!this.G.permission['_product/productBasicInfo/pages']) {
                return this.$notify.error('没有获取商品权限');
            }
            this.tableDataSpec = [];
            const param = {
                page: 1,
                limit: 5
            };
            if (initPage !== undefined) {
                param.page = this.currentPage;
                param.limit = this.pageSize;
            }
            if (this.searchInput) {
                param.keyword = this.searchInput;
            }
            param.isDisplay = this.isDisplay;
            if (this.searchDate && this.searchDate.length) {
                param.startTime = this.searchDate[0];
                param.endTime = this.searchDate[1];
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.productBasicInfoPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    if (item.createDateTime) {
                        const date = new Date();
                        date.setYear(parseInt(item.createDateTime.substring(0, 4), 10));
                        date.setMonth(parseInt(item.createDateTime.substring(4, 6) - 1, 10));
                        date.setDate(parseInt(item.createDateTime.substring(6, 8), 10));
                        item.createDateTime = date.customFormat('#YYYY#-#MM#-#DD#');
                    }
                    item.productMerchantTypeIds = item.productMerchantTypeIds === null ? [] : item.productMerchantTypeIds;
                    return item;
                });
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.productBasicInfoPagers();
        }
    },
    created() {
        this.productCategoryBasic();
        this.productBasicInfoPagers();
        this.productMerchantType();
    }
};
</script>
