<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row>
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="用户名">
                    </el-input>
                </el-col>
                <el-col :span="3">
                    <el-input v-model="search.accountLike" placeholder="学校名称">
                    </el-input>
                </el-col>

                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>
            </el-row>

            <el-row class="list-con clearfix">
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :label="column.title" :key="key" align="center">
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
                                @click.stop="handleEditUser(scope.row)">查看任务明细
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[10, 15, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i
                            class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
    </section>
</template>

<script>
import userApi from '@/api/user'


const search = {
    nameLike: '',
    accountLike: ''
}

const columns = [
        { key: 'name', title: '名称' },
        { key: 'account', title: '账号' },
        { key: 'telephone', title: '手机号码' },
        { key: 'status', title: '状态' },
        { key: 'nature', title: '角色' },
        { key: 'supplierName', title: '对应供应商' }
        ];

export default {
    data() {
        return {
            search: JSON.parse(JSON.stringify(search)),

            tableData: [],
            columns: columns,
            currentPage: 1,
            totalSize: 0,
            pageSize: 10,
            loading: false,
        };
    },
    methods: {

        handleSizeChange(size) {
                this.pageSize = size;
                this.handlePagers();
            },

        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.handlePagers();
        },

        handleSearch() {
            this.handlePagers();
        },
        handleClear() {
            this.search = JSON.parse(JSON.stringify(search));
        },
        handlePagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize
            };
            if (this.search.nameLike) {
                param.nameLike = this.search.nameLike;
            }
            if (this.search.accountLike) {
                param.accountLike = this.search.accountLike;
            }
            userApi.pages(param).then(res => {
                this.tableData = res.records;
                this.totalSize = res.total;
            });
        },
        // 编辑用户
        handleEditUser() {
            
        }
    },

    mounted() {
        this.setUserInfo();
    }

};
</script>
