<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        
        <el-table :data="tableData" border>
            <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
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
                <span class="el-pagination__total refresh" @click="subscribersPagers(currentPage)"><i class="el-icon-refresh"></i></span>
            </el-pagination>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/subscribestockuser'
const columns = [
    { key: 'merchantName', title: '商家名称' },
    { key: 'merchantTelephone', title: '商家手机号' },
    { key: 'createDateTime', title: '订阅时间' }
]
export default {
    props: ['formType', 'form'],
    data() {
        return {
            formLabelWidth: '120px',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 10
        };
    },
    methods: {
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.subscribersPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.subscribersPagers();
        },
        subscribersPagers() {
            const param = {
                productId: this.form.productId,
                specificationId: this.form.specificationId,
                page: this.currentPage,
                limit: this.pageSize
            }
            api.subscribersPagers(param).then(res => {
                console.log(res);
                this.tableData = res.records;
                this.totalPage = res.total;
            })
        }
    },
    created() {
        this.subscribersPagers();
    }
};
</script>
