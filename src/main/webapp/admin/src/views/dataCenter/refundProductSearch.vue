<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" type="primary"  @click.stop="handleExport">导出Excel</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-form :inline="true">
                        <el-form-item label="商品名称">
                            <el-autocomplete
                                v-model="productName"
                                :fetch-suggestions="querySearchProductAsync"
                                placeholder="请输入商品名称"
                                @select="handleProductSelect"
                                @input="productNameChange"
                                :trigger-on-focus='false'>
                            </el-autocomplete>
                        </el-form-item>
                        <el-form-item label="商品规格">
                            <el-select :disabled="!productId" v-model="specificationId" placeholder="请选择规格">
                                <el-option
                                    v-for="item in specificationArr"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="状态">
                            <el-select v-model="refundStatus" multiple placeholder="请选择状态">
                                <el-option
                                    v-for="item in refundStatusArr"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item  label="申请日期">
                            <el-date-picker
                                v-model="searchDate"
                                type="daterange"
                                align="right"
                                unlink-panels
                                value-format="yyyy-MM-dd"
                                range-separator="至"
                                start-placeholder="申请日期(开始)"
                                end-placeholder="申请日期(结束)">
                            </el-date-picker>
                        </el-form-item>
                        <el-form-item  label="送达日期">
                            <el-date-picker
                                v-model="deliverDate"
                                type="daterange"
                                align="right"
                                unlink-panels
                                value-format="yyyy-MM-dd"
                                range-separator="至"
                                start-placeholder="送达日期(开始)"
                                end-placeholder="送达日期(结束)">
                            </el-date-picker>
                        </el-form-item>
                        <el-form-item label="商户名或手机号">
                            <el-autocomplete
                                v-model="merchantKeyword"
                                :fetch-suggestions="querySearchMerchantAsync"
                                placeholder="商户名或手机号"
                                :trigger-on-focus="false">
                            </el-autocomplete>
                        </el-form-item>
                        <el-form-item label="订单类型">
                    <el-select v-model="type" placeholder="订单类型" @change="handleSearch">
                        <el-option v-for="(item,key) in orderTypeMap" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-form-item>
                    </el-form>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns"
                                     :prop="column.key"
                                     :label="column.title"
                                     :key="key"
                                     :width="column.width">
                        <!--<template slot-scope="scope">
                            <span>{{scope.row | filterRow(column.key) }}</span>
                        </template>-->
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[5, 10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPage">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
                <el-row>
                    <el-col :span="4">
                        合计数量：{{sumRefundNum}}&nbsp;
                    </el-col>
                    <el-col :span="4">
                        合计成本总价：{{sumCost}} 元&nbsp;
                    </el-col>
                    <el-col :span="4">
                        合计销售总价：{{sumPrice}} 元&nbsp;
                    </el-col>
                </el-row>
            </el-row>
        </el-card>
    </section>
</template>

<script>

    import apiRefund from '@/api/refundProductSearch';
    import util from '@/libs/util';
    import { orderRefundStatus, orderType } from '@/api/dict';

    const columns = [
        { key: 'merchantTelephone', title: '手机号(注册)' },
        { key: 'consigneeMerchantName', title: '商户名称' },
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '商品规格' },
        { key: 'unit', title: '单位' },
        { key: 'status', title: '退货状态' },
        { key: 'refundNum', title: '退货数量' },
        { key: 'specificationUnitPrice', title: '销售单价' },
        { key: 'refundPrice', title: '退款金额', width: '110px' },
        { key: 'cost', title: '商品成本价' }
    ];

    export default {
        name: 'refundProductSearch',

        data() {
            return {
                /* 查询条件相关 */
                warehouseArr: [],
                productName: '',
                productId: '',
                specificationId: '',
                specificationArr: [],
                refundStatus: [],
                refundStatusArr: [],
                searchDate: [],
                deliverDate: [],
                merchantKeyword: '',
                merchantKeywordArr: [],
                orderTypeMap: {},
                /* 表格相关 */
                columns: columns,
                tableData: [],
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalPage: 0,
                type: '',

                sumRefundNum: 0,
                sumCost: 0,
                sumPrice: 0
            };
        },

        methods: {

            /* 查询条件相关 */
            defaluDate() {
                const today = new Date().customFormat('#YYYY#-#MM#-#DD#');
                this.searchDate[0] = today;
                this.searchDate[1] = today;
            },

            querySearchProductAsync(productName, callback) {
                if (!productName) {
                    this.specificationArr = [{ id: '', name: '请选择' }];
                    return;
                }
                const param = {
                    name: productName
                };
                apiRefund.listCityProduct(param).then(res => {
                    if (res && res.code === 100) {
                        const data = res.content;
                        const productArr = data.map(item => ({ value: item.name, id: item.id }));
                        callback(productArr);
                    }
                });
            },

            handleProductSelect(item) {
                this.productId = item.id;
                const param = { productId: this.productId };
                apiRefund.selectCitySpecification(param).then(res => {
                    if (res && res.code === 100) {
                        this.specificationArr = res.content.map(item => ({ id: item.id, name: item.specificationName }));
                        this.specificationArr.unshift({ id: '', name: '请选择规格' });
                    }
                });
            },

            productNameChange() {
                if (!this.productName) {
                    this.specificationArr = [{ id: '', name: '请选择规格' }];
                    this.specificationId = null;
                    this.productId = '';
                }
            },

            querySearchMerchantAsync(productName, callback) {
                const param = {
                    name: this.merchantKeyword
                };
                apiRefund.listByKeyword(param).then(res => {
                    if (res && res.code === 100) {
                        let data = res.content;
                        if (data) {
                            if (typeof data === 'string') {
                                data = JSON.parse(data);
                            }
                            const productArr = data.map(item => ({ value: item.telephone, id: item.id }));
                            callback(productArr);
                        } else {
                            callback([])
                        }
                    }
                });
            },

            initWarehouseList() {
                apiRefund.getCityRelatedWarehouseList().then(res => {
                    if (res && res.code === 100) {
                        window.LOG(this.warehouseArr);
                        this.warehouseArr = res.content;
                        this.warehouseArr.unshift({ id: '', name: '请选择冻库' });
                    }
                });
            },

            /* 加载表格 */

            handleClear() {
                this.productId = '';
                this.productName = '';
                this.specificationId = '';
                this.merchantKeyword = '';
                this.searchDate = [];
                this.deliverDate = [];
                this.refundStatus = [];
                this.currentPage = 1;
                this.loadRefundProductList();
            },

            handleSearch() {
                this.currentPage = 1;
                this.loadRefundProductList();
            },
            // 基本信息页码改变
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.currentPage = 1;
                this.loadRefundProductList();
            },
            // 基本信息翻页
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.loadRefundProductList();
            },

            loadRefundProductList() {
                if (!this.G.permission['_stat/refundProductSearch/pages']) {
                    return this.$notify.error('没有列表权限');
                }
                const param = {};
                param.page = this.currentPage;
                param.limit = this.pageSize;
                this.setParams(param)
                apiRefund.pagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            item.refundPrice = util.precisionMath(this.number(item.refundNum) * this.number(item.specificationUnitPrice));
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                    this.sumRefundProduct(param);
                });
            },

            setParams(param) {
                if (this.searchDate && this.searchDate[0]) {
                    param.applicationDateBegin = this.searchDate[0];
                    param.applicationDateEnd = this.searchDate[1];
                }
                if (this.deliverDate && this.deliverDate[0]) {
                    param.deliveryDateBegin = this.deliverDate[0];
                    param.deliveryDateEnd = this.deliverDate[1];
                }
                if (this.productId) {
                    param.productId = this.productId;
                }
                if (this.specificationId) {
                    param.specificationId = this.specificationId;
                }
                if (this.refundStatus && this.refundStatus.length) {
                    param.refundStatus = util.strAssembling(this.refundStatus, ',');
                }
                if (this.merchantKeyword) {
                    param.merchantKeyword = this.merchantKeyword;
                }
                if (this.type) {
                    param.type = this.type
                }
            },
            orderType() {
                orderType().then(res => {
                    if (res.code === 100) {
                        const map = res.content;
                        map[''] = '请选择';
                        this.orderTypeMap = map;
                    }
                })
            },
            number(data) {
                return data ? data : 0;
            },

            handleExport() {
                const param = {};
                if (this.searchDate && this.searchDate[0]) {
                    if (util.isTimeLimitExceeded(new Date(this.searchDate[0]), new Date(this.searchDate[1]))) {
                        this.$notify.error('导出的时间范围不能大于30天');
                        return;
                    }
                }
                if (this.deliverDate && this.deliverDate[0]) {
                    if (util.isTimeLimitExceeded(new Date(this.deliverDate[0]), new Date(this.deliverDate[1]))) {
                        this.$notify.error('导出的时间范围不能大于30天');
                        return;
                    }
                }
                this.setParams(param);
                if (!param.applicationDateBegin && !param.deliveryDateBegin) {
                    this.$notify.info('请选择导出时间范围');
                    return;
                }
                return location.href = window.vars.URLApiBase + '/stat/refundProductSearch/exportExcel?' + util.qs(param);
            },

            sumRefundProduct(param) {
                if (!this.G.permission['_stat/refundProductSearch/sumRefundProduct']) {
                    return this.$notify.error('没有统计退货商品权限');
                }
                delete param.page;
                delete param.limit;
                apiRefund.sumRefundProduct(param).then(res => {
                    this.sumRefundNum = res.sumRefundNum;
                    this.sumCost = res.sumCost;
                    this.sumPrice = res.sumPrice;
                });
            },
            orderRefundStatus() {
                orderRefundStatus().then(res => {
                    if (res.code === 100) {
                        const obj = res.content;
                        const arr = [];
                        for (const item in obj) {
                            if (obj.hasOwnProperty(item)) {
                                const ele = obj[item];
                                arr.push({ id: item, name: ele });
                            }
                        }
                        this.refundStatusArr = arr;
                    }
                });
            }

            /* checkDate(startTime, endTime) {
                if (startTime === '' && endTime === '') {
                    return true;
                }
                if (new Date(startTime).getDate() === startTime.substring(startTime.length - 2)) {
                    if (new Date(endTime).getDate() === endTime.substring(endTime.length - 2)) {
                        if (new Date(endTime).getTime() >= new Date(startTime).getTime()) {
                            return true;
                        }
                    }
                }
                return false;
            }*/
        },

        created() {
            this.orderRefundStatus();
            this.defaluDate();
            this.initWarehouseList();
            this.loadRefundProductList();
            this.orderType()
        }
    };
</script>

<style scoped>

</style>
