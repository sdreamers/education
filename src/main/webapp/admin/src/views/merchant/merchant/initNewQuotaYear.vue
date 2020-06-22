<template>
    <el-dialog :close-on-click-modal="false" title="修改额度" :visible="dialogVisible" width="20%" @close="$emit('close')">
        <el-form :model="form" label-position="left" ref="ruleForm" label-width="70px">
            <el-form-item label="开始日期">
                <el-date-picker
                    v-model="form.startDate"
                    @change="orderPagers"
                    type="date"
                    align="right"
                    value-format="yyyy-MM-dd"
                    unlink-panels
                    range-separator="至">
                </el-date-picker>
            </el-form-item>
            <el-form-item label="年份" label-width="70px">
                <el-input v-model="form.quotaYear" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import api from '@/api/quotaYear';

    export default {
        props: ['dialogVisible', 'formLabelWidth', 'form'],
        data() {
            return {
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }]
                }
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                const param = {
                    startDate: this.form.startDate,
                    quotaYear: this.form.quotaYear
                };
                if (!param.startDate) {
                    return this.$notify.error('请选择开始日期');
                }
                if (!param.quotaYear) {
                    return this.$notify.error('请输入年份');
                }
                api.initNewQuotaYear(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success('新建成功');
                        this.$emit('close');
                    }
                });
            }
        }
    };
</script>
