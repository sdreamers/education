<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                    <el-row :gutter="20">
                        <el-col :span="6" v-show="G.permission['_account/department/listAll']">
                            <el-tree style="overflow-x: auto;border: 1px solid #eee;margin-top: 20px;" :data="treeData" :props="defaultProps" @node-click="handleSideNodeClick" default-expand-all highlight-current>
                                <span class="custom-tree-node" slot-scope="{ node, data }">
                                    <span style="font-size:12px;">{{ node.label }}</span>
                                    <span>
                                        <el-button
                                            v-if="G.permission['_account/department/insert']"
                                            type="text"
                                            @click="() => handleDepartmentAdd(data)">
                                            添加
                                        </el-button>
                                        <el-button
                                            v-if="G.permission['_account/department/update']"
                                            type="text"
                                            @click="() => handleDepartmentEdit(data)">
                                            修改
                                        </el-button>
                                        <el-button
                                            v-if="G.permission['_account/department/delete']"
                                            type="text"
                                            @click="() => handleDepartmentDelete(data)">
                                            删除
                                        </el-button>
                                    </span>
                                </span>
                            </el-tree>
                        </el-col>
                        <el-col :span="18">
                            <el-row class="mb20" :gutter="20">
                                <el-col :span="6">
                                    <el-input v-model="nickName" placeholder="姓名"></el-input>
                                </el-col>
                                <el-col :span="6">
                                    <el-input v-model="email" placeholder="电子邮箱"></el-input>
                                </el-col>
                                <el-col :span="6">
                                    <el-input v-model="telephone" placeholder="手机号码"></el-input>
                                </el-col>
                                <el-col :span="6">
                                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                                    <el-button @click="clear">清除</el-button>
                                </el-col>
                            </el-row>
                            <el-table :data="tableData" border v-loading="loading" @row-click="rowClick">
                                <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                                    <template slot-scope="scope">
                                        <span>{{scope.row[column.key]}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column label="状态" width="100px" v-if="G.permission['_account/member/forbidUserById']">
                                    <template slot-scope="scope">
                                        <section style="text-align:center">
                                            <el-switch
                                                v-model="scope.row.status === 1"
                                                active-color="#13ce66"
                                                inactive-color="#ff4949">
                                            </el-switch>
                                        </section>
                                    </template>
                                </el-table-column>
                                <el-table-column label="操作" width="240px">
                                    <template slot-scope="scope">
                                        <el-button
                                        type="text"
                                        v-if="G.permission['_account/usercities/userCityies']"
                                        @click.stop="handleEditCity(scope.row)">可访问城市</el-button>

                                        <el-button
                                        v-if="scope.row.departmentId && G.permission['_account/departmentUser/delete']"
                                        type="text"
                                        @click.stop="handleEditDepartment(scope.row)">离开部门</el-button>
                                        <el-button
                                        v-if="!scope.row.departmentId && G.permission['_account/departmentUser/delete']"
                                        type="text"
                                        @click.stop="handleEditDepartment(scope.row)">加入部门</el-button>

                                        <el-button
                                        type="text"
                                        v-if="G.permission['_account/member/deleteUserById']"
                                        @click.stop="handleRemove(scope.row)">删除账户</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-col>
                    </el-row>
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
        <city
            v-if="dialogCityVisible"
            :userId="userId"
            @close="dialogCityVisible=false,memberPagers(),departmentListAll()"></city>
        <department
            v-if="dialogUserDepartmentVisible"
            :userId="userId"
            :defaultProps="defaultProps"
            :treeData="treeData"
            @close="dialogUserDepartmentVisible=false,memberPagers(),departmentListAll()"
        ></department>
        <edit
            v-if="dialogDepartmentVisible"
            :departmentName="departmentName"
            :departmentPName="departmentPName"
            :defaultProps="defaultProps"
            :treeData="treeData"
            :departmentPid="departmentPid"
            :departmentId="departmentId"
            :formType="formType"
            @close="dialogDepartmentVisible=false,memberPagers(),departmentListAll()"></edit>
    </section>
</template>
<script>
import api from '@/api/member';
import Util from '@/libs/util';

import city from './city';
import department from './department';
import edit from './edit';

const columns = [
    { key: 'nickName', title: '姓名', width: '100px' },
    { key: 'telephone', title: '手机号码', width: '100px' },
    { key: 'email', title: '电子邮箱', width: '150px' },
    { key: 'roleName', title: '角色', width: '140px' },
    { key: 'departmentName', title: '所在部门', width: '120px' },
    { key: 'jobNumber', title: '工号', width: '80px' },
    { key: 'createTime', title: '注册时间', width: '150px' },
    { key: 'lastLoginTime', title: '最近登录时间', width: '150px' }
];
export default {
    components: {
        city,
        department,
        edit
    },
    data() {
        return {
            nickName: '',
            email: '',
            telephone: '',

            departmentId: '',
            departmentName: '',
            departmentPid: '',
            departmentPName: '',

            tableData: [],
            columns: columns,

            defaultProps: {
                children: 'children',
                label: 'name'
            },
            treeData: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,

            loading: false,

            dialogUserDepartmentVisible: false,

            dialogCityVisible: false,

            dialogDepartmentVisible: false,

            dialogFormVisible: false,
            formType: '',
            form: {}
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.memberPagers();
        },
        // 清除搜索
        clear() {
            this.nickName = '';
            this.email = '';
            this.telephone = '';
            this.departmentId = '';
            this.currentPage = 1;
            this.memberPagers();
        },
        // 点击「编辑可访问城市」
        handleEditCity(data) {
            this.userId = data.userId;
            this.dialogCityVisible = true;
        },
        // 点击「离开或者加入部门」
        handleEditDepartment(data) {
            if (data.departmentId) {
                this.$confirm('确定将该人员移出当前所在的部门吗?', '确认移出', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: data.id
                    };
                    api.departmentUserDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.dialogUserDepartmentVisible = false;
                            this.memberPagers();
                            this.departmentListAll();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(() => {
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            } else {
                this.userId = data.userId;
                this.dialogUserDepartmentVisible = true;
                this.memberPagers();
                this.departmentListAll();
            }
        },
        // 点击删除人员
        handleRemove(data) {
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.userId
                };
                if (!param.id) {
                    this.$notify.error('未选择人员');
                }
                api.memberDeleteUserById(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.memberPagers();
                        this.departmentListAll();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }).catch(() => {
                this.$notify({
                    type: 'info',
                    message: '取消'
                });
            });
        },
        // 点击部门
        handleSideNodeClick(data) {
            this.departmentId = data.id;
            this.departmentName = data.name.split('(')[0];
            this.departmentPid = data.pId;
            this.memberPagers();
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.memberPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.memberPagers();
        },
        // 添加部门
        handleDepartmentAdd(data) {
            window.LOG(data);
            this.formType = 'add';
            setTimeout(() => {
                this.departmentId = '';
                this.departmentName = '';
                this.departmentPid = data ? data.id : '0';
                this.departmentPName = data ? data.name : '';
                this.dialogDepartmentVisible = true;
            }, 100);
        },
        // 编辑部门
        handleDepartmentEdit(data) {
            this.departmentId = data.id;
            this.departmentPid = data.pId;
            this.departmentName = data.name;
            setTimeout(() => {
                this.formType = 'edit';
                if (this.departmentId) {
                    const pName = Util.getTreeCheckedNodes({
                        checkedKey: 'id',
                        childrenKey: 'children'
                    }, this.treeData, this.departmentPid, []);
                    if (pName.length) {
                        this.departmentPName = pName[0].name;
                    } else {
                        this.departmentPName = '';
                    }

                    this.dialogDepartmentVisible = true;
                } else {
                    this.$notify.error('请选择部门');
                }
            }, 100);
            // this.departmentId = this.departmentId.split();
        },
        // 删除部门
        handleDepartmentDelete() {
            setTimeout(() => {
                if (!this.departmentId) {
                    return this.$notify.error('请选择部门');
                }
                this.$confirm(`您确认删除${this.departmentName}吗？删除以后将无法恢复。`, '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: this.departmentId
                    };
                    api.departmentDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.memberPagers();
                            this.departmentListAll();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(() => {
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            }, 100);
        },

        // 获取规格，或者显示隐藏
        rowClick(row, event, column) {
            const id = row.userId;
            if (!id) {
                return this.$notify.error('id 异常');
            }
            if (column.label === '状态') {
                const status = row.status === 0 ? 1 : 0;
                this.$confirm('确定' + (status === 0 ? '禁止' : '激活') + '该用户？').then(() => {
                    api.forbidUserById({ id, status }).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.memberPagers();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(() => {
                    window.LOG('取消');
                });
            }
        },
        // 获取分页
        memberPagers() {
            if (!this.G.permission['_account/member/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            if (this.nickName) {
                param.nickName = this.nickName;
            }
            if (this.email) {
                param.email = this.email;
            }
            if (this.telephone) {
                param.telephone = this.telephone;
            }
            if (this.departmentId) {
                param.departmentId = this.departmentId;
            }
            api.memberPagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        // 创建时间、上次登录时间初始化去掉.0
                        if (item.createTime) {
                            item.createTime = item.createTime.replace(/.[0-9]*$/, '');
                        }
                        if (item.lastLoginTime) {
                            item.lastLoginTime = item.lastLoginTime.replace(/.[0-9]*$/, '');
                        }
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.info('无数据');
                }
                this.totalPage = res.total;
            });
        },
        // 获取部门树状图
        departmentListAll() {
            const param = {
                showDetailsFlag: true
            };
            api.departmentListAll(param).then(res => {
                const data = JSON.parse(res.content);
                if (res.code === 100) {
                    this.treeData = Util.transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'pId',
                        childrenKey: 'children'
                    }, data);
                }
            });
        }
    },
    created() {
        this.departmentListAll();
        this.memberPagers();
    }
};
</script>
