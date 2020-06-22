<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-form :inline="true">
                <el-row class="mb20" :gutter="20">
                    <el-form-item label="冻库">
                        <el-select v-model="search.warehouseId" placeholder="冻库" @change="handleWarehouseChange">
                            <el-option v-for="(item,key) in warehouseList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="运费（元/吨）">
                        <el-input v-model="search.shippingPrice" placeholder="运费标准（元/吨）" @input="calTotalCost"></el-input>
                    </el-form-item>
                    <el-form-item label="供应商">
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
                        <!-- <el-input v-model="txtQuery" placeholder="商户关键字"></el-input> -->
                    </el-form-item>
                    <el-form-item label="支付方式">
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
                    </el-form-item>
                    <el-form-item label="其它金额">
                        <el-input v-model="search.otherExpenses" placeholder="其它金额" @input="calTotalCost"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleAddToTable">添加商品</el-button>
                        <el-button type="success" @click="handleShowInsert">获取订单商品</el-button>
                    </el-form-item>
                </el-row>
                </el-form>
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
                                @change="handleProductSearchSelect($event,scope.row,true)"
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
                                @change="handleSpecSelect($event, scope.row,true)">
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
                            <el-input type="text" v-model="scope.row.num" maxlength="11" @input="calTotalCost"></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="purchasePrice" label="采购单价" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.purchasePrice" maxlength="11" @input="calTotalCost"></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="price" label="线上售价" min-width="100px"></el-table-column>
                    <el-table-column prop="totalCost" label="成本单价(元)(采购单价+运费+平摊其他费用)" min-width="100px">
                        <template slot="header" slot-scope="scope">
                            <el-tooltip class="item" effect="dark" content="(采购单价+运费+平摊其他费用)" placement="top">
                                <el-button type="text">成本单价(元)<i class="el-icon-info" style="margin-left:5px"></i></el-button>
                            </el-tooltip>
                        </template>
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.totalCost" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="totalPrice" label="总金额（元）" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.totalPrice" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="comment" label="备注" min-width="100px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.comment" maxlength="100"></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cost" label="成本单价" min-width="100px"></el-table-column>
                    <el-table-column prop="lastStockPrice" label="上次采购单价" min-width="100px"></el-table-column>
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
                    <el-col :span="6">总运费：{{sumShippingPrice}}</el-col>
                    <el-col :span="6">采购总金额：{{sumTotalPrice}}</el-col>
                </el-row>
                <el-row style="margin-top: 20px;">
                    <h5>上传图片：</h5>
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        list-type="picture-card"
                        :data="qiniuForm"
                        :on-success="handleDetailSuccess"
                        :file-list="search.imageURLs"
                        :on-preview="handleDetailPreview"
                        :on-remove="handleDetailRemove"
                        :before-upload="handleBeforeUpload">
                        <i class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-row>
                <el-row style="margin-top: 20px;">
                    <el-button type="primary" style="float: right;margin-left: 10px;" @click.stop="handleSubmit(1)" v-if="G.permission['_stock/purchaseStockNote/update']">提交审核</el-button>
                    <el-button style="float: right;margin-left: 10px;" @click.stop="handleSubmit(0)" v-if="G.permission['_stock/purchaseStockNote/update']">保存草稿</el-button>
                    <el-button style="float: right;margin-right: 10px;" @click.stop="handleBack">返回</el-button>
                </el-row>
            </el-row>
        </el-card>
        <el-dialog
            title="提示"
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
                    warehouseId: '',
                    warehouseIdOld: '',
                    shippingPrice: '',
                    supplierId: '',
                    purchasePaymentMethod: '',
                    otherExpenses: ''
                },
                keywordList: [],
                addressList: [],

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

                dialogOrderVisible: false,
                dialogFormVisible: false,
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
                sumTotalPrice: 0,


            // 七牛表单
                qiniuForm: {}
            };
        },
        methods: {
        // 获取数据
            handleOrderSubmit() {
                if (this.search.warehouseId === '') {
                    return this.$notify.error('未选择冻库');
                }
                this.$confirm('根据订单信息添加采购单时，会将已经填写的采购单覆盖', '确认').then(() => {
                    const param = {
                        orderNo: this.orderNo
                    };
                    api.getOrderInfoNotContainRefund(param).then(res => {
                        if (res.code === 100) {
                            const data = res.content;
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
                                console.log(item.sellingPrice);

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
            // 详情图片上传到七牛云成功
            handleDetailSuccess(data, file, list) {
                const fileName = this.qiniuForm.domain + data.key;

                list[list.length - 1].url = fileName;
                list[list.length - 1].key = fileName;

                this.search.imageURLs = list;
            },
            handleDetailPreview(data) {
                window.open(data.url);
            },
            // 移除详情图
            handleDetailRemove(data, list) {
                this.$set(this.search, 'imageURLs', list);
            },
            // 图片上传到七牛云之前
            handleBeforeUpload() {
                const param = {
                    // fileName: file.name
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

                this.tableData = this.tableData.map(item => {
                    let cost = 0;
                    if (!item.purchasePrice || !item.num || !item.weight) {
                        item.totalCost = 0;
                        item.totalPrice = 0;
                    } else {
                        item.purchasePrice = Util.keepTwoFloatNum(item.purchasePrice);
                        item.num = Util.keepTwoFloatNum(item.num);
                        if (sumWeight === 0) {
                            cost = parseFloat((parseFloat(item.purchasePrice) + parseFloat(shippingPrice) / 1000 * parseFloat(item.weight)).toFixed(2));
                        } else {
                            cost = parseFloat((parseFloat(item.purchasePrice) + (parseFloat(item.weight) / 1000 * parseFloat(shippingPrice)) + (parseFloat(item.weight) / 1000 / sumWeight * otherExpenses)).toFixed(2));
                        }
                        // 展示成本价保留两位小数
                        item.totalCost = Util.precisionMath(cost, 2);

                        const money = cost * parseFloat(item.num);
                        console.log(money);
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
                })
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
                const param = {
                    warehouseId: this.search.warehouseId,
                    available: 1,
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
                data.totalCost = '';
                data.totalPrice = '';
                data.comment = '';
                data.cost = '';
                data.lastStockPrice = '';
                data.price = '';
            },

            // 选中商品时加载规格
            handleProductSearchSelect(event, data, clean) {
                const that = data.productList.filter(item => item.id === event)[0];
                data.productName = that.name;
                if (clean) {
                    this.initDataItem(data);
                    this.calTotalCost();
                }
                // data.weight = 0;
                // data.unit = 0;
                // data.quantity = 0;
                // data.cost = 0;
                // data.lastStockPrice = 0;
                // data.num = 0;
                // data.purchasePrice = 0;
                // data.totalCost = 0;
                // data.totalPrice = 0;
                return api.selectSpecification({ productBasicInfoId: that.id }).then(res => {
                    if (res.code === 100) {
                        data.productBasicSpecificationId = '';
                        // data.productSpecificationVOList = res.content;
                        this.$set(data, 'productSpecificationVOList', res.content)
                    }
                });
            },
            // 点击商品名称输入框时，拿到该行数据，以确认后续修改时，修改的是哪行数据
            handleProductSearchFocus(data) {
                window.LOG('handleProductSearchFocus', data);
                this.currentRow = data;
            },
            // 选中规格时，加载规格的信息
            handleSpecSelect(event, data, clean) {
                const info = data.productSpecificationVOList.filter(item => item.id === event)[0];
                if (info) {
                    data.specificationName = info.name;
                }

                if (clean) {
                    this.initDataItem(data);
                    this.calTotalCost();
                }

                const param = {
                    productBasicInfoId: data.productBasicInfoId,
                    productBasicSpecificationId: data.productBasicSpecificationId,
                    warehouseId: this.search.warehouseId
                };
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
                    return this.$notify.error('运费不得小于0');
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
                    if (!item.purchasePrice || parseFloat(item.purchasePrice) <= 0) {
                        return this.$notify.error('请输入大于0的成本单价，保留两位小数，如无需添加，请移除该条记录');
                    }
                    if (!item.num || parseFloat(item.num) <= 0) {
                        return this.$notify.error('入库数量输入错误，入库数量必须大于0，如无需添加，请移除该条记录');
                    }
                }
                const param = {
                    stockNoteId: this.$route.params.id,
                    otherExpenses: this.search.otherExpenses,
                    productCostPrice: this.sumTotalPrice,
                    purchasePaymentMethod: this.search.purchasePaymentMethod,
                    shippingPrice: this.search.shippingPrice,
                    status: status,
                    supplierId: this.search.supplierId,
                    totalWeight: this.sumWeight,

                    warehouseId: this.search.warehouseId,
                    productList: this.tableData.map(item => {
                        item.cost = item.totalCost;
                        return item;
                    }),
                    imageURLs: this.search.imageURLs.map(item => item.url).join(',')
                };
                api.purchaseStockNoteUpdate(param).then(res => {
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
                })
            },
            purchaseStockNoteGet(id) {
                const param = {
                    id
                };
                api.purchaseStockNoteGet(param).then(async res => {
                    window.LOG(res);
                    if (res.code === 100) {
                        const data = res.content;
                        this.search.warehouseId = data.warehouseId.toString();
                        this.search.shippingPrice = data.shippingPrice;
                        this.search.supplierId = data.supplierId;
                        this.search.purchasePaymentMethod = data.purchasePaymentMethod;
                        this.search.otherExpenses = data.otherExpenses;
                        this.search.imageURLs = data.imageurls ? data.imageurls.split(',').map(item => {
                            let url = item;
                            if (url.indexOf('temp') === -1) {
                                url = 'https://temp.dongpinyun.cn/' + url;
                            }
                            item = {};
                            item.key = url;
                            item.url = url;
                            return item;
                        }) : [];
                        this.tableData = data.purchaseStockNoteDetailInfoVOList.map((item, index) => {
                            item.selectedSpeIndex = index + 1;
                            item.uuid = Math.random().toString(32).substring(2);
                            item.productList = [{ id: item.productBasicInfoId, name: item.productName }];
                            const productBasicSpecificationId = item.productBasicSpecificationId;
                            this.handleProductSearchSelect(item.productBasicInfoId, item).then(() => {
                                item.productBasicSpecificationId = productBasicSpecificationId;
                                this.handleSpecSelect(productBasicSpecificationId, item).then(() => {
                                    this.calTotalCost();
                                });
                                return item;
                            });
                            return item;
                        })
                    } else {
                        setTimeout(() => {
                            this.$router.push({ name: '/stock/purchaseStockNote/index' });
                        }, 233);
                    }
                });
            }
        },
        async created() {
            await Promise.all([
                this.getSupplier(),
                this.purchasePaymentMethod(),
                this.warehouseByUserCurrentCity()
            ]).then(() => {
                const id = this.$route.params.id;
                this.stockNoteId = id;
                if (!id) {
                    return this.$notify.error('id 异常');
                }
                this.purchaseStockNoteGet(id);
            });
        }
};
</script>
