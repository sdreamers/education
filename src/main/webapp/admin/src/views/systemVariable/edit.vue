<template>
    <el-dialog :close-on-click-modal="false" title="填写环境变量" :visible="true" @close="$emit('close')">
        <el-form :model="Form" :rules="rules" ref="ruleForm">
            <el-form-item label="key" :label-width="formLabelWidth" prop="key">
                <el-input v-model="Form.key" type="text" auto-complete="off" maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="value" :label-width="formLabelWidth" prop="value">
                <el-input v-model="Form.value" type="text" auto-complete="off" maxlength="1024"></el-input>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
                <el-input v-model="Form.remark" type="text" auto-complete="off" maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="作用域" :label-width="formLabelWidth" prop="scope">
                <el-checkbox-group v-model="Form.scope" @change="handleScopeChange">
                    <el-checkbox :label="item" v-for="(item, key) in scopeMap" :key="key"></el-checkbox>
                    <!-- <el-checkbox label="APP"></el-checkbox>
                    <el-checkbox label="服务端"></el-checkbox> -->
                </el-checkbox-group>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/systemVariable';
const scopeMap = {
    'service,app': '所有',
    service: '服务端',
    app: 'APP'
}
const scopeMapping = {
    所有: 'service,app',
    服务端: 'service',
    APP: 'app'
}
export default {
    props: ['form', 'formType'],
    data() {
        return {
            formLabelWidth: '120px',

            Form: {},

            scopeMap: scopeMap,

            rules: {
                key: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleScopeChange(data) {
            if (data[0] !== '所有') {
                if (data.includes('所有')) {
                    data = ['所有'];
                    this.Form.scope = ['所有'];
                }
            } else {
                if (data.includes('所有') && data.length > 1) {
                    data = data.filter(item => item !== '所有');
                    this.Form.scope = data;
                }
            }
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            const form = JSON.parse(JSON.stringify(this.Form));
            form.scope = form.scope.map(item => scopeMapping[item]).join(',');
            if (!form.scope) {
                return this.$notify.error('作用域不能为空');
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = form;
                        api.systemVariableUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = form;
                        api.systemVariableInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
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
    },
    created() {
        const form = JSON.parse(JSON.stringify(this.form));
        form.scope = form.scope.length ? form.scope.split(',').map(item => scopeMap[item]) : [];
        this.Form = JSON.parse(JSON.stringify(form));
    }
};
</script>
