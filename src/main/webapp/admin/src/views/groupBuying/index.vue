<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="6">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="开始时间"
                            end-placeholder="结束时间">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="4" :offset="10">
                        <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_market/groupBuying']">添加团购</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="关联订单">
                        <template slot-scope="scope">
                            <i class="el-icon-view" @click.stop="handleViewOrders(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_market/groupBuyingDetail/pages']"
                            @click.stop="handleViewProduct(scope.row)">查看商品</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_market/groupBuyingDetail']"
                            @click.stop="handleAddProduct(scope.row)">添加商品</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_market/groupBuying']"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_market/groupBuying']"
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
        <!-- 修改团购 -->
        <edit
            v-if="dialogFormVisible"
            @close="dialogFormVisible = false,groupBuyingPagers()"
            :formType="formType"
            :formLabelWidth="formLabelWidth"
            :form="form"></edit>
        <!-- 团购商品列表 -->
        <productList
            @close="dialogProductVisible = false,groupBuyingPagers()"
            v-if="dialogProductVisible"
            :formLabelWidth="formLabelWidth"
            :product="product"></productList>
        <addProduct
            @close="dialogProductAddVisible = false,groupBuyingPagers()"
            v-if="dialogProductAddVisible"
            :formLabelWidth="formLabelWidth"
            :groupBuyingId="groupBuyingId"
            :groupBuyingCode="groupBuyingCode"
        ></addProduct>
    </section>
</template>
<script>
import api from '@/api/groupBuying';
import Util from '@/libs/util';
// 编辑弹层
import edit from './edit';
// 查看商品
import productList from './productList';
// 添加商品
import addProduct from './addProduct';

import common from '@/components/common';
import headerList from '@/config/activity';

const columns = [
    { key: 'id', title: 'ID' },
    { key: 'code', title: '团购代码' },
    { key: 'createTime_formatted', title: '创建时间' },
    { key: 'startTime_formatted', title: '开始时间' },
    { key: 'endTime_formatted', title: '结束时间' }
];
const form = {
    date: []
};
export default {
    components: {
        edit,
        productList,
        addProduct,
        common
    },
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        };
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('序号必须为大于等于0的正整数'));
        };
        const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        return {
            headerList: headerList,
            searchForm: {
                searchDate: []
            },

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10,

            product: {},

            groupBuyingId: '',
            groupBuyingCode: '',

            // 修改团购
            dialogFormVisible: false,
            // 团购列表
            dialogProductVisible: false,
            // 添加商品
            dialogProductAddVisible: false,
            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formType: '',
            rules: {
                name: [{ required: true, message: '不能为空', trigger: 'blur' }],
                code: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt],
                provinceId: [{ required: true, message: '不能为空', trigger: 'blur' }],
                cityId: [{ required: true, message: '不能为空', trigger: 'blur' }],
                formDate: [{ required: true, message: '不能为空', trigger: 'blur' }]
            }
        };
    },
    methods: {
        // 搜索
        handleSearch() {
            this.currentPage = 1;
            this.groupBuyingPagers();
        },
        // 清除搜索
        handleClear() {
            this.searchForm.searchDate = [];
            this.currentPage = 1;
            this.groupBuyingPagers();
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
            data.date = [Util.formatTime(new Date(data.startTime)), Util.formatTime(new Date(data.endTime))];
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        // 点击删除按钮
        handleRemove(data) {
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.groupBuyingDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.groupBuyingPagers();
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
        handleViewProduct(data) {
            // this.product.provinceId = data.provinceId;
            // this.product.cityId = data.cityId;
            this.product.id = data.id;
            this.dialogProductVisible = true;
        },
        // 添加商品
        handleAddProduct(data) {
            this.groupBuyingId = data.id;
            this.groupBuyingCode = data.code;
            this.dialogProductAddVisible = true;
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.groupBuyingPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.groupBuyingPagers();
        },
        // 获取数据
        groupBuyingPagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            if (this.searchForm.searchDate && this.searchForm.searchDate.length) {
                param.createTimeBegin = this.searchForm.searchDate[0];
                param.createTimeEnd = this.searchForm.searchDate[1];
            }
            api.groupBuyingPagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        // item.beginTime = Util.parseTimeStr(item.beginTime);
                        // item.endTime = Util.parseTimeStr(item.endTime);
                        item.createTime_formatted = Util.formatTime(new Date(item.createTime))
                        item.startTime_formatted = Util.formatTime(new Date(item.startTime))
                        item.endTime_formatted = Util.formatTime(new Date(item.endTime))
                        console.log('item');
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.info('无数据');
                }
                this.totalPage = res.total;
            });
        },
        handleViewOrders() {
            console.log('handleViewOrders');
        }
    },
    created() {
        this.groupBuyingPagers();
    }
};
</script>
