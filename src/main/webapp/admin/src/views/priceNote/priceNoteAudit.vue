<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-row>

                    <!-- <el-col :span="4"> -->
                    <el-form-item label="操作员:">
                        <el-input v-model="searchForm.operatorName" placeholder="搜索操作员"></el-input>
                    </el-form-item>
                    <!-- </el-col> -->

                    <!-- <el-col :span="4"> -->
                    <el-form-item label="审核人:">
                        <el-input v-model="searchForm.auditorName" placeholder="搜索审核人"></el-input>
                    </el-form-item>
                    <!-- </el-col> -->

                    <!-- <el-col :span="4"> -->
                    <el-form-item label="审核状态:">
                        <el-select v-model="searchForm.status" placeholder="请选择">
                            <el-option
                                v-for="(item,key) in auditStatusList"
                                :label="item"
                                :value="key"
                                :key="key">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <!-- </el-col> -->
                    <!-- <el-col :span="8"> -->
                    <el-form-item label="审核日期">
                        <el-date-picker
                            v-model="searchForm.auditDateArr"
                            type="daterange"
                            align="right"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd">
                        </el-date-picker>
                    </el-form-item>
                    <!-- </el-col> -->
                <!-- </el-row> -->
                <!-- <el-row> -->

                    <!-- <el-col :span="8"> -->
                    <el-form-item label="操作日期">
                        <el-date-picker
                            v-model="searchForm.opDateArr"
                            type="daterange"
                            align="right"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd">
                        </el-date-picker>
                    </el-form-item>
                    <!-- </el-col> -->
                    <!-- <el-col :span="4"> -->
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear(searchForm)">清空</el-button>
                    </el-form-item>
                    <!-- </el-col> -->
                </el-row>
            </el-form>

            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key === 'priceChange'" v-html="priceChange(scope.row)"></span>
                            <span v-else v-html="scope.row[column.key]"></span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" width="150px">
                        <template slot-scope="scope">
                            <el-button
                                type="success"
                                v-if="scope.row.status === '0'"
                                @click.stop="handleAuditor(scope.row.id,1)">通过</el-button>
                            <el-button
                                type="danger"
                                v-if="scope.row.status === '0'"
                                @click.stop="handleAuditor(scope.row.id,2)">拒绝</el-button>
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
    </section>
</template>

<script>
    import api from '@/api/priceNote';
    import Form from '@/libs/form';
    import Util from '@/libs/util';
    import { getPriceStatus } from '@/api/dict';

    const columns = [
        { key: 'productName', title: '商品名' },
        { key: 'specificationName', title: '规格名' },
        { key: 'oldPrice', title: '旧售价' },
        { key: 'newPrice', title: '新售价' },
        { key: 'priceChange', title: '价格变动' },
        { key: 'statusName', title: '审核状态' },
        { key: 'operatorName', title: '操作员' },
        { key: 'operationTime', title: '操作时间', width: '150px' },
        { key: 'auditorName', title: '审核人' },
        { key: 'auditTime', title: '审核时间', width: '150px' }
    ];
    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                loading: false,

                searchForm: {
                    operatorName: '',
                    auditorName: '',
                    status: '',
                    auditDateArr: [],
                    opDateArr: [new Date(), new Date()]
                },

                auditStatusList: {},

                sumNum: 0,
                sumCost: 0
            };
        },
        methods: {
            handleSearch() {
                this.pagersData(true);
            },
            handleClear() {
                Form.initForm(this.searchForm);
                this.pagersData(true);
            },
            priceChange(data) {
                const newPrice = Number(data.newPrice);
                const oldPrice = Number(data.oldPrice);
                if (isNaN(newPrice) || isNaN(oldPrice)) {
                    return '';
                }
                if (newPrice > oldPrice) {
                    return '<b style="color:green">涨：' + Math.abs(oldPrice - newPrice).toFixed(2) + '</b>';
                } else {
                    return '<b style="color:red">跌：' + Math.abs(oldPrice - newPrice).toFixed(2) + '</b>';
                }
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.pagersData(false);
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.pagersData(false);
            },
            // 获取数据
            pagersData(isInitPage) {
                if (!this.G.permission['_stock/priceNote/pages']) {
                    return this.$notify.error('没有列表权限');
                }
                const param = {
                    page: 1,
                    limit: this.pageSize
                };
                if (!isInitPage) {
                    param.page = this.currentPage;
                    param.limit = this.pageSize;
                }

                if (this.searchForm.operatorName) {
                    param.operatorName = this.searchForm.operatorName;
                }
                if (this.searchForm.auditorName) {
                    param.auditorName = this.searchForm.auditorName;
                }
                if (this.searchForm.status) {
                    param.status = this.searchForm.status;
                }
                if (this.searchForm.auditDateArr && this.searchForm.auditDateArr.length) {
                    param.auditStartTime = Util.formatTime(new Date(this.searchForm.auditDateArr[0]), true);
                    param.auditEndTime = Util.formatTime(new Date(this.searchForm.auditDateArr[1]), true);
                }
                if (this.searchForm.opDateArr && this.searchForm.opDateArr.length) {
                    param.operationStartTime = Util.formatTime(new Date(this.searchForm.opDateArr[0]), true);
                    param.operationEndTime = Util.formatTime(new Date(this.searchForm.opDateArr[1]), true);
                }
                api.priceNotePagers(param).then(res => {
                    this.tableData = res.records.map(item => {
                        // 时间初始化去掉  .0
                        if (item.auditTime) {
                            item.auditTime = item.auditTime.replace(/.[0-9]*$/, '');
                        }
                        if (item.operationTime) {
                            item.operationTime = item.operationTime.replace(/.[0-9]*$/, '');
                        }
                        return item;
                    });
                    this.totalPage = res.total;
                });
            },
            getPriceStatus() {
                if (!this.G.permission['_priceNote/getDict']) {
                    return this.$notify.error('没有字典权限');
                }
                getPriceStatus().then(res => {
                    if (res.code === 100) {
                        this.auditStatusList = res.content;
                    } else {
                        // this.$notify.error(res.message);
                    }
                });
            },
            audit(id, status) {
                const param = {
                    id: id,
                    status: status
                };
                api.audit(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.pagersData();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleAuditor(id, status) {
                if (!this.G.permission['_stock/priceNote/audit']) {
                    return this.$notify.error('没有审核权限');
                }
                // 审核通过 status = 1
                if (status === 1) {
                    this.$confirm('是否确认审核通过？', '确认信息', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.audit(id, status);
                    }).catch(() => {
                        this.$notify({
                            type: 'info',
                            message: '取消'
                        });
                    });
                } else {
                    this.$confirm('是否确认审核拒绝？', '确认信息', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.audit(id, status);
                    }).catch(() => {
                        this.$notify({
                            type: 'info',
                            message: '取消'
                        });
                    });
                }
            }
        },
        created() {
            this.getPriceStatus();
            this.pagersData();
        }

    };
</script>

<style scoped>

</style>
