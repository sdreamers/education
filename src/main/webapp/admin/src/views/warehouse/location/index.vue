<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="3">
                        <el-select v-model="searchForm.warehouseId" placeholder="请选择冻库">
                            <el-option
                                v-for="item in warehouseList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <el-autocomplete
                            v-model="searchForm.productName"
                            :fetch-suggestions="handleQueryProduct"
                            :disabled="searchForm.warehouseId === ''"
                            placeholder="请输入商品关键字"
                            @select="handleSelectProduct"
                            :trigger-on-focus='false'
                            style="width: 100%"
                            @input="productNameChange"
                        ></el-autocomplete>
                    </el-col>
                    <el-col :span="3">
                        <el-select v-model="searchForm.productSpecificationId" :disabled="searchForm.warehouseId === ''" placeholder="请选择规格">
                            <el-option
                                v-for="item in specificationList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="3">
                        <el-select v-model="searchForm.auditStatus" placeholder="请选择审核状态">
                            <el-option
                                v-for="item in workFlowStatusList"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-col>
                    <el-col :span="5">
                        <el-date-picker
                            v-model="searchForm.searchDate"
                            type="daterange"
                            align="right"
                            unlink-panels
                            value-format="yyyy-MM-dd"
                            range-separator="至"
                            start-placeholder="开始申请日期"
                            end-placeholder="结束申请日期">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="records" border v-loading="loading">
                    <el-table-column
                        v-for="(column,key) in columns"
                        :prop="column.key"
                        :label="column.title"
                        :key="key"
                        align="center"
                        :width="column.width">
                    </el-table-column>
                    <el-table-column label="审核流程">
                        <template slot-scope="scope">
                            <i class="el-icon-view" @click.stop="handleViewNode(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="审核">
                        <template slot-scope="scope">
                            <i class="el-icon-edit-outline" v-if="scope.row.auditFlag === true" @click.stop="handleAudit(scope.row)"></i>
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
                        :total="totalSize">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>

        <!-- 查看审核流程 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelView" title="仓位审核流程" :visible.sync="nodeForm.visible" width="40%">
            <el-table :data="nodeForm.records" border v-loading="loading">
                <el-table-column
                    v-for="(column,key) in nodeForm.columns"
                    :prop="column.key"
                    :label="column.title"
                    :key="key"
                    align="center"
                    :width="column.width">
                </el-table-column>
            </el-table>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelView">取 消</el-button>
            </div>
        </el-dialog>

        <!-- 审核 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelAudit" title="仓位调整的审核信息" :visible.sync="auditForm.visible" width="30%">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>商品名称:</p>
                    <el-input v-model="auditForm.productName"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>规格名称:</p>
                    <el-input v-model="auditForm.specificationName"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>主仓位:</p>
                    <el-input v-model="auditForm.warehouseLocationMaster"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>副仓位:</p>
                    <el-input v-model="auditForm.warehouseLocationSlave"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>创建人:</p>
                    <el-input v-model="auditForm.creatorName"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>创建时间:</p>
                    <el-input v-model="auditForm.createDateTime"></el-input>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>审核记录:</p>
                    <section v-for="(item, key) in auditForm.auditRecords" :key="key">
                        {{item}}
                    </section>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>您的批语:</p>
                    <el-input v-model="auditForm.auditRemark" type="textarea" :rows="3"></el-input>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button type="success" @click.stop="handleAuditPass">审核通过</el-button>
                <el-button type="danger" @click.stop="handleAuditReject">审核驳回</el-button>
                <el-button @click="handleCancelAudit">取 消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>

    import common from '@/api/common';
    import warehouseLocation from '@/api/warehouseLocation';
    import util from '@/libs/util';

    const searchForm = {
        warehouseId: '',
        productId: '',
        productSpecificationId: '',
        auditStatus: '',
        searchDate: [],
        productName: ''
    };

    const columns = [
        { key: 'productName', title: '商品名称' },
        { key: 'specificationName', title: '规格名称' },
        { key: 'warehouseLocationMaster', title: '主库位' },
        { key: 'warehouseLocationSlave', title: '备库位' },
        { key: 'createDateTime', title: '申请时间' },
        { key: 'creatorName', title: '申请人' },
        { key: 'auditorName', title: '待审核人' },
        { key: 'auditStatus', title: '审批状态' }
    ];

    const nodeColumns = [
        { key: 'refundOrderOperator', title: '操作人' },
        { key: 'auditRecordStatus', title: '审核状态' },
        { key: 'auditDescription', title: '备注/审核批语' }
    ];

    const nodeForm = {
        visible: false,
        records: [],
        columns: nodeColumns
    };

    const auditForm = {
        id: '',
        productName: '',
        specificationName: '',
        warehouseLocationMaster: '',
        warehouseLocationSlave: '',
        creatorName: '',
        createDateTime: '',
        auditRecords: [],
        auditRemark: '',
        visible: false
    };

    export default {
        data() {
            return {
                warehouseList: [],
                searchForm: JSON.parse(JSON.stringify(searchForm)),
                productList: [],
                specificationList: [],
                workFlowStatusList: [],

                records: [],
                columns: columns,
                loading: false,
                currentPage: 1,
                pageSize: 10,
                totalSize: 0,

                nodeForm: JSON.parse(JSON.stringify(nodeForm)),

                auditForm: JSON.parse(JSON.stringify(auditForm)),

                /* 无用字段 */
                productSpecificationList: []
            }
        },

        methods: {
            handleLoadWarehouse() {
                common.getWarehouseByUserCurrentCityId().then(res => {
                    if (res.code === 100 && res.content) {
                        for (const item in res.content) {
                            if (res.content.hasOwnProperty(item)) {
                                this.warehouseList.push({ id: item, name: res.content[item] });
                            }
                        }
                        this.warehouseList.unshift({ id: '', name: '请选择冻库' });
                    }
                });
            },

            /**
             * 搜索商品
             * */
            handleQueryProduct(name, cb) {
                if (!this.searchForm.warehouseId || !name) {
                    return cb([]);
                }
                console.log('handleQueryProduct');
                this.searchForm.productId = '';
                this.searchForm.productSpecificationId = '';
                this.searchForm.specificationList = [];
                const param = {
                    name: name,
                    warehouseId: this.searchForm.warehouseId
                };
                warehouseLocation.listProductByKeyword(param).then(res => {
                    if (res.code === 100 && res.content) {
                        this.productList = res.content;
                        const productArr = res.content.map(item => ({ id: item.id, value: item.name }));
                        return cb(productArr);
                    } else {
                        return cb([]);
                    }
                })
            },

            handleSelectProduct(selectedProduct) {
                this.searchForm.productId = selectedProduct.id;
                const product = this.productList.filter(item => item.id === selectedProduct.id)[0];
                if (product.productSpecificationList) {
                    this.specificationList = product.productSpecificationList.map(item => ({ id: item.id, name: item.name }));
                }
                this.specificationList.unshift({ id: '', name: '请选择规格' });
            },

            productNameChange() {
                console.log('productNameChange');
                // if (!this.searchForm.productName) {
                this.searchForm.specificationList = [];
                this.searchForm.productSpecificationId = null;
                this.searchForm.productId = '';
                // }
            },

            /**
             * 加载状态字典
             * */
            handleLoadWorkFlowStatus() {
                common.getWorkFlowStatusDict().then(res => {
                    if (res.code === 100 && res.content) {
                        for (const item in res.content) {
                            if (res.content.hasOwnProperty(item)) {
                                this.workFlowStatusList.push({ id: item, name: res.content[item] });
                            }
                        }
                        this.workFlowStatusList.unshift({ id: '', name: '请选择审核状态' });
                    }
                })
            },

            handleSearch() {
                this.currentPage = 1;
                this.handlePagers();
            },

            handleClear() {
                this.searchForm = JSON.parse(JSON.stringify(searchForm));
                this.currentPage = 1;
                this.handlePagers();
            },

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers();
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                const param = JSON.parse(JSON.stringify(this.searchForm));
                if (param.searchDate && param.searchDate.length === 2) {
                    param.applicationDateBegin = param.searchDate[0];
                    param.applicationDateEnd = param.searchDate[1];
                }
                delete param.searchDate;
                param.page = this.currentPage;
                param.limit = this.pageSize;
                warehouseLocation.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.createDateTime = util.parseTimeStr(item.createDateTime);
                        })
                    }
                    this.records = res.records;
                    this.totalSize = res.total;
                })
            },

            /**
             * 查看审核流程
             * */
            handleViewNode(row) {
                warehouseLocation.listWorkflowNodeAndRecordStatus({ id: row.id }).then(res => {
                    if (res.code === 100) {
                        this.nodeForm.records = res.content;
                        this.nodeForm.visible = true;
                    }
                })
            },

            handleCancelView() {
                this.nodeForm = JSON.parse(JSON.stringify(nodeForm));
            },

            /**
             * 审核
             * */
            handleAudit(row) {
                warehouseLocation.getAudit({ id: row.id }).then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.auditForm.productName = content.productName;
                        this.auditForm.specificationName = content.specificationName;
                        this.auditForm.creatorName = content.creatorName;
                        if (content.createDateTime) {
                            this.auditForm.createDateTime = util.parseTimeStr(content.createDateTime);
                        }
                        this.auditForm.warehouseLocationMaster = content.warehouseLocationMaster;
                        this.auditForm.warehouseLocationSlave = content.warehouseLocationSlave;
                        this.auditForm.id = content.id;
                        if (content.auditRecord) {
                            this.auditForm.auditRecords = content.auditRecord.split('<br/>').map((item, index) => {
                                if (content.auditRecords[index].auditRemark) {
                                    item += '，' + content.auditRecords[index].auditRemark;
                                }
                                return item;
                            });
                        }
                        this.auditForm.visible = true;
                    }
                });
            },

            handleCancelAudit() {
                this.auditForm = JSON.parse(JSON.stringify(auditForm));
            },

            /**
             * 审核通过
             * */
            handleAuditPass() {
                this.$confirm('确认审核通过？', '二次确认', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    if (!this.auditForm.id) {
                        return this.$notify.error('审核记录不存在');
                    }
                    if (this.auditForm.auditRemark.length > 100) {
                        return this.$notify.error('批语长度不能超过100');
                    }
                    const param = {
                        id: this.auditForm.id,
                        auditRemark: this.auditForm.auditRemark
                    };
                    warehouseLocation.updatePassed(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('操作成功');
                            this.auditForm = JSON.parse(JSON.stringify(auditForm));
                            this.handlePagers();
                        }
                    })
                }).catch(err => {
                    window.LOG(err);
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },

            /**
             * 审核驳回
             * */
            handleAuditReject() {
                if (!this.auditForm.id) {
                    return this.$notify.error('审核记录不存在');
                }
                if (!this.auditForm.auditRemark) {
                    return this.$notify.error('请输入批语');
                }
                if (this.auditForm.auditRemark.length > 100) {
                    return this.$notify.error('批语长度不能超过100');
                }
                const param = {
                    id: this.auditForm.id,
                    auditRemark: this.auditForm.auditRemark
                };
                warehouseLocation.updateRejected(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('操作成功');
                        this.auditForm = JSON.parse(JSON.stringify(auditForm));
                        this.handlePagers();
                    }
                });
            },

            defaultDate() {
                const now = util.getDateSubDays(0);
                this.searchForm.searchDate = [now, now];
            }
        },

        created() {
            this.defaultDate();
            this.handleLoadWarehouse();
            this.handleLoadWorkFlowStatus();
            this.handlePagers();
        }
    }
</script>
