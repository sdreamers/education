<template>
    <el-dialog :close-on-click-modal="false" title="库存变动记录" :visible="true" @close="$emit('close')">
        <el-row :gutter="20">
            <!-- <el-col :span="8"> -->
                <el-date-picker
                    v-model="formStockRecord.dateStart"
                    :type="pickerType"
                    :value-format="pickerFormat"
                    align="right"
                    unlink-panels
                    placeholder="开始日期">
                </el-date-picker>
            <!-- </el-col> -->
            <!-- <el-col :span="8"> -->
                <el-date-picker
                    v-model="formStockRecord.dateEnd"
                    :type="pickerType"
                    :value-format="pickerFormat"
                    align="right"
                    unlink-panels
                    placeholder="结束日期">
                </el-date-picker>
            <!-- </el-col> -->
            <!-- <el-col :span="4" :offset="4"> -->
                <el-button type="primary" @click="stockRecordPagers">查询</el-button>
            <!-- </el-col> -->
        </el-row>

        <el-table class="mt20" :data="tableDataStockRecord" border>
            <el-table-column v-for="(column,key) in columnsStockRecord" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
        </el-table>
        <div class="pager-wrapper">
            <el-pagination
            @size-change="handleSizeChangeStock"
            @current-change="handleCurrentChangeStock"
            :current-page.sync="currentPageStock"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSizeStock"
            layout="total, sizes, slot, prev, pager, next"
            :total="totalPageStock">
                <span class="el-pagination__total refresh" @click="handleCurrentChangeStock(currentPageStock)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/productMaintain';
import Util from '@/libs/util';
const columnsStockRecord = [
    { key: 'productName', title: '商品名称', width: '100px' },
    { key: 'specificationName', title: '规格名称', width: '100px' },
    { key: 'subType', title: '单据类型', width: '100px' },
    { key: 'num', title: '数量', width: '100px' },
    { key: 'stockTime', title: '操作时间' }
];

const subTypeSymbol = {
    1: '+',
    2: '+',
    3: '-',
    4: '-',
    5: '+'
}

const subTypeMap = {
    1: '入库',
    2: '盘盈',
    3: '盘亏',
    4: '售出',
    5: '退货'
}

export default {
    props: ['formStockRecord', 'formLabelWidth', 'warehouseId'],
    data() {
        return {

            pickerType: 'date',
            pickerFormat: 'yyyy-MM-dd',

            tableDataStockRecord: [],
            columnsStockRecord: columnsStockRecord,

            currentPageStock: 1,
            pageSizeStock: 5,
            totalPageStock: 0
        };
    },
    methods: {
        // 每页显示数改变时
        handleSizeChangeStock(pageSize) {
            this.pageSizeStock = pageSize;
            this.stockRecordPagers();
        },
        // 翻页时
        handleCurrentChangeStock(currentPage) {
            this.currentPageStock = currentPage;
            this.stockRecordPagers();
        },
        stockRecordPagers() {
            console.log('stockRecordPagers');
            const param = {
                page: this.currentPageStock,
                limit: this.pageSizeStock,
                productSpecificationId: this.formStockRecord.productSpecificationId
            };

            param.startTime = this.formStockRecord.dateStart ? Util.formatTime(new Date(this.formStockRecord.dateStart), true) : undefined;
            param.endTime = this.formStockRecord.dateEnd ? Util.formatTime(new Date(this.formStockRecord.dateEnd), true) : undefined;

            if (param.startTime || param.endTime) {
                if (!param.startTime || !param.endTime) {
                    return this.$notify.error('请正确输入起止时间');
                }
                if (new Date(param.startTime) > new Date(param.endTime)) {
                    return this.$notify.error('开始时间不得晚于结束时间');
                }
            }

            api.stockRecordPagers(param).then(res => {
                if (res.records) {
                    this.tableDataStockRecord = res.records.map(item => {
                        item.stockTime = Util.formatTime(new Date(item.stockTime));
                        item.num = subTypeSymbol[item.subType] + item.num.toString();
                        item.subType = subTypeMap[item.subType];
                        // subTypeSymbol[item.subType] + item.num.toString();
                        return item;
                    });
                    this.totalPageStock = res.total;
                } else {
                    this.tableDataStockRecord = [];
                    this.totalPageStock = 0;
                }
            });
        }
    },
    created() {
        this.stockRecordPagers();
    }
};
</script>
