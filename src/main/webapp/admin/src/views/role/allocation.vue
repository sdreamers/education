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
                        <el-input v-model="findContent" placeholder="输入用户昵称 / 用户帐号"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span v-html="scope.row[column.key]"></span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" v-if="G.permission['_account/roleAllocation/addRole2User']" width="100px">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleEdit(scope.row)">给用户分配角色</el-button>
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
        <el-dialog :close-on-click-modal="false" title="添加角色" :visible.sync="dialogFormVisible" v-if="G.permission['_account/roleAllocation/addRole2User']">
            <div v-for="(role, key) in roleList" :key="key">
                <el-checkbox :label="role.name" :key="role.id" v-model="role.check">{{role.name}}</el-checkbox>
            </div>
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
    { key: 'nickname', title: '用户昵称', width: '100px' },
    { title: 'Email/帐号', key: 'email', width: '200px' },
    { title: '状态', key: 'statusText', width: '100px' },
    { title: '拥有的角色', key: 'roleNames' }
];
export default {
    data() {
        return {
            findContent: '',

            tableData: [],
            columns: columns,

            roleList: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,

            loading: false,

            dialogFormVisible: false,
            formLabelWidth: '120px'
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.roleAllocationPagers();
        },
        // 清除搜索
        handleClear() {
            this.findContent = '';
            this.currentPage = 1;
            this.roleAllocationPagers();
        },
        // 提交表单
        handleSubmit() {
            const param = {
                userId: this.userId,
                ids: this.roleList.filter(item => item.check).map(item => item.id).join(',')
            };
            api.roleUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogFormVisible = false;
                    this.roleAllocationPagers();
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        },
        // 取消表单
        handleCancel() {
            this.dialogFormVisible = false;
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.dialogFormVisible = true;
            this.userId = data.id;
            this.selectRoleByUserId(data.id);
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.roleAllocationPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.roleAllocationPagers();
        },
        selectRoleByUserId(id) {
            const param = {
                id
            };
            this.roleList = [];
            api.selectRoleByUserId(param).then(res => {
                this.roleList = res;
            });
        },
        // 获取数据
        roleAllocationPagers() {
            if (!this.G.permission['_account/roleAllocation/pages']) {
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
            api.roleAllocationPagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.statusText = item.status === 1 ? '<span style="color: #19a318">有效</span>' : '<span style="color: #e23a38">无效</span>';
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.error('无数据');
                }
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.roleAllocationPagers();
    }
};
</script>
