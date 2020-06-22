<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix" v-if="G.permission['_newEvaluationRecord/index']">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <el-input v-model="searchForm.orderNo" placeholder="订单号"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <el-input v-model="searchForm.merchantTelephone" placeholder="商户手机号码"></el-input>
                    </el-col>
                    <el-col :span="3">
                        <el-input v-model="searchForm.deliverymanTelephone" placeholder="配送员手机号码"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="评分开始日期"
                            end-placeholder="评分结束日期">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="3">
                        <el-select v-model="searchForm.type" placeholder="请选择类型">
                            <el-option
                                v-for="item in typeList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="6">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                        <el-button type="success" @click="handleExportExcel" v-if="G.permission['_order/evaluationRecord/exportExcel']">导出</el-button>
                    </el-col>
                </el-row>
                <el-table :data="records" border v-loading="loading">
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        align="center"
                        :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key !== 'orderDetail'">{{scope.row[column.key]}}</span>
                            <section v-else>
                                <i class="el-icon-view" @click.stop="handleViewOrderDetail(scope.row)"></i>
                            </section>
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
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
            <el-row v-else>没有权限</el-row>
        </el-card>

        <!-- 订单详情 -->
        <el-dialog :close-on-click-modal="false" title="订单信息" top="5vh" @close="handleCancelOrderDetail" :visible.sync="formInfo.visible" width="40%" class="dialogw80">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>订单号</p>
                    <el-input readonly :value="formInfo.orderNo" placeholder="订单号"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>订单状态</p>
                    <el-input readonly :value="orderStatusMap[formInfo.orderStatus]" placeholder="订单状态"></el-input>
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
                    <el-input readonly :value="formInfo.appointDeliveryStartTime | dateFilter" placeholder="预约开始时间"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>预约结束时间</p>
                    <el-input readonly :value="formInfo.appointDeliveryEndTime | dateFilter" placeholder="预约结束时间"></el-input>
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
                    <el-input readonly :value="payMethodMap[formInfo.payMethod]" placeholder="支付方式"></el-input>
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
                    <el-input readonly :value="orderSourceMap[formInfo.orderSource]" placeholder="订单来源"></el-input>
                </el-col>
            </el-row>
            <el-row>
                <el-table :data="formInfo.orderProductSnapshotList" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columnsOrderInfo" :width="column.width" :prop="column.key" :label="column.title" :key="key">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelOrderDetail">取 消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import common from '@/components/common';
    import headerList from '@/config/evaluateList';
    import newEvaluationRecord from '@/api/newEvaluationRecord';
    import util from '@/libs/util';
    import api from '@/api/refundOrder';
    import {
        getOrderFlag,
        getOrderSource,
        getOrderStatusMap,
        getPayMethodMap
    } from '@/api/dict';

    const searchForm = {
        orderNo: '',
        merchantTelephone: '',
        deliverymanTelephone: '',
        searchDate: [],
        type: ''
    };

    const typeList = [
        { id: '', name: '请选择类型' },
        { id: 0, name: '不满意' },
        { id: 1, name: '满意' }
    ];

    const columns = [
        { key: 'orderNo', title: '订单号' },
        { key: 'orderDetail', title: '订单详情', width: '80%' },
        { key: 'deliverymanName', title: '配送员姓名' },
        { key: 'serviceLabel', title: '评价类型' },
        { key: 'comment', title: '用户评论' },
        { key: 'merchantName', title: '商铺名称' },
        { key: 'merchantTelephone', title: '商户手机号' },
        { key: 'createDateTime', title: '评分日期时间' }
    ];

    const columnsOrderInfo = [
        { key: 'index', title: '序列号' },
        { key: 'productName', title: '商品名', width: '150%' },
        { key: 'specificationName', title: '规格名称', width: '150%' },
        { key: 'purchasedNum', title: '购买数量' },
        { key: 'unit', title: '单位' },
        { key: 'specificationUnitPrice', title: '单价' },
        { key: 'totalPrice', title: '总价' }
    ];

    export default {
        components: {
            common
        },

        data() {
            return {
                headerList: headerList,
                typeList: typeList,
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                formInfo: {},
                columnsOrderInfo: columnsOrderInfo,

                payMethodMap: {},
                orderStatusMap: {},
                orderSourceMap: {},
                printFlagMap: {},

                /* 无用字段  */
                orderInfo: '',
                expireTime: '',
                shippingTime: '',
                totalRealOrderNum: '',
                redPacketAmount: '',
                pointDeductionPrice: '',
                orderTime: '',
                assignTime: '',
                promotionPrice: ''
            }
        },

        filters: {
            dateFilter(data) {
                if (!data) {
                    return data;
                }
                return util.formatTime(new Date(data));
            }
        },

        methods: {

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                const param = this.encapsulateParam();
                param.page = this.currentPage;
                param.limit = this.pageSize;

                newEvaluationRecord.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.createDateTime = util.parseTimeStr(item.createDateTime);
                        });
                    }
                    this.totalSize = res.total;
                    this.records = res.records;
                });
            },

            encapsulateParam() {
                const param = {};
                if (this.searchForm.orderNo) {
                    param.orderNo = this.searchForm.orderNo;
                }
                if (this.searchForm.merchantTelephone) {
                    param.merchantTelephone = this.searchForm.merchantTelephone;
                }
                if (this.searchForm.deliverymanTelephone) {
                    param.deliverymanTelephone = this.searchForm.deliverymanTelephone;
                }
                if (this.searchForm.type !== '') {
                    param.type = this.searchForm.type;
                }
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    param.startDate = this.searchForm.searchDate[0];
                    param.endDate = this.searchForm.searchDate[1];
                }
                return param;
            },

            /**
             * 导出EXCEL
             * */
            handleExportExcel() {
                const exportUrl = window.vars.URLApiBase + '/order/evaluationRecord/exportExcel';
                const param = util.qs(this.encapsulateParam());
                return location.href = exportUrl + '?' + param;
            },

            /**
             * 查看订单明细
             * */
            handleViewOrderDetail(row) {
                const parm = { orderNo: row.orderNo };
                api.getOrderInfo(parm).then(res => {
                    console.log(res.content)
                    this.formInfo = res.content;
                    if (this.formInfo.type === '1') {
                        this.formInfo.consigneeName = '';
                    }
                    if (this.formInfo != null) {
                        console.log(this.formInfo)
                        this.formInfo.orderProductSnapshotList.map((item, index) => {
                            item.index = 1 + index;
                            if (index + 1 === this.formInfo.orderProductSnapshotList.length) {
                                item.index = '合计';
                            }
                            return item;
                        });
                    }

                    this.formInfo.visible = true;
                });
            },

            handleCancelOrderDetail() {
                this.formInfo = {};
            },

            /**
             * 字典方法
             * */
            getPayMethodMap() {
                getPayMethodMap().then(res => {
                    if (res.code === 100 && res.content) {
                        this.payMethodMap = res.content;
                    }
                });
            },

            getOrderStatusMap() {
                getOrderStatusMap().then(res => {
                    if (res.code === 100) {
                        this.orderStatusMap = res.content;
                    }
                });
            },

            getOrderSource() {
                getOrderSource().then(res => {
                    if (res.code === 100) {
                        this.orderSourceMap = res.content;
                    }
                });
            },

            getOrderFlag() {
                getOrderFlag().then(res => {
                    if (res.code === 100) {
                        this.printFlagMap = res.content;
                    }
                });
            }
        },

        created() {
            Promise.all([
                this.getPayMethodMap(),
                this.getOrderStatusMap(),
                this.getOrderSource(),
                this.getOrderFlag()
            ]).then(() => {
                this.handlePagers();
            });
        }


    }
</script>
