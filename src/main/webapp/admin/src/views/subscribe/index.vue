<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" type="primary" @click="handleAdd" v-if="G.permission['_account/subscribe/insert']">添加订阅</el-button>
                <el-button style="float: right;margin-left: 10px;" @click="handleInit" v-if="G.permission['_account/subscribe/init']">初始化</el-button>
            </div>
            <el-row class="mb20" :gutter="20">
                <el-col :span="4">
                    <p>订阅标题：</p>
                    <el-input v-model="search.title" placeholder="订阅标题"></el-input>
                </el-col>
                <el-col :span="6">
                    <p>搜索：</p>
                    <el-button type="primary" @click="subscribePagers()">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_account/subscribe/update']"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_account/subscribe/delete']"
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
            @close="dialogFormVisible=false,subscribePagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/subscribe';

import edit from './edit';

const columns = [
    { key: 'id', title: '编号' },
    { key: 'title', title: '订阅标题' },
    { key: 'code', title: '订阅代码' },
    { key: 'defaultFlag_text', title: '默认订阅' },
    { key: 'userEditFlag_text', title: '用户是否可选' },
    { key: 'pushType_text', title: '推送模式' },
    { key: 'isSendSms_text', title: '是否发生短信' }
];
const form = {
    id: '',
    title: '',
    code: '',
    defaultFlag: '1',
    allowUserType: '',
    pushType: '0',
    isSendSms: '0'
};
export default {
    components: {
        edit
    },
    data() {
        return {
            search: {
                title: ''
            },

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
            // 品牌列表
            productGroupList: [],
            // 单位列表
            unitList: []
        };
    },
    methods: {
        handleClear() {
            this.search.title = '';
            this.subscribePagers();
        },
        handleInit() {
            api.subscribeInit().then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                }
            })
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
        // 删除基本信息
        handleRemove(data) {
            console.log(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.subscribeDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.subscribePagers();
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
            this.subscribePagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.subscribePagers();
        },
        // 获取基本信息列表
        subscribePagers() {
            if (!this.G.permission['_account/subscribe/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;

            param.title = this.search.title;
            api.subscribePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    item.defaultFlag_text = item.defaultFlag === '1' ? '是' : '否';
                    item.userEditFlag_text = item.userEditFlag === '1' ? '可选' : '不可选';
                    item.pushType_text = item.pushType === '1' ? '单播推送' : '广播推送';
                    item.isSendSms_text = item.isSendSms === '1' ? '发送' : '不发送';
                    return item;
                });
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.subscribePagers();
    }
};
</script>
