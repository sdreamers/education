<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" v-if="G.permission['_stock/purchaseStockNote/insert']" @click="$router.push({name: '/stock/purchaseStockNote/insert'})">添加采购</el-button>
                <el-button style="float: right;margin-left: 10px;" @click="print()" v-if="G.permission['_stock/purchaseStockNote/listPrint']">打印采购单</el-button>
                <el-button style="float: right;margin-left: 10px;" @click="printOut()" v-if="G.permission['_stock/purchaseStockNote/listOutPrint']">打印入库单</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="6">
                            <p style="font-size:14px">制单日期</p>
                            <el-date-picker
                                v-model="search.date"
                                @change="purchaseStockNotePagers"
                                type="daterange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="制单开始日期"
                                end-placeholder="制单结束日期">
                            </el-date-picker>
                        </el-col>
                        <el-col :span="4">
                            <p style="font-size:14px">搜索</p>
                            <div style="width:150px">
                                <el-button type="primary" @click="handleSearch">搜索</el-button>
                                <el-button @click="handleClear">清除</el-button>
                            </div>
                        </el-col>
                    </el-row>
                    <el-table :data="tableDataFormatted" border v-loading="loading" @selection-change="handleSelectTable">
                        <el-table-column
                            type="selection"
                            width="55">
                        </el-table-column>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                                <span v-if="column.key === 'openDetails'"><el-button type="text" @click="handleOpenDetails(scope.row)" v-if="G.permission['_stock/purchaseStockNote/getDetails']">详情</el-button></span>
                                <span v-if="column.key === 'openAudit'"><el-button type="text" @click="handleOpenAudit(scope.row)" v-if="G.permission['_stock/purchaseStockNote/exportExcel']">导出</el-button></span>
                                <span v-if="column.key === 'edit' && scope.row.editFlag && G.permission['_stock/purchaseStockNote/get']"><el-button type="text" @click="handleEdit(scope.row)">编辑</el-button></span>
                                <span v-if="column.key === 'audit' && scope.row.auditFlag && G.permission['_stock/purchaseStockNote/getAudit']"><el-button type="text" @click="handleAudit(scope.row)">审批</el-button></span>
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
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
            </el-row>
        </el-card>
        <viewInfo
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,purchaseStockNotePagers()"
            :detailID="detailID"
            :warehouseMap="warehouseMap"
            :purchasePaymentMethodMap="purchasePaymentMethodMap"></viewInfo>
    </section>
</template>
<script>
import api from '@/api/purchaseStockNote';
import { warehouseByUserCurrentCity, purchasePaymentMethod } from '@/api/dict';
import Util from '@/libs/util';

import viewInfo from './viewInfo';

