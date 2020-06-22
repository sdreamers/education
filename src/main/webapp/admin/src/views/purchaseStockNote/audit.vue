<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button type="primary" style="float: right;margin-left: 10px;" @click.stop="handlePass()" v-if="G.permission['_purchaseStockNote/updatePassed']">审核通过</el-button>
                <el-button style="float: right;margin-left: 10px;" @click.stop="handleReject()" v-if="G.permission['_purchaseStockNote/updateRejected']">审批驳回</el-button>
                <el-button style="float: right;margin-left: 10px;" @click.stop="$router.push({ name: '/stock/purchaseStockNote/index' });">返回</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-select v-model="search.warehouseId" placeholder="冻库" readonly>
                            <el-option v-for="(item,key) in warehouseList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <el-input v-model="search.shippingPrice" placeholder="运费标准（元/吨）" readonly></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-select
                            v-model="search.supplierId"
                            filterable
                            readonly
                            placeholder="请选择供应商">
                            <el-option
                            v-for="item in supplierList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                            </el-option>
                        </el-select>
                        <!-- <el-input v-model="txtQuery" placeholder="商户关键字"></el-input> -->
                    </el-col>
                    <el-col :span="4">
                        <el-select
                            v-model="search.purchasePaymentMethod"
                            placeholder="支付方式"
                            readonly>
                            <el-option
                            v-for="item in purchasePaymentMethodList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <el-input v-model="search.otherExpenses" placeholder="其它金额" readonly></el-input>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column prop="selectedSpeIndex" label="序号" width="50px">
                        <template slot-scope="scope">
                            {{scope.row.selectedSpeIndex}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="productBasicInfoId" label="商品名称">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.productName" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column label="规格名称">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.specificationName" readonly></el-input>
                        </template>
                    </el-table-column>

                    <el-table-column prop="weight" label="重量（KG）" width="120px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.weight" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="unit" label="单位" width="120px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.unit" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="num" label="采购数量" width="120px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.num" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="purchasePrice" label="采购单价" width="120px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.purchasePrice" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="lastStockPrice" label="最近一次采购单价" width="120px">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.lastStockPrice" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cost" label="进货成本价(元)(采购单价+运费+平摊其他费用)">
                        <template slot="header" slot-scope="scope">
                            <el-tooltip class="item" effect="dark" content="(采购单价+运费+平摊其他费用)" placement="top">
                                <el-button type="text">进货成本价(元)<i class="el-icon-info" style="margin-left:5px"></i></el-button>
                            </el-tooltip>
                        </template>
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.viewCost" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="comment" label="备注">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.comment" readonly></el-input>
                        </template>
                    </el-table-column>
                    <el-table-column prop="totalPrice" label="商品总价">
                        <template slot-scope="scope">
                            <el-input type="text" v-model="scope.row.totalPrice" readonly></el-input>
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
                    <p><b>附件图片：</b></p>
                    <a :href="item" v-if="search.imageurls.length" v-for="item in search.imageurls" target="_blank">
                        <img :src="item" alt="" style="width: 100px;height:100px;">
                    </a>
                    <p v-if="search.imageurls.length === 0">无</p>
                </el-row>
                <el-row style="margin-top: 20px;">
                    <p><b>审批记录：</b></p>
                    <p v-if="search.auditRecord" v-html="search.auditRecord"></p>
                    <p v-if="!search.auditRecord">无</p>
                </el-row>
            </el-row>
        </el-card>

        <el-dialog :title="auditForm.auditTitle" width="500px" :close-on-click-modal="false" @close="handleCloseAudit" :visible.sync="auditDialogVisible">
            <el-form :model="auditForm" label-position="top">
                <el-form-item label="批语">
                    <el-input type="textarea" :rows="7" v-model="auditForm.text"  width="30%"></el-input>
                </el-form-item>
                <el-button
                    type="primary"
                    style="float: right; margin-right: 10px; margin-bottom: 5px"
                    round
                    @click="handleSubmitAudit">
                    确定
                </el-button>
                <el-button
                    type="info"
                    style="float: right; margin-right: 10px"
                    round
                    @click="handleCloseAudit">
                    取消
                </el-button>
            </el-form>
        </el-dialog>
    </section>
</template>
<script>
import api from '@/api/purchaseStockNote';
import Util from '@/libs/util';
import { warehouseByUserCurrentCity, purchasePaymentMethod } from '@/api/dict';
const form = {
    unitName: ''
};
export default {
    data() {
        return {
            search: {
                warehouseId: '',
                shippingPrice: '',
                supplierId: '',
                purchasePaymentMethod: '',
                otherExpenses: '',
                imageurls: [],
                auditRecord: ''
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

            auditDialogVisible: false,
            auditForm: {}
        };
    },
    methods: {
        calTotalCost() {
            const shippingPrice = this.search.shippingPrice;
            let sumWeight = 0;
            let sumStock = 0;
            let sumShippingPrice = 0;
            let sumTotalPrice = 0;
            this.tableData.forEach(item => {
                if (item.purchasePrice && item.num && item.weight) {
                    sumShippingPrice += parseFloat(shippingPrice) / 1000 * parseFloat(item.weight) * parseFloat(item.num);
                    sumWeight += parseFloat(item.weight) / 1000 * parseFloat(item.num);
                    sumStock += parseFloat(item.num);
                    sumTotalPrice += parseFloat(parseFloat(item.totalPrice).toFixed(2));
                }
            });
            this.sumStock = sumStock.toFixed(2);
            sumWeight = Util.precisionMath(sumWeight, 2);
            this.sumWeight = sumWeight;
            sumShippingPrice = Util.precisionMath(sumShippingPrice, 2);
            this.sumShippingPrice = sumShippingPrice;

            this.sumTotalPrice = Util.precisionMath(sumTotalPrice);
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
                viewCost: '',
                totalPrice: '',
                comment: '',
                lastStockPrice: '',
                selectedSpeIndex: selectedSpeIndex
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
        // 清除搜索
        clear() {
            this.txtQuery = '';
            this.currentPage = 1;
            this.productUnitPagers();
        },
        handlePass() {
            this.auditForm.auditTitle = '审批通过';
            this.auditDialogVisible = true;
        },

        handleReject() {
            this.auditForm.auditTitle = '审批驳回';
            this.auditForm.type = 'reject';
            this.auditDialogVisible = true;
        },

        handleCloseAudit() {
            this.auditForm = {};
            this.auditDialogVisible = false;
        },

        handleSubmitAudit() {
            if (!this.auditForm.text && this.auditForm.type === 'reject') {
                return this.$message.error('请输入批语');
            }
            if (this.auditForm.text && this.auditForm.text.length > 255) {
                return this.$message.error('批语最多255个字符');
            }
            const param = { id: this.$route.params.id, auditDescription: this.auditForm.text };
            if (this.auditForm.type === 'reject') {
                api.updateRejected(param).then(res => {
                    if (res.code === 100) {
                        this.auditForm = {};
                        this.auditDialogVisible = false;
                        this.$notify.success(res.message || '成功');
                        this.$router.push({ name: '/stock/purchaseStockNote/index' });
                    }
                });
            } else {
                api.updatePassed(param).then(res => {
                    if (res.code === 100) {
                        this.auditForm = {};
                        this.auditDialogVisible = false;
                        this.$notify.success(res.message || '成功');
                        this.$router.push({ name: '/stock/purchaseStockNote/index' });
                    }
                });
            }
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
        },
        purchaseStockNoteGetAudit() {
            if (!this.G.permission['_stock/purchaseStockNote/get']) {
                return this.$notify.error('没有查看采购单详情的权限');
            }
            const param = {
                id: this.$route.params.id
            };
            api.purchaseStockNoteGetAudit(param).then(async res => {
                if (res.code === 100) {
                    const data = res.content;
                    this.search.warehouseId = data.warehouseId.toString();
                    this.search.shippingPrice = data.shippingPrice;
                    this.search.supplierId = data.supplierId;
                    this.search.purchasePaymentMethod = data.purchasePaymentMethod;
                    this.search.otherExpenses = data.otherExpenses;
                    this.search.auditRecord = data.auditRecord;
                    if (data.imageurls == null || data.imageurls === '') {
                        this.search.imageurls = [];
                    } else {
                        this.search.imageurls = data.imageurls.split(',');
                    }
                    this.tableData = data.purchaseStockNoteDetailInfoVOList.map((item, index) => {
                        item.selectedSpeIndex = index + 1;
                        item.totalPrice = parseFloat((item.cost * item.num).toFixed(2));
                        item.viewCost = Util.precisionMath(item.cost);
                        // item.unit = '';
                        item.productList = [{ id: item.productBasicInfoId, name: item.productName }];
                        this.handleSpecSelect(item).then(() => {
                            this.calTotalCost();
                        });
                        return item;
                    });
                }
            });
        },
        // 选中规格时，加载规格的信息
        handleSpecSelect(data) {
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
                    data.lastStockPrice = content.lastStockPrice;
                    Vue.set(data, '1', 1);
                }
            });
        }
    },
    async created() {
        await Promise.all([
            this.getSupplier(),
            this.purchasePaymentMethod(),
            this.warehouseByUserCurrentCity()
        ]);
        this.id = this.$route.params.id;
        if (!this.id) {
            return this.$notify.error('id 异常');
        }

        this.purchaseStockNoteGetAudit();
    }
};
</script>
