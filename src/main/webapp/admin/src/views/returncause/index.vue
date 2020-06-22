<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_order/returncause/insert']">添加退货原因</el-button>
            </div>
            <el-row class="list-con clearfix">
                    <el-table :data="tableData" border v-loading="loading">
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_order/returncause/update']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_order/returncause/delete']"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
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

        <edit
            v-if="dialogFormVisible"
            :formType="formType"
            :form="form"
            @close="dialogFormVisible=false,returncausePagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/returncause';

import edit from './edit';

const columns = [
    { key: 'returnCauseNo', title: '退货原因代码' },
    { key: 'returnCauseDescription', title: '退货原因描述' },
    { key: 'isNeedImage_text', title: '申请退货是否需要上传图片' }
];
const form = {
    returnCauseNo: '',
    returnCauseDescription: '',
    isNeedImage: ''
};
const isNeedImageMap = {
    0: '否',
    1: '是'
}
export default {
    components: {
        edit
    },
    data() {
        return {
            key: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 20,
            sortBy: '',
            direction: '',
            loading: false,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: ''
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.returncausePagers();
        },
        // 清除搜索
        handleClear() {
            this.key = '';
            this.currentPage = 1;
            this.returncausePagers();
        },
        // 点击添加按钮
        handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑按钮
        handleEdit(data) {
            this.formType = 'edit';
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        // 点击删除按钮
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
                api.returncauseDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.returncausePagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
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
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.returncausePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.returncausePagers();
        },
        // 获取数据
        returncausePagers() {
            if (!this.G.permission['_order/returncause/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            api.returncausePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    item.isNeedImage_text = isNeedImageMap[item.isNeedImage];
                    return item;
                });
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.returncausePagers();
    }
};
</script>
