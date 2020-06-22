<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-button style="float: right; margin-right: 30px;"
                               type="success"
                               v-if="G.permission['_account/workOrder/insert']"
                               round
                               @click="handleAddWorkOrder">
                        添加工单
                    </el-button>
                </el-row>
            </el-row>
            <el-row class="list-con clearfix">
                <el-table :data="records" border :loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                    </el-table-column>
                    <el-table-column label="查看工单" v-if="G.permission['_account/workOrder/commentList']">
                        <template slot-scope="scope">
                            <i class="el-icon-edit" @click.stop="handleViewWorkOrder(scope.row)"></i>
                        </template>
                    </el-table-column>
                    <el-table-column label="删除" v-if="G.permission['_account/workOrder/delete']">
                        <template slot-scope="scope">
                            <i class="el-icon-delete" @click.stop="handleRemoveWorkOrder(scope.row)"></i>
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

        <!-- 添加工单 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelAdd" title="详细信息" :visible.sync="insertForm.visible" width="25%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>标题</p>
                    <el-input v-model="insertForm.title"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>内容</p>
                    <el-input v-model="insertForm.content" type="textarea" :rows="5"></el-input>
                </el-col>
                <el-col>
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        :data="insertForm.qiniuForm"
                        :file-list="insertForm.fileList"
                        :on-success="handlePreviewSuccess"
                        :before-upload="handleBeforeUpload">
                        <el-button size="small" type="primary">点击上传</el-button>
                    </el-upload>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancelAdd">取 消</el-button>
                <el-button @click="handleSubmit">确定</el-button>
            </div>
        </el-dialog>

        <!-- 工单详情 -->
        <el-dialog :close-on-click-modal="false" @close="handleCancelAdd" title="详细信息" :visible.sync="detailForm.visible" width="40%">
            <el-row :gutter="20">
                <el-col :span="24" class="mb20">
                    <p>标题</p>
                    <el-input v-model="detailForm.title"></el-input>
                </el-col>
                <el-col :span="24" class="mb20">
                    <p>内容</p>
                    <el-input v-model="detailForm.content" type="textarea" :rows="5"></el-input>
                </el-col>
                <el-row style="margin-left: 5px">
                    附件(点击下载)：<a :href="detailForm.attachment" style="color: #2d8cf0">{{detailForm.fileName}}</a>
                </el-row>
                <el-row style="margin-top: 10px; margin-left: 5px">
                    <span style="color: red">沟通记录</span>：
                </el-row>
                <el-row v-for="(item, key) in detailForm.comments" :key="key">
                    <span style="color: #000">{{item.createDateTime}} {{item.userName}}</span>: 
                    <p style="word-wrap: break-word;">{{item.comment}}</p>
                </el-row>
                <br/>
                <el-col :span="24" class="mb20">
                    <span style="color: red">我要留言</span>:
                    <el-input v-model="detailForm.comment" type="textarea" :rows="5"></el-input>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click.stop="handleSubmitComment" v-if="G.permission['_account/workOrder/insertComment']">提交留言</el-button>
                <el-button @click.stop="handleUpdateStatus" v-if="detailForm.status !== '3' && detailForm.hasPermission">{{detailForm.button}}</el-button>
                <el-button @click.stop="handleCancelView">取消</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>

    import workOrder from '@/api/workOrder';
    import util from '@/libs/util';
    import common from '@/api/common';

    const columns = [
        { key: 'id', title: 'ID' },
        { key: 'title', title: '标题' },
        { key: 'content', title: '内容' },
        { key: 'creatorName', title: '提出人' },
        { key: 'statusCN', title: '状态' },
        { key: 'createDateTime', title: '制单时间' }
    ];

    const insertForm = {
        visible: false,
        title: '',
        content: '',
        qiniuForm: {},
        attachment: '',
        fileName: '',
        fileList: []
    };

    const detailForm = {
        visible: false,
        title: '',
        content: '',
        contents: '',
        attachment: '',
        comment: '',
        hasPermission: false
    };

    const status = {
        0: '已提交',
        1: '已受理',
        2: '已处理',
        3: '已结单'
    };

    export default {

        data() {
            return {
                records: [],
                loading: false,
                currentPage: 1,
                pageSize: 5,
                totalPage: 0,
                columns: columns,

                insertForm: JSON.parse(JSON.stringify(insertForm)),
                detailForm: JSON.parse(JSON.stringify(detailForm))
            }
        },

        methods: {

            handleSizeChange(size) {
                this.pageSize = size;
                this.currentPage = 1;
                this.handlePagers()
            },

            handleCurrentChange(page) {
                this.currentPage = page;
                this.handlePagers();
            },

            handlePagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                workOrder.pagers(param).then(res => {
                    if (res.records) {
                        res.records.forEach(item => {
                            item.createDateTime = util.parseTimeStr(item.createDateTime);
                            if (item.status) {
                                item.statusCN = status[item.status];
                            }
                        });
                    }
                    this.totalPage = res.total;
                    this.records = res.records;
                })
            },

            /**
             * 添加工单
             * */
            handleAddWorkOrder() {
                this.insertForm = JSON.parse(JSON.stringify(insertForm));
                this.insertForm.visible = true;
            },

            handleBeforeUpload(file) {
                const param = {
                    fileName: file.name
                };
                return common.getQiniuToken().then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.insertForm.qiniuForm = {
                            key: param.fileName,
                            token: content.uploadToken,
                            domain: content.domain
                        };
                    } else {
                        this.$notify.error('上传失败： token 获取失败');
                    }
                }).catch(err => {
                    this.$notify.error('上传失败： token 获取失败');
                    window.LOG('上传失败： token 获取失败', err);
                });
            },

            handlePreviewSuccess(data) {
                this.insertForm.fileName = data.key;
                this.insertForm.attachment = this.insertForm.qiniuForm.domain + data.key;
                console.log(data);
                this.insertForm.fileList = [{ name: data.key, url: this.insertForm.attachment }]
            },

            handleCancelAdd() {
                this.insertForm = JSON.parse(JSON.stringify(insertForm));
            },

            handleSubmit() {
                if (!this.insertForm.title) {
                    return this.$notify.error('请输入标题');
                }
                if (!this.insertForm.content) {
                    return this.$notify.error('请输入内容');
                }
                if (this.insertForm.title.length > 50) {
                    return this.$notify.error('标题字数不能超过50个字符');
                }
                if (this.insertForm.content.length > 255) {
                    return this.$notify.error('内容字数不能超过255个字符');
                }
                const param = {
                    title: this.insertForm.title,
                    content: this.insertForm.content,
                    fileName: this.insertForm.fileName,
                    attachment: this.insertForm.attachment
                };
                workOrder.insert(param).then(res => {
                    if (res.code === 100) {
                        this.insertForm = JSON.parse(JSON.stringify(insertForm));
                        this.handlePagers();
                    }
                })
            },

            /**
             * 删除工单
             * */
            handleRemoveWorkOrder(row) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: row.id
                    };
                    workOrder.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '删除成功');
                            this.checkIsLastItemOfLastPage();
                            this.handlePagers();
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

            /**
             * 删除单项后是否需要跳转上一页
             */
            checkIsLastItemOfLastPage() {
                if (this.records.length === 1 && this.currentPage > 1) {
                    this.currentPage = this.currentPage - 1;
                }
            },

            /**
             * 详情
             * */
            handleViewWorkOrder(row) {
                console.log(row);
                workOrder.commentList({ workOrderId: row.id }).then(res => {
                    if (res.code === 100 && res.content) {
                        res.content.forEach(item => {
                            item.createDateTime = util.parseTime(new Date(item.createDateTime));
                        });
                        row.comment = '';
                        this.detailForm = JSON.parse(JSON.stringify(row));
                        this.detailForm.comments = res.content;
                        this.detailForm.attachment = `${this.detailForm.attachment}?attname=${this.detailForm.fileName}`;
                        this.detailForm.visible = true;
                        this.detailForm.comment = '';
                        if (this.detailForm.status === '0') {
                            this.detailForm.button = '受理工单';
                            this.detailForm.hasPermission = Vue.prototype.G.permission['_account/workOrder/update'];
                        } else if (this.detailForm.status === '1') {
                            this.detailForm.button = '处理工单';
                            this.detailForm.hasPermission = Vue.prototype.G.permission['_account/workOrder/update'];
                        } else if (this.detailForm.status === '2') {
                            this.detailForm.button = '完结工单';
                            this.detailForm.hasPermission = Vue.prototype.G.permission['_workOrder/finishWorkOrder'];
                        }
                        console.log(this.detailForm);
                    }
                });
            },

            /**
             * 提交留言
             * */
            handleSubmitComment() {
                if (!this.detailForm.comment) {
                    return this.$notify.error('留言不能为空');
                }
                if (this.detailForm.comment.length > 255) {
                    return this.$notify.error('留言长度不能大于255个字符');
                }
                const param = {
                    workOrderId: this.detailForm.id,
                    comment: this.detailForm.comment
                };
                workOrder.insertComment(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('留言成功');
                        const form = JSON.parse(JSON.stringify(this.detailForm));
                        // this.detailForm = JSON.parse(JSON.stringify(detailForm));
                        this.handleViewWorkOrder(form);
                    }
                })
            },

            /**
             * 更新状态
             * */
            handleUpdateStatus() {
                workOrder.updateStatus({ workOrderId: this.detailForm.id }).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('更新成功');
                        this.detailForm = JSON.parse(JSON.stringify(detailForm));
                        this.handlePagers();
                    }
                });
            },

            handleCancelView() {
                this.detailForm = JSON.parse(JSON.stringify(detailForm));
            }
        },

        created() {
            this.handlePagers();
        }
    }
</script>
