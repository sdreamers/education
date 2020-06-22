<template>
    <el-dialog :close-on-click-modal="false" title="关联的订单" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-row :gutter="20">
            <el-col :span="4">
                <el-input v-model="orderNo" placeholder="查询订单号"></el-input>
            </el-col>
            <!-- <el-col :span="4"> -->
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="handleClear">清除</el-button>
            <!-- </el-col> -->
        </el-row>
        <el-table :data="handleFormatData" border class="mt20" v-loading="loading">
            <el-table-column v-for="(column,key) in columnsOrder" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
        </el-table>
        <div class="pager-wrapper">
            <el-pagination
            @size-change="handleSizeChangeOrder"
            @current-change="handleCurrentChangeOrder"
            :current-page.sync="currentPageOrder"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSizeOrder"
            layout="total, sizes, slot, prev, pager, next"
            :total="totalPageOrder">
                <span class="el-pagination__total refresh" @click="handleCurrentChangeOrder(currentPageOrder)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/deliveryman';
import { getOrderStatusMap } from '@/api/dict'

const columnsOrder = [
    { key: 'orderNo', title: '订单号', width: '80px' },
    { key: 'shippingPrice', title: '运费金额', width: '48px' },
    { key: 'orderStatus', title: '订单状态', width: '48px' },
    { key: 'appointDeliveryStartTimeStr', title: '预送达时间', width: '48px' },
    { key: 'deliveryTime', title: '交货时间', width: '48px' },
    { key: 'consigneeAddress', title: '收件人地址', width: '48px' },
    { key: 'consigneeTelephone', title: '收件人电话', width: '48px' }
];
export default {
    props: ['deliverymanId'],

    computed: {
        handleFormatData() {
            if (this.tableDataOrder) {
                this.tableDataOrder.forEach(item => {
                    item.orderStatus = this.orderStatusMap[item.orderStatus];
                });
            }
            return this.tableDataOrder;
        }
    },

    data() {
        return {
            orderNo: '',
            loading: false,
            tableDataOrder: [],
            columnsOrder: columnsOrder,

            currentPageOrder: 1,
            totalPageOrder: 0,
            pageSizeOrder: 10,

            orderStatusMap: {}
        };
    },
    methods: {
        handleSearch() {
            this.currentPageOrder = 1;
            this.orderPagers();
        },
        handleClear() {
            this.orderNo = '';
            this.orderPagers();
        },
        handleSizeChangeOrder(pageSize) {
            this.pageSizeOrder = pageSize;
            this.orderPagers();
        },
        handleCurrentChangeOrder(currentPage) {
            this.currentPageOrder = currentPage;
            this.orderPagers();
        },
        orderPagers() {
            const param = {
                deliverymanId: this.deliverymanId,
                page: this.currentPageOrder,
                limit: this.pageSizeOrder
            };
            if (this.orderNo) {
                param.orderNo = this.orderNo
            }
            this.loading = true;
            api.orderPagers(param).then(res => {
                this.loading = false;
                if (res.records) {
                    this.tableDataOrder = res.records.map(item => {
                        item.deliveryTime = item.deliveryTime ? new Date(item.deliveryTime).customFormat('#YYYY#/#MM#/#DD# #AMPM##hh#:#mm#') : '';
                        return item;
                    });
                } else {
                    this.tableDataOrder = [];
                    this.$notify.info('无数据');
                }
                this.totalPageOrder = res.total;
            });
        },

        orderStatus() {
            return getOrderStatusMap().then(res => {
                if (res.code === 100) {
                    this.orderStatusMap = res.content;
                }
            });
        }
    },
    created() {
        this.orderStatus();
        this.orderPagers();
    }
};
</script>
