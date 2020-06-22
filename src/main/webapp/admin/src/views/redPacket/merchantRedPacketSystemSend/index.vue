<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <p>审批状态：</p>
                        <el-select v-model="search.auditStatus" placeholder="审批状态">
                            <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <p>手机号：</p>
                        <el-input v-model="search.merchantTelephone" placeholder="手机号"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <p>红包面额：</p>
                        <el-row :gutter="0">
                            <el-col :span="11">
                                <el-input v-model="search.amountMin" placeholder="最小面额"></el-input>
                            </el-col>
                            <el-col :span="2"  style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="search.amountMax" placeholder="最大面额"></el-input>
                            </el-col>
                        </el-row>
                    </el-col>
                    <el-col :span="6">
                        <p>截止时间：</p>
                        <el-date-picker
                            v-model="search.expiryDate"
                            type="date"
                            align="right"
                            placeholder="截止时间">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <p>搜索：</p>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="2">
                        <p>派发：</p>
                        <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_acount/merchantRedPacketSystemSend/insert']">派发红包</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading" @row-click="rowClick"  @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="查看关联商户">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_acount/merchantRedPacketSystemSend/getMerchant']"
                            @click.stop="getMerchant(scope.row)">查看</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="审核">
                        <template slot-scope="scope">
                            <el-button
                            v-if="scope.row.auditFlag && G.permission['_acount/merchantRedPacketSystemSend/getAudit']"
                            type="text"
                            @click.stop="handleAudit(scope.row)">审核</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page.sync="currentPage"
                    :page-sizes="[10, 20]"
                    :page-size="pageSize"
                    layout="total, sizes, slot, prev, pager, next"
                    :total="totalPage">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
        <detail
            v-if="dialogFormVisible"
            :form="merchantForm"
            @close="dialogFormVisible=false,merchantRedPacketSystemSendPagers()"></detail>
        <distribute
            v-if="dialogDistributeVisible"
            :form="distributeForm"
            @close="dialogDistributeVisible=false,merchantRedPacketSystemSendPagers()"></distribute>
        <audit
            v-if="dialogAuditVisible"
            :form="auditForm"
            @close="dialogAuditVisible=false,merchantRedPacketSystemSendPagers()"></audit>
    </section>
</template>
<script>
import api from '@/api/redpacket';
import Util from '@/libs/util';
import common from '@/components/common';
import headerList from '@/config/redPacketList';

import detail from './detail';
import distribute from './distribute';
import audit from './audit';

