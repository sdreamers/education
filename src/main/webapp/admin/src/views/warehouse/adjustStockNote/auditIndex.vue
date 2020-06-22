<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <el-select v-model="searchForm.type" placeholder="请选择类型" @change="handleSearch">
                            <el-option
                                v-for="item in typeList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="5">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            @change="handleSearch"
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="制单开始时间"
                            end-placeholder="制单结束时间">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="8">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                        <el-button type="success" @click="handleExportAll" v-if="G.permission['_adjustStockNote/exportExcelForStatistics']">导出统计</el-button>
                    </el-col>
                </el-row>
                <el-table :data="records" border v-loading="loading">
                    <el-table-column type="expand">
                        <template slot-scope="props">
                            <el-table :data="props.row.adjustStockNoteDetailInfoVOList" border v-loading="loading">
                                <el-table-column v-for="(column,key) in expandColumns" :prop="column.key" :label="column.title" :key="key">
                                    <template slot-scope="scope">
                                        <span v-html="scope.row[column.key]"></span>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </template>
                    </el-table-column>
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key === 'typeCN'" :class="scope.row.typeStyle">{{scope.row[column.key]}}</span>
                            <span v-else-if="column.key === 'statusCN'" :class="scope.row.statusStyle">{{scope.row[column.key]}}</span>
                            <section v-else-if="column.key === 'pass' && G.permission['_stock/adjustStockNote/pass']">
                                <el-button type="primary" v-if="scope.row.status === '1'" @click.stop="handlePass(scope.row)">审核通过</el-button>
                            </section>
                            <section v-else-if="column.key === 'reject' && G.permission['_stock/adjustStockNote/reject']">
                                <el-button type="danger" v-if="scope.row.status === '1'" @click.stop="rejectForm.id=scope.row.id;rejectForm.visible=true">审核驳回</el-button>
                            </section>
                            <section v-else-if="column.key === 'export' && G.permission['_adjustStockNote/exportExcel']">
                                <el-button type="success" @click.stop="handleExportExcel(scope.row)">导出</el-button>
                            </section>
                            <section v-else-if="column.key === 'auditDetail' && G.permission['_stock/adjustStockNote/getDetails']">
                                <el-button type="primary" @click.sto="handleViewAuditDetail(scope.row)">查看</el-button>
                            </section>
                            <span v-else>{{scope.row[column.key]}}</span>
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
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i
                            class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <!-- 审批驳回 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelReject" title="填写批语" :visible.sync="rejectForm.visible" width="30%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p style="font-weight: bold ">批语(100字以内):</p>
                    <el-input v-model="rejectForm.auditDescription" type="textarea" :rows="5"></el-input>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click.stop="handleSubmitReject">提交留言</el-button>
                <el-button @click.stop="handleCancelReject">取消</el-button>
            </div>
        </el-dialog>

        <!-- 审核记录 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelViewAuditDetail" title="审核记录" :visible.sync="auditDetail.visible" width="30%">
            <el-row :gutter="20">
                <el-col class="mb20">
                    <p style="font-weight: bold ">审核记录:</p>
                </el-col>
                <el-row>
                    <div v-html="auditDetail.auditRecord"></div>
                </el-row>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click.stop="handleCancelViewAuditDetail">取消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import common from '@/api/common';
    import adjustStockNote from '@/api/adjustStockNote';
    import util from '@/libs/util';

    const searchForm = {
        status: '',
        warehouseId: 1,
        type: '',
        searchDate: []
    };

    const typeList = [
        { id: '', name: '请选择类型' },
        { id: '1', name: '盘盈' },
        { id: '2', name: '盘亏' }
    ];

    const typeMap = {
        1: '盘盈', 2: '盘亏'
    };

    const statusMap = {
        1: '待审批', 4: '审核通过', 5: '审核驳回'
    };

    const statusList = [
        { id: '', name: '请选择状态' },
        { id: '1', name: '待审批' },
        { id: '4', name: '审核通过' },
        { id: '5', name: '审核驳回' }
    ];

    const rejectForm = {
        auditDescription: '',
        id: '',
        visible: false
    };

    const columns = [
        { key: 'typeCN', title: '操作类型' },
        { key: 'statusCN', title: '状态' },
        { key: 'auditorName', title: '待审核人' },
        { key: 'pass', title: '审核通过' },
        { key: 'reject', title: '审核驳回' },
        { key: 'export', title: '导出' },
        { key: 'adjustCheckName', title: '制单员' },
        { key: 'adjustCheckTimeStr', title: '制单时间' },
        { key: 'auditDetail', title: '审核记录' }
    ];

    const expandColumns = [
        { key: 'index', title: '序号' },
        { key: 'productName', title: '商品名' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'num', title: '件数' },
        { key: 'cost', title: '成本单价' },
        { key: 'totalCost', title: '成本总价' },
        { key: 'comment', title: '备注' }
    ];

    const auditDetail = {
        auditRecord: '',
        visible: false
    };

    export default {
        data() {
            return {
                searchForm: JSON.parse(JSON.stringify(searchForm)),
                typeList: typeList,
                statusList: statusList,
                warehouseList: [],

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,
                expandColumns: expandColumns,

                rejectForm: JSON.parse(JSON.stringify(rejectForm)),
                auditDetail: JSON.parse(JSON.stringify(auditDetail)),


                /* 无用字段 */
                adjustStockNoteDetailInfoVOList: []
            }
        },

        methods: {

            defaultDate() {
                const now = util.getDateSubDays(0);
                this.searchForm.searchDate = [now, now];
            },

            handleLoadWarehouse() {
                common.getWarehouseByUserCurrentCityId().then(res => {
                    if (res.code === 100 && res.content) {
                        for (const item in res.content) {
                            if (res.content.hasOwnProperty(item)) {
                                this.warehouseList.push({ id: item, name: res.content[item] });
                            }
                        }
                        this.warehouseList.unshift({ id: '', name: '请选择仓库' });
                    }
                });
            },

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                if (!this.G.permission['_stock/adjustStockNote/pages']) {
                    return this.$notify.error('没有列表权限');
                }
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    if (dpy.validateHalfAYear(this.searchForm.searchDate[0], this.searchForm.searchDate[1])) {
                        return this.$notify.error('查询时间范围不能大于半年');
                    }
                }
                const param = this.encapsulateParam();
                param.page = this.currentPage;
                param.limit = this.pageSize;
                adjustStockNote.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            if (item.type === '1') {
                                item.typeStyle = 'green';
                            } else if (item.type === '2') {
                                item.typeStyle = 'red';
                            }
                            if (item.status === '1') {
                                item.statusStyle = 'blue';
                            } else if (item.status === '4') {
                                item.statusStyle = 'green';
                            } else if (item.status === '5') {
                                item.statusStyle = 'red';
                            } else {
                                item.statusStyle = 'gray';
                            }
                            if (item.adjustStockNoteDetailInfoVOList) {
                                let index = 1;
                                let totalNum = 0;
                                let totalCost = 0;
                                item.adjustStockNoteDetailInfoVOList.forEach(detail => {
                                    detail.index = index;
                                    totalNum += detail.num;
                                    totalCost += (parseFloat(detail.num) * parseFloat(detail.cost)).toFixed(2) * 1;
                                    detail.totalCost = totalCost.toFixed(2);
                                    if (detail.cost) {
                                        detail.cost = detail.cost.toFixed(2) * 1;
                                    }
                                    if (item.type === '1') {
                                        detail.num = '<b style="color:green">增: ' + detail.num + '</b>';
                                    } else if (item.type === '2') {
                                        detail.num = '<b style="color:red">减: ' + detail.num + '</b>';
                                    }
                                    index = index + 1;
                                });
                                item.adjustStockNoteDetailInfoVOList.push({
                                    index: '合计',
                                    productName: '',
                                    specificationName: '',
                                    num: totalNum,
                                    cost: '',
                                    totalCost,
                                    comment: ''
                                });
                            }
                            item.typeCN = typeMap[item.type];
                            item.statusCN = statusMap[item.status];
                        });
                    }
                    this.records = res.records;
                    this.totalSize = res.total;
                });
            },

            encapsulateParam() {
                const param = {};
                if (this.searchForm.status) {
                    param.status = this.searchForm.status;
                }
                if (this.searchForm.type) {
                    param.type = this.searchForm.type;
                }
                if (this.searchForm.warehouseId) {
                    param.warehouseId = this.searchForm.warehouseId;
                }
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    param.purchaserStartTime = this.searchForm.searchDate[0];
                    param.purchaserEndTime = this.searchForm.searchDate[1];
                }
                return param;
            },

            handleExportAll() {
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    const periodMillionSeconds = new Date(this.searchForm.searchDate[1]).getTime() - new Date(this.searchForm.searchDate[0]).getTime();
                    const periodDays = periodMillionSeconds / (24 * 3600 * 1000);
                    if (periodDays > 31) {
                        return this.$notify.error('导出制单时间范围不能大于一个月');
                    }
                } else {
                    return this.$notify.error('请选择制单时间范围');
                }
                const exportUrl = '/stock/adjustStockNote/exportExcelForStatistics';
                const param = util.qs(this.encapsulateParam());
                return location.href = vars.URLApiBase + exportUrl + '?' + param;
            },

            /**
             * 审核通过
             * */
            handlePass(row) {
                this.$confirm('是否确认审核通过?', '确认', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    adjustStockNote.updatePassed(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '操作成功');
                            this.handlePagers();
                        }
                    });
                }).catch(err => {
                    window.LOG(err);
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },

            /**
             * 驳回
             * */
            handleCancelReject() {
                this.rejectForm = JSON.parse(JSON.stringify(rejectForm));
            },

            handleSubmitReject() {
                if (!this.rejectForm.auditDescription) {
                    return this.$notify.error('请填写驳回批语');
                }
                if (this.rejectForm.auditDescription.length > 99) {
                    return this.$notify.error('请填写100个字以内的驳回批语');
                }
                const param = {
                    auditDescription: this.rejectForm.auditDescription,
                    id: this.rejectForm.id
                };
                adjustStockNote.updateRejected(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '操作成功');
                        this.rejectForm = JSON.parse(JSON.stringify(rejectForm));
                        this.handlePagers();
                    }
                });
            },

            /**
             * 导出excel
             * */
            handleExportExcel(row) {
                const exportUrl = '/stock/adjustStockNote/exportExcel';
                const param = util.qs({ stockNoteId: row.id });
                return location.href = vars.URLApiBase + exportUrl + '?' + param;
            },

            /**
             * 查看审核记录
             * */
            handleViewAuditDetail(row) {
                adjustStockNote.getDetails({ id: row.id }).then(res => {
                    if (res.code === 100) {
                        this.auditDetail.auditRecord = res.content.auditRecord;
                        this.auditDetail.visible = true;
                    }
                });
            },

            handleCancelViewAuditDetail() {
                this.auditDetail = JSON.parse(JSON.stringify(auditDetail));
            }
        },

        created() {
            this.defaultDate();
            this.handleLoadWarehouse();
            this.handlePagers();
        }
    }
</script>
<style scoped>
    .green {
        color: green
    }

    .red {
        color: red;
    }

    .blue {
        color: blue;
    }

    .gray {
        color: gray;
    }
</style>

