<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 团购" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="团购代码" :label-width="formLabelWidth" prop="code">
                <el-input v-model="form.code" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="团购时间" :label-width="formLabelWidth" prop="date">
                <el-date-picker
                    v-model="form.date"
                    type="datetimerange"
                    align="right"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :default-time="['00:00:00','23:59:59']">
                </el-date-picker>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/groupBuying';
import Util from '@/libs/util';

export default {
    props: ['dialogFormVisible', 'formLabelWidth', 'form', 'formType'],
    data() {
        return {
            provinceList: [],
            cityList: [],
            rules: {
                code: [{ required: true, message: '不能为空', trigger: 'blur' }],
                date: [{ required: true, message: '不能为空', trigger: 'blur' }]
            }
        };
    },
    methods: {
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        param.startTime = this.form.date[0];
                        param.endTime = this.form.date[1];
                        window.LOG(param);
                        api.groupBuyingUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        param.startTime = this.form.date[0];
                        param.endTime = this.form.date[1];
                        param.cityId = Util.getCookie('cityId');
                        api.groupBuyingInsert(param).then(res => {
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
    }
};
</script>
