<template>
    <el-dialog :close-on-click-modal="false" title="查看商品入库信息" :visible="true" @close="$emit('close')" class="dialogw90">
        <el-row>
            <el-col :span="4">总吨数: {{form.totalWeight}}</el-col>
        </el-row>
        <el-row class="mt20">
            <h5>入库详情：</h5>
            <el-table :data="tableDataDetailFormatted" border>
                <el-table-column v-for="(column,key) in columnsDetail" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
                    </template>
                </el-table-column>
            </el-table>
        </el-row>
    </el-dialog>
</template>
<script>
import api from '@/api/purchaseStockNote';
import Util from '@/libs/util';
const form = {
    sortNo: '',
    name: '',
    imageurls: []
};
const columnsDetail = [
    { key: 'index', title: '序号' },
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '规格名称' },
    { key: 'specificationUnit', title: '单位' },
    { key: 'num', title: '入库数量' },
    { key: 'comment', title: '备注' }
];
export default {
    props: ['detailID', 'warehouseMap', 'purchasePaymentMethodMap'],
    data() {
        return {
            tableDataDetail: [],
            columnsDetail: columnsDetail,
            form: JSON.parse(JSON.stringify(form))
        };
    },
    computed: {
        tableDataDetailFormatted() {
            let totalPrice = 0, num = 0;
            const arr = this.tableDataDetail.map(item => {
                item.totalPrice = (parseFloat(item.cost) * parseFloat(item.num)).toFixed(2);
                totalPrice += parseFloat(item.totalPrice);
                num += parseFloat(item.num);
                return item;
            });
            arr.push({ totalPrice: totalPrice.toFixed(2), num: num.toFixed(2) });
            return arr;
        }
    },
    methods: {
        getDetails(id) {
            const param = {
                id
            };
            api.getDetails(param).then(res => {
                if (res.code === 100) {
                    res.content.warehouseId = this.warehouseMap[res.content.warehouseId];
                    res.content.purchasePaymentMethod = this.purchasePaymentMethodMap[res.content.purchasePaymentMethod];

                    res.content.imageurls = res.content.imageurls ? res.content.imageurls.split(',') : [];
                    this.tableDataDetail = res.content.purchaseStockNoteDetailInfoVOList ? res.content.purchaseStockNoteDetailInfoVOList.map((item, index) => {
                        item.index = index + 1;
                        item.viewCost = Util.precisionMath(item.cost);
                        console.log(item.cost);
                        return item;
                    }) : [];
                    console.log(res.content);
                    this.form = res.content;
                }
            });
        }
    },
    created() {
        this.getDetails(this.detailID);
    }
};
</script>
