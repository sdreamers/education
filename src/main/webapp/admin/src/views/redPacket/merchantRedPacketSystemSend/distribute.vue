<template>
    
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="派发关联的商户注册手机号" :label-width="formLabelWidth" prop="merchantId">
                <!-- <el-input v-model="form.merchantId" type="text" auto-complete="off"></el-input> -->
                <el-select
                    v-model="form.merchantId"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="请输入手机号并在下拉框中选择"
                    :remote-method="handleAddingSearch"
                    @change="clickOpts">
                    <el-option
                    v-for="item in merchantList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="红包面额" :label-width="formLabelWidth" prop="amount">
                <el-input v-model="form.amount" type="text" auto-complete="off" maxlength="9"></el-input>
            </el-form-item>
            <el-form-item label="使用条件" :label-width="formLabelWidth" prop="amountCondition">
                <el-input v-model="form.amountCondition" type="text" auto-complete="off" maxlength="9"></el-input>
            </el-form-item>
            <el-form-item label="截止日期" :label-width="formLabelWidth" v-show="!form.expiryDateFlag">
                <el-date-picker
                    v-model="form.expiryDate"
                    type="date"
                    align="right"
                    value-format="yyyy-MM-dd"
                    placeholder="截止时间">
                </el-date-picker>
            </el-form-item>
            <el-form-item label="永久有效" :label-width="formLabelWidth">
                <el-checkbox v-model="form.expiryDateFlag" @change="handleFlagChange">永久有效</el-checkbox>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth">
                <el-input v-model="form.remark" type="textarea" auto-complete="off" maxlength="200"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleSubmit">提交</el-button>
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/redpacket';
export default {
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
            },
            form: {
                merchantId: '',
                amount: '',
                amountCondition: '',
                expiryDate: '',
                expiryDateFlag: '',
                remark: ''
            }
        }
    },
    methods: {
        handleAddingSearch(data) {
            this.form.merchantId = '';
            this.form.merchantTelephone = '';
            if (!data) {
                return;
            }
            api.merchantListByKeyword({ name: data }).then(res => {
                if (res.code === 100 && res.content) {
                    this.merchantList = JSON.parse(res.content);
                }
            })
        },
        handleFlagChange(data) {
            if (data) {
                this.form.expiryDate = '';
            }
        },
        clickOpts(data) {
            console.log(data);
            const that = this;
            const merchantId = this.form.merchantId;
            this.merchantList.forEach(item => {
                if (item.id === merchantId) {
                    that.form.merchantTelephone = item.telephone;
                }
            })
        },
        handleSubmit() {
            console.log(JSON.parse(JSON.stringify(this.form)));
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = this.form;
                    if (param.expiryDateFlag) {
                        param.expiryDateFlag = 1;
                    } else {
                        param.expiryDateFlag = 0;
                        if (!param.expiryDate) {
                            return this.$notify.error('请填写截止日期');
                        }
                    }
                    api.merchantRedPacketSystemSendInsert(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.$emit('close');
                        }
                    })
                } else {
                    this.$notify({
                        message: '请按要求填写表单',
                        type: 'warning'
                    });
                    return false;
                }
            });
        }
    }
}
</script>
