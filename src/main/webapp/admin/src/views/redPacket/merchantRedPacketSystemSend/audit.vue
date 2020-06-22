<template>

    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="红包面额" :label-width="formLabelWidth" prop="amount">
                <el-input v-model="form.amount" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="订单满足多少元可使用（使用条件）:" :label-width="formLabelWidth" prop="amountCondition">
                <el-input v-model="form.amountCondition" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="截止日期" :label-width="formLabelWidth">
                <el-input v-model="form.expiryDate" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="创建人" :label-width="formLabelWidth">
                <el-input v-model="form.creatorName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="创建时间" :label-width="formLabelWidth">
                <el-input v-model="form.createDateTime" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth">
                <el-input v-model="form.remark" type="textarea" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="审核记录" :label-width="formLabelWidth">
                <div v-html="form.auditRecord"></div>
            </el-form-item>
            <el-form-item label="您的批语" :label-width="formLabelWidth">
                <el-input v-model="form.auditRemark" type="textarea" auto-complete="off" maxlength="200"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handlePass" type="success" v-if="G.permission['_acount/merchantRedPacketSystemSend/pass']">审核通过</el-button>
            <el-button @click="handleReject" type="danger" v-if="G.permission['_acount/merchantRedPacketSystemSend/reject']">审核驳回</el-button>
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/redpacket';
export default {
    props: ['form'],
    data() {
        return {
            formLabelWidth: '120px',
            merchantList: [],

            rules: {
                merchantId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                amount: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                amountCondition: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        handlePass() {
            this.$confirm('确定审核通过?').then(() => {
                const param = { id: this.form.id, auditRemark: this.form.auditRemark };
                if (!param.id) {
                    return this.$notify.error('id 异常');
                }
                api.updatePassed(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.$emit('close');
                    }
                })
            }).catch(err => { console.log(err); })
        },
        handleReject() {
            const param = { id: this.form.id, auditRemark: this.form.auditRemark };
            if (!param.id) {
                return this.$notify.error('id 异常');
            }
            if (!param.auditRemark) {
                return this.$notify.error('请输入批语');
            }
            api.updateRejected(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                }
            })
        }
    },
    created() {
        console.log(this.form);
        this.form.auditRecord = '';
        if (this.form.workflowTaskRecords && this.form.workflowTaskRecords.length > 0) {
            this.form.workflowTaskRecords.forEach(item => {
                this.form.auditRecord += `${item.handlerName},${item.status === '1' ? '审核失败' : '审核成功'},批语: ${item.auditRemark}`;
                this.form.auditRecord += '<br/>'
            });
        }
        this.form.auditRemark = '';
    }
}
</script>
