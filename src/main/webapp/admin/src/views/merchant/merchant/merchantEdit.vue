<template>
    <el-dialog :close-on-click-modal="false" title="学校明细" :visible="dialogVisible" width="60%" @close="$emit('close')">
        <el-table :data="tableData" border  v-loading="loading" >
            <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center" >
                <el-table-column v-for="(subColumn,subKey) in column.columns" :prop="subColumn.key" width="120%"
                                 :label="subColumn.title" align="center" :key="subKey">
                    <template slot-scope="scope">
                        <span>{{scope.row[subColumn.key]}}</span>
                    </template>
                </el-table-column>
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                    <el-button
                        type="text"
                        @click.stop="handleEditMerchant(scope.row)">设备明细</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import api from '@/api/merchant';

    const tableData = [{ school: '一小', equipmentAmount: '12000', currentPoint: '生产', status: '已完成' },
        { school: '二小', equipmentAmount: '15000', currentPoint: '生产', status: '已完成' },
        { school: '三小', equipmentAmount: '18000', currentPoint: '生产', status: '未完成' },
        { school: '四小', equipmentAmount: '21000', currentPoint: '生产', status: '未完成' }];

    const columns = [{ key: 'school', title: '学校' }, { key: 'equipmentAmount', title: '设备金额(元)' },
        { key: 'currentPoint', title: '当前节点' }, { key: 'status', title: '状态' }];

    export default {
        props: ['dialogVisible', 'formLabelWidth', 'form', 'type'],
        data() {
            return {
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }]
                },

                tableData: tableData,
                columns: columns,
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                const param = {
                    name: this.form.name,
                    memberNumber: this.form.memberNumber
                };
                if (this.type === 'add') {
                    api.insertMerchant(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('添加成功');
                            this.$emit('close');
                        }
                    });
                } else {
                    param.id = this.form.id;
                    api.updateMerchant(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('添加成功');
                            this.$emit('close');
                        }
                    });
                }
            }
        }
    };
</script>
