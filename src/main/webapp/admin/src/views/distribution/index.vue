<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="$router.push({name: '/common/distribution/insert', params: {formType: 'add'}})"
                           v-if="G.permission['_common/distribution/insert']">新增配送区域</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="tableData" border  @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleOpen(scope.row)"
                                v-if="G.permission['_common/distribution/get']">查看</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleEdit(scope.row)"
                                v-if="G.permission['_common/distribution/get'] && G.permission['_common/distribution/update']">编辑</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleRemove(scope.row)"
                                v-if="G.permission['_common/distribution/delete']">删除</el-button>
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
            </el-row>
        </el-card>

    </section>
</template>
<script>
    import api from '@/api/distribution';
    import Util from '@/libs/util';

    const columns = [
        { key: 'id', title: 'id', width: '48px', sort: 'custom' },
        { key: 'name', title: '名称', width: '96px', sort: 'custom' }
    ];
    export default {
        data() {
            return {
                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 10,
                sortBy: '',
                direction: '',
                dialogFormVisible: false,
                formLabelWidth: '120px',
                formType: ''
            };
        },
        methods: {
            // 点击添加按钮
            handleAdd() {
                // this.formType = 'add';
                // this.form = JSON.parse(JSON.stringify(form));
                // this.dialogFormVisible = true;
            },
            handleOpen(data) {
                console.log(data);
                this.$router.push({ name: '/common/distribution/get', params: { id: data.id, data: data, formType: 'view' } });
            },
            // 点击编辑按钮
            handleEdit(data) {
                this.$router.push({ name: '/common/distribution/update', params: { id: data.id, data: data, formType: 'edit' } });
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
                    api.distributionScopeDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.pagers();
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
                this.pagers();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.pagers();
            },
            // 获取数据
            pagers() {
                const param = {
                    page: 1,
                    limit: 5
                };
                param.page = this.currentPage;
                param.limit = this.pageSize;
                param.cityId = Util.getCookie('cityId');
                param.sortBy = this.sortBy ? this.sortBy : undefined;
                param.direction = this.direction ? this.direction : undefined;
                api.distributionScopePagers(param).then(res => {
                    window.LOG(res);
                    if (res.records) {
                        this.tableData = res.records;
                        console.log(res);
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }

                    this.totalPage = res.total;
                });
            },
            sortChange(column) {
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.pagers();
            }
        },
        created() {
            this.pagers();
        }
    };
</script>
