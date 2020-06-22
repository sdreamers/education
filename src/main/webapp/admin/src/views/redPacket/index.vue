<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-button style="float: right; margin-right: 30px;"
                            type="success"
                            round
                            v-if="G.permission['_market/redPacket/insert']"
                            @click="handleAddRedPacket">
                        添加红包
                    </el-button>
                </el-row>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="records" border :loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                    </el-table-column>
                    <el-table-column label="编辑" v-if="G.permission['_market/redPacket/update']">
                        <template slot-scope="scope">
                            <i class="el-icon-edit" @click.stop="handleEditRedPacket(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除" v-if="G.permission['_market/redPacket/delete']">
                        <template slot-scope="scope">
                            <i class="el-icon-delete" @click.stop="handleRemoveRedPacket(scope.row)"></i>
                        </template>
                    </el-table-column>
                </el-table>
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

        <!-- 添加红包 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelAdd" title="详细信息" :visible.sync="insertForm.visible" width="20%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>红包面额</p>
                    <el-input v-model="insertForm.amount" placeholder="XXX.XX" maxlength="9"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>使用条件</p>
                    <el-input v-model="insertForm.amountCondition" placeholder="订单总额达到多少才能使用红包" maxlength="9"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>积分兑换门槛</p>
                    <el-input v-model="insertForm.exchangePoint" placeholder="积分需要达到多少才能兑换红包" maxlength="9"></el-input>
                </el-col>
                <el-col :span="24" class="mb20" v-show="!insertForm.expiryDaysFlag">
                    <p>有效天数</p>
                    <el-input v-model="insertForm.expiryDays" placeholder="红包发放之后的有效天数" maxlength="3"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <el-checkbox v-model="insertForm.expiryDaysFlag">永久有效</el-checkbox>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelAdd">取 消</el-button>
                <el-button @click="handleSubmit">确定</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>

    import redPacket from '@/api/redpacket';

    const columns = [
        { key: 'amount', title: '红包面额' },
        { key: 'amountCondition', title: '使用条件' },
        { key: 'exchangePoint', title: '积分兑换门槛' },
        { key: 'expiryDays_formatted', title: '有效天数' }
    ];

    const insertForm = {
        amount: '',
        amountCondition: '',
        exchangePoint: '',
        expiryDays: '',
        visible: false,
        expiryDaysFlag: false
    };

    export default {

        data() {
            return {
                records: [],
                loading: false,
                columns: columns,
                currentPage: 1,
                pageSize: 10,
                totalPage: 0,

                insertForm: JSON.parse(JSON.stringify(insertForm))
            }
        },

        methods: {

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers()
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                if (!this.G.permission['_market/redPacket/pages']) {
                    return this.$notify.error('没有权限');
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                redPacket.redPacketPagers(param).then(res => {
                    let records = [];
                    if (res.records) {
                        records = res.records.map(item => {
                            if (item.expiryDaysFlag === '1') {
                                item.expiryDays_formatted = '永久';
                            } else {
                                item.expiryDays_formatted = item.expiryDays;
                            }
                            return item;
                        });
                    }
                    console.log(records);
                    this.records = records;
                    this.totalPage = res.total;
                })
            },

            /**
             * 添加红包
             * */
            handleAddRedPacket() {
                this.insertForm.visible = true;
            },

            handleCancelAdd() {
                this.insertForm = JSON.parse(JSON.stringify(insertForm));
            },

            handleSubmit() {
                if (!this.insertForm.amount) {
                    return this.$notify.error('请输入红包面额');
                }
                if (!/^\d+(\.\d{1,2})?$/.test(this.insertForm.amount)) {
                    return this.$notify.error('红包面额只能是大于0的数字, 最多保留两位小数');
                }
                if (Number(this.insertForm.amount) <= 0) {
                    return this.$notify.error('红包面额只能是大于0的数字, 最多保留两位小数')
                }
                if (!this.insertForm.amountCondition) {
                    return this.$notify.error('请输入使用条件');
                }
                if (!/^\d+(\.\d{1,2})?$/.test(this.insertForm.amountCondition)) {
                    return this.$notify.error('使用条件只能是大于0的数字, 最多保留两位小数');
                }
                if (Number(this.insertForm.amount) >= Number(this.insertForm.amountCondition)) {
                    return this.$notify.error('使用条件必须大于红包面额');
                }
                if (!this.insertForm.exchangePoint) {
                    return this.$notify.error('请输入积分兑换门槛');
                }
                if (!/^\d+(\.\d{1,2})?$/.test(this.insertForm.exchangePoint)) {
                    return this.$notify.error('积分兑换门槛只能是大于0的数字, 最多保留两位小数');
                }
                if (Number(this.insertForm.exchangePoint) <= 0) {
                    return this.$notify.error('积分兑换门槛只能是大于0的数字, 最多保留两位小数');
                }
                if (!this.insertForm.expiryDaysFlag) {
                    if (!this.insertForm.expiryDays) {
                        return this.$notify.error('请输入有效天数');
                    }
                    if (!/^\d+$/.test(this.insertForm.expiryDays)) {
                        return this.$notify.error('有效天数只能是正整数');
                    }
                    if (Number(this.insertForm.expiryDays) <= 0) {
                        return this.$notify.error('有效天数只能是正整数');
                    }
                } else {
                    this.insertForm.expiryDays = '';
                }
                const param = {
                    amount: this.insertForm.amount,
                    amountCondition: this.insertForm.amountCondition,
                    exchangePoint: this.insertForm.exchangePoint,
                    expiryDays: this.insertForm.expiryDays,
                    expiryDaysFlag: this.insertForm.expiryDaysFlag ? '1' : '0'
                };
                if (!this.insertForm.id) {
                    redPacket.redPacketInsert(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('操作成功');
                            this.insertForm = JSON.parse(JSON.stringify(insertForm));
                            this.handlePagers();
                        }
                    })
                } else {
                    param.id = this.insertForm.id;
                    redPacket.redPacketUpdate(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('修改成功');
                            this.insertForm = JSON.parse(JSON.stringify(insertForm));
                            this.handlePagers();
                        }
                    });
                }
            },

            /**
             * 编辑红包
             * */
            handleEditRedPacket(row) {
                this.insertForm = JSON.parse(JSON.stringify(row));
                this.insertForm.expiryDaysFlag = row.expiryDaysFlag === '1';
                this.insertForm.visible = true;
            },

            /**
             * 删除红包
             * */
            handleRemoveRedPacket(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    redPacket.redPacketDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '删除成功');
                            this.checkIsLastItemOfLastPage();
                            this.handlePagers();
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
            checkIsLastItemOfLastPage() {
                if (this.records.length === 1 && this.currentPage > 1) {
                    this.currentPage = this.currentPage - 1;
                }
            }
        },

        created() {
            this.handlePagers();
        }
    }

</script>
