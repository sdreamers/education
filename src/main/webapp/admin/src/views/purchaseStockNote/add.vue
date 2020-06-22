<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <el-select v-model="search.warehouseId" v-if="false" placeholder="冻库" @change="handleWarehouseChange">
                            <el-option v-for="(item,key) in warehouseList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <!--<el-col :span="3">
                        <el-input v-model="search.shippingPrice" placeholder="运费标准（元/吨）" @input="calTotalCost"></el-input>
                    </el-col>-->
                   <!-- <el-col :span="3">
                        <el-select
                            v-model="search.supplierId"
                            filterable
                            placeholder="请选择供应商">
                            <el-option
                            v-for="item in supplierList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                            </el-option>
                        </el-select>
                        &lt;!&ndash; <el-input v-model="txtQuery" placeholder="商户关键字"></el-input> &ndash;&gt;
                    </el-col>
                    <el-col :span="3">
                        <el-select
                            v-model="search.purchasePaymentMethod"
                            placeholder="支付方式">
                            <el-option
                            v-for="item in purchasePaymentMethodList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>-->
                    <!--<el-col :span="3">
                        <el-input v-model="search.otherExpenses" placeholder="其它金额" @input="calTotalCost"></el-input>
                    </el-col>-->
                    <el-col :span="9">
                        <el-button type="primary" @click="handleAddToTable">添加商品</el-button>
                        <!--<el-button type="success" @click="handleShowInsert">获取订单商品</el-button>-->
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column prop="selectedSpeIndex" label="序号">
                        <template slot-scope="scope">
                            {{scope.row.selectedSpeIndex}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="productBasicInfoId" label="商品名称" min-width="230px">
                        <template slot-scope="scope">
                            <!-- <span>{{scope.row[column.key]}}</span> -->
                            <el-select
                                style="width:100%"
                                v-model="scope.row.productBasicInfoId"
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
                    <el-table-column prop="productBasicSpecificationId" label="规格名称" min-width="230px">
                        <template slot-scope="scope">
                            <el-select
                                style="width:100%"
                                v-model="scope.row.productBasicSpecificationId"
                                placeholder="规格名称"
                                @change="handleSpecSelect($event, scope.row)">
                                <el-option
                                v-for="item in scope.row.productSpecificationVOList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>

                    <el-table-column prop="weight" label="重量" min-width="100px"></el-table-column>
                    <el-table-column prop="unit" label="单位" min-width="100px"></el-table-column>
                    <el-table-column prop="quantity" label="库存" min-width="100px"></el-table-column>
                    <el-table-column prop="num" label="入库数量" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" pattern="\d*" maxlength="11" v-model="scope.row.num" @input="calTotalCost"></el-input>
                        </template>
                    </el-table-column>
                    <!--<el-table-column prop="purchasePrice" label="采购单价" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" pattern="\d*" maxlength="11" v-model="scope.row.purchasePrice" @input="calTotalCost" @blur="handleCheckPurchase(scope.row)"></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="price" label="线上售价" min-width="100px"></el-table-column>
                    <el-table-column prop="viewCost" label="成本单价(元)(采购单价+运费+平摊其他费用)" min-width="110px">
                        <template slot="header" slot-scope="scope">
                            <el-tooltip class="item" effect="dark" content="(采购单价+运费+平摊其他费用)" placement="top">
                                <el-button type="text">成本单价(元)<i class="el-icon-info" style="margin-left:5px"></i></el-button>
                            </el-tooltip>
                        </template>
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.viewCost" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="totalPrice" label="总金额（元）" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.totalPrice" readonly></el-input>
                        </template>
                    </el-table-column>-->
                    <el-table-column prop="comment" label="备注" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.comment" maxlength="100"></el-input>
                        </template>
                    </el-table-column>
                   <!-- <el-table-column prop="cost" label="成本单价" min-width="100px"></el-table-column>
                    <el-table-column prop="lastStockPrice" label="上次采购单价" min-width="100px"></el-table-column>-->
                    <el-table-column label="操作" min-width="130px">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                            <el-button
                            type="text"
                            @click.stop="handleInsert(scope.row)">插入商品</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <el-row style="margin-top: 20px;">
                    <el-col :span="6">采购总数：{{sumStock}}</el-col>
                    <el-col :span="6">总吨数：{{sumWeight}}</el-col>
                </el-row>
                <el-row style="margin-top: 20px;">
                    <el-button type="primary" style="float: right;" @click.stop="handleSubmit(1)">提交</el-button>
                   <!-- <el-button style="margin-right: 10px;float: right;" @click.stop="handleSubmit(0)">保存草稿</el-button>-->
                    <el-button style="float: right;margin-right: 10px;" @click.stop="handleBack">返回</el-button>
                </el-row>
            </el-row>
        </el-card>
        <el-dialog
            title="获取订单商品"
            :visible.sync="dialogOrderVisible"
            :close-on-click-modal="false"
            width="30%">
            <el-input v-model="orderNo" placeholder="输入订单号，根据订单填充采购单"></el-input>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogOrderVisible = false,orderNo = ''">取 消</el-button>
                <el-button type="primary" @click="handleOrderSubmit">确 定</el-button>
            </span>
        </el-dialog>
    </section>
</template>
<script>
import api from '@/api/purchaseStockNote';
import apiCommon from '@/api/common';
import Util from '@/libs/util';
import { purchasePaymentMethod, warehouseByUserCurrentCity } from '@/api/dict';

const form = {
    unitName: ''
};
export default {
    data() {
        return {
            orderNo: '',
            search: {
                warehouseId: 1,
                warehouseIdOld: '',
                shippingPrice: 0,
                supplierId: 1,
                purchasePaymentMethod: '1',
                otherExpenses: 0,
                imageURLs: []
            },
            keywordList: [],
            addressList: [],

                // 七牛表单
            qiniuForm: {},

            supplierList: [],

            purchasePaymentMethodMap: {},
            purchasePaymentMethodList: [],

            currentRow: {},

            tableData: [],

            warehouseList: [],

            shipTime: '',
            shipTimeList: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            loading: false,

            dialogFormVisible: false,
            dialogOrderVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formType: '',
            rules: {
                unitName: [
                    { required: true, message: '序号不能为空', trigger: 'blur' }
                ]
            },

            sumWeight: 0,
            sumStock: 0,
            sumShippingPrice: 0,
            sumTotalPrice: 0
        };
    },
    methods: {
        handleShowInsert() {
            if (this.search.warehouseId === '') {
                return this.$notify.error('未选择冻库');
            }
            if (this.search.shippingPrice === '') {
                return this.$notify.error('请输入运费');
            }
            if (this.search.supplierId === '') {
                return this.$notify.error('请选择供应商');
            }
            if (this.search.purchasePaymentMethod === '') {
                return this.$notify.error('请选择支付方式');
            }
            if (this.search.otherExpenses === '') {
                return this.$notify.error('请输入其它费用');
            }
            this.dialogOrderVisible = true;
            this.orderNo = '';
        },
        // 获取数据
        handleOrderSubmit() {
            if (this.search.warehouseId === '') {
                return this.$notify.error('未选择冻库');
            }
            if (this.orderNo.trim() === '') {
                return this.$notify.error('请输入订单号');
            }
            this.$confirm('根据订单信息添加采购单时，会将已经填写的采购单覆盖', '确认').then(() => {
                const param = {
                    orderNo: this.orderNo
                };
                api.getOrderInfoNotContainRefund(param).then(res => {
                    if (res.code === 100) {
                    // this.search = JSON.parse(JSON.stringify(res.content));
                        const data = res.content;
                    // this.handleAddingSearch(this.search.merchantKeyword);
                        if (!data.productList || data.productList.length < 1) {
                            return this.$notify.info('订单已取消');
                        }
                        this.tableData = data.productList.map((item, index) => {
                            item.selectedSpeIndex = index + 1;
                            item.uuid = Math.random().toString(32).substring(2);


                            item.productSpecificationVOList.forEach(ele => {
                                ele.name = ele.specificationName;
                                ele.cost = Util.precisionMath(ele.cost);
                            });
                            const info = item.productSpecificationVOList.filter(ele => ele.id === item.specificationId)[0];
                            item.productList = [{ id: info.productBasicInfoId, name: item.name }];
                            item.productBasicInfoId = info.productBasicInfoId;
                            item.productSpecificationVOList = [{ id: info.productBasicSpecificationId, name: info.name }];
                            item.productBasicSpecificationId = info.productBasicSpecificationId;
                            item.productName = item.name;
                            item.specificationName = info.name;
                            this.initDataItem(item);
                            item.num = item.purchaseNum;
                            item.purchasePrice = item.sellingPrice;
                            const param = {
                                productBasicInfoId: info.productBasicInfoId,
                                productBasicSpecificationId: info.productBasicSpecificationId,
                                warehouseId: this.search.warehouseId
                            };
                            api.selectSpecificationInfoByBasicId(param).then(res => {
                                window.LOG(res);
                                if (res.code === 100) {
                                    const content = res.content;
                                    item.weight = content.weight;
                                    item.unit = content.unit;
                                    item.quantity = content.quantity;
                                    item.cost = Util.precisionMath(content.cost);
                                    item.lastStockPrice = content.lastStockPrice;
                                    item.price = content.price;
                                    this.calTotalCost();
                                }
                            });
                            return item;
                        })

                        this.dialogOrderVisible = false;
                    }
                });
            })
        },
        handleWarehouseChange(data) {
            if (this.tableData.length) {
                if (data !== this.search.warehouseIdOld) {
                    return this.$confirm('如果重新选择冻库，已经填写的采购单将清空').then(() => {
                        this.search.warehouseIdOld = data;
                        this.tableData = [];
                        this.calTotalCost();
                    }).catch(() => {
                        this.search.warehouseId = this.search.warehouseIdOld;
                    })
                }
            }
            this.search.warehouseIdOld = data;
        },
        handleCheckPurchase(data) {
            if (data.purchasePrice) {
                window.LOG(data.purchasePrice, data.cost);
                console.log(data)
                if (parseFloat(data.purchasePrice) > parseFloat(data.lastStockPrice)) {
                    this.$notify.info('采购单价高于最近采购单价');
                } else if (parseFloat(data.purchasePrice) < parseFloat(data.lastStockPrice)) {
                    this.$notify.info('采购单价低于最近采购单价');
                }
            }
        },
        // 详情图片上传到七牛云成功
        handleDetailSuccess(data) {
            const fileName = this.qiniuForm.domain + data.key;
            let imageURLs = this.search.imageURLs;
            if (!imageURLs) {
                imageURLs = [];
            }
            imageURLs.push({ name: fileName, url: fileName });
            this.$set(this.search, 'imageURLs', imageURLs);
        },
        handleDetailPreview(data) {
            window.open(data.url);
        },
        // 移除详情图
        handleDetailRemove(data) {
            const url = data.url;
            const imageURLs = this.search.imageURLs.filter(item => item.url !== url);
            this.$set(this.search, 'imageURLs', imageURLs);
        },
        // 图片上传到七牛云之前
        handleBeforeUpload() {
            const param = {
                fileName: new Date().getTime() + '_' + Math.random().toString(32).substring(2)
            };
            return apiCommon.getQiniuTokenTemp()
            .then(res => {
                if (res.code === 100) {
                    const content = res.content;
                    this.qiniuForm = {
                        key: param.fileName,
                        token: content.uploadToken,
                        domain: content.domain
                    };
                } else {
                    this.$notify.error('图片上传失败： token 获取失败');
                }
            })
            .catch(err => {
                this.$notify.error('图片上传失败： token 获取失败');
                window.LOG('图片上传失败： token 获取失败', err);
            });
        },
        calTotalCost() {
            console.log(1);
            let shippingPrice = this.search.shippingPrice;
            let otherExpenses = this.search.otherExpenses;
            if (!shippingPrice) {
                shippingPrice = 0;
            }
            if (!otherExpenses) {
                otherExpenses = 0;
            }
            let sumWeight = 0;
            let sumStock = 0;
            let sumShippingPrice = 0;
            let sumTotalPrice = 0;
            // 总价
            this.tableData.forEach(item => {
                if (item.purchasePrice && item.num && item.weight) {
                    sumShippingPrice += parseFloat(shippingPrice) / 1000 * parseFloat(item.weight) * parseFloat(item.num);
                    sumWeight += parseFloat(item.weight) / 1000 * parseFloat(item.num);
                    sumStock += parseFloat(item.num);
                }
            });
            this.sumStock = sumStock.toFixed(2);
            this.sumWeight = Util.precisionMath(sumWeight, 2);
            sumShippingPrice = Util.precisionMath(sumShippingPrice, 2);
            this.sumShippingPrice = sumShippingPrice;

            this.tableData.map(item => {
                let cost = 0;
                // 如果没重量或者没入库数量或者没采购单价，则成本单价和总金额为0
                if (!item.purchasePrice || !item.num || !item.weight) {
                    item.totalCost = 0;
                    item.sumTotalPrice = 0;
                } else {
                    // 入库数量和采购单价全部保留2位小数
                    item.purchasePrice = Util.keepTwoFloatNum(item.purchasePrice);
                    item.num = Util.keepTwoFloatNum(item.num);
                    // 如果总重量为0
                    if (sumWeight === 0) {
                        cost = parseFloat((parseFloat(item.purchasePrice) + parseFloat(shippingPrice) / 1000 * parseFloat(item.weight)).toFixed(2));
                    } else {
                        cost = parseFloat((parseFloat(item.purchasePrice) + (parseFloat(item.weight) / 1000 * parseFloat(shippingPrice)) + (parseFloat(item.weight) / 1000 / sumWeight * otherExpenses)).toFixed(2));
                    }
                    // 展示成本价保留两位小数
                    item.viewCost = Util.precisionMath(cost, 2);
                    item.totalCost = cost;
                    const money = cost * parseFloat(item.num);
                    item.totalPrice = Util.precisionMath(money);
                    console.log(item.totalPrice);
                    sumTotalPrice = Util.precisionMath(parseFloat(sumTotalPrice) + parseFloat(money));
                }
                return item;
            });
            this.sumTotalPrice = sumTotalPrice;
        },
        initProductObj() {
            let selectedSpeIndex = this.tableData.length;
            selectedSpeIndex++;
            return {
                productBasicInfoId: '',
                productList: [],
                productBasicSpecificationId: '',
                productSpecificationVOList: [],
                weight: '',
                unit: '',
                num: '',
                purchasePrice: '',
                cost: '',
                totalPrice: '',
                comment: '',
                lastStockPrice: '',
                selectedSpeIndex: selectedSpeIndex,
                uuid: Math.random().toString(32).substring(2)
            };
        },
        // 添加商品
        handleAddToTable() {
            if (this.search.warehouseId === '') {
                return this.$notify.error('未选择冻库');
            }
            if (this.search.shippingPrice === '') {
                return this.$notify.error('请输入运费');
            }
            if (this.search.supplierId === '') {
                return this.$notify.error('请选择供应商');
            }
            if (this.search.purchasePaymentMethod === '') {
                return this.$notify.error('请选择支付方式');
            }
            if (this.search.otherExpenses === '') {
                return this.$notify.error('请输入其它费用');
            }
            this.tableData.push(this.initProductObj());
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.formType = 'edit';
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        // 点击删除按钮
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('确认删除？').then(() => {
                this.tableData = this.tableData.filter(item => item.uuid !== data.uuid);
                this.tableData.map((item, index) => {
                    item.selectedSpeIndex = index + 1;
                    return item;
                });
                this.calTotalCost();
            }).catch(() => {
                console.log('取消');
            })
            // 重新计算
        },
        handleInsert(data) {
            let index = 0;
            this.tableData.forEach((item, i) => {
                if (item.uuid === data.uuid) {
                    index = i;
                }
                return item;
            });
            const initObj = this.initProductObj();
            this.tableData.splice(index + 1, 0, initObj);
            this.tableData.map((item, index) => {
                item.selectedSpeIndex = index + 1;
                return item;
            });
            // console.log(arr);
        },
        searchProduct: function(data) {
            const param = {
                warehouseId: this.search.warehouseId,
                isDisplay: 1,
                name: data
            };
            this.currentRow.productList = [];
            api.listProductBasicInfo(param).then(res => {
                if (res.code === 100) {
                    this.$set(this.currentRow, 'productList', res.content);
                    this.tableData = this.tableData.map(item => item);
                }
            });
        },
        // 根据输入关键字去接口拿商品列表
        handleProductSearch(data) {
            if (data === this.currentRow.productName) {
                return;
            }
            this.initDataItem(this.currentRow, true, true);
            if (!data) {
                return;
            }
            this.searchProduct(data);
        },
        initDataItem(data, isProduct, isProductName) {
            if (isProductName) {
                // data.productBasicInfoId = '';
                data.productList = [];
            }
            if (isProduct) {
                data.productBasicSpecificationId = '';
                data.productSpecificationVOList = [];
            }
            data.weight = '';
            data.unit = '';
            data.quantity = '';
            data.num = '';
            data.purchasePrice = '';
            data.viewCost = '';
            data.totalPrice = '';
            data.comment = '';
            data.cost = '';
            data.lastStockPrice = '';
            data.price = '';
        },

        // 选中商品时加载规格
        handleProductSearchSelect(event, data) {
            const that = data.productList.filter(item => item.id === event)[0];
            data.productName = that.name;
            return api.selectSpecification({ productBasicInfoId: that.id }).then(res => {
                if (res.code === 100) {
                    this.initDataItem(data, true);
                    this.calTotalCost();

                    data.productBasicSpecificationId = '';
                    data.productSpecificationVOList = res.content;
                }
            });
        },
        // 点击商品名称输入框时，拿到该行数据，以确认后续修改时，修改的是哪行数据
        handleProductSearchFocus(data) {
            window.LOG('handleProductSearchFocus', data);
            if (data.productName) {
                this.searchProduct(data.productName);
            }
            this.currentRow = data;
        },
        // 选中规格时，加载规格的信息
        handleSpecSelect(event, data) {
            const info = data.productSpecificationVOList.filter(item => item.id === event)[0];
            data.specificationName = info.name;
            const param = {
                productBasicInfoId: data.productBasicInfoId,
                productBasicSpecificationId: data.productBasicSpecificationId,
                warehouseId: this.search.warehouseId
            };

            this.initDataItem(data);
            this.calTotalCost();

            return api.selectSpecificationInfoByBasicId(param).then(res => {
                window.LOG(res);
                if (res.code === 100) {
                    const content = res.content;
                    data.weight = content.weight;
                    data.unit = content.unit;
                    data.quantity = content.quantity;
                    data.cost = Util.precisionMath(content.cost, 2);
                    data.lastStockPrice = content.lastStockPrice;
                    data.price = content.price;
                }
            });
        },
        handleSubmit(status) {
            if (this.search.warehouseId === '') {
                return this.$notify.error('未选择冻库');
            }
            if (this.search.shippingPrice === '') {
                return this.$notify.error('请输入运费');
            }
            if (Number(this.search.shippingPrice) < 0) {
                return this.$notify.error('运费不得小于0');
            }
            if (this.search.supplierId === '') {
                return this.$notify.error('请选择供应商');
            }
            if (this.search.purchasePaymentMethod === '') {
                return this.$notify.error('请选择支付方式');
            }
            if (this.search.otherExpenses === '') {
                return this.$notify.error('请输入其它费用');
            }
            if (Number(this.search.otherExpenses) < 0) {
                return this.$notify.error('其它费用不得小于0');
            }
            if (this.tableData.length === 0) {
                return this.$notify.error('未添加商品');
            }
            const tableData = this.tableData;
            for (let i = 0; i < tableData.length; i++) {
                const item = tableData[i];

                if (!item.productBasicInfoId) {
                    return this.$notify.error(`未选择第${i + 1}行商品，如无需添加，请移除该条记录`);
                }
                if (!item.productBasicSpecificationId) {
                    return this.$notify.error(`未选择第${i + 1}行规格，如无需添加，请移除该条记录`);
                }
                if (!item.num || parseFloat(item.num) <= 0) {
                    return this.$notify.error('入库数量输入错误，入库数量必须大于0，如无需添加，请移除该条记录');
                }
            }

            const productList = [];
            this.tableData.forEach(item => {
                productList.push({
                    productBasicInfoId: item.productBasicInfoId,
                    productBasicSpecificationId: item.productBasicSpecificationId,
                    num: item.num,
                    cost: item.totalCost,
                    price: item.price,
                    comment: item.comment,
                    purchasePrice: item.purchasePrice,
                    unit: item.unit,
                    productName: item.productName,
                    specificationName: item.specificationName,
                    lastStockPrice: item.lastStockPrice
                })
            })
            const param = {
                otherExpenses: this.search.otherExpenses,
                productCostPrice: this.sumTotalPrice,
                purchasePaymentMethod: this.search.purchasePaymentMethod,
                shippingPrice: this.search.shippingPrice,
                status: status,
                supplierId: this.search.supplierId,
                totalWeight: this.sumWeight,
                warehouseId: this.search.warehouseId,
                productList: productList,
                imageURLs: this.search.imageURLs.map(item => item.url).join(',')
            };
            api.purchaseStockNoteInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$router.push({ name: '/stock/purchaseStockNote/index' });
                }
            });
        },
        handleBack() {
            this.$confirm('若返回，当前页面的内容将不会被保存', '确认返回？').then(() => {
                this.$router.push({ name: '/stock/purchaseStockNote/index' });
            }).catch(() => false)
        },
        getSupplier() {
            api.getSupplier().then(res => {
                if (res.code === 100) {
                    this.supplierList = res.content;
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
        },
        purchasePaymentMethod() {
            return purchasePaymentMethod().then(res => {
                if (res.code === 100) {
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.purchasePaymentMethodList = arr;
                    this.purchasePaymentMethodMap = res.content;
                }
            });
        }
    },
    created() {
        this.getSupplier();
        this.purchasePaymentMethod();
        this.warehouseByUserCurrentCity();
    }
};
</script>
