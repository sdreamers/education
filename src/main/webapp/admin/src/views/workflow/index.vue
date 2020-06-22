<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_common/workflow/insert']">添加流程</el-button>
            </div>
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
                                v-if="G.permission['_common/workflowNode/insertOrUpdate']"
                                @click.stop="handleNode(scope.row)">节点设置</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_common/workflow/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_common/workflow/delete']"
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
            :yesNoMap="yesNoMap"
            :workflowCodesMap="workflowCodesMap"
            @close="dialogFormVisible=false,workflowPagers()"></edit>

        <node
            v-if="dialogNodeVisible"
            :formNode="formNode"
            :workflowId="workflowId"
            @close="dialogNodeVisible=false,workflowPagers()"></node>
    </section>
</template>
<script>
import api from '@/api/workflow';

import { workflowCodes, roles, workflowHandlerTypes, workflowEvents } from '@/api/dict';

import edit from './edit';
import node from './node';

const columns = [
    { key: 'name', title: '流程名称' },
    { key: 'useFlag_text', title: '启用' },
    { key: 'sendPlatformMsgFlag_text', title: '平台提示' },
    { key: 'sendSmsFlag_text', title: '短信提示' }
];
const form = {
    name: '',
    useFlag: '1',
    sendSmsFlag: '1',
    sendPlatformMsgFlag: '1'
};
const yesNoMap = {
    1: '是',
    0: '否'
}
export default {
    components: {
        edit,
        node
    },
    data() {
        return {
            yesNoMap: yesNoMap,
            workflowCodesMap: {},
            rolesMap: {},
            workflowHandlerTypesMap: {},
            workflowEventsMap: {},

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
            formType: '',

            formNode: [],
            workflowId: '',

            dialogNodeVisible: false
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.workflowPagers();
        },
        // 清除搜索
        handleClear() {
            this.key = '';
            this.currentPage = 1;
            this.workflowPagers();
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
        handleNode(data) {
            console.log(data);
            const param = {
                workflowId: data.id
            }
            api.getByWorkflowId(param).then(res => {
                if (res.code === 100) {
                    this.formNode = res.content || [];
                    if (this.formNode) {
                        this.formNode.forEach(item => {
                            if (item.handler) {
                                item.handler = item.handler.replace(/,/g, '&');
                            }
                            if (item.receiver) {
                                item.receiver = item.receiver.replace(/,/g, '&');
                            }
                        })
                    }
                    this.workflowId = data.id;
                    this.dialogNodeVisible = true;
                }
            })
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
                api.workflowDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.workflowPagers();
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
            this.workflowPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.workflowPagers();
        },
        // 获取数据
        workflowPagers() {
            if (!this.G.permission['_common/workflow/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            api.workflowPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    item.useFlag_text = yesNoMap[item.useFlag];
                    item.sendPlatformMsgFlag_text = yesNoMap[item.sendPlatformMsgFlag];
                    item.sendSmsFlag_text = yesNoMap[item.sendSmsFlag];
                    return item;
                });
                this.totalPage = res.total;
            });
        },
        workflowCodes() {
            workflowCodes().then(res => {
                if (res.code === 100) {
                    this.workflowCodesMap = res.content;
                }
            })
        },
        roles() {
            roles().then(res => {
                if (res.code === 100) {
                    this.rolesMap = res.content;
                }
            })
        },
        workflowHandlerTypes() {
            workflowHandlerTypes().then(res => {
                if (res.code === 100) {
                    this.workflowHandlerTypesMap = res.content;
                }
            })
        },
        workflowEvents() {
            workflowEvents().then(res => {
                if (res.code === 100) {
                    this.workflowEventsMap = res.content;
                }
            })
        }
    },
    created() {
        this.workflowCodes();
        this.roles();
        this.workflowHandlerTypes();
        this.workflowEvents();
        this.workflowPagers();
    }
};
</script>
