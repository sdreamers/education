<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 会员等级" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="等级名称" :label-width="formLabelWidth" prop="levelName">
                <el-input v-model="form.levelName" type="text" maxlength="20" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="所需经验" :label-width="formLabelWidth" prop="experience">
                <el-input v-model="form.experience" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="折扣率" :label-width="formLabelWidth" prop="discount">
                <el-input v-model="form.discount" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/vipLevelRule';

const fieldMap = { experience: '经验值', discount: '折扣率' };
export default {
    props: ['formType', 'form'],
    data() {
        // const validateNaN = (rule, value, callback) => {
        //     if (isNaN(value)) {
        //         callback(new Error('请输入数字'));
        //     }
        //     callback();
        // };
        // const validateInt = (rule, value, callback) => {
        //     if (Number.isInteger(Number(value)) && Number(value) >= 0) {
        //         callback();
        //     }
        //     callback(new Error('必须为大于等于0的正整数'));
        // };
        // const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        // const formIsInt = { validator: validateInt, trigger: 'blur' };
        const validateNumber = (rule, value, callback) => {
            if (!/^\d{1,9}(\.\d{1,3})?$/.test(value)) {
                callback(new Error(`请输入正确的${fieldMap[rule.field]}`));
            } else {
                callback();
            }
        };
        return {
            formLabelWidth: '120px',

            // 状态列表
            statusList: [],

            // 七牛表单
            qiniuForm: {},


            rules: {
                levelName: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                experience: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: validateNumber, trigger: 'blur' }
                ],
                discount: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: validateNumber, trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        api.vipLevelRuleUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        api.vipLevelRuleInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
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
