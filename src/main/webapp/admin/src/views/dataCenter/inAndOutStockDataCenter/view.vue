<template>

    <el-dialog :close-on-click-modal="false" class="dialogw80" title="查看商品入库信息" :visible="true" @close="$emit('close')">
        <el-row>
            <el-col :span="8">入库仓库：{{stockInfo.warehouseName}}</el-col>
            <el-col :span="4">运费标准（元/吨）：{{stockInfo.shippingPrice}}</el-col>
            <el-col :span="4">供应商：{{stockInfo.supplierName}}</el-col>
            <el-col :span="4">采购支付方式：{{stockInfo.purchasePaymentMethod}}</el-col>
            <el-col :span="4">总吨数：{{stockInfo.totalWeight}}</el-col>
        </el-row>
        <el-row class="mt20">
            <p>入库详情：</p>

            <el-table :data="tableDataStockInfo" border>
                <el-table-column v-for="(column,key) in columnsStockInfo" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span v-html="scope.row[column.key]"></span>
                    </template>
                </el-table-column>
            </el-table>
        </el-row>
        <el-row class="mt20">
            <p>审核记录：</p>
            <p v-html="stockInfo.auditRecord"></p>
            <div class="mt20">
                <img v-if="stockInfo.imgArr.length" v-for="(item, key) in stockInfo.imgArr" :src="item" :key="key" alt="">
                <img v-if="stockInfo.imgArr.length === 0" src="/manager/images/refundDefaultImg.png" alt="">
            </div>
        </el-row>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/inAndOutStockDataCenter';
import { warehouseByUserCurrentCity, purchasePaymentMethod } from '@/api/dict';
const columnsStockInfo = [
    { key: 'index', title: '序号' },
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '规格名称' },
    { key: 'cost', title: '进货成本价（采购单价+运费+平摊其它费用）' },
    { key: 'num', title: '入库数量' },
    { key: 'total', title: '总金额' },
    { key: 'comment', title: '备注' }
];
export default {
    props: ['stockNoteId'],
    data() {
        return {
            tableDataStockInfo: [],
            columnsStockInfo: columnsStockInfo,

            warehouseMap: {},
            payMethodMap: {},

            stockInfo: {
                imgArr: []
            }
        };
    },
    methods: {
        // 获取列表
        purchaseStockGetDetail(id) {
            const param = {
                id
            };
            api.purchaseStockGetDetail(param).then(res => {
                if (res.code === 100) {
                    this.stockInfo = res.content;
                    this.stockInfo.warehouseName = this.warehouseMap[res.content.warehouseId];
                    this.stockInfo.purchasePaymentMethod = this.payMethodMap[res.content.purchasePaymentMethod];

                    if (this.stockInfo.imageURLs) {
                        this.stockInfo.imgArr = this.stockInfo.imageURLs.split(',');
                    } else {
                        this.stockInfo.imgArr = [];
                    }

                    this.tableDataStockInfo = res.content.stockNoteDetailInfoVOlist.map((item, index) => {
                        item.index = index + 1;
                        item.total = (item.cost & item.num).toFixed(2);
                        return item;
                    });
                    window.LOG(this.tableDataStockInfo);
                }
            });
        },
        // 根据用户获取冻库
        warehouseByUserCurrentCity() {
            return warehouseByUserCurrentCity().then(res => {
                if (res.code === 100) {
                    this.warehouseMap = res.content;
                }
            });
        },
        // 支付方式
        purchasePaymentMethod() {
            return purchasePaymentMethod().then(res => {
                if (res.code === 100) {
                    this.payMethodMap = res.content;
                }
            });
        }
    },
    created() {
        Promise.all([this.warehouseByUserCurrentCity(), this.purchasePaymentMethod()]).then(() => {
            this.purchaseStockGetDetail(this.stockNoteId);
        });
    }
};
</script>
