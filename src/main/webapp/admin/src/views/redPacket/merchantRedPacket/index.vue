<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <p>红包来源：</p>
                        <el-select v-model="search.comeFromType" placeholder="红包来源">
                            <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <p>手机号：</p>
                        <el-input v-model="search.telephone" placeholder="手机号"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <p>红包面额：</p>
                        <el-row :gutter="0">
                            <el-col :span="11">
                                <el-input v-model="search.amountmin" placeholder="最小面额"></el-input>
                            </el-col>
                            <el-col :span="2"  style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="search.amountmax" placeholder="最大面额"></el-input>
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
                    <el-col :span="6">
                        <p>搜索：</p>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading" @row-click="rowClick"  @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <span v-html="scope.row[column.key]"></span>
                        </template>
                    </el-table-column>
                    <el-table-column label="查看关联的商户信息">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_account/merchant/get']"
                            @click.stop="getMerchant(scope.row)">查看</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleDelete(scope.row)">删除</el-button>
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
            @close="dialogFormVisible=false,merchantRedPacketPagers()"></detail>
        <audit
            v-if="dialogAuditVisible"
            :form="auditForm"
            @close="dialogAuditVisible=false,merchantRedPacketPagers()"></audit>
    </section>
</template>
<script>
import api from '@/api/redpacket';
import Util from '@/libs/util';
import common from '@/components/common';
import headerList from '@/config/redPacketList';

import detail from './detail';
import audit from './audit';

const columns = [
    { key: 'id', title: 'ID' },
    { key: 'amount', title: '红包面额' },
    { key: 'amountCondition', title: '使用条件' },
    { key: 'comeFromType_Text', title: '红包来源' },
    { key: 'expiryDate', title: '红包截止时间' },
    { key: 'useFlag', title: '是否已使用' }
];
const redPacketType = {
    0: '积分兑换',
    1: '推荐奖励',
    2: '会员特权',
    3: '订单退款',
    4: '系统派发'
}
const form = {};
export default {
    components: {
        common,
        detail,
        audit
    },
    data() {
        return {
            headerList: headerList,

            search: {
                comeFromType: '',
                telephone: '',
                amountmin: '',
                amountmax: '',
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

            dialogAuditVisible: false,
            formType: '',
            // 单位列表
            unitList: [],
            statusList: [
                {
                    id: '',
                    name: '全部'
                },
                {
                    id: '0',
                    name: '积分兑换'
                },
                {
                    id: '1',
                    name: '推荐奖励'
                },
                {
                    id: '2',
                    name: '会员特权'
                },
                {
                    id: '3',
                    name: '订单退款'
                },
                {
                    id: '4',
                    name: '系统派发'
                }
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
            this.merchantRedPacketPagers();
        },
        // 清除搜索条件
        handleClear() {
            this.currentPage = 1;
            const search = this.search;
            for (const item in search) {
                if (search.hasOwnProperty(item)) {
                    search[item] = '';
                }
            }
            this.merchantRedPacketPagers();
        },
        handleDelete(data) {
            console.log(data);
            const comeFromType = data.comeFromType;
            const useFlag = data.useFlag;
            if (comeFromType !== '4') {
                return this.$notify.error('只能删除系统派发的订单');
            }
            if (useFlag === '1') {
                return this.$notify.error('使用过的红包不能删除');
            }
            this.$confirm('将无法恢复，确定要删除吗?', '确认删除').then(() => {
                const id = data.id;
                api.falseDeletion({ id }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.merchantRedPacketPagers();
                    }
                })
            });
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
                    this.merchantRedPacketPagers();
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
            this.merchantRedPacketPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.merchantRedPacketPagers();
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
                        this.merchantRedPacketPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            }
            // this.currentPageSpec = 1;
            // this.productBasicSpecificationPagers(id);
        },
        // 获取基本信息列表
        merchantRedPacketPagers() {
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
            api.merchantRedPacketPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    if (item.expiryDate) {
                        item.expiryDate = Util.parseTimeStr(item.expiryDate);
                    } else {
                        item.expiryDate = '永久';
                    }
                    item.comeFromType_Text = redPacketType[item.comeFromType];

                    item.useFlag = item.useFlag === '1' ? '<b style="color:#f00">是</b>' : '否';

                    return item;
                });
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.merchantRedPacketPagers();
        }
    },
    created() {
        this.merchantRedPacketPagers();
    }
};
</script>
