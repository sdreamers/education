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
                               v-if="G.permission['_order/reservation/insert']"
                               @click="handleAddReservation">
                        添加预约时间
                    </el-button>
                </el-row>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="records" border :loading="loading" @sort-change="sortChange">
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :width="column.width"
                        :sortable="column.sort"
                        :key="key">
                    </el-table-column>
                    <el-table-column label="编辑" v-if="G.permission['_order/reservation/update']">
                        <template slot-scope="scope">
                            <i class="el-icon-edit" @click.stop="handleEditReservation(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除" v-if="G.permission['_order/reservation/delete']">
                        <template slot-scope="scope">
                            <i class="el-icon-delete" @click.stop="handleRemoveReservation(scope.row)"></i>
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
        <el-dialog :close-on-click-modal="false" @close="handleCancelAdd" title="填写设置预约时间" :visible.sync="insertForm.visible" width="30%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>排序序号</p>
                    <el-input v-model="insertForm.sortNo" type="text" placeholder="排序序号"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>预约时间名称</p>
                    <el-input v-model="insertForm.datename" placeholder="例如: 8:00-10:00" maxlength="250"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>间隔当前时间天数：0表示当天，1表示明天*:</p>
                    <el-input v-model="insertForm.intervalCoefficient" type="text" placeholder="0"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>可选该预约时段的截止时间</p>
                    <el-time-picker
                        v-model="insertForm.optionalDeadline"
                        value-format="HH:mm:ss"
                        style="width: 100%"
                        placeholder="请选择时间">
                    </el-time-picker>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>起始金额(最小金额)</p>
                    <el-input v-model="insertForm.startingAmount" placeholder="0.00"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>结束金额(最大金额)</p>
                    <el-input v-model="insertForm.endAmount" placeholder="0.00"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>设置预约开始时间*:（预约开始时间 = 预约开始时间 + 间隔当前时间天数）</p>
                    <el-time-picker
                        v-model="insertForm.reservationStartTime"
                        value-format="HH:mm:ss"
                        style="width: 100%"
                        placeholder="请选择时间">
                    </el-time-picker>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>设置预约结束时间*:（设置预约结束时间 = 设置预约结束时间 + 间隔当前时间天数）</p>
                    <el-time-picker
                        v-model="insertForm.reservationEndTime"
                        value-format="HH:mm:ss"
                        style="width: 100%"
                        placeholder="请选择时间">
                    </el-time-picker>
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

    import reservation from '@/api/reservation';

    const columns = [
        { key: 'sortNo', title: '排序序号', width: '100', sort: 'custom' },
        { key: 'intervalCoefficient', title: '间隔当前时间天数', sort: 'custom' },
        { key: 'datename', title: '预约时间名称' },
        { key: 'optionalDeadline', title: '截止时间' },
        { key: 'startingAmount', title: '起始金额' },
        { key: 'endAmount', title: '结束金额' },
        { key: 'reservationStartTime', title: '设置预约开始时间' },
        { key: 'reservationEndTime', title: '设置预约结束时间' }
    ];

    const insertForm = {
        sortNo: '',
        intervalCoefficient: '',
        datename: '',
        optionalDeadline: '',
        startingAmount: '',
        endAmount: '',
        reservationStartTime: '',
        reservationEndTime: '',
        visible: false
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

                sortBy: '',
                direction: '',

                insertForm: JSON.parse(JSON.stringify(insertForm))
            }
        },

        methods: {
            sortChange(column) {
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.handlePagers();
            },

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
                if (!this.G.permission['_reservation/getAvailableAppointTimeArray']) {
                    return this.$notify.error('没有获取预约数据的权限');
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                reservation.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            if (item.optionalDeadline) {
                                item.optionalDeadline = this.formatTime(item.optionalDeadline);
                            }
                            if (item.reservationStartTime) {
                                item.reservationStartTime = this.formatTime(item.reservationStartTime);
                            }
                            if (item.reservationEndTime) {
                                item.reservationEndTime = this.formatTime(item.reservationEndTime);
                            }
                        });
                    }
                    this.records = res.records;
                    this.totalPage = res.total;
                })
            },

            formatTime(str) {
                const date = new Date();
                date.setHours(parseInt(str.substring(0, 2), 10));
                date.setMinutes(parseInt(str.substring(2, 4), 10));
                date.setSeconds(parseInt(str.substring(4, 6), 10));
                return date.customFormat('#hhhh#:#mm#:#ss#');
            },

            /**
             * 添加预约时间
             * */
            handleAddReservation() {
                this.insertForm.visible = true;
            },

            handleCancelAdd() {
                this.insertForm = JSON.parse(JSON.stringify(insertForm));
            },

            handleSubmit() {
                console.log(this.insertForm.sortNo);
                if (this.insertForm.sortNo === '') {
                    return this.$notify.error('请输入排序序号');
                }
                if (isNaN(this.insertForm.sortNo)) {
                    return this.$notify.error('排序序号只允许填写数字');
                }
                if (!Number.isInteger(parseFloat(this.insertForm.sortNo))) {
                    return this.$notify.error('排序序号只允许填写整数');
                }
                if (parseInt(this.insertForm.sortNo, 10) < 0) {
                    return this.$notify.error('排序序号必须为非负数');
                }
                if (this.insertForm.sortNo.length > 9) {
                    return this.$notify.error('序号不能大于9位');
                }
                if (!this.insertForm.datename) {
                    return this.$notify.error('预约时间名称不能为空');
                }
                if (!(this.insertForm.intervalCoefficient + '')) {
                    return this.$notify.error('间隔当前时间天数不能为空');
                }
                if (parseInt(this.insertForm.intervalCoefficient, 10) < 0) {
                    return this.$notify.error('间隔当前时间天数必须大于0');
                }
                if (!(Number(this.insertForm.intervalCoefficient) === 0 || Number(this.insertForm.intervalCoefficient) === 1)) {
                    return this.$notify.error('间隔当前时间天数请输入0或1');
                }
                if (!this.insertForm.optionalDeadline) {
                    return this.$notify.error('时间条件不能为空');
                }
                if (this.insertForm.startingAmount === '') {
                    return this.$notify.error('起始金额(最小金额)不能为空');
                }
                if (isNaN(this.insertForm.startingAmount)) {
                    return this.$notify.error('起始金额只允许填写数字');
                }
                if (parseInt(this.insertForm.startingAmount, 10) < 0) {
                    return this.$notify.error('起始金额必须为非负数');
                }
                if (this.insertForm.startingAmount.length > 9) {
                    return this.$notify.error('起始金额不能大于9位');
                }
                if (this.insertForm.endAmount === '') {
                    return this.$notify.error('结束金额(最大金额)不能为空');
                }
                if (isNaN(this.insertForm.endAmount)) {
                    return this.$notify.error('结束金额只允许填写数字');
                }
                if (parseInt(this.insertForm.endAmount, 10) < 0) {
                    return this.$notify.error('结束金额必须为非负数');
                }
                if (this.insertForm.endAmount.length > 9) {
                    return this.$notify.error('结束金额不能大于9位');
                }
                if (parseFloat(this.insertForm.startingAmount) > parseFloat(this.insertForm.endAmount)) {
                    return this.$notify.error('起始金额必须小于等于结束金额');
                }
                if (!this.insertForm.reservationStartTime) {
                    return this.$notify.error('预约开始时间不能为空');
                }
                if (!this.insertForm.reservationEndTime) {
                    return this.$notify.error('预约结束时间不能为空');
                }

                if (this.getTimeOfTimeStr(this.insertForm.reservationStartTime, this.insertForm.intervalCoefficient) >= this.getTimeOfTimeStr(this.insertForm.reservationEndTime, this.insertForm.intervalCoefficient)) {
                    return this.$notify.error('时间日期不合法，请检查日期格式或者结束日期是否大于开始日期');
                }
                if (this.getTimeOfTimeStr(this.insertForm.optionalDeadline, 0) > this.getTimeOfTimeStr(this.insertForm.reservationStartTime, this.insertForm.intervalCoefficient)) {
                    return this.$notify.error('时间日期不合法，请检查日期格式或者时间条件是否小于预约开始日期');
                }
                if (this.getTimeOfTimeStr(this.insertForm.optionalDeadline, 0) > this.getTimeOfTimeStr(this.insertForm.reservationEndTime, this.insertForm.intervalCoefficient)) {
                    return this.$notify.error('时间日期不合法，请检查日期格式或者时间条件是否小于预约结束日期');
                }

                const param = {
                    sortNo: this.insertForm.sortNo,
                    datename: this.insertForm.datename,
                    intervalCoefficient: this.insertForm.intervalCoefficient,
                    optionalDeadline: this.insertForm.optionalDeadline,
                    startingAmount: this.insertForm.startingAmount,
                    endAmount: this.insertForm.endAmount,
                    reservationStartTime: this.insertForm.reservationStartTime,
                    reservationEndTime: this.insertForm.reservationEndTime
                };
                if (!this.insertForm.id) {
                    reservation.insert(param).then(res => {
                        if (res.code === 100) {
                            this.insertForm = JSON.parse(JSON.stringify(insertForm));
                            this.$notify.success('操作成功');
                            this.handlePagers();
                        }
                    })
                } else {
                    param.id = this.insertForm.id;
                    reservation.update(param).then(res => {
                        if (res.code === 100) {
                            this.insertForm = JSON.parse(JSON.stringify(insertForm));
                            this.$notify.success('操作成功');
                            this.handlePagers();
                        }
                    })
                }
            },

            getTimeOfTimeStr(timeStr, intervalDays) {
                const date = new Date();
                date.setHours(parseInt(timeStr.substring(0, 2), 10));
                date.setMinutes(parseInt(timeStr.substring(3, 5), 10));
                date.setSeconds(parseInt(timeStr.substring(6, 8), 10));
                return date.getTime() + parseInt(intervalDays) * 24 * 60 * 60 * 1000;
            },

            /**
             * 编辑
             * */
            handleEditReservation(row) {
                this.insertForm = JSON.parse(JSON.stringify(row));
                this.insertForm.visible = true;
            },

            /**
             * 删除
             * */
            handleRemoveReservation(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    reservation.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '删除成功');
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
            }

        },

        created() {
            this.handlePagers();
        }
}
</script>
