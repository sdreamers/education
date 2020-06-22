<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-form :inline="true" :model="searchFormGather" class="demo-form-inline">
                <el-form-item label="日期">
                    <el-date-picker
                        v-model="searchFormGather.dateArr"
                        type="daterange"
                        align="right"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :clearable="false"
                        @change="gather"
                        value-format="yyyy-MM-dd">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="gather">查询</el-button>
                </el-form-item>
            </el-form>
            <el-row class="list-con clearfix" style="border-bottom:1px solid #eee;">
                <el-table
                    border
                    :show-header="false"
                    :data="tableData1">
                    <el-table-column prop="title"></el-table-column>
                    <el-table-column prop="value"></el-table-column>
                    <el-table-column prop="action">
                        <template slot-scope="scope">
                            <el-button type="primary" @click="outputExcel(scope.row.key)" v-if="!scope.row.hide">{{scope.row.buttonText}}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
            <el-form :inline="true" :model="searchFormGather" class="demo-form-inline" style="margin-top:50px;">
                <el-form-item label="日期">
                    <el-date-picker
                        v-model="searchFormGatherAdvancePayment.date"
                        type="date"
                        align="right"
                        :clearable="false"
                        @change="gatherAdvancePayment"
                        value-format="yyyy-MM-dd">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="gatherAdvancePayment">查询</el-button>
                </el-form-item>
            </el-form>
            <el-row class="list-con clearfix" style="border-bottom:1px solid #eee;">
                <el-table
                    border
                    :show-header="false"
                    :data="tableData2">
                    <el-table-column prop="title"></el-table-column>
                    <el-table-column prop="value"></el-table-column>
                    <el-table-column prop="action">
                        <template slot-scope="scope">
                            <el-button type="primary" @click="outputExcel(scope.row.key)" v-if="!scope.row.hide">{{scope.row.buttonText}}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
            <el-form :inline="true" :model="searchFormBusiness" class="demo-form-inline" style="margin-top:50px;">
                <el-form-item label="日期">
                    <el-date-picker
                        v-model="searchFormBusiness.date"
                        type="month"
                        align="right"
                        :clearable="false"
                        @change="business"
                        value-format="yyyy-MM-dd">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="business">查询</el-button>
                </el-form-item>
            </el-form>
            <el-row class="list-con clearfix" style="border-bottom:1px solid #eee;">
                <el-row>
                    <el-col :span="6">总销售额: {{totalAmount}} 元</el-col>
                    <el-col :span="6">平均值: {{avg}} 元</el-col>
                </el-row>
                <chart-bar :series="chartSeries" :xaxis="chartXAxis" :title="chartTitle" :legend="chartLegend"></chart-bar>
            </el-row>
        </el-card>
    </section>
</template>

