<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-select v-model="search.merchantId" @change="handlePagers" placeholder="单位名称">
                        <el-option v-for="(item,key) in merchantArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="5">
                    <el-date-picker
                        v-model="search.dateArr"
                        @change="handleDateChange"
                        type="daterange"
                        align="right"
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
                        unlink-panels
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading" @cell-dblclick="handleCellClick">
                    <el-table-column :label="quotaYear" align="center">
                        <el-table-column
                            prop="month"
                            align="center"
                            width="50%"
                            label="月">
                        </el-table-column>
                        <el-table-column
                            prop="date"
                            align="center"
                            width="50%"
                            label="日">
                        </el-table-column>
                    </el-table-column>
                    <el-table-column prop="orderNo" label="单号" width="200%" align="center"/>
                    <el-table-column prop="remark" label="摘要" width="300%" align="center"/>
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center">
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key"
                                         :label="subColumn.title" align="center" :key="subKey">
                            <template slot-scope="scope">
                                <span>{{scope.row[subColumn.key]}}</span>
                            </template>
                        </el-table-column>
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                v-if="scope.row.orderProductSnapshotId"
                                @click.stop="handleDelete(scope.row.orderProductSnapshotId)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i
                            class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <amount-edit
            :dialogVisible="amountEditDialogVisible"
            :form="amountEditForm"
            @close="amountEditClose"/>
    </section>
</template>
<script>

    import api from '@/api/order';
    import merchant from '@/api/merchant';
    import util from '@/libs/util';
    import amountEdit from './amountEdit';
    import quotaYear from '@/api/quotaYear';

    const search = {
        dateArr: []
    };

    let quotaYearStartDate = null;
    let quotaYearEndDate = null;

    export default {

        components: {
            amountEdit
        },

        data() {
            return {
                tableData: [],
                columns: [],
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                search: JSON.parse(JSON.stringify(search)),
                merchantArr: [],

                /* 添加支出 */
                expendDialogVisible: false,

                /* 添加收入 */
                incomeDialogVisible: false,

                quotaYear: util.getCookie('quotaYear') + '年',

                amountEditDialogVisible: false,
                amountEditForm: {},

                hiddenMonth: '',
                hiddenDate: '',
                hiddenOrderNo: '',

                pickerOptions: {
                    disabledDate(time) {
                        if (!quotaYearEndDate) {
                            return time.getTime() < new Date(quotaYearStartDate);
                        } else {
                            return time.getTime() < new Date(quotaYearStartDate) || time.getTime() > new Date(quotaYearEndDate);
                        }
                    }
                }
            }
        },

        methods: {
            handleSizeChange(size) {
                this.pageSize = size;
                this.handlePagers();
            },

            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.handlePagers();
            },

            handleClear() {
                this.search = JSON.parse(JSON.stringify(search));
                this.handlePagers();
            },

            handleSearch() {
                if (!this.search.merchantId) {
                    return this.$notify.error('请选择单位');
                }
                this.handlePagers();
            },

            handleDateChange() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    merchantId: this.search.merchantId
                };
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.orderTimeBegin = this.search.dateArr[0];
                    param.orderTimeEnd = this.search.dateArr[1];
                }
                api.separatePages(param).then(res => {
                    this.columns = res.columns;
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })
            },

            querySearchMerchant(merchantName, cb) {
                if (!merchantName) {
                    return;
                }
                const param = { searchStr: merchantName };
                merchant.search(param).then(res => {
                    if (res && res.code === 100) {
                        const merchantArr = [];
                        res.content.forEach(item => {
                            merchantArr.push({ value: item.name, id: item.id });
                        });
                        cb(merchantArr);
                    }
                })
            },

            handleSelectMerchant(item) {
                this.search.merchantId = item.id;
            },

            merchantNameChange(row) {
                if (!row.merchantName) {
                    this.search.merchantId = '';
                }
            },

            /**
             * 双击单元格
             * */
            handleCellClick(row, column) {
                if (!row.orderProductSnapshotId) {
                    return;
                }
                if (column.property.endsWith('-income') || column.property.endsWith('-expend') || column.property.endsWith('-actualExpend') || column.property === 'remark') {
                    if (!row[column.property]) {
                        return;
                    }
                    let typeZN = '';
                    if (column.property.endsWith('-income')) {
                        this.amountEditForm.type = '1';
                        typeZN = '收入';
                    } else if (column.property.endsWith('-expend')) {
                        this.amountEditForm.type = '2';
                        typeZN = '支出';
                    } else if (column.property.endsWith('-actualExpend')) {
                        this.amountEditForm.type = '3';
                        typeZN = '实际支出';
                    } else if (column.property === 'remark') {
                        this.amountEditForm.type = '4';
                        typeZN = '摘要';
                    }
                    this.amountEditForm.oldAmount = row[column.property];
                    this.amountEditForm.orderProductSnapshotId = row.orderProductSnapshotId;
                    this.amountEditForm.title = `修改 ${row.hiddenMonth}月${row.hiddenDate}日 ${row.hiddenOrderNo}-${row.remark} ${typeZN}`;
                    this.amountEditDialogVisible = true;
                }
            },

            amountEditClose() {
                this.amountEditForm = {};
                this.amountEditDialogVisible = false;
                this.handlePagers();
            },

            listAllMerchant() {
                merchant.list().then(res => {
                    if (res.code === 100) {
                        this.merchantArr = res.content;
                    }
                })
            },

            handleGetQuotaYear() {
                quotaYear.get({ year: util.getCookie('quotaYear') }).then(res => {
                    if (res.code === 100 && res.content) {
                        quotaYearStartDate = res.content.startDate;
                        if (res.content.endDate) {
                            quotaYearEndDate = res.content.endDate;
                        }
                    }
                })
            },

            handleExport() {
                if (!this.search.merchantId) {
                    return this.$notify.error('请先选择单位');
                }
                const param = {
                    merchantId: this.search.merchantId
                };
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.orderTimeBegin = this.search.dateArr[0];
                    param.orderTimeEnd = this.search.dateArr[1];
                }
                const exportUrl = window.vars.URLApiBase + '/order/exportSeparate';
                return location.href = exportUrl + '?' + util.qs(param);
            },

            handleDelete(orderProductSnapshotId) {
                this.$confirm('确定立即执行？', '确认', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        orderProductSnapshotId: orderProductSnapshotId
                    };
                    api.deleteOrderProductSnapshot(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.handlePagers();
                        }
                    });
                })
            }
        },

        created() {
            this.handleGetQuotaYear();
            this.handlePagers();
            this.listAllMerchant();
        }
    }
</script>
<style>
</style>
