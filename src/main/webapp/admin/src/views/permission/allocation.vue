<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="findContent" placeholder="输入角色名称 / 角色类型"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="100px;">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_account/permissionAllocation/addPermission2Role']"
                            @click.stop="handleEdit(scope.row)">分配权限</el-button>
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
        <el-dialog :close-on-click-modal="false" title="添加权限" :visible.sync="dialogFormVisible" top="5vh" class="h80percent dialogw80">
            <el-row>
                <el-col :span="14">
                    <el-tree
                    :data="roleList"
                    :props="defaultProps"
                    show-checkbox
                    default-expand-all
                    node-key="id"
                    :check-strictly="!relation.strict"
                    ref="tree"
                    :default-checked-keys="form.permissionIds">
                        <span class="custom-tree-node" slot-scope="{ node, data }"><span :style="{color: rgbMap[data.useType]}">{{data.id}}{{node.label}}</span></span>
                    </el-tree>
                </el-col>
                <el-col :span="10">
                    <h2>自定义配置</h2>
                    <p>父子关联关系</p>
                    <p><el-checkbox v-model="relation.strict">父子关联</el-checkbox></p>
                    <!-- <p>被勾选时：<el-checkbox v-model="relation.checkP">关联父</el-checkbox><el-checkbox v-model="relation.checkS">关联子</el-checkbox></p>
                    <p>取消勾选时：<el-checkbox v-model="relation.uncheckP">关联父</el-checkbox><el-checkbox v-model="relation.uncheckS">关联子</el-checkbox></p> -->
                </el-col>
            </el-row>
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
    { title: '角色名称', key: 'name', width: '100px' },
    { title: '角色类型', key: 'type', width: '100px' },
    { title: '拥有的权限', key: 'permissionNames' }
];
const form = {
    permissionIds: []
};
const rgbMap = {
    0: '#08c',
    1: '#19a318',
    2: '#e23a38'
};
export default {
    data() {
        return {
            findContent: '',

            tableData: [],
            columns: columns,

            roleList: [],
            // treeList: [],
            defaultProps: {
                children: 'children',
                label: 'label'
            },

            relation: {
                checkP: true,
                checkS: true,
                uncheckP: true,
                uncheckS: true,
                strict: false
            },

            roleId: '',

            rgbMap: rgbMap,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form))
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.permissionAllocation();
        },
        // 清除搜索
        handleClear() {
            this.findContent = '';
            this.currentPage = 1;
            this.permissionAllocation();
        },
        // 提交表单
        handleSubmit() {
            // 选中的叶子节点（包括根节点）
            const checkedKeys = this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys());
            const param = {
                roleId: this.roleId,
                ids: checkedKeys.join(',')
            };
            window.LOG(param.roleId, param.ids);
            if (!param.roleId || !param.ids) {
                return this.$notify.error('请正确选择');
            }
            api.permissionUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.permissionAllocation();
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        },
        // 取消表单
        handleCancel() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = false;
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.relation.strict = false;
            this.listPermissionByRoleIdOfZTree(data.id).then(res => {
                // this.treeList = res;
                this.roleList = Util.transformTozTreeFormat({
                    idKey: 'id',
                    pIdKey: 'parentId',
                    childrenKey: 'children'
                }, JSON.parse(JSON.stringify(res)));
                const arr = [];
                // 根据字段checked!=0表示选中
                res.forEach(item => {
                    // 只给叶子节点选中
                    if (item.checked !== 0) {
                        arr.push(item.id);
                    }
                    // return arr;
                });
                this.form.permissionIds = arr;
                this.dialogFormVisible = true;
                this.roleId = data.id;
            });
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.permissionAllocation();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.permissionAllocation();
        },
        listPermissionByRoleIdOfZTree(roleId) {
            const param = {
                roleId
            };
            return api.listPermissionByRoleIdOfZTree(param).then(res => this.roleList = res.map(item => {
                item.label = item.name;
                return item;
            }));
        },
        // 获取数据
        permissionAllocation() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            if (this.findContent) {
                param.findContent = this.findContent;
            }
            api.permissionAllocation(param).then(res => {
                this.tableData = res.records;
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.permissionAllocation();
    }
};
</script>
