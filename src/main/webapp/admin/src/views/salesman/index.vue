<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_salesman/salesman/insert']">添加业务员</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <el-input v-model="searchString" placeholder="手机号、姓名"></el-input>
                        </el-col>
                        <el-col :span="4">
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleClear">清除</el-button>
                        </el-col>
                        <el-col :span="8">
                            <span style="font-size:14px;">业务员角色：</span>
                            <el-select v-model="role" @change="handleStatusChange" placeholder="请选择">
                                <el-option
                                v-for="item in salesmanRoleArr"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading" @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key"  :sortable="column.sort">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageurl'" style="width: 60px;height: 60px;" :src="scope.row.previewImageurl" />
                                <span v-if="column.key !== 'previewImageurl'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="180">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                @click.stop="handleOrder(scope.row)">关联的订单</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_salesman/salesman/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_salesman/salesman/delete']"
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
        <el-dialog :close-on-click-modal="false" title="添加 \ 修改 业务员基本信息" :visible.sync="dialogFormVisible" @close="$refs.ruleForm.resetFields()">
            <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" type="text" auto-complete="off" maxlength="10"></el-input>
                </el-form-item>
                <el-form-item label="手机号码" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.telephone" type="text" auto-complete="off" maxlength="11"></el-input>
                </el-form-item>
                <el-form-item label="性别" :label-width="formLabelWidth" prop="name">
                    <el-select v-model="form.sex" placeholder="性别">
                        <el-option v-for="(item,key) in sexArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="年龄" :label-width="formLabelWidth" prop="age">
                    <el-input v-model="form.age" type="text" auto-complete="off" maxlength="3"></el-input>
                </el-form-item>
                <el-form-item label="业务员角色" :label-width="formLabelWidth" prop="name">
                    <el-select v-model="form.role" placeholder="业务员角色">
                        <el-option v-for="(item,key) in salesmanRoleArr_formatted" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="上级业务员" :label-width="formLabelWidth" prop="name">
                    <!-- <el-input v-model="form.leaderName" type="text" auto-complete="off" readonly ></el-input> -->
                    {{form.leaderName}} <span @click="handleShowLeader">修改</span>
                </el-form-item>
                <el-form-item v-if="formType==='edit'" label="密码维护" :label-width="formLabelWidth" prop="name">
                    <el-select v-model="form.loginPassReset" placeholder="密码维护">
                        <el-option v-for="(item,key) in pwdArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="关联的订单" :visible.sync="dialogOrderVisible" class="dialogw80" >
            <el-row class="mb20" :gutter="20">
                <el-col :span="4">
                    <el-input v-model="orderNo" placeholder="查询订单号"></el-input>
                </el-col>
                <!-- <el-col :span="4"> -->
                    <el-button type="primary" @click="handleSearchOrder">搜索</el-button>
                    <el-button @click="handleClearOrder">清除</el-button>
                <!-- </el-col> -->
            </el-row>
            <el-table :data="tableDataOrder" border v-loading="orderLoading">
                <el-table-column v-for="(column,key) in columnsOrder" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pager-wrapper">
                <el-pagination
                @size-change="handleSizeChangeOrder"
                @current-change="handleCurrentChangeOrder"
                :current-page.sync="currentPageOrder"
                :page-sizes="[2, 5, 10, 20]"
                :page-size="pageSizeOrder"
                layout="total, sizes, slot, prev, pager, next"
                :total="totalPageOrder">
                    <span class="el-pagination__total refresh" @click="handleCurrentChangeOrder(currentPageOrder)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="选择上级业务员" :visible.sync="dialogLeaderVisible" class="dialogw80">
            <el-row class="mb20" :gutter="20">
                <el-col :span="4">
                    <el-input v-model="searchStringLeader" placeholder="手机号、姓名"></el-input>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="leaderPagers">搜索</el-button>
                    <el-button @click="handleClearLeader">清除</el-button>
                </el-col>
                <el-col :span="8">
                    <span style="font-size:14px;">业务员角色：</span>
                    <el-select v-model="roleLeader" @change="leaderPagers" placeholder="请选择">
                        <el-option
                        v-for="item in salesmanRoleArr"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                        </el-option>
                    </el-select>
                </el-col>
            </el-row>
            <el-table :data="tableDataLeader" border v-loading="loading" ref="leaderTable" highlight-current-row @current-change="handleLeaderChange">
                <el-table-column v-for="(column,key) in columnsLeader" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pager-wrapper">
                <el-pagination
                @size-change="handleSizeChangeLeader"
                @current-change="handleCurrentChangeLeader"
                :current-page.sync="currentPageLeader"
                :page-sizes="[2, 5, 10, 20]"
                :page-size="pageSizeLeader"
                layout="total, sizes, slot, prev, pager, next"
                :total="totalPageLeader">
                    <span class="el-pagination__total refresh" @click="handleCurrentChangeLeader(currentPageLeader)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="handleSubmitLeader">确 定</el-button>
            </div>
        </el-dialog>

    </section>
