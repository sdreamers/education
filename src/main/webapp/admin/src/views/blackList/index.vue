<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="searchForm.merchantTelephone" placeholder="商户注册手机号"></el-input>
                    </el-col>

                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>

                    <el-button style="float: right; margin-right: 20px;"
                               type="primary"
                               round
                               v-if="G.permission['_account/blackList/moveToWhiteList']"
                               @click="handleMoveToWhiteList">加入白名单
                    </el-button>
                    <el-button style="float: right; margin-right: 20px;"
                               type="info"
                               round
                               v-if="G.permission['_account/blackList/batchDelete']"
                               @click="handleRemoveMerchant">移除
                    </el-button>
                    <el-button style="float: right; margin-right: 10px;"
                               type="success"
                               round
                               v-if="G.permission['_account/blackList/insert']"
                               @click="handleAddMerchant">
                        添加黑名单
                    </el-button>
                </el-row>

                <el-table :data="records" border v-loading="loading" @select="handleSelectRow" @select-all="handleSelectAllRow">
                    <el-table-column
                        type="selection"
                        align="center"
                        width="55">
                    </el-table-column>
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        :align="column.align"
                        :width="column.width" />
                    <el-table-column label="操作" width="150%" align="center">
                        <template slot-scope="scope">
                            <el-button
                                v-if="G.permission['_account/blackList/updateRemark']"
                                type="text"
                                @click.stop="handleEditRemark(scope.row)">编辑备注</el-button>
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

        <el-dialog :close-on-click-modal="false" title="修改备注" :visible="editForm.visible" @close="handleCancelEdit" width="30%">
            <el-form :model="editForm">
                <el-form-item label="备注:">
                    <el-input v-model="editForm.remark" type="textarea" :rows="4" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelEdit">取 消</el-button>
                <el-button type="primary" @click="handleSubmitEdit">确 定</el-button>
            </div>
        </el-dialog>

        <add-merchant :visible="addDialogVisible" @close="addDialogVisible=false;handlePagers()"/>
    </section>
</template>

<script>

    import headerList from '@/config/blackWhiteList';
    import blackList from '@/api/blackList';
    import common from '@/components/common';
    import addMerchant from './add';
    import util from '@/libs/util';

    const searchForm = {
        merchantTelephone: ''
    };

    const columns = [
        { key: 'merchantTelephone', title: '商户注册手机号码', width: '200%', align: 'center' },
        { key: 'createTime', title: '添加时间', width: '200%', align: 'center' },
        { key: 'source', title: '来源', width: '200%', align: 'center' },
        { key: 'remark', title: '备注' }
    ];

    const editForm = {
        visible: false,
        blackListId: '',
        remark: ''
    };

    const source = {
        0: '系统扫描',
        1: '手工添加'
    }

    export default {

        components: {
            common,
            addMerchant
        },

        data() {
            return {
                headerList: headerList,
                searchForm: JSON.parse(JSON.stringify(searchForm)),

                loading: false,
                records: [],
                columns: columns,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                selectedRows: [],

                editForm: JSON.parse(JSON.stringify(editForm)),

                addDialogVisible: false,

                sortField: 'create_time',
                direction: 'desc'
            }
        },

        methods: {

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    sortBy: this.sortField,
                    direction: this.direction
                };
                if (this.searchForm.merchantTelephone) {
                    param.merchantTelephoneLike = this.searchForm.merchantTelephone
                }
                blackList.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.source = source[item.source];
                            if (item.createTime) {
                                item.createTime = util.parseTime(new Date(item.createTime));
                            }
                        })
                    }
                    this.records = res.records;
                    this.totalSize = res.total;
                })
            },

            handleEditRemark(row) {
                this.editForm.remark = row.remark;
                this.editForm.blackListId = row.id;
                this.editForm.visible = true;
            },

            /**
             * 勾选选择框
             * */
            handleSelectRow(selection, row) {
                const selectedFlag = selection.some(item => item.id === row.id);
                if (selectedFlag) {
                    this.selectedRows.push(row);
                    return;
                }
                this.selectedRows = this.selectedRows.filter(item => item.id !== row.id);
            },

            handleSelectAllRow(selection) {
                if (selection.length > 0) {
                    selection.forEach(row => {
                        const existFlag = this.selectedRows.some(item => item.id === row.id);
                        if (!existFlag) {
                            this.selectedRows.push(row)
                        }
                    });
                } else {
                    const ids = this.tableRecords.map(item => item.id)
                    this.selectedRows = this.selectedRows.filter(row => !ids.includes(row.id));
                }
            },

            /**
             * 新增黑名单
             * */
            handleAddMerchant() {
                this.addDialogVisible = true;
            },

            /**
             * 移除黑名单
             * */
            handleRemoveMerchant() {
                if (!this.selectedRows || this.selectedRows.length < 1) {
                    return this.$notify.error('请至少选择一条黑名单记录');
                }
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const ids = this.selectedRows.map(item => item.id).join(',');
                    const param = {
                        ids: ids
                    };
                    blackList.batchDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.selectedRows = [];
                            this.checkIsLastItemOfLastPage();
                            this.handlePagers();
                        } else {
                            return this.$notify.error(res.message);
                        }
                    });
                }).catch(err => {
                    console.log(err);
                });
            },
            checkIsLastItemOfLastPage() {
                if (this.records.length === 1 && this.currentPage > 1) {
                    this.currentPage = this.currentPage - 1;
                }
            },

            /**
             * 加入白名单
             * */
            handleMoveToWhiteList() {
                if (!this.selectedRows || this.selectedRows.length < 1) {
                    return this.$notify.error('请至少选择一条黑名单记录');
                }
                this.$confirm('移至白名单以后将无法恢复，确定要继续吗？', '移至白名单', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const ids = this.selectedRows.map(item => item.id).join(',');
                    const param = {
                        ids: ids
                    };
                    blackList.moveToWhiteList(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('加入白名单成功');
                            this.selectedRows = [];
                            this.checkIsLastItemOfLastPage();
                            this.handlePagers();
                        } else {
                            return this.$notify.error(res.message);
                        }
                    });
                }).catch(err => {
                    console.log(err);
                });
            },

            /**
             * 取消修改备注
             * */
            handleCancelEdit() {
                this.editForm = JSON.parse(JSON.stringify(editForm));
            },

            handleSubmitEdit() {
                if (!this.editForm.remark) {
                    this.editForm.remark = '';
                }
                if (this.editForm.remark.length > 255) {
                    return this.$notify.error('备注长度不能超过255个字符');
                }
                const param = {
                    remark: this.editForm.remark,
                    id: this.editForm.blackListId
                };
                blackList.updateRemark(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('更新备注成功');
                        this.editForm = JSON.parse(JSON.stringify(editForm));
                        this.handlePagers();
                    }
                })
            }
        },

        created() {
            this.handlePagers();
        }
    }
</script>

<style scoped>

</style>
