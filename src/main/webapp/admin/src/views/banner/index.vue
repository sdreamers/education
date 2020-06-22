<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_market/banner/insert']">添加广告</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <a target="_blank" v-if="column.key === 'imageUrl'" :href="scope.row.imageUrl"><img style="width: 80px;height: 80px;" :src="scope.row.imageUrl" /></a>
                            <span v-if="column.key !== 'imageUrl'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="140">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleView(scope.row)" v-if="G.permission['_market/banner/get']">查看</el-button>
                            <el-button
                            type="text"
                            @click.stop="handleEdit(scope.row)" v-if="G.permission['_market/banner/update']">编辑</el-button>
                            <el-button
                            type="text"
                            @click.stop="handleRemove(scope.row)" v-if="G.permission['_market/banner/delete']">删除</el-button>
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
            @close="dialogFormVisible=false,bannerPagers()"
            :formType="formType"
            :form="form"></edit>
    </section>
</template>
<script>
import api from '@/api/banner';
import Util from '@/libs/util';

import edit from './edit';

const columns = [
    { key: 'showLocation_text', title: '展示位置' },
    { key: 'sortNo', title: '排序号' },
    { key: 'imageUrl', title: '缩略图' },
    { key: 'width', title: '图片宽度(px)' },
    { key: 'height', title: '图片高度(px)' },
    { key: 'detailUrl', title: '广告跳地址' },
    { key: 'expiryDaysFlag_text', title: '是否永久' },
    { key: 'beginTime', title: '起始时间' },
    { key: 'endTime', title: '结束时间' },
    { key: 'status_text', title: '状态' }
];
const bannerShowLocation = { APP_INDEX: 'APP-首页', APP_MEMBER_CENTER: 'APP-会员中心', APP_POP: 'APP-弹出' };
const form = {
    showLocation: '',
    sortNo: '',
    provinceId: '',
    cityId: '',
    status: '',
    detailUrl: '',
    imageUrl: '',
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
            this.form = JSON.parse(JSON.stringify(form));
            this.dialogFormVisible = true;
        },
        // 点击编辑基本信息按钮
        handleEdit(data) {
            this.formType = 'edit';
            const formData = JSON.parse(JSON.stringify(data));
            console.log(formData);
            if (formData.beginTime && formData.endTime && formData.beginTime !== ' ' && formData.endTime !== ' ') {
                formData.date = [formData.beginTime, formData.endTime]
            } else {
                formData.date = [];
            }
            this.form = JSON.parse(JSON.stringify(formData));
            this.dialogFormVisible = true;
        },
        handleView(data) {
            console.log(data);
            if (data.imageUrl) {
                window.open(data.imageUrl);
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
                api.bannerDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.bannerPagers();
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
            this.bannerPagers();
        },
        // 基本信息翻页
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.bannerPagers();
        },
        // 获取基本信息列表
        bannerPagers() {
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            api.bannerPagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records.map(item => {
                    item.expiryDaysFlag_text = item.expiryDaysFlag === '1' ? '是' : '否';
                    item.status_text = item.status === '1' ? '是' : '否';
                    item.showLocation_text = bannerShowLocation[item.showLocation];
                    item.beginTime = Util.parseTimeStr(item.beginTime) || '';
                    item.endTime = Util.parseTimeStr(item.endTime) || '';
                    return item;
                });
                this.totalPage = res.total;
            });
        }
    },
    created() {
        this.bannerPagers();
    }
};
</script>
