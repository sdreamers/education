<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" type="primary" @click="handleAdd" v-if="G.permission['_account/message/insertByMulticase']">添加消息</el-button>
            </div>
            <el-row class="mb20" :gutter="20">
                <el-col :span="4">
                    <p>消息类型：</p>
                    <el-select v-model="search.type" placeholder="消息类型">
                        <el-option v-for="(item,key) in messageTypes" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    <p>关联项目：</p>
                    <el-select v-model="search.projectType" placeholder="关联项目">
                        <el-option v-for="(item,key) in messageProjectTypes" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="4">
                    <p>关联订阅类型：</p>
                    <el-select v-model="search.subscribeId" placeholder="关联订阅类型">
                        <el-option v-for="(item,key) in subscribes" :label="item" :value="key" :key="key"></el-option>
                    </el-select>
                </el-col>
                
                <el-col :span="6">
                    <p>创建时间：</p>
                    <el-date-picker
                        v-model="search.formDate"
                        type="daterange"
                        align="right"
                        unlink-panels
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </el-col>
                <el-col :span="6">
                    <p>搜索：</p>
                    <el-button type="primary" @click="messagePagers()">搜索</el-button>
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
                            v-if="scope.row.type !== '3' && G.permission['_account/message/update']"
                            type="text"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            v-if="scope.row.type !== '3' && G.permission['_account/message/delete']"
                            type="text"
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
            @close="dialogFormVisible=false,messagePagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/message';
import Util from '@/libs/util';

import edit from './edit';
const messageTypes = { '': '全部', 0: '系统公告', 1: '业务提醒', 2: '业务私信', 3: '平台内部消息' };
const messageProjectTypes = { '': '全部', 0: '商品', 1: '活动', 2: '消息', 3: '订单' };
const subscribes = { '': '全部', 1: '系统公告', 2: '支付通知', 3: '退款通知', 4: '物流通知', 5: '售价调整通知', 6: '到货通知' };


const columns = [
    { key: 'id', title: 'ID' },
    { key: 'title', title: '消息标题' },
    { key: 'content', title: '消息内容' },
    { key: 'type_text', title: '消息类型' },
    { key: 'subscribeId', title: '订阅类型' },
    { key: 'projectType', title: '关联项目' },
    { key: 'projectId', title: '关联项目ID' },
    { key: 'senderName', title: '消息创建人' },
    { key: 'createDateTime', title: '创建时间' }
];
const form = {
    id: '',
    title: '',
    content: '',
    type: '0',
    projectId: '0',
    projectType: '2'
};
export default {
    components: {
        edit
    },
    data() {
        return {
            search: {
                type: '',
                projectType: '',
                subscribeId: '',
                formDate: []
            },

            tableData: [],
            columns: columns,

            messageTypes: messageTypes,
            messageProjectTypes: messageProjectTypes,
            subscribes: subscribes,

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
            this.search.type = '';
            this.search.projectType = '';
            this.search.subscribeId = '';
            this.search.formDate = [];
            this.messagePagers();
        },
        handleInit() {
            api.messageInit().then(res => {
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
            api.messageGet({ id: data.id }).then(res => {
                if (res.code === 100) {
                    this.form = JSON.parse(JSON.stringify(res.content));
                    this.dialogFormVisible = true;
                }
            })
        },
        // 删除基本信息
        handleRemove(data) {
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.messageDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.messagePagers();
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
            this.messagePagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.messagePagers();
        },
        // 获取基本信息列表
        messagePagers() {
            if (!this.G.permission['_account/message/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;

            param.type = this.search.type;
            param.projectType = this.search.projectType;
            param.subscribeId = this.search.subscribeId;
            if (this.search.formDate && this.search.formDate.length) {
                param.createDateTimeBegin = Util.formatTime(new Date(this.search.formDate[0]), true);
                param.createDateTimeEnd = Util.formatTime(new Date(this.search.formDate[1]), true);
            }
            api.messagePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records ? res.records.map(item => {
                    item.title = item.type !== '3' ? item.title : '';
                    item.type_text = messageTypes[item.type];
                    if (item.subscribeId) {
                        item.subscribeId = subscribes[item.subscribeId] ? subscribes[item.subscribeId] : ''
                    } else {
                        item.subscribeId = '';
                    }
                    item.projectType = item.projectType ? messageProjectTypes[item.projectType] : '';
                    item.createDateTime = Util.parseTimeStr(item.createDateTime);
                    return item;
                }) : [];
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.messagePagers();
    }
};
</script>
