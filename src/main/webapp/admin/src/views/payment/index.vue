<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button type="primary" style="float: right;margin-left: 10px;" @click.stop="paymentPagers(true)">导出Excel</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-select v-model="search.districtId" placeholder="请选择地区" @change="getStreet">
                                <el-option v-for="(item,key) in districtList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <el-select v-model="search.streetId" placeholder="请选择街道" @change="paymentPagers">
                                <el-option v-for="(item,key) in streetList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <el-select
                                v-model="search.types"
                                @change="paymentPagers"
                                collapse-tags
                                placeholder="支付类型">
                                <el-option
                                v-for="(item,key) in payTypeList"
                                :key="key"
                                :label="item"
                                :value="key">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <el-select
                                v-model="search.payMethodStr"
                                @change="paymentPagers"
                                multiple
                                collapse-tags
                                placeholder="支付方式">
                                <el-option
                                    v-for="(item,key) in payMethodList"
                                    :key="key"
                                    :label="item"
                                    :value="key">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="6">
                            <el-date-picker
                                v-model="search.date"
                                @change="paymentPagers"
                                type="daterange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="支付开始日期"
                                end-placeholder="支付结束日期">
                            </el-date-picker>
                        </el-col>
                    </el-row>
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="search.searchStr" placeholder="关键字查询"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-input v-model="search.merchantTelephone" placeholder="注册手机号"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-input v-model="search.orderNo" placeholder="订单号"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="clear">清除</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading" @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width" :sortable="column.sort">
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                    <!--<el-row class="mt20">-->
                        <!--<el-col :span="8"><span style="font-size:14px;">合计金额: {{totalPayPrice}}</span></el-col>-->
                    <!--</el-row>-->
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
    </section>
</template>
<script>
import api from '@/api/payment';
import apiCommon from '@/api/common';
import Util from '@/libs/util';
import { getPaymentType, getPayMethodMap } from '@/api/dict';

const columns = [
    { key: 'orderNo', title: '订单编号', width: '160px' },
    { key: 'paymentTime', title: '支付时间', width: '160px', sort: 'custom' },
    { key: 'paymentPrice', title: '支付金额', width: '100px', sort: 'custom' },
    { key: 'paymentMethod', title: '支付方式', width: '80px' },
    { key: 'type', title: '类型', width: '80px', sort: 'custom' },
    { key: 'merchantTelephone', title: '注册手机号', width: '120px', sort: 'custom' },
    { key: 'address', title: '收货地址' }
];
const form = {
    paymentName: '',
    taxNo: '',
    bank: '',
    bankCardNo: '',
    linkman: '',
    phone: '',
    address: ''
};
export default {
    data() {
        return {
            cityId: Util.getCookie('cityId'),

            search: {
                date: [new Date(new Date(new Date().toLocaleDateString()).getTime()), new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)],
                districtId: '',
                cityId: '',
                streetId: '',
                types: '',
                payMethodStr: [],
                merchantTelephone: '',
                orderNo: ''
            },
            // totalPayPrice: 0,

            payMethodList: {},
            payTypeList: {},

            districtList: [],
            streetList: [],

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formType: '',
            rules: {
                name: [
                    { required: true, message: '序号不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.paymentPagers();
        },
        // 清除搜索
        clear() {
            this.currentPage = 1;
            this.search.payMethodStr = [];
            this.search.paymentName = '';
            this.search.taxNo = '';
            this.search.phone = '';
            this.search.districtId = '';
            this.search.streetId = '';
            this.search.date = [];
            this.streetList = [];
            this.search.types = '';
            this.search.searchStr = '';
            this.search.merchantTelephone = '';
            this.search.orderNo = '';
            this.paymentPagers();
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.paymentPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.paymentPagers();
        },
        getStreet() {
            this.paymentPagers();
            const param = {
                districtId: this.search.districtId
            };
            apiCommon.availableAreasStreets(param).then(res => {
                if (res.code === 100) {
                    this.search.streetId = '';
                    this.streetList = res.content;
                    this.streetList.unshift({ id: '', name: '请选择街道' });
                } else {
                    // this.$notify.error(res.message || '获取街道异常');
                }
            });
        },
        getDistrict() {
            const param = {
                cityId: this.cityId
            };
            apiCommon.availableAreasDistricts(param).then(res => {
                if (res.code === 100) {
                    this.districtList = res.content;
                    this.districtList.unshift({ id: '', name: '请选择地区' });
                }
            });
        },
        // 获取数据
        paymentPagers(excel) {
            if (!this.G.permission['_order/payment/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            param.districtId = this.search.districtId ? this.search.districtId : undefined;
            param.cityId = this.cityId ? this.cityId : undefined;
            param.streetId = this.search.streetId ? this.search.streetId : undefined;
            param.types = this.search.types || undefined;
            param.payMethodStr = this.search.payMethodStr.join(',') || undefined;
            // window.LOG(this.search.payMethodStr.map(item => { debugger; return item.id; }));
            param.merchantTelephone = this.search.merchantTelephone || undefined;
            param.orderNo = this.search.orderNo || undefined;
            if (this.search.date) {
                param.startTime = this.search.date[0] ? Util.formatTime(new Date(this.search.date[0]), true) : undefined;
                param.endTime = this.search.date[1] ? Util.formatTime(new Date(this.search.date[1]), true) : undefined;
            }
            param.searchStr = this.search.searchStr || undefined;
            if (excel === true) {
                for (const item in param) {
                    if (param.hasOwnProperty(item)) {
                        if (param[item] === undefined) {
                            param[item] = '';
                        }
                    }
                }
                if (!param.startTime || !param.endTime) {
                    return this.$notify.error('导出时间范围为空,请选择时间范围,并且不能大于一个月');
                }
                if (new Date(param.endTime).getTime() - new Date(param.startTime).getTime() > 2678400000) {
                    return this.$notify.error('导出时间范围不能大于一个月');
                }
                return location.href = window.vars.URLApiBase + '/order/payment/getExcel?' + Util.qs(param);
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.paymentPagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        if (item.streetName === null) {
                            item.streetName = '';
                        }
                        item.address = item.type !== '充值支付' ? (item.cityName || '') + '-' + (item.districtName || '') + '-' + (item.streetName || '') + '-' + (item.consigneeAddress || '') + '-' + (item.merchantName || '') + '-' + (item.consigneeName || '') + '-' + (item.consigneeTelephone || '') : '';
                        item.paymentTime = Util.formatTime(new Date(item.paymentTime));
                        return item;
                    });
                    // this.totalPayPrice = res.dict.totalPayPrice
                } else {
                    this.tableData = [];
                }
                this.totalPage = res.total;
            });
        },
        getPaymentType() {
            return getPaymentType().then(res => {
                if (res.code === 100) {
                    this.payTypeList = res.content;
                }
            });
        },
        getPayMethodMap() {
            return getPayMethodMap().then(res => {
                if (res.code === 100) {
                    this.payMethodList = res.content;
                }
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.paymentPagers();
        }
    },
    created() {
        this.getDistrict();
        this.getPaymentType();
        this.getPayMethodMap();
        this.paymentPagers();
    }
};
</script>
