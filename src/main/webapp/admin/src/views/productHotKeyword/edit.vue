<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="热搜关键字" :label-width="formLabelWidth" prop="keyword">
                <el-input v-model="form.keyword" type="text" auto-complete="off" maxlength="50"></el-input>
            </el-form-item>
            <el-form-item label="排序号" :label-width="formLabelWidth" prop="sort">
                <el-input v-model="form.sort" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="显示状态" :label-width="formLabelWidth" prop="visibledFlag">
                <el-select v-model="form.visibledFlag" placeholder="请选择品牌">
                    <el-option v-for="(item,key) in statusList" :label="item.name" :value="item.id" :key="key"></el-option>
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
import api from '@/api/productHotKeyword';
export default {
    props: ['formType', 'form'],
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        };
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('必须为大于等于0的正整数'));
        };
        const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        return {
            formLabelWidth: '120px',

            // 状态列表
            statusList: [{ id: '0', name: '否' }, { id: '1', name: '是' }],

            // 七牛表单
            qiniuForm: {},


            rules: {
                keyword: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                sort: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    formIsNaN, formIsInt
                ],
                visibledFlag: [
                    { required: true, message: '不能为空', trigger: 'blur' }
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
                        api.productHotKeywordUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        api.productHotKeywordInsert(param).then(res => {
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
