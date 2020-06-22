<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="24">
                    <el-col :span="3">
                        <p style="font-size:12px">区域：</p>
                        <el-select v-model="search.districtId" placeholder="地区" @change="handleChangeDistrict">
                            <el-option v-for="(item,key) in districtList" :label="item.name" :value="item.id"
                                       :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <p style="font-size:12px">街道:</p>
                        <el-select v-model="search.streetId" style="width: 100%" placeholder="街道"
                                   @change="handleChangeStreet">
                            <el-option v-for="(item,key) in streetList" :label="item.name" :value="item.id"
                                       :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">审核状态:：</p>
                        <el-select v-model="search.postState" placeholder="审核状态:" @change="handleChangeStreet">
                            <el-option v-for="(item,key) in postStatus" :label="item.name" :value="item.id"
                                       :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">支付类型：</p>
                        <el-select v-model="search.paymentType" placeholder="支付类型" @change="handleChangeStreet">
                            <el-option v-for="(item,key) in paymentType" :label="item" :value="key"
                                       :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">支付方式：</p>
                        <el-select
                            v-model="search.payMethodStr"
                            multiple
                            collapse-tags
                            placeholder="支付方式">
                            <el-option
                                v-for="item in payMethodList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">订单状态：</p>
                        <el-select
                            v-model="search.orderStatus"
                            multiple
                            collapse-tags
                            placeholder="订单状态">
                            <el-option
                                v-for="item in orderStatusList"
                                :key="item.value"
                                :label="item.key"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">订单类型：</p>
                        <el-select v-model="search.orderType" placeholder="订单类型">
                            <el-option v-for="(item,key) in orderTypeMap" :label="item" :value="key"
                                       :key="key"></el-option>
                        </el-select>
                    </el-col>
                </el-row>
                <el-row class="mb20" :gutter="24">
                    <el-col :span="3">
                        <p style="font-size:12px">订单编号：</p>
                        <el-input v-model="search.orderNo" placeholder="订单号"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">收货人：</p>
                        <el-input v-model="search.consigneeName" placeholder="收货人"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">联系方式：</p>
                        <el-input v-model="search.consigneeTelephone" placeholder="收货人号码"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">店铺名：</p>
                        <el-input v-model="search.merchantName" placeholder="店铺名"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">指派单号：</p>
                        <el-input v-model="search.assignNo" placeholder="指派单号"></el-input>
                    </el-col>
                    <el-col :span="6">
                        <p style="font-size:12px">待过账时间</p>
                        <el-date-picker
                            v-model="search.formDate"
                            @change="orderPagers"
                            type="daterange"
                            align="right"
                            unlink-panels
                            range-separator="至"
                            start-placeholder="待过账时间开始日期"
                            end-placeholder="待过账时间结束日期">
                        </el-date-picker>
                    </el-col>
                </el-row>
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <p style="font-size:12px">配送员：</p>
                        <el-input v-model="search.deliverymanNameLike" placeholder="配送员"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <p style="font-size:12px">下单人：</p>
                        <el-input v-model="search.placeOrderManLike" placeholder="下单人"></el-input>
                    </el-col>
                    <el-col :span="8">
                        <p style="font-size:12px">搜索：</p>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                        <el-button @click="handleExport" v-if="G.permission['_order/paymentPost/getExcel']">导出</el-button>
                    </el-col>
                </el-row>
            </el-row>
            <el-table :data="tableData" border v-loading="loading" @selection-change="handleSelectTable">
                <el-table-column type="expand">
                    <template slot-scope="props">
                        <el-table :data="props.row.orderProductSnapshotList" border v-loading="loading">
                            <el-table-column v-for="(column,key) in columnsExpand" :prop="column.key"
                                             :label="column.title" :key="key">
                                <template slot-scope="scope">
                                    <span v-html="scope.row[column.key]"></span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </template>
                </el-table-column>
                <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"
                                 :width="column.width">
                    <template slot-scope="scope">
                        <span v-html="scope.row[column.key]"></span>
                    </template>
                </el-table-column>
                <el-table-column width="80px">
                    <template slot-scope="scope">
                        <button type="button" class="el-button el-button--primary el-button--mini"
                                @click.stop="handleView(scope.row)"
                                v-if="scope.row.type === '订单支付' || scope.row.type === '订单退款'"><span>详情</span></button>
                    </template>
                </el-table-column>
                <el-table-column prop="UpdateOnOrderStatus" label="" v-if="G.permission['_order/paymentPost/post']" width="80px">
                    <template slot-scope="scope">
                        <button type="button" class="el-button el-button--primary el-button--mini"
                                @click.stop="handlPaymentPost(scope.row)" v-if="scope.row.postStatus === '待过账'">
                            <span>过账</span></button>
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
                    <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i
                        class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
        </el-card>
        <el-dialog :close-on-click-modal="false" title="订单过账" :visible.sync="dialogPaymentPostVisible"
                   class="dialogw80">
            <el-form ref="ruleForm">
                <el-form-item label="订单编号">
                    <el-input readonly :value="paymentPostData.orderNo" placeholder="订单编号"></el-input>
                </el-form-item>
                <el-form-item label="商户名称">
                    <el-input readonly :value="paymentPostData.merchantName" placeholder="商户名称"></el-input>
                </el-form-item>
                <el-form-item label="支付金额">
                    <el-input readonly :value="paymentPostData.paymentPrice" placeholder="支付金额"></el-input>
                </el-form-item>
                <el-form-item label="支付方式">
                    <el-select v-model="paymentPostData.paymentType" placeholder="支付方式">
                        <el-option v-for="(item,key) in payMethodList" :label="item.name" :value="item.id"
                                   :key="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注信息">
                    <el-input v-model="paymentPostData.remark" placeholder="备注信息" type="textarea" rows="5"></el-input>
                </el-form-item>
                <el-form-item label="操作:" prop="isNeedImage">
                    <template>
                        <button type="button" class="el-button el-button--primary el-button--mini"
                                @click.stop="paymentPost()" v-if="G.permission['_order/paymentPost/post']"><span>过 账</span></button>
                        <button type="button" class="el-button el-button--primary el-button--mini"
                                @click.stop="paymentPostCancel()"><span>取 消</span></button>
                    </template>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="订单详细信息" :visible.sync="dialogFormVisible" class="dialogw80">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>订单号</p>
                    <el-input readonly :value="formInfo.orderNo" placeholder="订单号"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>订单状态</p>
                    <el-input readonly :value="formInfo.orderStatus" placeholder="订单状态"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>商品总金额</p>
                    <el-input readonly :value="formInfo.totalPrice" placeholder="商品总金额"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>优惠金额</p>
                    <el-input readonly :value="formInfo.promotionPrice" placeholder="优惠金额"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>运费</p>
                    <el-input readonly :value="formInfo.shippingPrice" placeholder="运费"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>应付金额</p>
                    <el-input readonly :value="formInfo.payablePrice" placeholder="应付金额"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>红包抵扣</p>
                    <el-input readonly :value="formInfo.redPacketAmount" placeholder="红包抵扣"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>积分抵扣</p>
                    <el-input readonly :value="formInfo.pointDeductionPrice" placeholder="积分抵扣"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>下单时间</p>
                    <el-input readonly :value="formInfo.orderTime | dateFilter" placeholder="下单时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>指派时间</p>
                    <el-input readonly :value="formInfo.assignTime | dateFilter" placeholder="指派时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>预约开始时间</p>
                    <el-input readonly :value="formInfo.appointDeliveryStartTime | dateFilter"
                              placeholder="预约开始时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>预约结束时间</p>
                    <el-input readonly :value="formInfo.appointDeliveryEndTime | dateFilter"
                              placeholder="预约结束时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>配送时间</p>
                    <el-input readonly :value="formInfo.shippingTime | dateFilter" placeholder="配送时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>送达时间</p>
                    <el-input readonly :value="formInfo.deliveryTime | dateFilter" placeholder="送达时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>支付时间</p>
                    <el-input readonly :value="formInfo.payTime | dateFilter" placeholder="支付时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>支付过期时间</p>
                    <el-input readonly :value="formInfo.expireTime | dateFilter" placeholder="支付过期时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>支付方式</p>
                    <el-input readonly :value="formInfo.payMethod" placeholder="支付方式"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>店铺名称</p>
                    <el-input readonly :value="formInfo.consigneeMerchantName" placeholder="店铺名称"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>收货人</p>
                    <el-input readonly :value="formInfo.consigneeName" placeholder="收货人"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>收货地址</p>
                    <el-input readonly :value="formInfo.consigneeAddress" placeholder="收货地址"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>收货电话</p>
                    <el-input readonly :value="formInfo.consigneeTelephone" placeholder="收货电话"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>配送人</p>
                    <el-input readonly :value="formInfo.deliverymanName" placeholder="配送人"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>配送人电话</p>
                    <el-input readonly :value="formInfo.deliverymanTelephone" placeholder="配送人电话"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>订单来源</p>
                    <el-input readonly :value="formInfo.orderSource | orderSourceFilter" placeholder="订单来源"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>备注信息</p>
                    <el-input v-model="formInfo.remark" readonly placeholder="备注信息" type="textarea" rows="5"></el-input>
                </el-col>
            </el-row>

            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>

    import apiCommon from '@/api/common';
    import Util from '@/libs/util';
    import apiPayment from '@/api/payment';
    import apiPaymentPost from '@/api/paymentPost';

    import { getOrderSource, getPaymentType, getPayMethodPostMap, orderType, getOrderStatusMap } from '@/api/dict';

    const columnsExpand = [
        { key: 'index', title: '序号' },
        { key: 'productName', title: '商品名' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'unit', title: '单位' },
        { key: 'purchasedNum', title: '购买数量' },
        { key: 'specificationUnitPrice', title: '单价' },
        { key: 'totalPrice', title: '总价' }
    ];

    const orderStatus = [
        { key: '待指派', value: '2' },
        { key: '已指派', value: '3' },
        { key: '配送中', value: '4' },
        { key: '已送达', value: '5' },
        { key: '已取消', value: '0' }
    ];

    const postStatus = [
        { key: '待过账', value: '0' },
        { key: '已过账', value: '1' }
    ];

    const columns = [
        { key: 'orderNo', title: '订单编号', width: '160px' },
        { key: 'paymentTime', title: '待过账时间', width: '150px' },
        { key: 'paymentPrice', title: '金额', width: '65px' },
        { key: 'paymentMethod', title: '支付方式', width: '75px' },
        { key: 'type', title: '类型', width: '75px' },
        { key: 'merchantTelephone', title: '商户手机号', width: '100px' },
        { key: 'consigneeName', title: '收货人', width: '70px' },
        { key: 'consigneeTelephone', title: '收货人手机号', width: '100px' },
        { key: 'defaultAddress', title: '收货地址', width: '550px' },
        { key: 'postStatus', title: '过账状态', width: '70px' },
        { key: 'deliverymanName', title: '配送员', width: '130px' },
        { key: 'placeOrderMan', title: '下单人', width: '70px' }
    ];

    let payMethodMap = {};
    let orderSourceMap = {};

    const searchForm = {
        assignNo: '',
        orderStatus: '',
        paymentType: '',
        postState: '',
        addressDistrictId: '',
        addressStreetId: '',
        payMethodStr: [],
        orderNo: '',
        merchantName: '',
        merchantTelephone: '',
        orderSource: '',
        consigneeName: '',
        consigneeTelephone: '',
        formDate: []
    };


    export default {
        data() {
            return {
                currentPage: 1,
                totalPage: 0,
                pageSize: 5,
                loading: false,
                dialogFormVisible: false,
                dialogPaymentPostVisible: false,
                tableData: [],
                districtList: [],
                streetList: [],
                payMethodList: [],
                orderSourceList: [],
                orderStatusList: orderStatus,
                paymentType: {},
                paymentTypeNotAll: {},
                paymentPostData: {},
                columnsExpand: columnsExpand,
                columns: columns.map(item => {
                    item.name = item.title;
                    item.id = item.key;
                    return item;
                }),
                postStatus: postStatus.map(item => {
                    item.name = item.key;
                    item.id = item.value;
                    return item;
                }),
                formInfo: {},
                search: {
                    assignNo: '',
                    orderStatus: '',
                    paymentType: '',
                    postState: '0',
                    districtId: '',
                    streetId: '',
                    payMethodStr: [],
                    orderNo: '',
                    merchantName: '',
                    merchantTelephone: '',
                    orderSource: '',
                    consigneeName: '',
                    consigneeTelephone: '',
                    orderType: '',
                    deliverymanNameLike: '',
                    placeOrderManLike: '',
                    formDate: [new Date(), new Date()]
                },
                orderTypeMap: {},
                orderStatusMap: {}
            }
        },
        methods: {
            handleSearch() {
                this.currentPage = 1;
                this.orderPagers();
            },
            handleClear() {
                this.search = JSON.parse(JSON.stringify(searchForm));
                this.orderPagers();
            },
            handleCancel() {
                this.dialogFormVisible = false;
            },
            handleExport() {
                if (!this.search.formDate || this.search.formDate.length !== 2) {
                    return this.$notify.error('请选择过账时间范围');
                } else {
                    if (Util.isTimeLimitExceeded(new Date(this.search.formDate[0]), new Date(this.search.formDate[1]))) {
                        this.$notify.error('过账时间范围不能大于30天');
                        return;
                    }
                }
                const param = this.assemblyParam();
                param.time = new Date().getTime();
                const url = vars.URLApiBase + '/order/paymentPost/exportExcel?' + Util.qs(param);
                location.href = url;
            },
            assemblyParam() {
                const param = {};
                const search = this.search;
                // const diyFiled = ['formDate', 'postState', 'paymentType', 'orderStatus', 'payMethodStr'];
                for (const item in search) {
                    if (search.hasOwnProperty(item)) {
                        const element = search[item];
                        if (item !== 'formDate' || item !== 'paymentType') {
                            if (element) {
                                param[item] = element;
                            }
                        }
                    }
                }
                param.postStatus = search.postState;
                param.types = search.paymentType;
                param.startTime = search.formDate && this.search.formDate.length ? Util.formatTime(new Date(this.search.formDate[0]), true) : undefined;
                param.endTime = search.formDate && this.search.formDate.length ? Util.formatTime(new Date(this.search.formDate[1]), true) : undefined;
                param.orderStatus = search.orderStatus && search.orderStatus.length ? search.orderStatus.join(',') : undefined;
                param.payMethodStr = search.payMethodStr.length ? search.payMethodStr.join(',') : undefined;
                return param;
            },
            orderPagers() {
                const param = this.assemblyParam();
                param.page = this.currentPage;
                param.limit = this.pageSize;
                param.cityId = Util.getCookie('cityId');
                console.log(param);
                apiPayment.paymentPostPagers(param).then(res => {
                    this.loading = false;
                    if (res.records && res.records.length) {
                        this.tableData = res.records.map(item => {
                            let totalRealOrderNum = 0;
                            let totalPurchasedNum = 0;
                            let totalPrice = 0;
                            item.orderProductSnapshotList.map((ele, index) => {
                                ele.paymentTime = Util.formatTime(new Date(ele.paymentTime));
                                ele.index = index + 1;
                                ele.totalPrice = parseFloat(ele.specificationUnitPrice * ele.purchasedNum).toFixed(2);
                                totalRealOrderNum = parseFloat(totalRealOrderNum) + parseFloat(ele.realOrderNum);
                                totalPurchasedNum += ele.purchasedNum;
                                totalPrice += parseFloat(ele.totalPrice);
                                return ele;
                            });
                            item.orderProductSnapshotList.push({
                                index: '合计',
                                productName: '',
                                specificationName: '',
                                realOrderNum: totalRealOrderNum.toFixed(2),
                                purchasedNum: totalPurchasedNum.toFixed(2),
                                unit: '',
                                specificationUnitPrice: '',
                                totalPrice: totalPrice.toFixed(2)
                            });
                            item.paymentTime = Util.formatTime(new Date(item.paymentTime));
                            if (item.postStatus === '1') {
                                item.postStatus = '已过账';
                            } else {
                                item.postStatus = '待过账';
                            }
                            if (item.type === '订单退款' || item.type === '订单支付') {
                                item.defaultAddress = '<span style="color:blue">' + (item.cityName || '') + '-' + (item.districtName || '') + '-' + (item.streetName || '') + '</span> -' + (item.consigneeAddress || '') + '<span style=\'color:red\'> ' + (item.merchantName || '') + '</span>';
                            } else {
                                item.defaultAddress = '';
                            }
                            return item;
                        });
                    } else {
                        this.tableData = [];
                    }
                    this.totalPage = res.total;
                })
            },
            paymentPost() {
                if (!this.paymentPostData.paymentType) {
                    this.$notify.error('请选择支付方式');
                    return;
                }
                if (this.paymentPostData.remark && this.paymentPostData.remark.length > 300) {
                    this.$notify.error('备注信息不能超过300字');
                    return;
                }
                const data = {
                    paymentId: this.paymentPostData.paymentId,
                    paymentMethod: this.paymentPostData.paymentType,
                    remark: this.paymentPostData.remark
                };
                apiPaymentPost.post(data).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.checkIsLastItemOfLastPage();
                        this.orderPagers();
                    }
                    this.dialogPaymentPostVisible = false
                });
            },
            checkIsLastItemOfLastPage() {
                if (this.tableData.length === 1 && this.currentPage > 1) {
                    this.handleCurrentChange(this.currentPage - 1)
                }
            },
            paymentPostCancel() {
                this.paymentPostData = {};
                this.dialogPaymentPostVisible = false;
            },
            handleSelectTable(selection) {
                this.selection = selection;
            },
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.orderPagers();
            },
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.orderPagers();
            },
            handleChangeDistrict() {
                this.orderPagers();
                this.streetList = [];
                this.search.streetId = '';
                if (!this.search.districtId) {
                    this.streetList.push({ id: '', name: '全部' });
                } else {
                    const param = {
                        districtId: this.search.districtId
                    };
                    apiCommon.availableAreasStreets(param).then(res => {
                        if (res.code === 100) {
                            this.streetList = res.content;
                            this.streetList.unshift({ id: '', name: '全部' });
                        }
                    });
                }
            },
            getOrderSource() {
                getOrderSource().then(res => {
                    if (res.code === 100) {
                        orderSourceMap = res.content;
                        const orderSourceList = [{ id: '', name: '请选择' }];
                        for (const item in orderSourceMap) {
                            if (orderSourceMap.hasOwnProperty(item)) {
                                orderSourceList.push({ id: item, name: orderSourceMap[item] });
                            }
                        }
                        this.orderSourceList = orderSourceList;
                    }
                });
            },
            getDistrict() {
                const param = {
                    cityId: Util.getCookie('cityId')
                };
                apiCommon.availableAreasDistricts(param).then(res => {
                    if (res.code === 100) {
                        this.districtList = res.content;
                        this.districtList.unshift({ id: '', name: '全部' });
                    }
                });
            },
            getPaymentType() {
                getPaymentType().then(res => {
                    if (res.code === 100) {
                        this.paymentType = res.content;
                        this.paymentTypeNotAll = JSON.parse(JSON.stringify(res.content));
                        this.paymentType['0'] = '全部';
                    }
                });
            },
            paymethodMap() {
                getPayMethodPostMap().then(res => {
                    if (res.code === 100) {
                        payMethodMap = res.content;
                        const payMethodList = [];
                        for (const item in payMethodMap) {
                            if (payMethodMap.hasOwnProperty(item)) {
                                payMethodList.push({ id: item, name: payMethodMap[item] });
                            }
                        }
                        this.payMethodList = payMethodList;
                    }
                });
            },
            handleView(data) {
                this.dialogFormVisible = true;
                console.log(data)
                const parm = { orderNo: data.orderNo, id: data.id };
                apiPayment.orderDetail(parm).then(res => {
                    console.log(res.content)
                    res.content.payMethod = data.paymentMethod;
                    this.formInfo = res.content;
                    if (this.formInfo.orderStatus) {
                        this.formInfo.orderStatus = this.orderStatusMap[`${this.formInfo.orderStatus}`];
                    }
                });
            },
            handlPaymentPost(data) {
                this.dialogPaymentPostVisible = true;
                this.paymentPostData = {
                    orderNo: data.orderNo,
                    merchantName: data.merchantName,
                    paymentPrice: data.paymentPrice,
                    paymentId: data.id
                };
            },
            handleChangeStreet() {
                this.orderPagers();
            },
            orderType() {
                orderType().then(res => {
                    if (res.code === 100) {
                        this.orderTypeMap[''] = '请选择';
                        for (const key in res.content) {
                            this.orderTypeMap[key] = res.content[key];
                        }
                    }
                })
            },
            getOrderStatusMap() {
                getOrderStatusMap().then(res => {
                    if (res.code === 100) {
                        for (const item in res.content) {
                            if (res.content.hasOwnProperty(item)) {
                                this.orderStatusMap[item] = res.content[item];
                            }
                        }
                    }
                });
            }
        },
        filters: {
            dateFilter(data) {
                if (!data) {
                    return data;
                }
                return Util.formatTime(new Date(data));
            },
            orderSourceFilter(data) {
                return orderSourceMap[data];
            }
        },
        created() {
            Promise.all([
                this.getPaymentType(),
                this.getDistrict(),
                this.paymethodMap(),
                this.getOrderSource(),
                this.orderType(),
                this.getOrderStatusMap()
            ]).then(() => {
                this.orderPagers();
            });
        }
    };
</script>
<style>
    .table-expand {
        font-size: 0;
    }

    .table-expand label {
        width: 90px;
        color: #99a9bf;
    }

    .table-expand .el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        width: 50%;
    }
</style>