const columns = [
    { key: 'merchantTelephone', title: '商户手机号' },
    { key: 'amount', title: '红包面额' },
    { key: 'amountCondition', title: '使用条件' },
    { key: 'expiryDate', title: '截止时间' },
    { key: 'creatorName', title: '创建人' },
    { key: 'createDateTime', title: '创建时间' },
    { key: 'auditorName', title: '审核人' },
    { key: 'auditDateTime', title: '审核时间' },
    { key: 'auditStatus', title: '审批状态' },
    { key: 'remark', title: '备注' }
];
const form = {};
export default {
    components: {
        common,
        detail,
        distribute,
        audit
    },
    data() {
        return {
            headerList: headerList,

            search: {
                auditStatus: '',
                merchantTelephone: '',
                amountMin: '',
                amountMax: '',
                expiryDate: ''
            },

            tableData: [],
            columns: columns,


            currentPage: 1,
            totalPage: 0,
            pageSize: 10,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),

            dialogDistributeVisible: false,

            dialogAuditVisible: false,
            formType: '',
            // 单位列表
            unitList: [],
            statusList: [
                { id: '0', name: '待审核' },
                { id: '1', name: '审核驳回' },
                { id: '2', name: '审核通过' }
            ],

            merchantForm: {},
            distributeForm: {},
            auditForm: {}
        };
    },
    methods: {
        // 点击搜索按钮
        handleSearch() {
            this.currentPage = 1;
            this.merchantRedPacketSystemSendPagers();
        },
        // 清除搜索条件
        handleClear() {
            for (const item in this.search) {
                if (this.search.hasOwnProperty(item)) {
                    this.search[item] = '';
                }
            }
            this.currentPage = 1;
            this.merchantRedPacketSystemSendPagers();
        },
        // 添加基本信息
        handleAdd() {
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogDistributeVisible = true;
        },
        handleAudit(data) {
            this.auditForm = JSON.parse(JSON.stringify(data));
            this.dialogAuditVisible = true;
        },
        // 点击编辑基本信息按钮
        getMerchant(data) {
            api.getMerchant({ merchantId: data.merchantId }).then(res => {
                console.log(res);
                if (res.code === 100) {
                    this.merchantForm = res.content;
                    if (this.merchantForm.merchantName === null || this.merchantForm.merchantName === '' || this.merchantForm.merchantName === 'null') {
                        this.merchantForm.merchantName = '商户老板没有在app认证';
                    }

                    if (this.merchantForm.qualificationStatus === null || this.merchantForm.qualificationStatus === '0') {
                        this.merchantForm.qualificationStatus = '商户老板没有在app认证';
                    } else if (this.merchantForm.qualificationStatus === '1') {
                        this.merchantForm.qualificationStatus = '商户老板已认证';
                    } else if (this.merchantForm.qualificationStatus === '2') {
                        this.merchantForm.qualificationStatus = '商户老板等待认证';
                    } else {
                        this.merchantForm.qualificationStatus = '商户老板认证失败';
                    }

                    if (this.merchantForm.codPrivilage === null || this.merchantForm.codPrivilage === '' || this.merchantForm.codPrivilage === 'null') {
                        this.merchantForm.codPrivilage = '系统没给商户老板开通货到付款功能';
                    }
                    if (this.merchantForm.codPrivilage === 0 || this.merchantForm.codPrivilage === '0') {
                        this.merchantForm.codPrivilage = '系统没给商户老板开通货到付款功能';
                    }

                    if (this.merchantForm.disabled === true) {
                        this.merchantForm.disabled = '是';
                    } else {
                        this.merchantForm.disabled = '否';
                    }

                    this.merchantForm.registerTime = new Date(this.merchantForm.registerTime).toLocaleString().replace(/:\d{1,2}$/, ' ');

                    this.dialogFormVisible = true;
                }
            })
            // this.formType = 'edit';
            // const formData = JSON.parse(JSON.stringify(data));
            // this.form = JSON.parse(JSON.stringify(formData));
        },
        // 删除基本信息
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
                this.merchantRedPacketSystemSendDelete(param).then(() => {
                    this.merchantRedPacketSystemSendPagers();
                });
            }).catch(err => {
                window.LOG(err);
                this.$notify({
                    type: 'info',
                    message: '取消'
                });
            });
        },
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.merchantRedPacketSystemSendPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.merchantRedPacketSystemSendPagers();
        },
        // 获取规格，或者显示隐藏
        rowClick(row, event, column) {
            const id = row.id;
            this.productBasicInfoId = id;
            if (column.label === '禁用状态') {
                // row.status = row.status === '1' ? '0' : '1';
                const status = row.status;
                const sortNo = row.sortNo;
                api.merchantRedPacketSystemSendUpdateStatus({ id, sortNo, status }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.merchantRedPacketSystemSendPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
            // this.currentPageSpec = 1;
            // this.productBasicSpecificationPagers(id);
        },
        // 获取基本信息列表
        merchantRedPacketSystemSendPagers() {
            if (!this.G.permission['_acount/merchantRedPacketSystemSend/pages']) {
                return this.$notify.error('没有权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            const search = this.search;
            for (const item in search) {
                if (search.hasOwnProperty(item)) {
                    const element = search[item];
                    if (element) {
                        param[item] = element;
                    }
                }
            }
            if (param.expiryDate) {
                param.expiryDate = Util.formatTime(param.expiryDate, true);
            }
            api.merchantRedPacketSystemSendPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    if (item.expiryDate) {
                        item.expiryDate = Util.parseTimeStr(item.expiryDate);
                    } else {
                        item.expiryDate = '永久';
                    }
                    item.createDateTime = Util.parseTimeStr(item.createDateTime);
                    item.auditDateTime = Util.parseTimeStr(item.auditDateTime);
                    return item;
                });
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.merchantRedPacketSystemSendPagers();
        }
    },
    created() {
        this.merchantRedPacketSystemSendPagers();
    }
};
</script>
