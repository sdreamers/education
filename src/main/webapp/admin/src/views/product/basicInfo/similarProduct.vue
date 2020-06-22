<template>
    <el-dialog :close-on-click-modal="false" title="关联的同类商品" :visible="true" @close="$emit('close')" class="dialogw90">
        <el-row class="mb20" :gutter="20" v-if="G.permission['_product/similarProduct/insert']">
            <el-col :span="8">
                <!-- <el-input v-model="keyword" placeholder="搜索商品名称"></el-input> -->
                <el-select
                    style="width:100%"
                    v-model="similarProductId"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="请输入商品关键词"
                    :remote-method="handleProductSearch">
                    <el-option
                    v-for="item in searchProductList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    </el-option>
                </el-select>
            </el-col>
            <el-button type="primary" @click="handleAddToSimilar">加入同类</el-button>
        </el-row>
        <el-table :data="tableData" border>
            <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-button type="primary" @click="handleDelete(scope.row)" v-if="G.permission['_product/similarProduct/delete']">移除</el-button>
                    </section>
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
            :total="productMechantTotalPage">
                <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/cityproduct';
import apiSimilar from '@/api/similarProduct';
const columns = [
    {
        key: 'productName',
        title: '产品名称'
    },
    {
        key: 'brandName',
        title: '品牌'
    }
];
export default {
    props: ['productId'],
    data() {
        return {
            similarProductId: '',
            searchProductList: [],

            tableData: [],
            columns: columns,

            currentPage: 1,
            productMechantTotalPage: 0,
            pageSize: 5
        };
    },
    methods: {
        handleProductSearch(name) {
            if (name === '') {
                return this.searchProductList = [];
            }
            const param = {
                name
            }
            api.productListProductBasicInfo(param).then(res => {
                if (res.code === 100) {
                    this.searchProductList = res.content;
                }
            })
        },
        // 搜索
        handleAddToSimilar() {
            if (!this.similarProductId) {
                return this.$notify.error('请选择商品');
            }
            const param = {
                productId: this.productId,
                similarProductId: this.similarProductId
            };
            this.similarProductId = '';
            apiSimilar.similarProductInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.similarProductId = '';
                    this.similarProductPagers();
                }
            })
        },
        handleDelete(data) {
            this.$confirm('确认移除出同类商品？').then(() => {
                const param = {
                    id: data.id
                }
                apiSimilar.similarProductDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.similarProductPagers();
                    }
                })
            })
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.similarProductPagers({ id: this.merchantTypeId });
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.similarProductPagers({ id: this.merchantTypeId });
        },
        similarProductPagers() {
            console.log(this)
            if (!this.G.permission['_product/similarProduct/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize,
                productId: this.productId
            };
            apiSimilar.similarProductPagers(param).then(res => {
                this.tableData = res.records;
                this.productMechantTotalPage = res.total;
            })
        }
    },
    created() {
        this.similarProductPagers();
    }
};
</script>
