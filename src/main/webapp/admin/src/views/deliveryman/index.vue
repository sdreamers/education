<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_account/deliveryman/insert']">添加配送员</el-button>
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
                            <span style="font-size:14px;">配送员状态：</span>
                            <el-select v-model="deliverymanStatus" @change="handleStatusChange" placeholder="请选择">
                                <el-option
                                v-for="item in statusArr"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                                </el-option>
                            </el-select>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border  @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort">
                            <template slot-scope="scope">
                                <img  v-if="column.key === 'previewImageurl'" style="width: 60px;height: 60px;" :src="scope.row.previewImageurl" />
                                <span v-if="column.key !== 'previewImageurl'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="180">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                @click.stop="handleOrder(scope.row)" v-if="G.permission['_order/order/deliverymanUnfinishOrder']">关联的订单</el-button>
                                <el-button
                                type="text"
                                @click.stop="handleEdit(scope.row)" v-if="G.permission['_account/deliveryman/update']">编辑</el-button>
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
        <edit
            v-if="dialogFormVisible"
            :form="form"
            @close="dialogFormVisible = false,deliverymanPagers()"
            :formType="formType"
            :formLabelWidth="formLabelWidth"></edit>

        <order
            v-if="dialogOrderVisible"
            :deliverymanId="deliverymanId"
            @close="dialogOrderVisible = false,deliverymanPagers()"
            ></order>

    </section>
</template>
<script>
import api from '@/api/deliveryman';
import { deliverymanStatusMap, deliverymanSex } from '@/api/dict';
import Util from '@/libs/util';

import edit from './edit';
import order from './order';

const columns = [
    { key: 'id', title: '编号', width: '48px', sort: 'custom' },
    { key: 'name', title: '姓名', width: '96px', sort: 'custom' },
    { key: 'telephone', title: '手机号码', width: '192px', sort: 'custom' },
    { key: 'sex_text', title: '性别', width: '64px', sort: 'custom' },
    { key: 'age', title: '年龄', width: '64px', sort: 'custom' },
    { key: 'status', title: '当前状态', sort: 'custom' },
    { key: 'provinceName', title: '省', width: '64px' },
    { key: 'cityName', title: '市', width: '64px' },
    { key: 'districtName', title: '区', width: '64px' }
];
const form = { name: '', telephone: '', sex: '', age: '', provinceId: '', cityId: '', districtId: '', loginPassReset: '' };
export default {
    components: {
        edit,
        order
    },
    data() {
        return {
            searchString: '',
            deliverymanStatus: '',

            deliverymanId: '',

            statusMap: {},
            statusArr: [],

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            dialogFormVisible: false,
            dialogOrderVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formType: ''
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.deliverymanPagers();
        },
        // 清除搜索
        handleClear() {
            this.searchString = '';
            this.deliverymanStatus = '';
            this.currentPage = 1;
            this.deliverymanPagers();
        },
        handleStatusChange() {
            this.deliverymanPagers();
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
            data.loginPassReset = '0';
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        handleOrder(data) {
            this.deliverymanId = data.id;
            this.dialogOrderVisible = true;
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
                api.deliverymanDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.deliverymanPagers();
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
            this.deliverymanPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.deliverymanPagers();
        },
        // 获取数据
        deliverymanPagers() {
            if (!this.G.permission['_account/deliveryman/pages']) {
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
            if (this.deliverymanStatus) {
                param.deliverymanStatus = this.deliverymanStatus;
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            api.deliverymanPagers(param).then(res => {
                window.LOG(res);
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.sex_text = this.sexMap[item.sex];
                        item.status = this.statusMap[item.status];
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.info('无数据');
                }

                this.totalPage = res.total;
            });
        },
        deliverymanSex() {
            return deliverymanSex().then(res => {
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
        deliverymanStatusMap() {
            return deliverymanStatusMap().then(res => {
                if (res.code === 100) {
                    const statusMap = res.content;
                    this.statusMap = statusMap;
                    const statusArr = [];
                    for (const item in statusMap) {
                        if (statusMap.hasOwnProperty(item)) {
                            const ele = statusMap[item];
                            statusArr.push({ id: item, name: ele });
                        }
                    }
                    statusArr.unshift({ id: '', name: '所有' });
                    this.statusArr = statusArr;
                }
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.deliverymanPagers();
        }
    },
    created() {
        Promise.all([
            this.deliverymanStatusMap(),
            this.deliverymanSex()
        ]).then(() => {
            this.deliverymanPagers();
        }).catch(err => {
            this.$notify.error('数据字典获取异常:' + err);
        });
    }
};
</script>