<script>
    import api from '@/api/statistics';
    import Util from '@/libs/util';

    import chartBar from '@/components/charts/chartBarNew.vue';

    export default {
        components: {
            chartBar
        },
        data() {
            return {
                tableData1: [],
                gatherForm: {
                    businessValumeForDay: 0,
                    rechargeStatistics: {
                        rechargeAmount: 0.00,
                        giftAmount: 0.00
                    }
                },
                searchFormGather: {
                    dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#'), new Date().customFormat('#YYYY#-#MM#-#DD#')]
                },

                tableData2: [],
                gatherAdvancePaymentForm: {
                    advancePayment: 0
                },
                searchFormGatherAdvancePayment: {
                    date: new Date().customFormat('#YYYY#-#MM#-#DD#')
                },

                searchFormBusiness: {
                    date: new Date().customFormat('#YYYY#-#MM#-#DD#')
                },

                chartSeries: [],
                chartXAxis: [],
                chartTitle: '月份销售额明细',
                chartLegend: []
            };
        },
        methods: {
            outputExcel(param) {
                const params = {
                    param,
                    cityId: Util.getCookie('cityId')
                };
                if (param === 'beforeDelivery') {
                    params.beginDate = this.searchFormGatherAdvancePayment.date;
                    params.endDate = this.searchFormGatherAdvancePayment.date;
                } else {
                    params.beginDate = this.searchFormGather.dateArr[0];
                    params.endDate = this.searchFormGather.dateArr[1];
                }
                console.log(param);
                return location.href = window.vars.URLApiBase + '/stat/statistics/outputExcel?' + Util.qs(params);
            },
            // 获取数据
            gather() {
                const param = {
                    cityId: Util.getCookie('cityId')
                };
                if (this.searchFormGather.dateArr && this.searchFormGather.dateArr.length) {
                    param.beginDate = this.searchFormGather.dateArr[0];
                    param.endDate = this.searchFormGather.dateArr[1];
                }
                console.log(param);
                api.gather(param).then(res => {
                    console.log(res);
                    if (res.code === 100) {
                        this.tableData1 = [
                            {
                                key: 'salesVolume',
                                buttonText: '销售列表',
                                title: '日期',
                                value: this.searchFormGather.dateArr[0] + '~' + this.searchFormGather.dateArr[1]
                            },
                            {
                                key: 'business',
                                buttonText: '导出',
                                title: '总营业额(当日平台总收入)',
                                value: res.content.businessValumeForDay
                            },
                            {
                                key: 'recharge',
                                buttonText: '导出',
                                title: '当日用户充值',
                                value: res.content.rechargeStatistics.rechargeAmount
                            },
                            {
                                title: '当日用户充值赠送',
                                value: res.content.rechargeStatistics.giftAmount,
                                hide: true
                            }
                        ]
                        this.gatherForm = res.content;
                    }
                });
            },
            gatherAdvancePayment() {
                const param = {
                    cityId: Util.getCookie('cityId')
                };
                if (this.searchFormGatherAdvancePayment.date) {
                    param.advancePaymentDate = this.searchFormGatherAdvancePayment.date;
                }
                api.gatherAdvancePayment(param).then(res => {
                    console.log(res);
                    if (res.code === 100) {
                        this.tableData2 = [
                            {
                                key: 'salesVolume',
                                buttonText: '销售列表',
                                title: '日期',
                                value: this.searchFormGatherAdvancePayment.date,
                                hide: true
                            },
                            {
                                key: 'beforeDelivery',
                                buttonText: '导出',
                                title: '营业额预付款部分金额',
                                value: res.content.advancePayment
                            }
                        ]
                        this.gatherAdvancePaymentForm = res.content;
                    }
                });
            },
            business() {
                const thisMonthStart = new Date(new Date(this.searchFormBusiness.date).setDate(1));
                const startDate = Util.formatTime(new Date(thisMonthStart), true);
                // const endDate = Util.formatTime(new Date(thisMonthStart.getFullYear() + '-' + (thisMonthStart.getMonth() + 3) + '-01'), true);
                const endDate = Util.formatTime(new Date(new Date(new Date(this.searchFormBusiness.date).setDate(32)).setDate(1)), true)
                const param = {
                    startDate,
                    endDate,
                    cityId: Util.getCookie('cityId')
                }
                api.business(param).then(res => {
                    // if (res.code === 100) {
                    console.log(res);
                    this.chartXAxis = res.date;
                    const date = startDate.split('-')[0] + '-' + startDate.split('-')[1]
                    // this.chartLegend = { data: [date + '销售额', date + '销售量'], itemGap: 5 };
                    // console.log(this.chartLegend)
                    this.chartSeries = [{ name: date + '销售额', data: res.data }, { name: date + '销售量', data: res.total }];
                    let totalAmount = 0;
                    res.data.forEach(item => {
                        totalAmount += item;
                    })
                    const avg = totalAmount / res.date.length;
                    this.totalAmount = totalAmount.toFixed(2);
                    if (isNaN(avg)) {
                        this.avg = 0;
                    } else {
                        this.avg = avg.toFixed(2);
                    }
                    // }
                });
            }
        },

        created() {
            this.gather();
            this.gatherAdvancePayment();
            this.business();
        }

    };
</script>
