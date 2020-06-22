<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                <el-input v-model="search.merchantId" placeholder="供应商">
                </el-input>
                </el-col>
                <el-col :span="3">
                <el-input v-model="search.merchantId" placeholder="包名">
                </el-input>
                </el-col>

                <el-col :span="3">
                    <el-input v-model="search.merchantId" placeholder="节点">
                    </el-input>
                </el-col>
                <el-col :span="6">
                    <el-date-picker
                        v-model="search.dateArr"
                        @change="handlePagers"
                        type="daterange"
                        align="right"
                        unlink-panels
                        :picker-options="pickerOptions"
                        value-format="yyyy-MM-dd"
                        range-separator="至"
                        start-placeholder="项目完成开始日期"
                        end-placeholder="项目完成结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导入</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border  v-loading="loading" >
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center" >
                        <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="120%"
                                         :label="subColumn.title" align="center" :key="subKey">
                            <template slot-scope="scope">
                                <span>{{scope.row[subColumn.key]}}</span>
                            </template>
                        </el-table-column>
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="400%">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleEditMerchant(scope.row)">查看学校明细</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <merchantEdit
            :dialogVisible="merchantEditDialogVisible"
            :form="merchantEditForm"
            :type="merchantEditType"
            @close="merchantEditClose"/>

        <product-edit
            :dialogVisible="productEditDialogVisible"
            :form="productEditForm"
            @close="productEditClose"/>

        <quota-edit
            :dialogVisible="quotaEditDialogVisible"
            :form="quotaEditForm"
            @close="quotaEditClose"/>

        <quota-click-edit
            :dialogVisible="quotaClickEditDialogVisible"
            :form="quotaClickEditForm"
            @close="quotaClickEditClose"/>

        <initNewQuotaYear
            :dialogVisible="initNewQuotaYearDialogVisible"
            :form="initNewQuotaYearForm"
            @close="initNewQuotaYearClose"/>
    </section>
