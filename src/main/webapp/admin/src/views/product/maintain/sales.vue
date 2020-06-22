<template>
    <el-dialog :close-on-click-modal="false" title="查看商品规格的销量趋势图" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-row :gutter="20">
            <!-- <el-col :span="4"> -->
                <el-select v-model="formSales.dateType" placeholder="请选择分组" @change="handleDateTypeChange">
                    <el-option v-for="(item,key) in dateType" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            <!-- </el-col>
            <el-col :span="8"> -->
                <el-date-picker
                    v-model="dateStart"
                    :type="pickerType"
                    :value-format="pickerFormat"
                    align="right"
                    unlink-panels
                    placeholder="开始日期">
                </el-date-picker>
            <!-- </el-col>
            <el-col :span="8"> -->
                <el-date-picker
                    v-model="dateEnd"
                    :type="pickerType"
                    :value-format="pickerFormat"
                    align="right"
                    unlink-panels
                    placeholder="结束日期">
                </el-date-picker>
            <!-- </el-col>
            <el-col :span="4"> -->
                <el-button type="primary" @click="handleSalesSearch">查询</el-button>
                <el-button type="primary" @click="handleResetDate">重置日期</el-button>
            <!-- </el-col> -->
        </el-row>
        <chart-bar :series="chartSeries" :xaxis="chartXAxis" :title="chartTitle"></chart-bar>
    </el-dialog>
</template>
<script>
import api from '@/api/productMaintain';
import chartBar from '@/components/charts/chartBar.vue';
import util from '@/libs/util.js';

const dateTypeCheck = {
    0: '日期', 1: '月份', 2: '年份'
};
const date_ = new Date();
const year = date_.getFullYear();
const month = date_.getMonth();
export default {
    props: ['formSales', 'formLabelWidth', 'warehouseId'],
    components: {
        chartBar
    },
    data() {
        return {
            dateType: [
                { id: 0, name: '按日期' },
                { id: 1, name: '按月份' },
                { id: 2, name: '按年份' }
            ],
            dateTypeCheck: dateTypeCheck,


            dateStart: util.formatTime(new Date(year, month, 1)).split(' ')[0],
            dateEnd: util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0],

            pickerType: 'date',
            pickerFormat: 'yyyy-MM-dd',

            chartSeries: [],
            chartXAxis: [],
            chartTitle: ''
        };
    },
    methods: {
        handleResetDate() {
            this.formSales.dateType = 0;
            this.handleDateTypeChange();
            this.dateStart = util.formatTime(new Date(year, month, 1)).split(' ')[0];
            this.dateEnd = util.formatTime(new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)).split(' ')[0];
            this.handleSalesSearch();
        },
        handleDateTypeChange() {
            if (this.formSales.dateType === 0) {
                this.pickerType = 'date';
                this.pickerFormat = 'yyyy-MM-dd';
            } else if (this.formSales.dateType === 1) {
                this.pickerType = 'month';
                this.pickerFormat = 'yyyy-MM';
            } else if (this.formSales.dateType === 2) {
                this.pickerType = 'year';
                this.pickerFormat = 'yyyy';
            }
            this.dateStart = '';
            this.dateEnd = '';
            if (this.formSales.dateType === 0) {
                this.chartTitle = '每日销售量趋势图';
            } else if (this.formSales.dateType === 1) {
                this.chartTitle = '每月销售量趋势图';
            } else if (this.formSales.dateType === 2) {
                this.chartTitle = '每年销售量趋势图';
            }
        },
        handleSalesSearch() {
            const param = {
                dateType: this.formSales.dateType,
                productSpecificationId: this.formSales.productSpecificationId,
                deliveryDateBegin: this.dateStart,
                deliveryDateEnd: this.dateEnd
            };
            if (!param.deliveryDateBegin || !param.deliveryDateEnd) {
                return this.$notify.error('请选择日期');
            }
            if (!util.compareDateString(param.deliveryDateBegin, param.deliveryDateEnd)) {
                return this.$notify.error(`开始${this.dateTypeCheck[param.dateType]}不能大于结束${this.dateTypeCheck[param.dateType]}`);
            }
            api.statProductSales(param).then(res => {
                const arr = [];
                const xaxis = [];
                const data = res.content;
                for (const item in data) {
                    if (data.hasOwnProperty(item)) {
                        const ele = data[item];
                        arr.push(ele);
                        xaxis.push(item);
                    }
                }
                if (this.formSales.dateType === 0) {
                    this.chartTitle = '每日销售量趋势图';
                } else if (this.formSales.dateType === 1) {
                    this.chartTitle = '每月销售量趋势图';
                } else if (this.formSales.dateType === 2) {
                    this.chartTitle = '每年销售量趋势图';
                }
                this.chartXAxis = xaxis;
                this.chartSeries = arr;
            });
        }
    },
    created() {
        this.handleSalesSearch();
    }
};
</script>
