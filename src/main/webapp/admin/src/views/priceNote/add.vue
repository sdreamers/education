<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="$router.push({ name: '/priceNote/index' })">取消</el-button>
                <el-button type="primary" style="float: right;margin-left: 10px;" @click.stop="handleSubmit">立即提交</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20">
                        <el-button type="primary" @click="handleAddToTable">添加商品</el-button>
                </el-row>
                <el-table :data="tableData" border>
                    <el-table-column prop="selectedSpeIndex" label="序号">
                        <template slot-scope="scope">
                            {{scope.row.selectedSpeIndex}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="商品名称">
                        <template slot-scope="scope">
                            <el-select
                                v-model="scope.row.productId"
                                filterable
                                remote
                                reserve-keyword
                                placeholder="请输入关键词"
                                :remote-method="handleProductSearch"
                                @change="handleProductSearchSelect($event,scope.row)"
                                @focus="handleProductSearchFocus(scope.row)">
                                <el-option
                                v-for="item in scope.row.productList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column prop="productSpecificationId" label="商品规格">
                        <template slot-scope="scope">
                            <el-select
                                v-model="scope.row.productSpecificationId"
                                placeholder="商品规格"
                                @change="handleSpecSelect($event, scope.row)">
                                <el-option
                                v-for="item in scope.row.productSpecificationVOList"
                                :key="item.id"
                                :label="item.specificationName"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>

                    <el-table-column prop="weight" label="重量"></el-table-column>
                    <el-table-column prop="unit" label="单位"></el-table-column>
                    <el-table-column prop="cost" label="成本单价"></el-table-column>
                    <el-table-column prop="price" label="当前售价"></el-table-column>
                    <el-table-column prop="newPrice" label="新售价">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.newPrice" placeholder="新售价"></el-input>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
        </el-card>
    </section>
</template>
<script>
import api from '@/api/priceNote';
import { warehouseByUserCurrentCity } from '@/api/dict';
export default {
    data() {
        return {
            search: {
                warehouseId: ''
            },
            keywordList: [],
            addressList: [],

            currentRow: {},

            tableData: [],

            warehouseList: [],

            shipTime: '',
            shipTimeList: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 5
        };
    },
    methods: {
        initProductObj() {
            let selectedSpeIndex = this.tableData.length;
            selectedSpeIndex++;
            return {
                selectedSpeIndex: selectedSpeIndex,
                productId: '',
                productSpecificationId: '',
                weight: '',
                unit: '',
                cost: '',
                price: '',
                newPrice: ''
            };
        },
        handleAddToTable() {
            if (!this.G.permission['_stock/priceNote/add']) {
                return this.$notify.error('没有权限');
            }
            this.tableData.push(this.initProductObj());
        },
        // 点击删除按钮
        handleRemove(data) {
            this.tableData = this.tableData.filter(item => item.selectedSpeIndex !== data.selectedSpeIndex);
            this.tableData = this.tableData.map((item, index) => {
                item.selectedSpeIndex = index + 1;
                return item;
            })
        },
        initData() {
            for (const item in this.currentRow) {
                if (this.currentRow.hasOwnProperty(item)) {
                    // const element = this.currentRow[item];
                    if (item !== 'selectedSpeIndex' && item !== 'productId' && item !== 'productSpecificationVOList' && item !== 'productList') {
                        this.currentRow[item] = '';
                    }
                }
            }
        },
        handleProductSearch(data) {
            if (!data) {
                return;
            }
            this.initData();
            const param = {
                warehouseId: this.search.warehouseId,
                available: 1,
                name: data
            };
            this.currentRow.productList = [];
            api.productListProduct(param).then(res => {
                if (res.code === 100) {
                    this.$set(this.currentRow, 'productList', res.content);
                    this.tableData = this.tableData.map(item => item);
                }
            });
        },
        handleProductSearchSelect(event) {
            // let specList = data.productList.filter(item => item.id === event)[0];
            // specList = specList.productSpecificationVOList || [];
            const param = {
                productId: event,
                available: 1
            };
            this.initData();
            api.selectSpecification(param).then(res => {
                if (res.code === 100) {
                    this.$set(this.currentRow, 'productSpecificationVOList', res.content);
                }
            });
            // window.LOG('handleProductSearchSelect', this.currentRow, data);
            // this.$set(this.currentRow, 'productSpecificationVOList', specList);
            // this.tableData = this.tableData.map(item => item);
        },
        handleProductSearchFocus(data) {
            window.LOG('handleProductSearchFocus', data);
            this.currentRow = data;
        },
        handleSpecSelect(id, data) {
            const info = data.productSpecificationVOList.filter(item => item.id === id)[0];
            window.LOG(info, data, id);
            data.weight = info.weight;
            data.unit = info.unit;
            data.cost = info.cost;
            data.price = info.price;
        },
        handleSubmit() {
            if (!this.G.permission['_stock/priceNote/add']) {
                return this.$notify.error('没有权限');
            }
            const tableData = this.tableData;
            if (tableData.length === 0) {
                return this.$notify.error('未添加商品');
            }
            for (let i = 0; i < tableData.length; i++) {
                const element = tableData[i];
                if (element.productId === '' || element.productSpecificationId === '' || element.newPrice === '') {
                    return this.$notify.error(`第${i + 1}行信息填写不完整`);
                }
                if (isNaN(element.newPrice)) {
                    return this.$notify.error(`第${i + 1}行的新售价必须填写数字`)
                }
            }
            const productList = [];
            tableData.forEach(item => {
                productList.push({
                    productId: item.productId,
                    productSpecificationId: item.productSpecificationId,
                    cost: item.cost,
                    oldPrice: item.price,
                    newPrice: item.newPrice
                });
            });
            const param = {
                productList,
                warehouseId: this.search.warehouseId
            };
            api.priceNoteAdd(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$router.push({ name: '/priceNote/index' });
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
                    window.LOG(this.warehouseList);
                }
            });
        }
    },
    created() {
        this.warehouseByUserCurrentCity();
    }
};
</script>
