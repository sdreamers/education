<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-table :data="records" border :loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleViewDetail(scope.row)">详情
                            </el-button>
                            <el-button
                                type="text"
                                v-if="G.permission['_account/member/changeSessionStatus']"
                                @click.stop="handleChangeStatus(scope.row)">
                                {{scope.sessionStatus ? '激活' : '踢出'}}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
        </el-card>

        <el-dialog :close-on-click-modal="false" title="详细信息" :visible.sync="visible" width="30%">
            <el-row :gutter="20">
                <el-col :span="12" class="mb20">
                    <p>Session ID</p>
                    <el-input readonly :value="formInfo.sessionId"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Session创建时间</p>
                    <el-input readonly :value="formInfo.startTime"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Session最后交互时间</p>
                    <el-input readonly :value="formInfo.lastAccess"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Session状态</p>
                    <el-input readonly :value="formInfo.sessionStatus"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Session Host</p>
                    <el-input readonly :value="formInfo.host"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Session timeout</p>
                    <el-input readonly :value="formInfo.timeout"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>姓名</p>
                    <el-input readonly :value="formInfo.nickname"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>Email/账号</p>
                    <el-input readonly :value="formInfo.email"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>创建时间</p>
                    <el-input readonly :value="formInfo.createTime"></el-input>
                </el-col>
                <el-col :span="12" class="mb20">
                    <p>最后登录时间</p>
                    <el-input readonly :value="formInfo.lastLoginTime"></el-input>
                </el-col>
            </el-row>
            <div slot="footer" class="dialog-footer">
                <el-button @click="visible=false;formInfo={}">取 消</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>
    import member from '@/api/member';
    import util from '@/libs/util';

    const columns = [
        { key: 'sessionId', title: 'SessionID' },
        { key: 'nickname', title: '姓名' },
        { key: 'email', title: 'Email/账号' },
        { key: 'startTime', title: '创建会话时间' },
        { key: 'lastAccess', title: '会话最后活动时间' },
        { key: 'status', title: '状态' }
    ]

    export default {

        data() {
            return {
                records: [],
                loading: false,
                columns: columns,

                formInfo: {},
                visible: false,

                // 无用字段
                sessionStatus: ''
            }
        },

        methods: {
            handlePagers() {
                member.memberOnline().then(res => {
                    if (res.code === 100 && res.content) {
                        res.content.forEach(item => {
                            item.startTime = util.parseTime(new Date(item.startTime));
                            item.lastAccess = util.parseTime(new Date(item.lastAccess));
                            item.createTime = util.parseTime(new Date(item.createTime));
                            item.lastLoginTime = util.parseTime(new Date(item.lastLoginTime));
                            item.status = item.sessionStatus ? '有效' : '已踢出';
                            item.timeout = `${item.timeout}(毫秒) = ${item.timeout / 1000}(秒) = ${item.timeout / 1000 / 60}(分钟)`;
                        });
                        this.records = res.content;
                    }
                });
            },

            handleViewDetail(row) {
                this.formInfo = row;
                this.visible = true;
            },

            /**
             * 踢出或者激活
             * */
            handleChangeStatus(row) {
                const status = row.sessionStatus ? '0' : '1';
                member.memberChangeSessionStatus({ status: status, sessionIds: row.sessionId }).then(res => {
                    if (res.status === 200) {
                        this.handlePagers();
                    }
                });
            }
        },

        created() {
            this.handlePagers();
        }
    }

</script>
