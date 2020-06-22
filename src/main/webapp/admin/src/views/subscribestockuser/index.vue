<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <img  v-if="column.key === 'previewImageURL'" style="width: 80px;height: 80px;" :src="scope.row.previewImageURL" />
                            <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleSubscribe(scope.row)">订阅人</el-button>
                            <el-button
                            type="text"
                            @click.stop="handleDetail(scope.row)">查看商品详情</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_account/subscribestockuser/delete']"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page.sync="currentPage"
                    :page-sizes="[10, 20]"
                    :page-size="pageSize"
                    layout="total, sizes, slot, prev, pager, next"
                    :total="totalPage">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
        <edit
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,subscribestockuserPagers()"
            :formType="formType"
            :form="form"></edit>
        <subscribe
            v-if="dialogSubscribeVisible"
            @close="dialogSubscribeVisible=false,subscribestockuserPagers()"
            :form="formSubscribe"
            ></subscribe>
    </section>
</template>
<script>
import api from '@/api/subscribestockuser';

import edit from './edit';
import subscribe from './subscribe';

const columns = [
    { key: 'productName', title: '商品名称' },
    { key: 'specificationName', title: '商品规格' },
    { key: 'countMerchant', title: '统计距当前时间有效订阅到货通知的人数' }
];
const form = {};
export default {
    components: {
        edit,
        subscribe
    },
    data() {
        return {
            searchInput: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: '',

            dialogSubscribeVisible: false,
            formSubscribe: {},
            // 品牌列表
            productGroupList: [],
            // 单位列表
            unitList: [],
            splitList: [
                { id: '0', name: '否' },
                { id: '1', name: '是' }
            ]
        };
    },
    methods: {
        // 添加基本信息
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        handleSubscribe(data) {
            console.log(data);
            this.formSubscribe = data;
            this.dialogSubscribeVisible = true;
        },
        // 点击编辑基本信息按钮
        handleDetail(data) {
            this.formType = 'edit';
            api.getProductSpecificationById({ specificationId: data.specificationId }).then(res => {
                if (res.code === 100) {
                    this.form = JSON.parse(JSON.stringify(res.content));
                    console.log(this.form.available);
                    this.form.available = this.form.available === 1 ? '是' : '否';
                    this.dialogFormVisible = true;
                }
            })
        },
        // 删除基本信息
        handleRemove(data) {
            console.log(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    productId: data.productId,
                    specificationId: data.specificationId
                };
                api.subscribestockuserDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.subscribestockuserPagers();
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
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.subscribestockuserPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.subscribestockuserPagers();
        },
        // 获取基本信息列表
        subscribestockuserPagers() {
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            api.subscribestockuserPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.subscribestockuserPagers();
    }
};
</script>
