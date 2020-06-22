<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_market/news/insert']">添加新闻轮播</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <a target="_blank" v-if="column.key === 'previewImageUrl'" :href="scope.row.previewImageUrl"><img style="width: 80px;height: 80px;" :src="scope.row.previewImageUrl" /></a>
                            <span v-if="column.key !== 'previewImageUrl'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_market/news/update']"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_market/news/delete']"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
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
        <edit
            v-if="dialogFormVisible"
            @close="dialogFormVisible=false,newsPagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/news';
// // import Util from '@/libs/util';

import edit from './edit';

const columns = [
    { key: 'sortNo', title: '序号' },
    { key: 'title', title: '新闻标题' },
    { key: 'contentUrl', title: '新闻内容链接地址' },
    { key: 'redirectionUrl', title: '可跳转至URL' },
    { key: 'previewImageUrl', title: '新闻预览图片' },
    { key: 'isShowIndex_text', title: '是否在首页展示' },
    { key: 'enableFlag_text', title: '是否在APP启用' }
];
const form = {
    showLocation: '',
    sortNo: '',
    provinceId: '',
    cityId: '',
    status: '',
    detailUrl: '',
    previewImageUrl: '',
    width: '',
    height: ''
};
export default {
    components: {
        edit
    },
    data() {
        return {
            searchInput: '',

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
            formType: '',
            // 品牌列表
            productGroupList: [],
            // 单位列表
            unitList: [],
            splitList: [
                { id: '0', name: '否' },
                { id: '1', name: '是' }
            ]
        };
    },
    methods: {
        // 添加基本信息
        handleAdd() {
            this.formType = 'add';
            form.content = '';
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑基本信息按钮
        handleEdit(data) {
            this.formType = 'edit';
            const formData = JSON.parse(JSON.stringify(data));
            formData.content = '';
            this.form = formData;
            this.dialogFormVisible = true;
        },
        handleView(data) {
            console.log(data);
            if (data.previewImageUrl) {
                window.open(data.previewImageUrl);
            }
        },
        // 删除基本信息
        handleRemove(data) {
            console.log(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.newsDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.newsPagers();
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
        // 基本信息页码改变
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.newsPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.newsPagers();
        },
        // 获取基本信息列表
        newsPagers() {
            if (!this.G.permission['_market/news/pages']) {
                return this.$notify.error('没有列表权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            api.newsPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    item.isShowIndex_text = item.isShowIndex === 'Y' ? '是' : '否';
                    item.enableFlag_text = item.enableFlag === 'Y' ? '是' : '否';
                    return item;
                });
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.newsPagers();
    }
};
</script>
