<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_account/vipLevelRule/insert']">添加会员等级</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" v-if="G.permission['_account/vipLevelRule/update']">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
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
                    :total="totalPage">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
        <edit
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,vipLevelRulePages()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/vipLevelRule';

import edit from './edit';

const columns = [
    { key: 'id', title: '序号' },
    { key: 'levelName', title: '等级名称' },
    { key: 'experience', title: '所需经验值' },
    { key: 'discount', title: '折扣率' }
];
const form = {};
export default {
    components: {
        edit
    },
    data() {
        return {
            tableData: [],
            columns: columns,


            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: '',
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
        handleGroupChange(data) {
            const param = {
                groupId: data.groupId,
                id: data.id
            };
            api.productBrandUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.vipLevelRulePages();
                } else {
                    this.$notify.warning(res.message || '异常错误');
                }
            });
        },
        // 点击搜索按钮
        handleSearch() {
            this.currentPage = 1;
            this.vipLevelRulePages();
        },
        // 清除搜索条件
        handleClear() {
            this.currentPage = 1;
            this.vipLevelRulePages();
        },
        // 关闭基本信息弹层
        handleCancel() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = false;
            this.$refs.ruleForm.resetFields();
        },
        // 添加基本信息
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑基本信息按钮
        handleEdit(data) {
            this.formType = 'edit';
            const formData = JSON.parse(JSON.stringify(data));
            this.form = JSON.parse(JSON.stringify(formData));
            this.dialogFormVisible = true;
        },
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.vipLevelRulePages();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.vipLevelRulePages();
        },
        // 获取基本信息列表
        vipLevelRulePages() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            api.vipLevelRulePages(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.vipLevelRulePages();
    }
};
</script>
