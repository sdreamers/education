<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-form :inline="true" :model="searchForm"  class="demo-form-inline">
                    <el-form-item label="商品名称:" >
                        <el-autocomplete
                            v-model="searchForm.productName"
                            :fetch-suggestions="querySearchAsync"
                            placeholder="请输入商品关键字.."
                            @select="handleSelectProduct"
                            style="width: 250px"
                            @input="handleProductNameChange"
                            :trigger-on-focus = 'false'
                        ></el-autocomplete>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="handleClear">清空</el-button>
                    </el-form-item>
                    <el-form-item style="float: right; margin-left: 10px;">
                        <el-button type="danger"
                                   round
                                   @click.stop="handleDeleteAll">
                            删除
                        </el-button>

                        <el-button type="success"
                                   round
                                   @click.stop="handleGeneratePurchasePlan">
                            生成采购计划
                        </el-button>
                    </el-form-item>
                </el-form>

                <el-table :data="tableRecords" ref="table" border v-loading="loading"
                          @select="handleSelect" @select-all="handleSelectAll">
                    <el-table-column
                        type="selection"
                        width="55">
                    </el-table-column>
                    <el-table-column v-for="(column,key) in columns"
                                     :prop="column.key"
                                     :label="column.title"
                                     :key="key">
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="primary"
                                @click.stop="handleEdit(scope.row)">
                                更新采购数量
                            </el-button>
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
        </el-card>
    </section>
</template>

<script>

    import cityProduct from '@/api/cityproduct';
    import purchaseCart from '@/api/purchaseCart';
    import purchasePlanNote from '@/api/purchasePlanNote';

    const searchForm = {
        productName: '',
        productId: ''
    };

    const columns = [
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '商品规格' },
        { key: 'quantity', title: '当前库存' },
        { key: 'thresholdValue', title: '预警值' },
        { key: 'amount', title: '采购数量' }
    ];

    export default {

        data() {
            return {
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                tableRecords: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 15,
                totalSize: 0,

                selectedRows: []
            }
        },

        methods: {
            /**
             * 商品搜索
             * */
            querySearchAsync(productName, cb) {
                const param = { name: productName };
                cityProduct.productListProduct(param).then(res => {
                    if (res && res.code === 100) {
                        const productArr = [];
                        res.content.forEach(product => {
                            productArr.push({ value: product.name, id: product.id });
                        });
                        cb(productArr);
                    } else {
                        return this.$notify.error(res.message);
                    }
                })
            },

            handleSelectProduct(item) {
                this.searchForm.productId = item.id;
            },

            handleProductNameChange() {
                if (!this.searchForm.productName) {
                    this.searchForm.productId = '';
                }
            },

            handleSearch() {
                if (this.searchForm.productName && this.searchForm.productId === '') {
                    return this.$notify.error('未选择商品');
                }
                this.selectedRows = [];
                this.currentPage = 1;
                this.handlePurchaseCartPagers();
            },

            handleClear() {
                this.selectedRows = [];
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePurchaseCartPagers();
            },

            handleSizeChange(size) {
                this.selectedRows = [];
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePurchaseCartPagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                Promise.all([
                    this.handlePurchaseCartPagers()
                ]).then(() => {
                    this.tableRecords.forEach(row => {
                        const selectedFlag = this.selectedRows.some(item => item.id === row.id);
                        if (selectedFlag) {
                            this.$refs.table.toggleRowSelection(row, true);
                        }
                    });
                })
            },

            handlePurchaseCartPagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.searchForm.productId) {
                    param.productId = this.searchForm.productId;
                }
                return purchaseCart.pagers(param).then(res => {
                    this.tableRecords = res.records;
                    this.totalSize = res.total;
                })
            },

            /**
             * 编辑购物单
             * */
            handleEdit(row) {
                this.$prompt('更新采购数量', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPattern: /^\d{1,10}$/
                }).then(({ value }) => {
                    if (value.length > 10) {
                        return this.$notify.error('不得大于10位');
                    }
                    if (!value) {
                        return this.$notify.error('请输入采购数量');
                    }
                    if (isNaN(value)) {
                        return this.$notify.error('请输入正确的采购数量');
                    }
                    const param = {
                        id: row.id,
                        amount: value
                    };

                    purchaseCart.update(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('更新成功');
                            this.handlePurchaseCartPagers();
                        }
                    })
                }).catch(() => {
                    console.log('取消');
                });
            },

            handleDelete(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    purchaseCart.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.handlePurchaseCartPagers();
                        } else {
                            return this.$notify.error(res.message);
                        }
                    });
                }).catch(err => {
                    console.log(err);
                });
            },

            handleSelect(selection, row) {
                const selectedFlag = selection.some(item => item.id === row.id);
                if (selectedFlag) {
                    this.selectedRows.push(row);
                    return;
                }
                this.selectedRows = this.selectedRows.filter(item => item.id !== row.id);
            },

            handleSelectAll(selection) {
                if (selection.length > 0) {
                    selection.forEach(row => {
                        const existFlag = this.selectedRows.some(item => item.id === row.id);
                        if (!existFlag) {
                            this.selectedRows.push(row)
                        }
                    });
                } else {
                    const ids = this.tableRecords.map(item => item.id)
                    this.selectedRows = this.selectedRows.filter(row => !ids.includes(row.id));
                }
            },

            /**
             * 删除全部所选
             * */
            handleDeleteAll() {
                if (!this.selectedRows || this.selectedRows.length < 1) {
                    return this.$notify.error('请至少选择一个商品');
                }
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const ids = this.selectedRows.map(item => item.id).join(',');
                    const param = {
                        ids: ids
                    };
                    purchaseCart.deleteAll(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.selectedRows = [];
                            this.handlePurchaseCartPagers();
                        }
                    });
                }).catch(err => {
                    console.log(err);
                });
            },

            /**
             * 生成采购计划
             * */
            handleGeneratePurchasePlan() {
                if (!this.selectedRows || this.selectedRows.length < 1) {
                    return this.$notify.error('请至少选择一个商品');
                }
                for (const item of this.selectedRows) {
                    if (!item.amount || isNaN(item.amount)) {
                        return this.$notify.error(`${item.productName}: 请输入正确的采购数量`)
                    }
                    if (Number(item.amount) <= 0) {
                        return this.$notify.error(`${item.productName}: 请输入大于0的采购数量`);
                    }
                }
                const details = [];
                this.selectedRows.forEach(item => {
                    details.push({
                        productId: item.productId,
                        productName: item.productName,
                        specificationName: item.specificationName,
                        specificationId: item.specificationId,
                        productBasicInfoId: item.productBasicInfoId,
                        productBasicSpecificationId: item.productBasicSpecificationId,
                        amount: item.amount,
                        purchaseCartId: item.id
                    });
                });

                const param = {
                    detailList: details
                };
                purchasePlanNote.generatePurchasePlan(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('生成采购计划成功');
                        this.selectedRows = [];
                        this.handlePurchaseCartPagers();
                    }
                })
            }
        },

        created() {
            this.handlePurchaseCartPagers();
        }

    }
</script>

<style scoped>

</style>