</template>
<script>
    import api from '@/api/merchantproductquota';
    import merchant from '@/api/merchant';
    import merchantEdit from './merchantEdit';
    import productEdit from './productEdit';
    import quotaEdit from './quotaEdit';
    import quotaClickEdit from './quotaClickEdit';
    import quotaYear from '@/api/quotaYear';
    import initNewQuotaYear from './initNewQuotaYear';
    import util from '@/libs/util';

    const merchantEditForm = {
        name: '',
        telephone: '',
        memberNumber: ''
    };

    const productEditForm = {
        id: '',
        name: ''
    };

    const quotaEditForm = {
        merchantId: '',
        title: ''
    };

    const quotaClickEditForm = {
        title: '',
        quotaYear: '',
        merchantId: '',
        productId: '',
        oldAmount: '',
        type: ''
    };

    const search = {
        quotaYear: ''
    };

    const columns = [{ key: 'project', title: '包名' }, { key: 'supplier', title: '供应商' }, { key: 'taskProgress', title: '任务进度' }, { key: 'moneyProgress', title: '金额进度' }];

    const typeMap = { amount: '全年需求量', firstHalfYearAmount: '上半年配额', secondHalfYearAmount: '下半年配额' };

    export default {
        components: {
            merchantEdit,
            productEdit,
            quotaEdit,
            quotaClickEdit,
            initNewQuotaYear
        },

        data() {
            return {
                tableData: [],
                columns: columns,
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                quotaYearArr: [],
                search: JSON.parse(JSON.stringify(search)),

                /* 添加商户 */
                merchantEditDialogVisible: false,
                merchantEditType: '',
                merchantEditForm: JSON.parse(JSON.stringify(merchantEditForm)),

                /* 添加商品 */
                productEditDialogVisible: false,
                productEditForm: JSON.parse(JSON.stringify(productEditForm)),

                /* 编辑额度 */
                quotaEditDialogVisible: false,
                quotaEditForm: JSON.parse(JSON.stringify(quotaEditForm)),

                /* 双击编辑额度 */
                quotaClickEditDialogVisible: false,
                quotaClickEditForm: JSON.parse(JSON.stringify(quotaClickEditForm)),

                /* 新建年份 */
                initNewQuotaYearDialogVisible: false,
                initNewQuotaYearForm: {}
            }
        },

        methods: {

            handleSizeChange(size) {
                this.pageSize = size;
                this.handlePagers();
            },

            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.handlePagers();
            },

            handleSearch() {
                this.handlePagers();
            },

            handleClear() {
                this.search = JSON.parse(JSON.stringify(search));
            },

            handlePagers() {
                this.tableData = [{ project: 'A包-家具A', supplier: '成都更新供应商', taskProgress: '80%', moneyProgress: '65%' },
                    { project: 'B包-家具B', supplier: '成都家家新供应商', taskProgress: '40%', moneyProgress: '85%' },
                    { project: 'C包-家具C', supplier: '成都大家好供应商', taskProgress: '100%', moneyProgress: '100%' }];
            },

            handleAddMerchant() {
                this.merchantEditType = 'add';
                this.merchantEditDialogVisible = true;
            },

            merchantEditClose() {
                this.merchantEditDialogVisible = false;
                this.merchantEditForm = JSON.parse(JSON.stringify(merchantEditForm));
                this.handlePagers();
            },

            handleAddProduct() {
                this.productEditDialogVisible = true;
            },

            productEditClose() {
                this.productEditDialogVisible = false;
                this.productEditForm = JSON.parse(JSON.stringify(productEditForm));
                this.handlePagers();
            },

            handleEditMerchant(row) {
                this.merchantEditForm = {};
                this.merchantEditType = 'edit';
                this.merchantEditDialogVisible = true;
            },

            handleEditQuota(row, type) {
                api.quotaMap({ merchantId: row.merchantId, type: type }).then(res => {
                    if (res.code === 100 && res.content) {
                        this.quotaEditForm = res.content;
                        this.quotaEditForm.title = `${res.content.merchantName} ${typeMap[type]}`;
                        this.quotaEditForm.type = type;
                        this.quotaEditDialogVisible = true;
                    }
                })
            },

            quotaEditClose() {
                this.quotaEditDialogVisible = false;
                this.quotaEditForm = JSON.parse(JSON.stringify(quotaEditForm));
                this.handlePagers();
            },

            handleListAllQuotaYear() {
                quotaYear.list().then(res => {
                    if (res.code === 100) {
                        if (res.content) {
                            this.quotaYearArr = res.content.map(item => ({ id: item.year, name: item.year + '年油料需求计划表' }));
                        }
                    }
                })
            },

            /**
             * 双击单元格
             * */
            handleCellClick(row, column) {
                if (column.property.endsWith('-amount') || column.property.endsWith('-firstHalfYearAmount') || column.property.endsWith('-secondHalfYearAmount')) {
                    this.quotaClickEditForm.productId = column.property.split('-')[0];
                    this.quotaClickEditForm.merchantId = row.merchantId;
                    this.quotaClickEditForm.merchantName = row.merchantName;
                    this.quotaClickEditForm.oldAmount = row[column.property];
                    if (!this.quotaClickEditForm.oldAmount) {
                        this.quotaClickEditForm.oldAmount = 0;
                    }
                    this.quotaClickEditForm.type = column.property.split('-')[1];
                    this.quotaClickEditForm.title = `${row.merchantName} ${util.getCookie('quotaYear')}年 ${typeMap[this.quotaClickEditForm.type]}`;
                    this.quotaClickEditDialogVisible = true;
                }
            },

            quotaClickEditClose() {
                this.quotaClickEditForm = JSON.parse(JSON.stringify(quotaClickEditForm));
                this.quotaClickEditDialogVisible = false;
            },

            initNewQuotaYearClose() {
                this.initNewQuotaYearForm = {};
                this.initNewQuotaYearDialogVisible = false;
                this.handlePagers();
            },

            handleExport() {
                const exportUrl = window.vars.URLApiBase + '/quota/export';
                return location.href = exportUrl;
            }
        },

        created() {
            this.handleListAllQuotaYear();
            this.handlePagers();
        }
    }
</script>
