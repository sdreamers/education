<template>
    <!-- 添加商品到活动弹层 -->
    <el-dialog :close-on-click-modal="false" title="填写活动商品信息" :visible="true" @close="$emit('close')">
        <el-row :gutter="20">
            <el-col :span="6">
                <!-- <el-input v-model="formAdd.productName" type="text" auto-complete="off" placeholder="商品名称" :fetch-suggestions="handleAddingSearch"></el-input> -->
                <p>商品</p>
                <el-select
                    v-model="formAdd.productName"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="请输入关键词"
                    :remote-method="handleAddingSearch"
                    @change="handleAddingSearchSelect">
                    <el-option
                    v-for="item in keywordList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    </el-option>
                </el-select>
            </el-col>
            <el-col :span="6">
                <p>规格</p>
                <el-select v-model="formAdd.specificationId" placeholder="请选择商品规格" :disabled="!formAdd.productName">
                    <el-option v-for="(item,key) in specificationList" :label="item.specificationName" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-col>
            <el-col :span="6">
                <el-button
                type="primary"
                @click.stop="handleAddToActivity" style="margin-top:20px" :disabled="!formAdd.specificationId">加入活动</el-button>
            </el-col>
        </el-row>
        <el-table :data="tableDataAdding" border style="margin-top:20px">
            <el-table-column v-for="(column,key) in columnsAdding" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="活动单价">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.activityPrice" type="text" auto-complete="off"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="活动库存">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.stockNum" type="text" auto-complete="off"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                    type="text"
                    @click.stop="handleAddingRemove(scope.row)">移除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click.stop="handleAddingConfirm">确定</el-button>
        </div>
    </el-dialog>
</template>
<script>
const columnsAdding = [
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '商品规格' },
    { key: 'price', title: '商品原单价' },
    { key: 'quantity', title: '商品库存' }
];
import api from '@/api/activity';
import { warehouseByUserCurrentCity } from '@/api/dict';
export default {
    props: ['activityId', 'activityCode'],
    data() {
        return {
            formAdd: {
                productId: '',
                specificationId: ''
            },
            warehouseList: [],
            keywordList: [],
            specificationList: [],

            tableDataAdding: [],
            columnsAdding: columnsAdding
        };
    },
    methods: {
        // 选中搜索下拉选项
        handleAddingSearchSelect(data) {
            this.formAdd.productId = data;
            this.formAdd.specificationId = '';
            const param = {
                productId: data,
                available: 1
            };
            api.productSpecificationSelectSpecification(param).then(res => {
                if (res.code === 100) {
                    this.specificationList = res.content;
                } else {
                    this.specificationList = [];
                }
            });
        },
        handleAddingSearch(query) {
            if (query === '') {
                return this.keywordList = [];
            }
            if (!query) {
                return;
            }
            const param = {
                // warehouseId: this.formAdd.warehouseId,
                available: 1,
                name: query
            };
            api.productListProduct(param).then(res => {
                if (res.code === 100) {
                    this.keywordList = res.content;
                } else {
                    this.keywordList = [];
                }
            });
        },

        // 选中商品规格后加入活动
        handleAddToActivity() {
            const productId = this.formAdd.productId;
            const specificationId = this.formAdd.specificationId;

            // 装填商户信息
            const productArr = this.keywordList.filter(item => item.id === productId);
            const cityProduct = productArr[0];
            const productName = cityProduct.name;
            const brandId = cityProduct.brandId;
            const brandName = cityProduct.brandName;
            const areaId = cityProduct.areaId;
            const areaName = cityProduct.areaName;
            const categoryId = cityProduct.categoryId;
            const categoryName = cityProduct.categoryName;
            const factoryName = cityProduct.factoryName;
            const qualityGuaranteePeriod = cityProduct.qualityGuaranteePeriod;
            const saveMethod = cityProduct.saveMethod;
            const sale = cityProduct.sale;
            const remark = cityProduct.remark;
            const productPreviewImageURL = cityProduct.productPreviewImageURL;
            const productDetailImageURLs = cityProduct.productDetailImageURLs;

            // 装填规格信息
            const selectedArr = this.specificationList.filter(item => item.id === specificationId);
            const citySpecification = selectedArr[0];
            const specificationName = citySpecification.specificationName;
            const price = citySpecification.price || 0;
            const quantity = citySpecification.quantity || 0;
            const weight = citySpecification.weight || 0;
            const originalPrice = citySpecification.price;
            const unit = citySpecification.unit;
            const cost = citySpecification.cost;

            if (!productName) {
                return this.$notify.error('未能获取商品名');
            }
            if (!specificationName) {
                return this.$notify.error('未能获取规格名');
            }

            for (let index = 0; index < this.tableDataAdding.length; index++) {
                const element = this.tableDataAdding[index];
                if (element.specificationId === this.formAdd.specificationId) {
                    return this.$notify.error('已经添加过改商品规格');
                }
            }

            this.tableDataAdding.push({
                productId,
                productName,
                specificationId,
                specificationName,
                price,
                quantity,
                weight,
                activityPrice: '',
                stockNum: '',
                brandId,
                brandName,
                areaId,
                areaName,
                categoryId,
                categoryName,
                factoryName,
                qualityGuaranteePeriod,
                saveMethod,
                sale,
                remark,
                originalPrice,
                productPreviewImageURL,
                productDetailImageURLs,
                unit,
                cost
            });
        },
        // 活动添加商品时，暂时移除
        handleAddingRemove(data) {
            this.tableDataAdding = this.tableDataAdding.filter(item => item.specificationId !== data.specificationId);
        },
        // 确定添加商品到活动
        handleAddingConfirm() {
            console.log('handleAddingConfirm')
            const param = this.tableDataAdding.map(item => {
                item.activityId = this.activityId;
                item.activityCode = this.activityCode;
                return item;
            });
            for (let index = 0; index < param.length; index++) {
                const element = param[index];
                if (element.activityPrice === '') {
                    return this.$notify.error(`第${index + 1}行未填写价格`);
                }
                if (element.stockNum === '') {
                    return this.$notify.error(`第${index + 1}行未填写库存`);
                }
                if (element.activityPrice === '' || element.activityPrice < 0 || isNaN(element.activityPrice)) {
                    return this.$notify.error(`第${index + 1}行价格不合法`);
                }
                if (element.activityPrice > 999999999.99) {
                    this.$notify.error(`第${index + 1}行活动单价不能大于 999999999.99`);
                    return;
                }
                if (element.stockNum === '' || element.stockNum < 0 || isNaN(element.stockNum)) {
                    return this.$notify.error(`第${index + 1}行库存不合法`);
                }
                if (Math.floor(Number(element.stockNum)) !== Number(element.stockNum)) {
                    return this.$notify.error(`第${index + 1}行库存须为正整数`);
                }
            }
            api.activityProductInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                }
            });
        },
        warehouseByUserCurrentCity() {
            return warehouseByUserCurrentCity().then(res => {
                if (res.code === 100) {
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
        }
    },
    created() {
        this.warehouseByUserCurrentCity();
    }
};
</script>