const columns = [
    { key: 'id', title: '序号' },
    { key: 'purchaserName', title: '采购制单人' },
    { key: 'purchaserTime', title: '采购制单时间' },
    { key: 'openDetails', title: '查看详情' },
    { key: 'openAudit', title: '导出Excel' }
];
export default {
    components: {
        viewInfo
    },
    data() {
        return {
            search: {
                warehouseId: '',
                supplierId: '',
                status: '',
                date: [new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 00:00:00', new Date().customFormat('#YYYY#-#MM#-#DD#') + ' 23:59:59'],
                auditDateArr: []
            },
            selection: [],

            workflowStatusMap: {},
            workflowStatusList: [],

            purchasePaymentMethodMap: {},
            purchasePaymentMethodList: [],

            supplierList: [],
            statusList: [],

            warehouseList: [],
            warehouseMap: {},

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 20,

            loading: false,

            detailID: '',

            dialogFormVisible: false,
            dialogFormEditVisible: false,
            formLabelWidth: '120px',
            formEdit: {},
            formType: '',
            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    computed: {
        tableDataFormatted() {
            return this.tableData.map(item => {
                item.warehouseId = this.warehouseMap[item.warehouseId];
                item.purchaserTime = Util.formatTime(new Date(item.purchaserTime));
                item.warehousingTime = Util.formatTime(new Date(item.purchaserTime));
                item.status = this.statusMap[item.status];
                return item;
            });
        }
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.purchaseStockNotePagers();
        },
        // 清除搜索
        handleClear() {
            this.currentPage = 1;

            this.search.warehouseId = '';
            this.search.supplierId = '';
            this.search.status = '';
            this.search.date = [];
            this.search.auditDateArr = [];
            this.purchaseStockNotePagers();
        },
        handleOpenDetails(data) {
            this.detailID = data.id;
            this.dialogFormVisible = true;
        },
        handleOpenAudit(data) {
            window.LOG(data);
            if (data.id) {
                location.href = vars.URLApiBase + '/stock/purchaseStockNote/exportExcel?id=' + data.id;
            } else {
                this.$notify.error('异常：无 ID');
            }
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.$router.push({ name: '/stock/purchaseStockNote/get', params: { id: data.id } });
        },
        handleAudit(data) {
            this.$router.push({ name: '/stock/purchaseStockNote/getAudit', params: { id: data.id } });
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.purchaseStockNotePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.purchaseStockNotePagers();
        },
        // 获取数据
        purchaseStockNotePagers() {
            if (!this.G.permission['_stock/purchaseStockNote/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize,
                warehouseId: this.search.warehouseId || undefined,
                supplierId: this.search.supplierId || undefined,
                status: this.search.status || undefined,
                purchaserStartTime: this.search.date && this.search.date.length ? Util.formatTime(new Date(this.search.date[0]), true) : undefined,
                purchaserEndTime: this.search.date && this.search.date.length ? Util.formatTime(new Date(this.search.date[1]), true) : undefined,
                auditStartTime: this.search.auditDateArr && this.search.auditDateArr.length ? this.search.auditDateArr[0] : undefined,
                auditEndTime: this.search.auditDateArr && this.search.auditDateArr.length ? this.search.auditDateArr[1] : undefined
            };
            api.purchaseStockNotePagers(param).then(res => {
                window.LOG(res);
                this.tableData = res.records || [];
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        if (item.status === '4') {
                            item.lastAuditDateTime = Util.parseTimeStr(item.lastAuditDateTime);
                        } else {
                            item.lastAuditDateTime = '';
                        }
                        return item;
                    });
                }

                this.totalPage = res.total;
            });
        },
        getSupplier() {
            api.getSupplier().then(res => {
                if (res.code === 100) {
                    const arr = res.content;
                    arr.unshift({ id: '', name: '请选择' })
                    this.supplierList = arr;
                }
            });
        },
        getWorkflow() {
            this.workflowStatusMap = { 0: '待审核', 1: '审核驳回', 2: '审核通过' };
            this.workflowStatusList = [{ id: '', name: '请选择' }, { id: '0', name: '待审核' }, { id: '1', name: '审核驳回' }, { id: '2', name: '审核通过' }];
        },
        getStatus() {
            this.statusMap = { 0: '草稿', 1: '待审批', 4: '审批通过', 5: '审批驳回' };
            this.statusList = [{ id: '', name: '请选择' }, { id: '0', name: '草稿' }, { id: '1', name: '待审批' }, { id: '4', name: '审批通过' }, { id: '5', name: '审批驳回' }];
        },
        warehouseByUserCurrentCity() {
            return warehouseByUserCurrentCity().then(res => {
                if (res.code === 100) {
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    arr.unshift({ id: '', name: '请选择' });
                    this.warehouseList = arr;
                    this.warehouseMap = res.content;
                }
            });
        },
        handleSelectTable(selection) {
            this.selection = selection;
        },
        purchasePaymentMethod() {
            return purchasePaymentMethod().then(res => {
                if (res.code === 100) {
                    const arr = [];
                    for (const item in res.content) {
                        if (res.content.hasOwnProperty(item)) {
                            const ele = res.content[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.purchasePaymentMethodList = arr;
                    this.purchasePaymentMethodMap = res.content;
                }
            });
        },

        printOut() {
            const ids = this.selection.map(item => item.id).join(',');
            if (ids.length < 1) {
                this.$notify.error('请选择入库单');
                return
            }
            const data = { ids: ids };
            api.listOutPrint(data).then(res => {
                console.log(res);
                if (res.code === 100) {
                    for (let i = 0; i < res.content.length; i++) {
                        const htmlArray = getPrintOutWarehousingArr(res.content[i]);
                        for (const j in htmlArray) {
                            LODOP.SET_PRINT_PAGESIZE(0, 2413, 1397, 'customerPageSize');
                            LODOP.ADD_PRINT_HTM(0, 30, '100%', '100%', htmlArray[j]);
                            // 真机打印
                            LODOP.PRINT();
                            // 打印预览
                            // LODOP.PREVIEW();
                        }
                    }
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            })
        },

        print() {
            const ids = this.selection.map(item => item.id).join(',');
            console.log(ids);
            if (ids.length < 1) {
                this.$notify.error('请选择入库单');
                return
            }
            const data = { ids: ids };
            api.listPrint(data).then(res => {
                console.log(res);
                if (res.code === 100) {
                    for (let i = 0; i < res.content.length; i++) {
                        const htmlArray = getPrintWarehousingArr(res.content[i]);
                        for (const j in htmlArray) {
                            LODOP.SET_PRINT_PAGESIZE(0, 2413, 1397, 'customerPageSize');
                            LODOP.ADD_PRINT_HTM(0, 30, '100%', '100%', htmlArray[j]);
                            // 真机打印
                            LODOP.PRINT();
                            // 打印预览
                            // LODOP.PREVIEW();
                        }
                    }
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            })
        }
    },
    created() {
        this.purchasePaymentMethod();
        this.getStatus();
        this.getWorkflow();
        this.getSupplier();
        this.warehouseByUserCurrentCity();
        this.purchaseStockNotePagers();
    }
};
</script>
