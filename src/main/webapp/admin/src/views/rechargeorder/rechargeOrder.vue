<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="searchForm.telephone" placeholder="商户注册手机号"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-popover
                            ref="popover"
                            placement="bottom"
                            width="400"
                            trigger="click">
                            <el-checkbox-group v-model="searchForm.paymethodArr">
                                <el-checkbox v-for="(label,key) in payMethodMap" name="type"
                                             :label="key" :key="key">{{label}}</el-checkbox>
                            </el-checkbox-group>
                        </el-popover>
                        <el-input v-model="searchForm.paymentMethodText" v-popover:popover placeholder="支付方式"></el-input>
                    </el-col>
                    <el-col :span="6" label="日期">
                        <el-date-picker
                            v-model="searchForm.dateArr"
                            type="daterange"
                            align="right"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :picker-options="pickerOptions"
                            value-format="yyyy-MM-dd">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">查询</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                    <el-col :span="6">
                        <el-button style="float: right;margin-left: 10px;" @click="handleAdd" type="primary"  v-if="G.permission['_order/rechargeOrder/insert']">后台充值</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span  v-if="column.key === 'orderSource'">{{scope.row[column.key] | dictFilter(sourceMap)}}</span>
                            <span  v-else-if="column.key === 'status'">{{scope.row[column.key] | dictFilter(orderStatusMap)}}</span>
                            <span  v-else-if="column.key === 'paymentMethod'">{{scope.row[column.key] | dictFilter(payMethodMap)}}</span>
                            <span  v-else-if="column.key === 'auditStatus'">{{scope.row[column.key] | dictFilter(auditStatusMap)}}</span>
                            <span v-else>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                v-if="scope.row.auditFlag === true && G.permission['_order/rechargeOrder/getAudit']"
                                type="text"
                                @click.stop="handleAuditor(scope.row.id)">审核
                            </el-button>
                            <!--支付状态：0：取消支付，1：未支付-->
                            <el-button
                                v-if="(scope.row.status ==='0' || scope.row.status ==='1') && G.permission['_order/rechargeOrder/delete']"
                                type="text"
                                @click.stop="handleRemove(scope.row.id)">删除
                            </el-button>
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

        <el-dialog :close-on-click-modal="false" title="填写充值信息" :visible.sync="dialogFormVisible" @close="$refs.ruleForm.resetFields()">
            <el-form :model="form" :rules="rules" ref="ruleForm" label-width="130px">
                <el-form-item label="商户注册手机号" prop="merchantTelephone">
                    <el-input v-model="form.merchantTelephone" type="text" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="充值方式" prop="paymentMethod">
                    <el-select v-model="form.paymentMethod" placeholder="请选择">
                        <el-option
                            v-for="(item,key) in payMethodMap"
                            :label="item"
                            :value="key"
                            :key="key">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="充值金额" prop="payablePrice">
                    <el-input v-model="form.payablePrice" type="text" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="赠送金额" prop="giftAmount">
                    <el-input v-model="form.giftAmount" type="text" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="充值原因" prop="remark">
                    <el-input v-model="form.remark" type="textarea" auto-complete="off" maxlength="255" placeholder="最多只能输入255个字"></el-input>
                </el-form-item>
                <el-form-item label="上传图片" prop="imageURLs">
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        list-type="picture-card"
                        :data="qiniuForm"
                        :on-success="handleDetailSuccess"
                        :file-list="form.imageURLs"
                        :on-remove="handleDetailRemove"
                        :on-preview="handleDetailPreview"
                        :before-upload="handleBeforeUpload">
                        <i class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="充值审核信息" :visible.sync="dialogVisibleAudit" @close="$refs.audit.resetFields()">
            <el-form label-width="130px" ref="audit">
                <el-form-item label="商户注册手机号:">
                    <span>{{auditData.merchantTelephone}}</span>
                </el-form-item>
                <el-form-item label="充值支付类型:">
                    <span>{{auditData.paymentMethod | dictFilter(payMethodMap)}}</span>
                </el-form-item>
                <el-form-item label="充值金额:">
                    <span>{{auditData.payablePrice}}</span>
                </el-form-item>
                <el-form-item label="赠送金额:">
                    <span>{{auditData.giftAmount}}</span>
                </el-form-item>
                <el-form-item label="审核记录:">
                    <span v-html="auditData.auditRecords"></span>
                </el-form-item>
                <el-form-item label="充值原因:" prop="remark">
                    <span v-html="auditData.remark"></span>
                </el-form-item>
                <el-form-item label="批语:" prop="remark">
                    <el-input v-model="auditData.auditRemark" type="textarea" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="图片：">
                    <a :href="item.url" target="_blank" v-for="(item,key) in this.auditIamge" :key="key" style="margin-left:5px;">
                        <div :style="'width:100px;height:100px;display:inline-block;background-image:url(' + item.url + ');background-position:center;background-size:cover;'"></div>
                    </a>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="auditCancel">取 消</el-button>
                <el-button type="success" @click="handlePass" v-if="G.permission['_order/rechargeOrder/pass']">审核通过</el-button>
                <el-button type="warning" @click="handleReject" v-if="G.permission['_order/rechargeOrder/reject']">审核驳回</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>
    import api from '@/api/rechargeOrder';
    import headerList from '@/config/rechargeList';
    import common from '@/components/common';
    import apiCommon from '@/api/common';

    const columns = [
        { key: 'orderSource', title: '充值渠道' },
        { key: 'orderNo', title: '充值单号', width: '160px' },
        { key: 'merchantTelephone', title: '商户手机号', width: '100px' },
        { key: 'merchantName', title: '商户名称（认证）', width: '120px' },
        { key: 'payablePrice', title: '充值金额（元）', width: '120px' },
        { key: 'giftAmount', title: '赠送金额（元）', width: '120px' },
        { key: 'status', title: '支付状态' },
        { key: 'paymentMethod', title: '支付方式' },
        { key: 'tradeNo', title: '支付交易号', width: '90px' },
        { key: 'remark', title: '充值原因' },
        { key: 'paymentTime', title: '支付时间', width: '160px' },
        { key: 'creatorName', title: '创建人' },
        { key: 'initTime', title: '创建时间', width: '160px' },
        { key: 'auditStatus', title: '审核状态' },
        { key: 'auditorName', title: '最后一次审核人', width: '110px' },
        { key: 'auditDateTime', title: '最近一次审核时间', width: '160px' }
    ];
    const form = {
        merchantTelephone: '',
        paymentMethod: '',
        payablePrice: '',
        giftAmount: '',
        remark: '',
        imageURLs: []
    };
    const auditData = {
        auditRemark: ''
    };
    const pickerOptions = {
        shortcuts: [{
            text: '最近一周',
            onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                window.LOG(start);
                picker.$emit('pick', [start, end]);
            }
        }, {
            text: '最近一个月',
            onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                picker.$emit('pick', [start, end]);
            }
        }]
    };
    export default {
        components: {
            common
        },
        data() {
            return {
                headerList: headerList,


                searchForm: {
                    telephone: '',
                    paymentMethodText: '',
                    dateArr: [new Date().customFormat('#YYYY#-#MM#-#DD#'), new Date().customFormat('#YYYY#-#MM#-#DD#')],
                    paymethodArr: []
                },

                auditData: auditData,

                tableData: [],
                columns: columns,
                qiniuForm: {},
                auditIamge: '',
                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                loading: false,
                pickerOptions: pickerOptions,

                payMethodMap: {},
                rechargeOrderMap: {},
                sourceMap: {},
                orderStatusMap: {},
                auditStatusMap: {},

                dialogFormVisible: false,
                dialogVisibleAudit: false,
                form: form,
                rules: {
                    merchantTelephone: [
                        { required: true, message: '请输入商户注册手机号', trigger: 'blur' },
                        { min: 11, max: 11, message: '请输入正确手机号', trigger: 'blur' }
                    ],
                    paymentMethod: [
                        { required: true, message: '请选择充支付方式', trigger: 'change' }
                    ],
                    payablePrice: [
                        { required: true, message: '请输入充值金额', trigger: 'blur' }
                    ]
                }
            };
        },
        methods: {
            handleSearch() {
                this.currentPage = 1;
                this.reloadPagerDate();
            },
            handleClear() {
                this.searchForm.telephone = '';
                this.searchForm.paymentMethodText = '';
                this.searchForm.dateArr = [];
                this.searchForm.paymethodArr = [];
                this.currentPage = 1;
                this.reloadPagerDate();
            },
            handleAuditor(id) {
                this.auditData = auditData;
                this.getAudit(id);
                this.dialogVisibleAudit = true;
            },
            handleRemove(id) {
                this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: id
                    };
                    api.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.reloadPagerDate();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            handleAdd() {
                this.form = JSON.parse(JSON.stringify(form));
                this.dialogFormVisible = true;
            },
            // 获取相关字典
            getMap() {
                if (!this.G.permission['_common/dict/rechargeOrderMap']) {
                    return this.$notify.error('没有字典权限');
                }
                api.getMap().then(res => {
                    this.payMethodMap = res.content.payMethodMap;
                    this.sourceMap = res.content.rechargeOrderSource;
                    this.orderStatusMap = res.content.rechargeOrderStatus;
                    this.auditStatusMap = res.content.rechargeOrderAuditStatus;
                    this.payMethodMap.weixin = '微信支付';
                    this.payMethodMap.ali = '支付宝支付';
                });
            },
            // 获充值审核信息
            getAudit(id) {
                const params = {
                    id: id
                };
                api.getAudit(params).then(res => {
                    res.content.auditRecords = res.content.auditRecords && res.content.auditRecords.length ? res.content.auditRecords.map(item => item.auditRemark) : '';
                    res.content.auditRecord = res.content.auditRecord && res.content.auditRecord.split('<br/>') || '';
                    if (res.content.auditRecords.length) {
                        res.content.auditRecords = res.content.auditRecords.map((item, index) => {
                            item = '<p style="font-weight:bold">' + item + '</p>' + res.content.auditRecord[index];
                            return item;
                        }).join('<br>')
                    }
                    console.log(res.content.auditRecords);
                    this.auditData = res.content;
                    this.auditIamge = res.content.imageURLs ?
                            res.content.imageURLs.split(',').map(item => ({ name: item, url: item })) : [];
                });
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.pagersData();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.pagersData();
            },
            // 翻页获取列表数据
            pagersData() {
                if (!this.G.permission['_order/rechargeOrder/pages']) {
                    return this.$notify.error('没有列表权限');
                }
                const param = {
                    page: 1,
                    limit: 5
                };
                if (this.searchForm.telephone !== '') {
                    param.merchantTelephone = this.searchForm.telephone;
                }
                if (this.searchForm.paymethodArr) {
                    param.payMethods = this.searchForm.paymethodArr.join(',');
                }
                if (this.searchForm.dateArr) {
                    param.startTime = this.searchForm.dateArr[0];
                    param.endTime = this.searchForm.dateArr[1];
                }
                param.page = this.currentPage;
                param.limit = this.pageSize;
                api.pagers(param).then(res => {
                    window.LOG(res);

                    this.tableData = res.records;
                    this.totalPage = res.total;
                });
            },
            // 查询、清空查询条件获取列表数据
            reloadPagerDate() {
                const param = {
                    page: 1,
                    limit: this.pageSize
                };
                if (this.searchForm.telephone !== '') {
                    param.merchantTelephone = this.searchForm.telephone;
                }
                if (this.searchForm.paymethodArr) {
                    param.payMethods = this.searchForm.paymethodArr.join(',');
                }
                if (this.searchForm.dateArr) {
                    param.startTime = this.searchForm.dateArr[0];
                    param.endTime = this.searchForm.dateArr[1];
                }
                api.pagers(param).then(res => {
                    this.tableData = res.records;
                    this.totalPage = res.total;
                });
            },

           // 详情图片上传到七牛云成功
            handleDetailSuccess(data) {
                const fileName = this.qiniuForm.domain + data.key;
                let imageURLs = this.form.imageURLs;
                if (!imageURLs) {
                    imageURLs = [];
                }
                imageURLs.push({ name: data.key, url: fileName });
                window.LOG(imageURLs);
                this.$set(this.form, 'imageURLs', imageURLs);
            },
            // 移除详情图
            handleDetailRemove(data) {
                const url = data.url;
                const imageURLs = this.form.imageURLs.filter(item => item.url !== url);
                this.$set(this.form, 'imageURLs', imageURLs);
            },
            handleDetailPreview(data) {
                window.open(data.url);
            },
            // 图片上传到七牛云之前
            handleBeforeUpload() {
                if (this.form.imageURLs.length === 3) {
                    this.$notify.error('最多上传3张图片');
                    return;
                }
                const param = {
                // fileName: file.name
                    fileName: new Date().getTime() + '_' + Math.random().toString(32).substring(2)
                };
                return apiCommon.getQiniuTokenTemp()
                .then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.qiniuForm = {
                            key: param.fileName,
                            token: content.uploadToken,
                            domain: content.domain
                        };
                    } else {
                        this.$notify.error('图片上传失败： token 获取失败');
                    }
                })
                .catch(err => {
                    this.$notify.error('图片上传失败： token 获取失败');
                    window.LOG('图片上传失败： token 获取失败', err);
                });
            },

            // 取消表单
            handleCancel() {
                this.form = JSON.parse(JSON.stringify(form));
                this.dialogFormVisible = false;
                this.$refs.ruleForm.resetFields();
            },
            // 提交表单
            handleSubmit() {
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        const param = this.form;
                        console.log(this.form.imageURLs)
                        param.imageURLs = this.form.imageURLs.map(item => item.name).join(',');
                        api.insert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.reloadPagerDate();
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else {
                        this.$notify({
                            message: '请按要求填写表单',
                            type: 'warning'
                        });
                        return false;
                    }
                });
            },
            handlePass() {
                if (this.auditData.auditRemark && this.auditData.auditRemark.length > 255) {
                    return this.$notify.error('审核批语不能超过255个字符');
                }
                const params = {
                    id: this.auditData.id,
                    auditRemark: this.auditData.auditRemark
                };
                api.pass(params).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogVisibleAudit = false;
                        this.reloadPagerDate();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            handleReject() {
                if (this.auditData.auditRemark && this.auditData.auditRemark.length > 255) {
                    return this.$notify.error('审核批语不能超过255个字符');
                }
                const params = {
                    id: this.auditData.id,
                    auditRemark: this.auditData.auditRemark
                };
                api.reject(params).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogVisibleAudit = false;
                        this.reloadPagerDate();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            auditCancel() {
                // this.auditData = JSON.parse(JSON.stringify(auditData));
                this.auditData = auditData;
                this.dialogVisibleAudit = false;
                this.$refs.audit.resetFields();
            }
        },
        created() {
            this.getMap();
            this.pagersData();
        },
        watch: {
            'searchForm.paymethodArr': function(curVal) {
                const arr = [];
                for (let i = 0; i < curVal.length; i++) {
                    arr.push(this.payMethodMap[curVal[i]]);
                }
                this.searchForm.paymentMethodText = arr.toString();
            }
        }
    };
</script>

<style scoped>

</style>
