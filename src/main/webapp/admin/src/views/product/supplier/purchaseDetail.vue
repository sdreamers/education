<template>
    <el-dialog title="采购统计"
               :visible="visible"
               :close-on-click-modal="false"
               width="60%" height="80%"
               @close="handleClose">
        <section class="view-content">
            <el-card shadow="none">
                <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="10">
                            <el-date-picker
                                v-model="search.searchDate"
                                @change="handlePagers"
                                type="datetimerange"
                                align="right"
                                range-separator="至"
                                start-placeholder="采购开始时间"
                                end-placeholder="采购结束时间"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :default-time="['00:00:00','23:59:59']">
                            </el-date-picker>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns"
                                         :prop="column.key"
                                         :label="column.title"
                                         align="center"
                                         :key="key"
                                         :width="column.width">
                            <template slot-scope="scope">
                                {{ scope.row[column.key]}}
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pager-wrapper">
                        <el-pagination
                            @size-change="handleSizeChange"
                            @current-change="handleCurrentChange"
                            :current-page.sync="currentPage"
                            :page-sizes="[5, 10, 15, 20]"
                            :page-size="pageSize"
                            layout="total, sizes, slot, prev, pager, next"
                            :total="totalPage">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
                </el-row>
            </el-card>
        </section>
    </el-dialog>
</template>

<script>

    import purchaseStockNoteDetailInfo from '@/api/purchaseStockNoteDetailInfo';
    import util from '@/libs/util';

    const search = {
        searchDate: []
    };

    const columns = [
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'warehousingTime', title: '最近一次采购时间' },
        { key: 'specificationUnit', title: '单位' },
        { key: 'sumNum', title: '总数量' }
    ];

    export default {

        props: ['supplierId', 'visible'],

        watch: {
            supplierId(value) {
                if (value && this.visible) {
                    this.handlePagers();
                }
            }
        },

        data() {
            return {
                search: JSON.parse(JSON.stringify(search)),

                tableData: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 5,
                totalPage: 0
            }
        },

        methods: {

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.currentPage = 1;
                this.search = JSON.parse(JSON.stringify(search));
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.currentPage = 1;
                this.pageSize = size;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    supplierId: this.supplierId,
                    notInStatus: '0,1,5'
                };
                if (this.search.searchDate && this.search.searchDate.length === 2) {
                    param.createDateTimeBegin = this.search.searchDate[0];
                    param.createDateTimeEnd = this.search.searchDate[1];
                }
                purchaseStockNoteDetailInfo.listSpecificationAndSumNumPaging(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            if (item.warehousingTime) {
                                item.warehousingTime = util.parseTime(new Date(item.warehousingTime));
                            }
                        })
                    }
                    this.tableData = res.records;
                    this.totalPage = res.total;
                });
            },

            handleClose() {
                this.$parent.purchaseDetailForm.supplierId = null;
                this.$parent.purchaseDetailForm.visible = false;
                this.search = JSON.parse(JSON.stringify(search));
                this.tableData = [];
                this.currentPage = 1;
                this.totalPage = 0;
            }
        }
    }
</script>

<style scoped>

</style>
