<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_order/rechargeRules/saveRechargeRules']">添加规则</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                            <template slot-scope="scope">
                                <span v-if="column.key !== 'previewImageUrl'">{{scope.row[column.key]}}</span>
                                <img style="width:70px;height:52px;" v-if="column.key === 'previewImageUrl'" :src="scope.row[column.key]"/>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_order/rechargeRules/updateRechargeRules']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_order/rechargeRules/deleterechargeRules']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
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
                        :total="totalPage">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
            </el-row>
        </el-card>

        <edit
            v-if="dialogFormVisible"
            :formType="formType"
            :form="form"
            @close="dialogFormVisible=false,rechargeRulesPagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/rechargeRules';

import edit from './edit';

const columns = [
    { key: 'id', title: '序号' },
    { key: 'payablePrice', title: '充值金额（元）' },
    { key: 'giftAmount', title: '赠送金额（元）' },
    { key: 'previewImageUrl', title: '图片地址' }
];
const form = {
    payablePrice: '',
    giftAmount: '',
    previewImageUrl: ''
};
export default {
    components: {
        edit
    },
    data() {
        return {
            key: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 20,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: ''
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.rechargeRulesPagers();
        },
        // 清除搜索
        handleClear() {
            this.key = '';
            this.currentPage = 1;
            this.rechargeRulesPagers();
        },
        // 点击添加按钮
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.formType = 'edit';
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        // 点击删除按钮
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.rechargeRulesDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.rechargeRulesPagers();
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
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.rechargeRulesPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.rechargeRulesPagers();
        },
        // 获取数据
        rechargeRulesPagers() {
            if (!this.G.permission['_order/rechargeRules/pages']) {
                return this.$notify.error('没有获取列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            api.rechargeRulesPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.rechargeRulesPagers();
    }
};
</script>
