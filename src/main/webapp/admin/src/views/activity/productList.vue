<template>
<div class="">
    <el-dialog :close-on-click-modal="false" title="活动商品信息详情" class="dialogw80" :visible="true" @close="$emit('close')">
        <el-table :data="tableDataProduct" border @row-click="rowClick">
            <el-table-column v-for="(column,key) in columnsProduct" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="上下架" v-if="G.permission['_market/activityProduct/updateAvailable']">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-switch
                            v-model="scope.row.available === 1"
                            active-color="#13ce66"
                            inactive-color="#ff4949">
                        </el-switch>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                    type="text"
                    v-if="G.permission['_market/activityProduct/update']"
                    @click.stop="handleEditProduct(scope.row)">编辑</el-button>
                    <el-button
                    type="text"
                    v-if="G.permission['_market/activityProduct/delete']"
                    @click.stop="handleRemoveProduct(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pager-wrapper">
            <el-pagination
            @size-change="handleSizeChangeProduct"
            @current-change="handleCurrentChangeProduct"
            :current-page.sync="currentPageProduct"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSizeproduct"
            layout="total, sizes, slot, prev, pager, next"
            :total="productTotalPage">
                <span class="el-pagination__total refresh" @click="handleCurrentChangeProduct(currentPageProduct)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
    </el-dialog>
    <!-- 修改活动商品的库存和单价 -->
    <productPrice
        @close="dialogActivityVisible = false,activityProductPagers()"
        v-if="dialogActivityVisible"
        :formLabelWidth="formLabelWidth"
        :formActivity="formActivity"
        :rules="rules"></productPrice>
</div>
</template>
<script>
import api from '@/api/activity';
import productPrice from './productPrice';
const columnsProduct = [
    { key: 'id', title: 'ID' },
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '商品规格' },
    { key: 'originalPrice', title: '商品原单价' },
    { key: 'quantity', title: '商品库存' },
    { key: 'activityPrice', title: '活动单价' },
    { key: 'stockNum', title: '活动库存' }
];
export default {
    components: {
        productPrice
    },
    props: ['formLabelWidth', 'rules', 'product'],
    data() {
        return {
            dialogActivityVisible: false,
            formActivity: {},

            tableDataProduct: [],

            columnsProduct: columnsProduct,

            currentPageProduct: 1,
            pageSizeproduct: 5,
            productTotalPage: 0

        };
    },
    methods: {
        rowClick(row, event, column) {
            const id = row.id;
            if (column.label === '上下架') {
                const param = {
                    id, available: row.available
                };
                api.activityProductUpdateActivityProductOnSaleStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.activityProductPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
        },
        handleRemoveProduct(data) {
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id,
                    specificationId: data.specificationId,
                    activityCode: data.activityCode
                };
                api.activityProductDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.activityProductPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
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
        handleEditProduct(data) {
            this.dialogActivityVisible = true;
            this.formActivity = JSON.parse(JSON.stringify(data));
        },
        handleSizeChangeProduct(pageSize) {
            this.pageSizeproduct = pageSize;
            this.activityProductPagers();
        },
        handleCurrentChangeProduct(currentPage) {
            this.currentPageProduct = currentPage;
            this.activityProductPagers();
        },
        activityProductPagers() {
            const param = {
                provinceId: this.product.provinceId,
                cityId: this.product.cityId,
                code: this.product.code,
                page: this.currentPageProduct,
                limit: this.pageSizeproduct
            };

            return api.activityProductPagers(param).then(res => {
                this.tableDataProduct = res.records || [];
                this.productTotalPage = res.total;
            });
        }
    },
    created() {
        this.activityProductPagers();
    }
};
</script>
