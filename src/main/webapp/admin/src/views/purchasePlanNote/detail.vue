<template>
    <el-dialog :close-on-click-modal="false" title="采购计划明细清单" :visible="visible" @close="$emit('close')" class="dialogw80">
        <el-row class="mt20">
            <el-table :data="tableRecords" border v-loading="loading">
                <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
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
                    :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
        </el-row>
    </el-dialog>
</template>

<script>

    import purchasePlanNote from '@/api/purchasePlanNote';

    const columns = [
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'amount', title: '计划采购数量' }
    ];

    export default {

        props: ['visible', 'purchasePlanNoteId'],

        watch: {
            purchasePlanNoteId(value) {
                if (!value || !this.visible) {
                    return;
                }
                this.handleDetailPagers();
            }
        },

        data() {
            return {
                visible: false,

                tableRecords: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0
            }
        },

        methods: {

            handleSizeChange(size) {
                this.pageSize = size;
                this.handleDetailPagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handleDetailPagers();
            },

            handleDetailPagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    purchasePlanNoteId: this.purchasePlanNoteId
                };
                purchasePlanNote.detailPagers(param).then(res => {
                    this.tableRecords = res.records;
                    this.totalSize = res.total;
                })
            }
        }
    }
</script>

<style scoped>

</style>
