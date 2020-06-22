<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_account/role/addRole']">添加角色</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="findContent" placeholder="输入角色类型 / 角色名称"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" v-if="G.permission['_account/role/deleteRoleById']">
                            <template slot-scope="scope">
                                <el-button
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
                        :page-sizes="[2, 5, 10, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPage">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
            </el-row>
        </el-card>
        <el-dialog :close-on-click-modal="false" title="添加角色" :visible.sync="dialogFormVisible" @close="$refs.ruleForm.resetFields()" v-if="G.permission['_account/role/addRole']">
            <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="角色名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" type="text" auto-complete="off" maxlength="32" placeholder="请输入角色名称"></el-input>
                </el-form-item>
                <el-form-item label="角色类型" :label-width="formLabelWidth" prop="type">
                    <el-input v-model="form.type" type="text" auto-complete="off" maxlength="6" placeholder="请输入角色类型  [字母 + 数字] 6位"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
import api from '@/api/role';
const columns = [
    { key: 'name', title: '角色名称' },
    { key: 'type', title: '角色类型' }
];
const form = {
    type: '',
    name: ''
};
export default {
    data() {
        function checkType(rule, value, callback) {
            if (!/^[a-z0-9A-Z]{6}$/.test(value)) {
                callback(new Error('角色类型为6数字字母'));
            } else {
                callback();
            }
        }
        return {
            findContent: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,

            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px',
            form: form,
            formType: '',
            rules: {
                type: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: checkType, trigger: 'blur' }
                ],
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.rolePagers();
        },
        // 清除搜索
        handleClear() {
            this.findContent = '';
            this.currentPage = 1;
            this.rolePagers();
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        api.roleUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.rolePagers();
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.roleInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.rolePagers();
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    }
                } else {
                    this.$notify({
                        message: '请按要求填写表单',
                        type: 'warning'
                    });
                    return false;
                }
            });
        },
        // 取消表单
        handleCancel() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = false;
            this.$refs.ruleForm.resetFields();
        },
        // 点击添加按钮
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击删除按钮
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除「' + data.name + '」', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    ids: data.id
                };
                api.roleDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.rolePagers();
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
            this.rolePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.rolePagers();
        },
        // 获取数据
        rolePagers() {
            if (!this.G.permission['_account/role/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.findContent) {
                param.findContent = this.findContent;
            }
            api.rolePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.rolePagers();
    }
};
</script>
