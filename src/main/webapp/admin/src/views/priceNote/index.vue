<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd">修改商品价格</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="4">
                            <p style="font-size:14px;">操作员：</p>
                            <el-input v-model="search.operatorName" placeholder="操作员"></el-input>
                        </el-col>
                        <el-col :span="5">
                            <p style="font-size:14px;">操作日期：</p>
                            <el-date-picker
                                v-model="search.formDate"
                                @change="priceNotePagers"
                                type="daterange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="操作开始日期"
                                end-placeholder="操作结束日期">
                            </el-date-picker>
                        </el-col>
                        <el-col :span="3">
                            <p style="font-size:14px;">审核人：</p>
                            <el-input v-model="search.auditorName" placeholder="审核人"></el-input>
                        </el-col>
                        <el-col :span="5">
                            <p style="font-size:14px;">审核日期：</p>
                            <el-date-picker
                                v-model="search.formDateAudit"
                                @change="priceNotePagers"
                                type="daterange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="审核开始日期"
                                end-placeholder="审核结束日期">
                            </el-date-picker>
                        </el-col>
                        <el-col :span="3">
                            <p style="font-size:14px;">审核状态：</p>
                            <el-select v-model="search.status" placeholder="审核状态" @change="priceNotePagers">
                                <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="4">
                            <p style="font-size:14px;">搜索：</p>
                            <div style="width:150px">
                                <el-button type="primary" @click="handleSearch">搜索</el-button>
                                <el-button @click="clear">清除</el-button>
                            </div>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                            <template slot-scope="scope">
                                <span v-html="scope.row[column.key]"></span>
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
    </section>
</template>
<script>
import api from '@/api/priceNote';
import { warehouseByUserCurrentCity, getPriceStatus } from '@/api/dict';
import Util from '@/libs/util';
const columns = [
    { key: 'productName', title: '商品名' },
    { key: 'specificationName', title: '规格名' },
    { key: 'oldPrice', title: '旧售价' },
    { key: 'newPrice', title: '新售价' },
    { key: 'priceStatus', title: '价格变动' },
    { key: 'statusName', title: '审核状态' },
    { key: 'operatorName', title: '操作员' },
    { key: 'operationTime', title: '操作时间', width: '150px' },
    { key: 'auditorName', title: '审核人' },
    { key: 'auditTime', title: '审核时间', width: '150px' }
];
export default {
    data() {
        return {
            search: {
                operatorName: '',
                formDate: [new Date(new Date(new Date().toLocaleDateString()).getTime()), new Date(new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1)],
                auditorName: '',
                formDateAudit: [],
                status: ''
            },

            statusList: [],

            warehouseList: [],

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 5,

            loading: false
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.priceNotePagers();
        },
        // 清除搜索
        clear() {
            this.search.operatorName = '';
            this.search.auditorName = '';
            this.search.status = '';
            this.search.formDate = [];
            this.search.formDateAudit = [];
            this.currentPage = 1;
            this.priceNotePagers();
        },
        // 点击添加按钮
        handleAdd() {
            if (!this.G.permission['_stock/priceNote/add']) {
                return this.$notify.error('没有权限');
            }
            this.$router.push({ name: '/stock/priceNote/add' });
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.priceNotePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.priceNotePagers();
        },
        // 获取数据
        priceNotePagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };

            param.operatorName = this.search.operatorName ? this.search.operatorName : undefined;
            param.auditorName = this.search.auditorName ? this.search.auditorName : undefined;
            param.status = this.search.status ? this.search.status : undefined;
            param.warehouseId = this.search.warehouseId ? this.search.warehouseId : undefined;

            if (this.search.formDate && this.search.formDate.length) {
                param.operationStartTime = Util.formatTime(new Date(this.search.formDate[0]), true);
                param.operationEndTime = Util.formatTime(new Date(this.search.formDate[1]), true);
            }

            if (this.search.formDateAudit && this.search.formDateAudit.length) {
                param.auditStartTime = Util.formatTime(new Date(this.search.formDateAudit[0]), true);
                param.auditEndTime = Util.formatTime(new Date(this.search.formDateAudit[1]), true);
            }

            api.priceNotePagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.priceStatus = '';
                        if (item.oldPrice < item.newPrice) {
                            item.priceStatus = `<b style="color: #44b549">涨：${(Math.abs(item.oldPrice - item.newPrice)).toFixed(2)}</b>`;
                        } else {
                            item.priceStatus = `<b style="color: #f00">降：${(Math.abs(item.oldPrice - item.newPrice)).toFixed(2)}</b>`;
                        }
                        // 时间初始化去掉  .0
                        if (item.auditTime) {
                            item.auditTime = item.auditTime.replace(/.[0-9]*$/, '');
                        }
                        if (item.operationTime) {
                            item.operationTime = item.operationTime.replace(/.[0-9]*$/, '');
                        }
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.error('无数据');
                }

                this.totalPage = res.total;
            });
        },
        warehouseByUserCurrentCity() {
            return warehouseByUserCurrentCity().then(res => {
                if (res.code === 100) {
                    const warehouseMap = res.content;
                    const arr = [];
                    for (const item in warehouseMap) {
                        if (warehouseMap.hasOwnProperty(item)) {
                            const ele = warehouseMap[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    this.warehouseList = arr;
                    this.search.warehouseId = arr[0].id;
                } else {
                    // this.$notify.error(res.message);
                }
            });
        },
        getPriceStatus() {
            return getPriceStatus().then(res => {
                if (res.code === 100) {
                    const statusMap = res.content;
                    const arr = [];
                    for (const item in statusMap) {
                        if (statusMap.hasOwnProperty(item)) {
                            const ele = statusMap[item];
                            arr.push({ id: item, name: ele });
                        }
                    }
                    arr.unshift({ id: '', name: '全部' })
                    this.statusList = arr;
                } else {
                    // this.$notify.error(res.message);
                }
            });
        }
    },
    created() {
        this.getPriceStatus();
        this.warehouseByUserCurrentCity();
        this.priceNotePagers();
    }
};
</script>
