<template>
    <el-dialog :close-on-click-modal="false" title="填写流程信息" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="本次创建流程" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.name" placeholder="请选择" :disabled="formType === 'edit'">
                    <el-option
                    v-for="(item,key) in workflowCodesMap"
                    :key="key"
                    :label="item"
                    :value="key">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="激活启用" :label-width="formLabelWidth" prop="useFlag">
                <el-select v-model="form.useFlag" placeholder="请选择">
                    <el-option
                    v-for="(item,key) in yesNoMap"
                    :key="key"
                    :label="item"
                    :value="key">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="业务内容" :label-width="formLabelWidth" prop="smsProjectContent">
                <el-input v-model="form.smsProjectContent" type="text" auto-complete="off" maxlength="30"></el-input>
            </el-form-item>
            <el-form-item label="发送短信给处理人" :label-width="formLabelWidth" prop="sendSmsFlag">
                <el-select v-model="form.sendSmsFlag" placeholder="请选择">
                    <el-option
                    v-for="(item,key) in yesNoMap"
                    :key="key"
                    :label="item"
                    :value="key">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="发送平台消息给处理人" :label-width="formLabelWidth" prop="sendPlatformMsgFlag">
                <el-select v-model="form.sendPlatformMsgFlag" placeholder="请选择">
                    <el-option
                    v-for="(item,key) in yesNoMap"
                    :key="key"
                    :label="item"
                    :value="key">
                    </el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/workflow';
export default {
    props: ['form', 'formType', 'workflowCodesMap', 'yesNoMap'],
    data() {
        return {
            formLabelWidth: '160px',

            qiniuForm: {},

            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                smsProjectContent: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            const form = JSON.parse(JSON.stringify(this.form));
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = form;
                        api.workflowUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    } else if (formType === 'add') {
                        form.code = form.name;
                        form.name = this.workflowCodesMap[form.name];
                        const param = form;
                        api.workflowInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    }
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
};
</script>
