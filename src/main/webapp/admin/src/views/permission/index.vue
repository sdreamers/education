<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_account/permission/addPermission']">添加权限</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="permissionName" placeholder="权限名称"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-input v-model="permissionUrl" placeholder="权限地址"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-select v-model="permissionType" @change="handleSearchTypeChange" placeholder="权限类型">
                                <el-option
                                v-for="item in typeList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading"  @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort"  :width="column.width">
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="权限类别" width="100px">
                            <template slot-scope="scope">
                                <el-button
                                :type="scope.row.typeColor">{{scope.row.typeText}}</el-button>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_account/permission/addPermission']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_account/permission/deletePermissionById']"
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
        <el-dialog :close-on-click-modal="false" title="权限树结构" :visible.sync="dialogTreeVisible" top="5vh" class="h80percent">
            <el-tree :data="treeData" :props="defaultProps" @node-click="handleNodeClick" default-expand-all>
                <span class="custom-tree-node" slot-scope="{ node, data }"><span :style="{color: rgbMap[data.useType]}">{{data.useType}}{{node.label}}</span></span>
            </el-tree>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogTreeVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitTree">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :close-on-click-modal="false" title="添加 修改权限" :visible.sync="dialogFormVisible" @close="$refs.ruleForm.resetFields()"  v-if="G.permission['_account/permission/addPermission']">
            <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="权限类别" :label-width="formLabelWidth" prop="name">
                    <el-select v-model="form.type" placeholder="请选择">
                        <el-option
                        v-for="item in typeList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                            <span :style="{color: rgbMap[item.id]}">{{ item.name }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="父节点" :label-width="formLabelWidth" prop="name">
                    <div>{{form.parentName}}<span @click="handleShowParentTree" style="color:#08c;cursor:pointer">修改</span></div>

                    <!-- <el-input v-model="form.parentName" type="text" auto-complete="off" disabled @click="handleShowParentTree"></el-input> -->

                </el-form-item>
                <el-form-item label="权限名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" type="text" auto-complete="off" maxlength="64"></el-input>
                </el-form-item>
                <el-form-item label="权限URL地址" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.url" type="text" auto-complete="off" maxlength="255"></el-input>
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
import api from '@/api/permission';
import Util from '@/libs/util';
const columns = [
    { key: 'id', title: 'ID', width: '100px', sort: 'custom' },
    { key: 'name', title: '权限名称', width: '200px' },
    { key: 'url', title: '对应地址' }
];
const form = {
    type: '',
    name: ''
};
const colorMap = {
    0: 'primary',
    1: 'success',
    2: 'danger'
};
const textMap = {
    0: '菜单',
    1: '页面',
    2: '功能'
};
const typeList = [
    { id: null, name: '请选择' },
    { id: 0, name: '菜单' },
    { id: 1, name: '页面' },
    { id: 2, name: '功能' }
];
const rgbMap = {
    0: '#08c',
    1: '#19a318',
    2: '#e23a38'
};

export default {
    data() {
        return {
            permissionName: '',
            permissionUrl: '',
            permissionType: '',

            tableData: [],
            columns: columns,

            typeList: typeList,

            defaultProps: {
                children: 'children',
                label: 'label'
            },
            dialogTreeVisible: false,
            treeData: [],

            rgbMap: rgbMap,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            currentNode: {},
            formType: '',
            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.permissionPagers();
        },
        // 清除搜索
        handleClear() {
            this.permissionName = '';
            this.permissionUrl = '';
            this.permissionType = '';
            this.currentPage = 1;
            this.permissionPagers();
        },
        handleSearchTypeChange(data) {
            window.LOG(data);
            this.permissionType = data;
            this.permissionPagers();
        },
        handleNodeClick(data) {
            window.LOG(data);
            this.currentNode = data;
        },
        handleSubmitTree() {
            this.form.parentId = this.currentNode.id;
            this.form.parentName = this.currentNode.label;
            this.dialogTreeVisible = false;
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;

            const type = this.form.type;
            const useType = this.currentNode.useType;
            if (parseInt(type === 0)) {
                if (parseInt(useType) !== 0) {
                    return this.$notify.error('您选择的菜单节点，父节点只能选菜单节点（蓝色字体）');
                }
            } else if (parseInt(type) === 1) {
                if (parseInt(useType) === 2) {
                    return this.$notify.error('您选择的页面节点，父节点可选菜单节点（蓝色字体），页面节点（绿色字体）');
                }
            } else if (parseInt(type) === 2) {
                if (parseInt(useType) !== 1) {
                    return this.$notify.error('您选择的功能节点，父节点只能选页面节点（绿色字体）');
                }
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        window.LOG(param);
                        api.updateByPrimaryKeySelective(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.permissionPagers();
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        window.LOG(param);
                        api.addPermission(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.permissionPagers();
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
        handleEdit(data) {
            this.formType = 'edit';
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
            this.form.name = data.name;
            api.selectByPrimaryKey({ id: data.parentId }).then(res => {
                if (res.code === 100) {
                    this.currentNode.id = res.content.id;
                    this.currentNode.label = res.content.name;
                    this.currentNode.useType = res.content.type;
                    this.$set(this.form, 'parentName', res.content.name);
                    this.$set(this.form, 'parentId', res.content.id);
                }
            });
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
                    ids: data.id
                };
                api.deletePermissionById(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.permissionPagers();
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
            this.permissionPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.permissionPagers();
        },
        handleShowParentTree() {
            window.LOG('123');
            if (this.form.type === '') {
                return this.$notify.error('请先选择权限类别');
            }
            this.dialogTreeVisible = true;
            this.listPermissionOfZTree();
        },
        listPermissionOfZTree() {
            this.treeData = [];
            api.listPermissionOfZTree({ type: this.form.type }).then(res => {
                if (res.code === 100) {
                    const data = res.content.map(item => {
                        item.label = item.name;
                        return item;
                    });
                    this.treeData = Util.transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'parentId',
                        childrenKey: 'children'
                    }, data);
                    // window.LOG(transformTozTreeFormat({
                    //     idKey: 'id',
                    //     pIdKey: 'parentId',
                    //     childrenKey: 'children'
                    // }, data));
                }
            });
        },
        // 获取数据
        permissionPagers() {
            if (!this.G.permission['_account/permission/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.permissionName !== undefined) {
                param.permissionName = this.permissionName;
            }
            if (this.permissionUrl !== undefined) {
                param.permissionUrl = this.permissionUrl.replace(/ /g, '');
            }
            if (this.permissionType !== undefined) {
                param.type = this.permissionType;
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.permissionPagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.typeColor = colorMap[item.type];
                        item.typeText = textMap[item.type];
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.error('无数据');
                }
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.permissionPagers();
        }
    },
    created() {
        this.permissionPagers();
    }
};
</script>
