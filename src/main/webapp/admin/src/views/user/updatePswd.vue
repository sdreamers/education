<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleSubmit">提交</el-button>
            </div>
            <el-row>
                <el-form ref="ruleForm" :model="userInfo" :rules="rules" label-width="120px">
                    <el-form-item label="原密码" prop="pswd">
                        <el-input type="text" v-model="userInfo.pswd" maxlength="20" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPswd">
                        <el-input type="text" v-model="userInfo.newPswd" maxlength="20" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="reNewPswd">
                        <el-input type="text" v-model="userInfo.reNewPswd" maxlength="20" show-password></el-input>
                    </el-form-item>
                </el-form>

            </el-row>
        </el-card>
    </section>
</template>

<script>
import userApi from '@/api/user';

export default {
    data() {
        return {
            userInfo: {
                pswd: '',
                newPswd: '',
                reNewPswd: ''
            },
            rules: {
                pswd: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                newPswd: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                reNewPswd: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSubmit() {
            // console.log(this.userInfo);
            const param = {
                oldPassword: this.userInfo.pswd,
                newPassword: this.userInfo.newPswd,
                reNewPassword: this.userInfo.reNewPswd
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (param.newPswd !== param.reNewPswd) {
                        return this.$notify.error('两次输入的新密码不一致');
                    }
                    console.log(param);
                    userApi.updatePassword(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            setTimeout(() => {
                                location.reload();
                            }, 1000);
                        }
                    })
                }
            })
        }
    }
};
</script>
