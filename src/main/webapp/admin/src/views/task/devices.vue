<template>
    <section class="view-content">
        <el-card shadow="none">
            <el-row class="mb20" :gutter="20">
                <el-col :span="3">
                    <el-input v-model="search.nameLike" placeholder="设备名称">
                    </el-input>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>

               <!--  <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleImport">导入</el-button>
                <el-button style="float: right;margin-right: 30px;" type="primary" @click="handleExport">导出</el-button> -->
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
                    <el-table-column prop="inProgressStatus" label="当前状态" align="center">
                        <template slot-scope="scope">
                            <el-select v-model="scope.row.inProgressStatus" @change="handleStatusChange(scope.row)">
                                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
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

        <schoolEdit
            :dialogVisible="merchantEditDialogVisible"
            :form="merchantEditForm"
            :type="merchantEditType"
            @close="merchantEditClose"/>

        <appointSupplier
            :dialogVisible="supplierDialogVisible"
            :form="supplierForm"
            @close="supplierDialogClose"/>

        <importExcel
            :dialogVisible="importDialogVisible"
            @close="importDialogClose"/>

    </section>
</template>
<script>
    import schoolEdit from './schoolDetail'
    import appointSupplier from './supplier'
    import supplierApi from '@/api/supplier'
    import importExcel from './importExcel';
    import deviceApi from '@/api/device';

    const merchantEditForm = {
        projectId: ''
    };

    const supplierForm = {
        projectId: '',
        oldSupplier: '',
        newSupplier: ''
    };

    const search = {
        packetId: '',
        type: '',
        nameLike: ''
    };

    const normalStatus = [{ label: '生产中', value: 1 }, { label: '到货中', value: 2 }, { label: '安装中', value: 3 }, { label: '已完成', value: 7 }]
    const informationStatus = [{ label: '到货中', value: 4 }, { label: '安装中', value: 5 }, { label: '调试中', value: 6 }, { label: '已完成', value: 7 }]

    const columns = [
        { key: 'schoolName', title: '学校名称' },
        { key: 'name', title: '设备名称' },
        { key: 'num', title: '数量' },
        { key: 'unit', title: '单位' },
        { key: 'excludingTaxPrice', title: '不含税单价(元)' },
        { key: 'tax', title: '税金(元)' },
        { key: 'totalAmount', title: '含税总价(元)' }
    ];

    export default {
        components: {
            schoolEdit,
            appointSupplier,
            importExcel
        },

        data() {
            return {
                tableData: [],
                columns: columns,
                currentPage: 1,
                totalSize: 0,
                pageSize: 10,
                loading: false,
                search: JSON.parse(JSON.stringify(search)),
                statusOptions: []
            }
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
                if (!this.search.packetId) {
                    this.$notify.error('异常');
                    return;
                }
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize,
                    packetId: this.search.packetId
                };
                if (this.search.nameLike) {
                    param.nameLike = this.search.nameLike;
                }
                deviceApi.pages(param).then(res => {
                    this.tableData = res.records;
                    this.totalSize = res.total;
                })
            },

            handleStatusChange(row) {
                deviceApi.updateStatus({ status: row.inProgressStatus, id: row.id }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                    }
                })
            }
        },

        created() {
            this.search.packetId = this.$route.params.packetId;
            this.search.type = this.$route.params.type;
            if (this.search.type === 1) {
                this.statusOptions = JSON.parse(JSON.stringify(normalStatus));
            } else {
                 this.statusOptions = JSON.parse(JSON.stringify(informationStatus));
            }
            this.handlePagers();
        }
    }
</script>