</template>
<script>
    import api from '@/api/salesman';
    import { getOrderStatusMap, salesmanRole, salesmanSex, salesmanStatusMap } from '@/api/dict';
    import Util from '@/libs/util';

    const columns = [
  { key: 'id', title: '编号', sort: 'custom' },
  { key: 'name', title: '姓名', sort: 'custom' },
  { key: 'telephone', title: '手机号码', sort: 'custom' },
  { key: 'sex', title: '性别', sort: 'custom' },
  { key: 'age', title: '年龄', sort: 'custom' },
  { key: 'cityName', title: '市' },
  { key: 'roleName', title: '业务员角色' },
  { key: 'leaderName', title: '上级业务员' }
];
const columnsOrder = [
  { key: 'orderNo', title: '订单号' },
  { key: 'shippingPrice', title: '运费金额' },
  { key: 'orderStatus', title: '订单状态' },
  { key: 'appointDeliveryStartTimeStr', title: '预送达时间' },
  { key: 'deliveryTime', title: '交货时间' },
  { key: 'consigneeAddress', title: '收件人地址' },
  { key: 'consigneeTelephone', title: '收件人电话' }
];
const form = {};
const pwdArr = [
  { id: '0', name: '维持当前密码' },
  { id: '1', name: '重置密码' }
];
export default {
        data() {
            const validateNaN = (rule, value, callback) => {
                if (isNaN(value)) {
                    callback(new Error('请输入数字'));
                }
                callback();
            };
            const validateInt = (rule, value, callback) => {
                if (Number.isInteger(Number(value)) && Number(value) >= 1) {
                    callback();
                }
                callback(new Error('年龄必须为大于等于1的正整数'));
            };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                searchString: '',
                role: '',

                sexArr: [],
                sexMap: [],

                statusMap: {},

                orderStatusMap: {},

                pwdArr: pwdArr,

        // salesmanRoleMap: salesmanRoleMap,
                salesmanRoleArr: [],
                salesmanRoleArr_formatted: [],
                loading: false,

                tableData: [],
                columns: columns,

                orderNo: '',
                tableDataOrder: [],
                columnsOrder: columnsOrder,

                tableDataLeader: [],
                columnsLeader: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 10,

                searchStringLeader: '',
                roleLeader: '',

                currentPageOrder: 1,
                totalPageOrder: 0,
                pageSizeOrder: 10,
                sortBy: '',
                direction: '',
                currentPageLeader: 1,
                totalPageLeader: 0,
                pageSizeLeader: 5,
                orderLoading: false,
                dialogFormVisible: false,
                dialogLeaderVisible: false,
                dialogOrderVisible: false,
                formLabelWidth: '120px',
                form: JSON.parse(JSON.stringify(form)),
                formType: '',
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    age: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt]
                }
            };
        },
        methods: {
            handleSearch() {
                this.currentPage = 1;
                this.salesmanPagers();
            },
        // 清除搜索
            handleClear() {
                this.searchString = '';
                this.role = '';
                this.currentPage = 1;
                this.salesmanPagers();
            },
            handleClearLeader() {
                this.roleLeader = '';
                this.searchStringLeader = '';
                this.currentPageLeader = 1;
                this.leaderPagers();
            },
            handleSearchOrder() {
                this.currentPageOrder = 1;
                this.orderPagers();
            },
            handleClearOrder() {
                this.orderNo = '';
                this.orderPagers();
            },
            handleShowLeader() {
                this.dialogLeaderVisible = true;
                this.leaderPagers();
            },
            handleSubmitLeader() {
                this.dialogLeaderVisible = false;
            },
            handleLeaderChange(data) {
                window.LOG(data);
                if (data) {
                    this.form.leaderName = data.name;
                    this.form.leaderId = data.id;
                }
            },
            handleStatusChange() {
                this.salesmanPagers();
            },
        // 提交表单
            handleSubmit() {
                const formType = this.formType;
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        const param = this.form;
                        const age = Number(param.age);
                        if (age > 200 || age < 0) {
                            return this.$notify.error('年龄只能输入0-200的数字');
                        }
                        if (formType === 'edit') {
                            api.salesmanUpdate(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.dialogFormVisible = false;
                                    this.salesmanPagers();
                                } else {
                    // this.$notify.error(res.message || '未知错误');
                                }
                            });
                        } else if (formType === 'add') {
                            param.provinceId = 0;
                            param.districtId = 0;
                            api.salesmanInsert(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.dialogFormVisible = false;
                                    this.salesmanPagers();
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
            async handleAdd() {
                this.formType = 'add';
                this.form = JSON.parse(JSON.stringify(form));
                this.dialogFormVisible = true;
            },
        // 点击编辑按钮
            async handleEdit(data) {
                this.formType = 'edit';
                data.loginPassReset = '0';
                this.form = JSON.parse(JSON.stringify(data));
                for (const key in this.sexMap) {
                    if (this.form.sex === this.sexMap[key]) {
                        this.form.sex = key;
                    }
                }
                this.orderSalesmanId = data.id;
                this.dialogFormVisible = true;
            },
        // 点击删除按钮
            handleRemove(data) {
                window.LOG(data);
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
        .then(() => {
            const param = {
                id: data.id
            };
            api.salesmanDelete(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.salesmanPagers();
                } else {
                // this.$notify.error(res.message || '未知错误');
                }
            });
        })
        .catch(err => {
            window.LOG(err);
            this.$notify({
                type: 'info',
                message: '取消'
            });
        });
            },
            handleOrder(data) {
                this.orderSalesmanId = data.id;
                this.dialogOrderVisible = true;
                this.currentPageOrder = 1;
                this.orderPagers();
            },
        // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.salesmanPagers();
            },
        // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.salesmanPagers();
            },
            handleSizeChangeOrder(pageSize) {
                this.pageSizeOrder = pageSize;
                this.orderPagers();
            },
            handleCurrentChangeOrder(currentPage) {
                this.currentPageOrder = currentPage;
                this.orderPagers();
            },
            handleSizeChangeLeader(pageSize) {
                this.pageSizeLeader = pageSize;
                this.leaderPagers();
            },
            handleCurrentChangeLeader(currentPage) {
                this.currentPageLeader = currentPage;
                this.leaderPagers();
            },
            leaderPagers() {
                const param = {
                    excludeId: this.orderSalesmanId,
                    page: this.currentPageLeader,
                    limit: this.pageSizeLeader
                };
                if (this.searchStringLeader) {
                    param.searchString = this.searchStringLeader;
                }
                if (this.roleLeader) {
                    param.role = this.roleLeader;
                }
                api.salesmanPagers(param).then(res => {
                    this.tableDataLeader = res.records;
                    this.totalPageLeader = res.total;
                });
            },
            orderPagers() {
                const param = {
                    orderSalesmanId: this.orderSalesmanId,
                    page: this.currentPageOrder,
                    limit: this.pageSizeOrder
                };
                if (this.orderNo) {
                    param.orderNo = this.orderNo;
                }
                this.orderLoading = true;
                api.orderPagers(param).then(res => {
                    this.orderLoading = false;
                    if (res.records) {
                        this.tableDataOrder = res.records.map(item => {
                            item.orderStatus = this.orderStatusMap[item.orderStatus];
                            return item;
                        });
                    } else {
                        this.tableDataOrder = [];
                        this.$notify.error('无数据');
                    }
                    this.totalPageOrder = res.total;
                });
            },

            sortChange(column) {
                console.log(column + '-' + column.prop + '-' + column.order);
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.salesmanPagers();
            },
        // 获取数据
            salesmanPagers() {
                if (!this.G.permission['_salesman/salesman/pages']) {
                    return this.$notify.error('没有权限');
                }
                const param = {
                    page: 1,
                    limit: 5
                };
                param.page = this.currentPage;
                param.limit = this.pageSize;
                param.cityId = Util.getCookie('cityId');
                if (this.searchString) {
                    param.searchString = this.searchString;
                }
                if (this.role) {
                    param.role = this.role;
                }
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                api.salesmanPagers(param).then(res => {
                    window.LOG(res);
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            item.sex = this.sexMap[item.sex];
                            item.statusName = this.statusMap[item.status];
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.error('无数据');
                    }
                    this.totalPage = res.total;
                });
            },
            salesmanRole() {
                return salesmanRole().then(res => {
                    if (res.code === 100) {
                        const salesmanRoleMap = res.content;
                        const salesmanRoleArr = [];
                        for (const item in salesmanRoleMap) {
                            if (salesmanRoleMap.hasOwnProperty(item)) {
                                const ele = salesmanRoleMap[item];
                                salesmanRoleArr.push({ id: parseInt(item), name: ele });
                            }
                        }
                        salesmanRoleArr.unshift({ id: '', name: '所有' });
                        this.salesmanRoleArr = salesmanRoleArr;
                        this.salesmanRoleArr_formatted = this.salesmanRoleArr.filter(
            item => item.id !== ''
          );
                    }
                });
            },
            salesmanSex() {
                return salesmanSex().then(res => {
                    if (res.code === 100) {
                        const sexMap = res.content;
                        this.sexMap = sexMap;
                        const sexArr = [];
                        for (const item in sexMap) {
                            if (sexMap.hasOwnProperty(item)) {
                                const ele = sexMap[item];
                                sexArr.push({ id: item, name: ele });
                            }
                        }
                        this.sexArr = sexArr;
                    }
                });
            },
            salesmanStatus() {
                return salesmanStatusMap().then(res => {
                    if (res.code === 100) {
                        const salesmanStatus = res.content;
                        this.statusMap = salesmanStatus;
                    }
                });
            },
            orderStatus() {
                return getOrderStatusMap().then(res => {
                    if (res.code === 100) {
                        this.orderStatusMap = res.content;
                    }
                });
            }
        },
        async created() {
            await Promise.all([
                this.orderStatus(),
                this.salesmanStatus(),
                this.salesmanSex(),
                this.salesmanRole()
            ]);
            this.salesmanPagers();
        }
};
</script>
