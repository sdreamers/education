<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-button style="float: right; margin-right: 30px;"
                                   type="success"
                                   round
                                   @click="handleAddConfig">
                            添加配置
                        </el-button>
                    </el-row>
                </el-row>
                <el-table
                    :data="records"
                    :span-method="handleObjectSpan"
                    v-loading="loading"
                    border>
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        align="center"
                        :width="column.width">
                    </el-table-column>
                    <el-table-column label="修改" align="center">
                        <template slot-scope="scope">
                            <i class="el-icon-edit" @click.stop="handleEditConfig(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除" align="center">
                        <template slot-scope="scope">
                            <i class="el-icon-delete" @click.stop="handleRemoveConfig(scope.row)"></i>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
        </el-card>

        <!-- 工单详情 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelEdit" title="新增/修改评分信息" :visible.sync="editForm.visible" width="15%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>类型</p>
                    <el-select v-model="editForm.type" style="width: 100%">
                        <el-option v-for="(item, key) in typeList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>评分原因</p>
                    <el-input v-model="editForm.reason" maxlength="6"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>排序号</p>
                    <el-input type="number" v-model="editForm.sortNo" maxlength="10"></el-input>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click.stop="handleSubmitEdit">确定</el-button>
                <el-button @click.stop="handleCancelEdit">取消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import common from '@/components/common';
    import headerList from '@/config/evaluateList';
    import newEvaluationConfig from '@/api/newEvaluationConfig';
    import commonApi from '@/api/common';

    const columns = [
        { key: 'typeCN', title: '类型', width: '300%' },
        { key: 'sortNo', title: '排序号', width: '300%' },
        { key: 'reason', title: '评分原因' }
    ];

    const editForm = {
        type: '',
        reason: '',
        oldReason: '',
        sortNo: '',
        id: '',
        visible: false
    };

    const typeList = [
        { id: 1, name: '满意' },
        { id: 0, name: '不满意' }
    ];

    export default {
        components: {
            common
        },
        data() {
            return {
                headerList: headerList,

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                editForm: JSON.parse(JSON.stringify(editForm)),
                typeList: typeList,

                evaluationTypeMap: {}
            }
        },

        methods: {
            handlePagers() {
                newEvaluationConfig.listAll().then(res => {
                    if (res.code === 100 && res.content) {
                        let records = [];
                        console.log(this.evaluationTypeMap);
                        for (const type in this.evaluationTypeMap) {
                            const arr = res.content.filter(item => item.type === Number(type));
                            records = records.concat(arr);
                        }
                        records.forEach(item => {
                            item.typeCN = this.evaluationTypeMap[item.type];
                        });
                        console.log(records);
                        const satisfied = [];
                        const unsatisfied = [];
                        records.forEach(item => {
                            if (item.type === 1) {
                                satisfied.push(item);
                            } else {
                                unsatisfied.push(item);
                            }
                        });

                        this.records = [...satisfied.sort((a, b) => a.sortNo - b.sortNo), ...unsatisfied.sort((a, b) => a.sortNo - b.sortNo)];
                        console.log(this.records);
                    }
                });
            },

            handleObjectSpan({ rowIndex, columnIndex }) {
                if (columnIndex === 0) {
                    if (rowIndex === 0) {
                        const rowspan = this.records.filter(item => item.type === this.records[rowIndex].type).length;
                        return { rowspan: rowspan, colspan: 1 };
                    } else {
                        // 比较和前一行类型是否一样
                        if (this.records[rowIndex].type === this.records[rowIndex - 1].type) {
                            return { rowspan: 0, colspan: 0 };
                        } else {
                            const rowspan = this.records.filter(item => item.type === this.records[rowIndex].type).length;
                            return { rowspan: rowspan, colspan: 1 };
                        }
                    }
                }
            },

            /**
             * 删除配置
             * */
            handleRemoveConfig(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    newEvaluationConfig.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '删除成功');
                            this.handlePagers();
                        }
                    });
                }).catch(err => {
                    window.LOG(err);
                });
            },

            /**
             * 添加配置
             * */
            handleAddConfig() {
                this.editForm.visible = true;
            },

            /**
             * 编辑配置
             * */
            handleEditConfig(row) {
                this.editForm = JSON.parse(JSON.stringify(row));
                this.editForm.oldReason = this.editForm.reason;
                this.editForm.visible = true;
            },

            handleCancelEdit() {
                this.editForm = JSON.parse(JSON.stringify(editForm));
            },

            handleSubmitEdit() {
                if (this.editForm.type === '') {
                    return this.$notify.error('请选择类型');
                }
                if (!this.editForm.reason) {
                    return this.$notify.error('请输入评分原因');
                }
                if (this.editForm.reason.length > 6) {
                    return this.$notify.error('原因不能超过6个字符');
                }
                if (this.editForm.sortNo === '') {
                    return this.$notify.error('请输入排序号');
                }
                if (this.editForm.sortNo.toString().length > 9) {
                    return this.$notify.error('排序号不得大于9位');
                }
                if (!/^[0-9]+$/.test(this.editForm.sortNo)) {
                    return this.$notify.error('请输入正确的排序号');
                }
                const param = JSON.parse(JSON.stringify(this.editForm));
                delete param.visible;
                if (param.oldReason === param.reason) {
                    param.reason = '';
                }
                delete param.oldReason;
                if (param.id) {
                    newEvaluationConfig.update(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('更新成功');
                            this.editForm = JSON.parse(JSON.stringify(editForm));
                            this.handlePagers();
                        }
                    })
                } else {
                    newEvaluationConfig.insert(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('新增成功');
                            this.editForm = JSON.parse(JSON.stringify(editForm));
                            this.handlePagers();
                        }
                    })
                }
            },

            handleLoadEvaluationType() {
                commonApi.getEvaluationTypeDict().then(res => {
                    if (res.code === 100) {
                        for (const item in res.content) {
                            this.evaluationTypeMap[Number(item)] = res.content[item];
                        }
                    }
                })
            }
        },

        created() {
            Promise.all([
                this.handleLoadEvaluationType()
            ]).then(() => {
                this.handlePagers();
            });
        }
    }
</script>
