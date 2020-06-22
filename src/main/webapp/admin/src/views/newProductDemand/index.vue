<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :lg="5" :xl="4">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-input v-model="searchForm.telephone" placeholder="商户注册手机号"></el-input>
                    </el-col>

                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>

                <el-table :data="records" border v-loading="loading">
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        align="center"
                        :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key !== 'detailInfo'">{{scope.row[column.key]}}</span>
                            <section v-else>
                                <el-button
                                    type="text"
                                    @click.stop="handleViewDetailInfo(scope.row)">查看</el-button>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                v-if="scope.row.status === 0"
                                @click.stop="handleUpdateStatus(1, scope.row.id)">采纳</el-button>
                            <el-button
                                type="text"
                                v-if="scope.row.status === 0"
                                @click.stop="handleUpdateStatus(2, scope.row.id)">驳回</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleDelete(scope.row.id)">删除</el-button>
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
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <el-dialog :close-on-click-modal="false" title="详细信息" :visible.sync="visible" width="30%">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>商品名称</p>
                    <el-input readonly :value="formInfo.productName" placeholder="商品名称"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>规格名称</p>
                    <el-input readonly :value="formInfo.specificationName" placeholder="规格名称"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>分类</p>
                    <el-input readonly :value="formInfo.categoryName" placeholder="分类"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>品牌</p>
                    <el-input readonly :value="formInfo.brandName" placeholder="品牌"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>条形码</p>
                    <el-input readonly :value="formInfo.barCode" placeholder="条形码"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>月需求量</p>
                    <el-input readonly :value="formInfo.monthlyConsumption" placeholder="月需求量"></el-input>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24" class="mb20">
                    <p>备注</p>
                    <el-input readonly type="textarea" :rows="2" :value="formInfo.remark" placeholder="备注"></el-input>
                </el-col>
            </el-row>
            <el-row style="margin-top: 20px;">
                <p><b>附件图片：</b></p>
                <a :href="item" v-if="formInfo.imageUrls && formInfo.imageUrls.length" v-for="item in formInfo.imageUrls" target="_blank">
                    <img :src="item" alt="" style="width: 100px;height:100px;">
                </a>
                <p v-if="!formInfo.imageUrls || formInfo.imageUrls.length === 0">无</p>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>

    import newProductDemand from '@/api/newProductDemand';
    import util from '@/libs/util';

    const searchForm = {
        searchDate: [],
        telephone: ''
    };

    const columns = [
        { key: 'createTime', title: '提交时间' },
        { key: 'merchantTelephone', title: '注册手机号码' },
        { key: 'productName', title: '商品名称' },
        { key: 'categoryName', title: '分类' },
        { key: 'detailInfo', title: '详细信息' },
        { key: 'statusName', title: '状态' }
    ];

    const status = {
        0: '待处理',
        1: '已采纳',
        2: '已驳回'
    };

    export default {

        data() {
            return {
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                status: status,

                formInfo: {},
                visible: false
            }
        },

        methods: {

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
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    param.createTimeBegin = this.searchForm.searchDate[0];
                    param.createTimeEnd = this.searchForm.searchDate[1];
                }
                if (this.searchForm.telephone) {
                    param.merchantTelephoneLike = this.searchForm.telephone;
                }
                newProductDemand.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.statusName = this.status[item.status];
                            item.createTime = util.parseTime(new Date(item.createTime))
                        })
                    }
                    this.records = res.records;
                    this.totalSize = res.total;
                })
            },

            /**
             * 详细信息
             * */
            handleViewDetailInfo(row) {
                this.formInfo = row;
                this.formInfo.imageUrls = [];
                if (row.imageUrl) {
                    this.formInfo.imageUrls = row.imageUrl.split(',');
                }
                this.visible = true;
            },

            /**
             * 更新状态
             * */
            handleUpdateStatus(status, id) {
                const param = {
                    status: status,
                    id: id
                };
                newProductDemand.updateStatus(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('操作成功');
                        this.handlePagers();
                    }
                })
            },

            handleCancel() {
                this.visible = false;
                this.formInfo = {};
            },

            /**
             * 删除
             * */
            handleDelete(id) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除？', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: id
                    };
                    newProductDemand.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            if (this.records && this.records.length === 1 && this.currentPage > 1) {
                                this.currentPage = this.currentPage - 1;
                            }
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
            }
        },

        created() {
            this.handlePagers();
        }

    }
</script>

<style scoped>

</style>
