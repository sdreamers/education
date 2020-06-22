<template>
<div class="">
    <el-dialog :close-on-click-modal="false" title="团购商品信息详情" class="dialogw80" :visible="true" @close="$emit('close')">
        <el-table :data="tableDataProduct" border @row-click="rowClick">
            <el-table-column v-for="(column,key) in columnsProduct" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                    type="text"
                    v-if="G.permission['_market/groupBuyingDetail']"
                    @click.stop="handleEditProduct(scope.row)">编辑</el-button>
                    <el-button
                    type="text"
                    v-if="G.permission['_market/groupBuyingDetail']"
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
    <!-- 修改团购商品的库存和单价 -->
    <productPrice
        @close="dialogActivityVisible = false,groupBuyingProductPagers()"
        v-if="dialogActivityVisible"
        :formLabelWidth="formLabelWidth"
        :formActivity="formActivity"
        :rules="rules"></productPrice>
</div>
</template>
<script>
import api from '@/api/groupBuying';
import productPrice from './productPrice';
const columnsProduct = [
    { key: 'sortNo', title: '序号' },
    { key: 'cityProductName', title: '商品名称' },
    { key: 'citySpecificationName', title: '商品规格' },
    { key: 'price', title: '团购价格' },
    { key: 'openUpNum', title: '起团数量' },
    { key: 'addUpPurchaseNum', title: '累计团购数量' },
    { key: 'maxPurchaseNum', title: '最大购买数量' }
    // { key: 'stockNum', title: '团购库存' }
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
        handleRemoveProduct(data) {
            console.log(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                    // groupBuyingId: data.groupBuyingId
                };
                api.groupBuyingProductDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.groupBuyingProductPagers();
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
            this.groupBuyingProductPagers();
        },
        handleCurrentChangeProduct(currentPage) {
            this.currentPageProduct = currentPage;
            this.groupBuyingProductPagers();
        },
        groupBuyingProductPagers() {
            const param = {
                groupBuyingId: this.product.id,
                // cityId: this.product.cityId,
                // code: this.product.code,
                page: this.currentPageProduct,
                limit: this.pageSizeproduct
            };

            return api.groupBuyingProductPagers(param).then(res => {
                this.tableDataProduct = res.records || [];
                this.productTotalPage = res.total;
            });
        }
    },
    created() {
        this.groupBuyingProductPagers();
    }
};
</script>
