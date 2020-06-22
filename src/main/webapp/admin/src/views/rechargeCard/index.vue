<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <el-input v-model="searchForm.id" placeholder="充值卡号"></el-input>
                    </el-col>
                    <el-col :span="2">
                        <el-input v-model="searchForm.merchantTelephone" placeholder="充值手机号码"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-select v-model="searchForm.status" placeholder="请选择类型">
                            <el-option
                                v-for="item in statusList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="2">
                        <el-select v-model="searchForm.printFlag" placeholder="请选择类型">
                            <el-option
                                v-for="item in printFlagList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="4">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="创建开始时间"
                            end-placeholder="创建结束时间">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="5">
                        <el-button style="margin-left: 10px;"
                                type="primary"
                                round
                                v-if="G.permission['_order/rechargeCard/batchInsert']"
                                @click="handleInsertCard">批量制卡
                        </el-button>
                        <el-button style="margin-left: 10px;"
                                type="success"
                                round
                                v-if="G.permission['_order/rechargeCard/export']"
                                @click="handleExportExcel">
                            导出Excel
                        </el-button>
                    </el-col>
                </el-row>
                <el-table :data="records" border v-loading="loading">
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        align="center"
                        :width="column.width">
                        <template slot-scope="scope">
                            <span v-if="column.key !== 'orderDetail'">{{scope.row[column.key]}}</span>
                            <section v-else>
                                <i class="el-icon-view" @click.stop="handleViewOrderDetail(scope.row)"></i>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="禁用" v-if="G.permission['_order/rechargeCard/updateDislabeFlag']">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.disableFlag" @change="handleChangeDisableFlag(scope.row)">
                                <el-option
                                    v-for="item in disableFlagList"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除" v-if="G.permission['_order/rechargeCard/delete']">
                        <template slot-scope="scope">
                            <i class="el-icon-delete" @click.stop="handleRemoveRechargeCard(scope.row)"></i>
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
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <!-- 批量制卡 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelAdd" title="填写充值卡信息" :visible.sync="insertForm.visible" width="300px">
            <el-form :model="insertForm" :rules="rules" ref="ruleForm">
                <el-row :gutter="20">
                    <el-form-item label="充值面额" label-width="80px" prop="amount">
                        <el-input v-model="insertForm.amount" maxlength="8"></el-input>
                    </el-form-item>
                    <el-form-item label="赠送面额" label-width="80px" prop="giftAmount">
                        <el-input v-model="insertForm.giftAmount" maxlength="8"></el-input>
                    </el-form-item>
                    <el-form-item label="制卡数量" label-width="80px" prop="insertNum">
                        <el-input v-model="insertForm.insertNum" maxlength="3"></el-input>
                    </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelAdd">取 消</el-button>
                <el-button @click="handleSubmit">确定</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import common from '@/components/common';
    import headerList from '@/config/rechargeList';
    import commonApi from '@/api/common';
    import rechargeCard from '@/api/rechargeCard';
    import util from '@/libs/util';

    const searchForm = {
        id: '',
        merchantTelephone: '',
        status: '',
        printFlag: '',
        searchDate: []
    };

    const columns = [
        { key: 'id', title: '充值卡号' },
        { key: 'amount', title: '充值面额' },
        { key: 'giftAmount', title: '赠送面额' },
        { key: 'password', title: '充值密码' },
        { key: 'statusCN', title: '充值状态' },
        { key: 'printFlagCN', title: '打印状态' },
        { key: 'createDateTime', title: '创建日期时间' },
        { key: 'merchantName', title: '认证充值商户' },
        { key: 'merchantTelephone', title: '充值手机号码' },
        { key: 'rechargeDateTime', title: '充值时间' }
    ];

    const disableFlagList = [
        { id: '0', name: '否' },
        { id: '1', name: '是' }
    ];

    const insertForm = {
        amount: '',
        giftAmount: '',
        insertNum: '',
        visible: false
    };

    export default {
        components: {
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
                if (Number.isInteger(Number(value)) && Number(value) >= 1) {
                    callback();
                }
                callback(new Error('序号必须为大于等于1的正整数'));
            };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                headerList: headerList,
                searchForm: JSON.parse(JSON.stringify(searchForm)),
                rechargeCardPrintFlag: '',
                rechargeCardStatus: '',
                printFlagList: [],
                statusList: [],
                statusMap: {},
                printFlagMap: {},

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                disableFlagList: disableFlagList,

                insertForm: JSON.parse(JSON.stringify(insertForm)),

                rules: {
                    amount: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt],
                    giftAmount: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt],
                    insertNum: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt]
                }
    
            }
        },

        methods: {
            handleLoadRechargeCardDict() {
                commonApi.getRechargeCardDict().then(res => {
                    if (res.code === 100) {
                        this.printFlagMap = res.content.rechargeCardPrintFlag;
                        for (const item in res.content.rechargeCardPrintFlag) {
                            if (res.content.rechargeCardPrintFlag.hasOwnProperty(item)) {
                                this.printFlagList.push({ id: item, name: res.content.rechargeCardPrintFlag[item] });
                            }
                        }
                        this.printFlagList.unshift({ id: '', name: '请选择打印状态' });
                        this.statusMap = res.content.rechargeCardStatus;
                        for (const item in res.content.rechargeCardStatus) {
                            if (res.content.rechargeCardStatus.hasOwnProperty(item)) {
                                this.statusList.push({ id: item, name: res.content.rechargeCardStatus[item] });
                            }
                        }
                        this.statusList.unshift({ id: '', name: '请选择充值卡状态' })
                    }
                });
            },

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                if (!this.G.permission['_order/rechargeCardpages']) {
                    return this.$notify.error('没有列表权限');
                }
                const param = this.encapsulateParam();
                if ((isNaN(param.id) || !Number.isInteger(Number(param.id))) && param.id) {
                    return this.$notify.error('卡号必须为数字');
                }
                param.page = this.currentPage;
                param.limit = this.pageSize;
                rechargeCard.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.statusCN = this.statusMap[item.status];
                            item.printFlagCN = this.printFlagMap[item.printFlag];
                            item.rechargeDateTime = util.parseTimeStr(item.rechargeDateTime);
                            item.createDateTime = util.parseTimeStr(item.createDateTime);
                        });
                    }
                    this.totalSize = res.total;
                    this.records = res.records;
                });
            },

            encapsulateParam() {
                const param = {};
                if (this.searchForm.id) {
                    param.id = this.searchForm.id;
                }
                if (this.searchForm.merchantTelephone) {
                    param.merchantTelephone = this.searchForm.merchantTelephone;
                }
                if (this.searchForm.status) {
                    param.status = this.searchForm.status;
                }
                if (this.searchForm.printFlag) {
                    param.printFlag = this.searchForm.printFlag;
                }
                if (this.searchForm.searchDate && this.searchForm.searchDate.length === 2) {
                    param.createDateTimeBegin = this.searchForm.searchDate[0];
                    param.createDateTimeEnd = this.searchForm.searchDate[1];
                }
                return param;
            },

            handleExportExcel() {
                const exportUrl = window.vars.URLApiBase + '/order/rechargeCard/export';
                const param = util.qs(this.encapsulateParam());
                return location.href = exportUrl + '?' + param;
            },

            /**
             * 删除充值
             * */
            handleRemoveRechargeCard(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    rechargeCard.delete({ id: row.id }).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.handlePagers();
                        }
                    })
                }).catch(err => {
                    window.LOG(err);
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },

            /**
             * 更改禁用状态
             * */
            handleChangeDisableFlag(row) {
                const param = { id: row.id, disableFlag: row.disableFlag };
                rechargeCard.updateDisableFlag(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('操作成功');
                        this.handlePagers();
                    }
                });
            },

            /**
             * 批量制卡
             * */
            handleInsertCard() {
                this.insertForm.visible = true;
            },

            handleCancelAdd() {
                this.insertForm = JSON.parse(JSON.stringify(insertForm));
            },

            handleSubmit() {
                if (!this.insertForm.amount) {
                    return this.$notify.error('请输入充值面额');
                }
                if (isNaN(this.insertForm.amount)) {
                    return this.$notify.error('充值面额只能输入数字');
                }
                if (!this.insertForm.giftAmount) {
                    this.insertForm.giftAmount = 0.00;
                }
                if (isNaN(this.insertForm.giftAmount)) {
                    return this.$notify.error('赠送面额只能输入数字');
                }
                if (!this.insertForm.insertNum) {
                    return this.$notify.error('请输入批量制卡数量');
                }
                if (isNaN(this.insertForm.insertNum)) {
                    return this.$notify.error('批量制卡数量只能输入数字');
                }

                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        rechargeCard.batchInsert(this.insertForm).then(res => {
                            if (res.code === 100) {
                                this.$notify.success('操作成功');
                                this.insertForm = JSON.parse(JSON.stringify(insertForm));
                                this.handlePagers();
                            }
                        });
                    }
                })
            }
        },

        created() {
            this.handleLoadRechargeCardDict();
            this.handlePagers();
        }
    }
</script>
