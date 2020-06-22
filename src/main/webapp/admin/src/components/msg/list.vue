<template>
    <el-dialog :close-on-click-modal="false" title="我的消息" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-row>
            <el-button type="primary" @click="handleMark">标记为已读</el-button>
        </el-row>
        <el-table :data="tableData" border class="mt20" @selection-change="handleSelectTable" ref="multipleTable">
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :align="column.align">
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
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/msg';

const columns = [
    { key: 'content', title: '消息内容', align: 'left' }
];
export default {
    data() {
        return {

            tableData: [],
            columns: columns,

            selection: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 10
        };
    },
    methods: {
        handleSelectTable(selection) {
            this.selection = selection;
        },
        handleMark() {
            const ids = this.selection.map(item => item.id);
            if (ids.length === 0) {
                return this.$notify.error('未选择');
            }
            const param = {
                ids: ids
            }
            api.markedRead(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    // this.$refs.multipleTable.clearSelection();
                    this.msgPagers();
                } else {
                    this.$notify.success(res.message || '异常');
                }
            })
        },
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.msgPagers();
        },
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.msgPagers();
        },
        msgPagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            api.userMessagePagers(param).then(res => {
                if (res.records) {
                    this.tableData = res.records.map(item => {
                        item.content = item.readFlag === '0' ? `<b>${item.content}</b>` : item.content;
                        return item;
                    });
                } else {
                    this.tableData = [];
                    this.$notify.info('无数据');
                }
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.msgPagers();
    }
};
</script>
