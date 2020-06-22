<template>

    <el-dialog :close-on-click-modal="false" title="菜系类型关联商品" :visible="true" @close="$emit('close')" class="dialogw90">
        <el-row class="mb20" :gutter="20">
            <el-col :span="8">
                <el-input v-model="keyword" placeholder="搜索商品名称"></el-input>
            </el-col>
                <el-button type="primary" @click="searchMerchant">搜索</el-button>
                <el-button @click="clearMerchant">清除</el-button>
        </el-row>
        <el-table :data="columnsProductData" border @row-click="rowClick">
            <el-table-column v-for="(column,key) in columnsProduct" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <img  v-if="column.key === 'categoryPreviewImageURL'" style="width: 80px;height: 80px;" :src="scope.row.categoryPreviewImageURL" />
                    <span v-if="column.key !== 'categoryPreviewImageURL'">{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="是否添加（立即生效）">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-switch
                            v-model="scope.row.merchantTypeId !== 0"
                            active-color="#13ce66"
                            inactive-color="#ff4949">
                        </el-switch>
                    </section>
                </template>
            </el-table-column>
        </el-table>
        <div class="pager-wrapper">
            <el-pagination
            @size-change="handleSizeChangeProductMechant"
            @current-change="handleCurrentChangeProductMechant"
            :current-page.sync="currentPageProductMerchant"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSizeProductMechant"
            layout="total, sizes, slot, prev, pager, next"
            :total="productMechantTotalPage">
                <span class="el-pagination__total refresh" @click="handleCurrentChangeProductMechant(currentPageProductMerchant)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/product';
const columnsProduct = [
    {
        key: 'name',
        title: '产品名称'
    },
    {
        key: 'categoryPreviewImageURL',
        title: '预览图'
    },
    {
        key: 'brandName',
        title: '品牌'
    },
    {
        key: 'categoryName',
        title: '品类'
    },
    {
        key: 'areaName',
        title: '产地'
    }
];
export default {
    props: ['merchantTypeId'],
    data() {
        return {
            keyword: '',

            columnsProductData: [],
            columnsProduct: columnsProduct,

            currentPageProductMerchant: 1,
            productMechantTotalPage: 0,
            pageSizeProductMechant: 5
        };
    },
    methods: {

        // 搜索
        searchMerchant() {
            this.currentPageProductMerchant = 1;
            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
        },
        // 清除搜索
        clearMerchant() {
            this.keyword = '';
            this.currentPageProductMerchant = 1;
            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
        },
        // 每页显示数改变时
        handleSizeChangeProductMechant(pageSize) {
            this.pageSizeProductMechant = pageSize;
            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
        },
        // 翻页时
        handleCurrentChangeProductMechant(currentPage) {
            this.currentPageProductMerchant = currentPage;
            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
        },
        // 点击菜系关联时，添加菜系关联
        rowClick(row, event, column) {
            if (column.label === '是否添加（立即生效）') {
                const merchantTypeId = this.merchantTypeId;
                const productBasicInfoIds = row.id;
                if (row.merchantTypeId === 0) {
                    if (!this.G.permission['_product/productMerchantType/addMerchantTypeProduct']) {
                        return this.$notify.error('没有权限');
                    }
                    api.productMerchantTypeAddMerchantTypeProduct({ merchantTypeId, productBasicInfoIds }).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                } else {
                    if (!this.G.permission['_product/productMerchantType/deleteMerchantTypeProduct']) {
                        return this.$notify.error('没有权限');
                    }
                    api.productMerchantTypeDeleteMerchantTypeProduct({ merchantTypeId, productBasicInfoId: row.id }).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }
            }
        },
        handleAddMerchantTypeProduct(data) {
            const param = {
                page: this.currentPageProductMerchant,
                limit: this.pageSizeProductMechant,
                merchantTypeId: data.id
            };
            if (this.keyword) {
                param.keyword = this.keyword;
            }
            if (!this.G.permission['_product/productMerchantType/productBasicInfo/pages']) {
                return this.$notify.error('没有列表权限');
            }
            api.productMerchantTypeProductBasicInfoPagers(param).then(res => {
                this.columnsProductData = res.records;

                this.productMechantTotalPage = res.total;
            });
        }
    },
    created() {
        this.handleAddMerchantTypeProduct({ id: this.merchantTypeId });
    }
};
</script>
