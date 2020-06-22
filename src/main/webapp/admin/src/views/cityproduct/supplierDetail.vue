<template>
    <el-dialog :close-on-click-modal="false" title="供应详情" :visible="visible" @close="handleClose" width="30%">
        <section class="view-content">
            <el-card shadow="none">
        <el-form :model="form">
            <el-form-item label="最后一次采购时间" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.lastPurchaseTime" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="供应商" :label-width="formLabelWidth" prop="thresholdValue">
                <el-input v-model="form.supplierName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
        </el-form>
        <el-table :data="tableData" border v-loading="loading">
            <el-table-column
                v-for="(column,key) in columns"
                :prop="column.key"
                :label="column.title"
                align="center"
                :key="key"></el-table-column>
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
            </el-card>
        </section>
    </el-dialog>
</template>

<script>
    import purchaseStockNoteDetailInfo from '@/api/purchaseStockNoteDetailInfo';
    import util from '@/libs/util';

    const form = {
        lastPurchaseTime: '',
        supplierName: ''
    };

    const columns = [
        { key: 'supplierName', title: '供应商' },
        { key: 'sumNum', title: '总数量' }
    ];

    export default {

        props: ['cityProductId', 'isSplit', 'citySpecificationId', 'visible'],

        watch: {
            citySpecificationId(value) {
                if (value && this.visible) {
                    this.getLastPurchaseInfo();
                    this.handlePagers();
                }
            }
        },

        data() {
            return {
                formLabelWidth: '140px',

                form: JSON.parse(JSON.stringify(form)),


                loading: false,
                tableData: [],
                columns: columns,
                currentPage: 1,
                pageSize: 5,
                totalPage: 0
            }
        },

        methods: {
            handleSizeChange(size) {
                this.currentPage = 1;
                this.pageSize = size;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            getLastPurchaseInfo() {
                const param = {
                    citySpecificationId: this.citySpecificationId,
                    cityProductId: this.cityProductId,
                    split: this.isSplit
                };
                purchaseStockNoteDetailInfo.getLastPurchaseInfo(param).then(res => {
                    if (res.code === 100 && res.content) {
                        this.form.lastPurchaseTime = util.parseTime(new Date(res.content.warehousingTime));
                        this.form.supplierName = res.content.supplierName;
                    }
                })
            },

            handlePagers() {
                const param = {
                    citySpecificationId: this.citySpecificationId,
                    cityProductId: this.cityProductId,
                    split: this.isSplit,
                    notInStatus: '0,1',
                    page: this.currentPage,
                    limit: this.pageSize
                };
                purchaseStockNoteDetailInfo.listSupplierAndSumNumPaging(param).then(res => {
                    this.tableData = res.records;
                    this.totalPage = res.total;
                })
            },

            handleClose() {
                this.$parent.supplierDialog.citySpecificationId = null;
                this.$parent.supplierDialog.visible = false;
                this.form = JSON.parse(JSON.stringify(form));
                this.currentPage = 1;
                this.totalPage = 0;
                this.tableData = [];
            }
        }
    }
</script>

<style scoped>

</style>
