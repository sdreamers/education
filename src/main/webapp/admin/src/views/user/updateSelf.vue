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
                    <el-form-item label="昵称：" prop="nickname">
                        <el-input type="text" v-model="userInfo.nickname" maxlength="8"></el-input>
                    </el-form-item>
                    <el-form-item label="Email/帐号：">
                        <el-input type="text" v-model="userInfo.email" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="手机号" prop="telephone">
                        <el-input type="text" v-model="userInfo.telephone" maxlength="11"></el-input>
                    </el-form-item>
                </el-form>

            </el-row>
        </el-card>
    </section>
</template>

<script>
import api from '@/api/user';
import regx from '@/libs/regx';

export default {
    data() {
        return {
            userInfo: {
                nickname: '',
                email: '',
                telephone: ''
            },
            rules: {
                nickname: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                telephone: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: regx.checkMobile, trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
            // 获取数据
        setUserInfo() {
            this.userInfo = this.G.userInfo;
        },
        handleSubmit() {
            // console.log(this.userInfo);
            const param = {
                id: this.userInfo.id,
                nickname: this.userInfo.nickname,
                telephone: this.userInfo.telephone
            }
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    console.log(param);
                    api.updateSelf(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            setTimeout(() => {
                                location.reload();
                            }, 2000);
                        }
                    })
                }
            })
        }
    },

    created() {
        console.log(this.userInfo);
        this.setUserInfo();
    }

};
</script>
