<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="5">
                    <el-date-picker
                        v-model="search.dateArr"
                        @change="handlePagers"
                        type="daterange"
                        align="right"
                        unlink-panels
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
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
                <el-button style="float: right;margin-right: 30px;" type="danger" @click="handleAddExpend">支出
                </el-button>
                <el-button style="float: right;margin-right: 30px;" type="success" @click="handleAddIncome">收入
                </el-button>
            </el-row>
            <el-row class="mb20" :gutter="20">
                &nbsp;&nbsp;&nbsp;&nbsp;计量单位：公斤
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
                    <el-table-column prop="orderNo" label="单号" width="150%" align="center"/>
                    <el-table-column prop="remark" label="摘要" width="250%" align="center"/>
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
                    <el-table-column label="操作" align="center" width="150%">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                v-if="scope.row.orderNo !== '合计'"
                                @click.stop="addSnapshot(scope.row.orderNo)">编辑总账</el-button>

                            <el-button
                                type="text"
                                v-if="scope.row.orderNo !== '合计'"
                                @click.stop="handleDelete(scope.row.orderNo)">删除</el-button>
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

        <add-expend
            :dialogVisible="expendDialogVisible"
            @close="addExpendClose"/>

        <add-income
            :dialogVisible="incomeDialogVisible"
            @close="addIncomeClose"/>

        <editOrderNo
            :dialogVisible="editOrderNoDialogVisible"
            :form="editOrderNoForm"
            @close="editClose"/>

        <add-snapshot
            :dialogVisible="addSnapshotDialogVisible"
            :form="addSnapshotForm"
            @close="addSnapshotClose"/>
    </section>
</template>
<script>

    import api from '@/api/order';
    import addExpend from './addExpend';
    import addIncome from './addIncome';
    import quotaYear from '@/api/quotaYear';
    import util from '@/libs/util';
    import editOrderNo from './editOrderNo';
    import addSnapshot from './addSnapshot';

    const search = {
        dateArr: []
    };

    let quotaYearStartDate = null;
    let quotaYearEndDate = null;

    export default {

        components: {
            addExpend,
            addIncome,
            editOrderNo,
            addSnapshot
        },

        data() {
            return {
                quotaYear: util.getCookie('quotaYear'),

                tableData: [],
                columns: [],
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                search: JSON.parse(JSON.stringify(search)),

                /* 添加支出 */
                expendDialogVisible: false,

                /* 添加收入 */
                incomeDialogVisible: false,

                pickerOptions: {
                    disabledDate(time) {
                        if (!quotaYearEndDate) {
                            return time.getTime() < new Date(quotaYearStartDate);
                        } else {
                            return time.getTime() < new Date(quotaYearStartDate) || time.getTime() > new Date(quotaYearEndDate);
                        }
                    }
                },


                editOrderNoForm: {},
                editOrderNoDialogVisible: false,

                addSnapshotForm: {},
                addSnapshotDialogVisible: false
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
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.search.dateArr && this.search.dateArr.length === 2) {
                    param.orderTimeBegin = this.search.dateArr[0];
                    param.orderTimeEnd = this.search.dateArr[1];
                }
                api.pages(param).then(res => {
                    this.columns = res.columns;
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })
            },

            /**
             * 添加支出订单
             * */
            handleAddExpend() {
                this.expendDialogVisible = true;
            },

            handleAddIncome() {
                this.incomeDialogVisible = true;
            },

            addExpendClose() {
                this.expendDialogVisible = false;
                this.handleSearch();
            },

            addIncomeClose() {
                this.incomeDialogVisible = false;
                this.handleSearch();
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
                const exportUrl = window.vars.URLApiBase + '/order/export';
                // const param = util.qs(this.encapsulateParam());
                return location.href = exportUrl;
            },

            /**
             * 双击单元格
             * */
            handleCellClick(row, column) {
                if (row[column.property] === '合计') {
                    return;
                }
                if (column.property === 'orderNo' || column.property === 'remark') {
                    if (!row[column.property]) {
                        return;
                    }
                    let typeZN = '';
                    if (column.property === 'remark') {
                        this.editOrderNoForm.type = '2';
                        typeZN = '摘要';
                    } else if (column.property === 'orderNo') {
                        this.editOrderNoForm.type = '1';
                        typeZN = '单号';
                    }
                    this.editOrderNoForm.oldValue = row[column.property];
                    this.editOrderNoForm.orderNo = row.orderNo;
                    this.editOrderNoForm.title = `修改 ${row.month}月${row.date}日 ${row.orderNo} ${typeZN}`;
                    this.editOrderNoDialogVisible = true;
                }
            },

            editClose() {
                this.editOrderNoForm = {};
                this.editOrderNoDialogVisible = false;
                this.handlePagers();
            },

            /**
             * 添加分账
             * */
            addSnapshot(orderNo) {
                api.getByOrderNo({ orderNo: orderNo }).then(res => {
                    res.content.orderProductSnapshots.forEach(item => {
                        item.sid = Symbol();
                    });
                    res.content.orderTime = res.content.orderTimeStr;
                    this.addSnapshotForm = res.content;
                    this.addSnapshotDialogVisible = true;
                });
            },

            addSnapshotClose() {
                this.addSnapshotForm = {};
                this.addSnapshotDialogVisible = false;
                this.handlePagers();
            },

            handleDelete(orderNo) {
                this.$confirm('确定立即执行？', '确认', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        orderNo: orderNo
                    };
                    api.delete(param).then(res => {
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
        }
    }
</script>
<style>
</style>
