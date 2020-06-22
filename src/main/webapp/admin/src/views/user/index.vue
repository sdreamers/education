<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row>
                <el-form ref="form" :model="form" label-width="120px">
                    <el-form-item label="昵称：">
                        {{userInfo.nickname}}
                    </el-form-item>
                    <el-form-item label="Email/帐号：">
                        {{userInfo.email}}
                    </el-form-item>
                    <el-form-item label="创建时间：">
                        {{userInfo.createTime | formatTime}}
                    </el-form-item>
                    <el-form-item label="最后登录时间：">
                        {{userInfo.lastLoginTime | formatTime}}
                    </el-form-item>
                </el-form>

            </el-row>
        </el-card>
    </section>
</template>

<script>
import Util from '@/libs/util';

export default {
    data() {
        return {
            userInfo: {}
        };
    },
    filters: {
        formatTime(data) {
            return Util.formatTime(new Date(data))
        }
    },
    methods: {
            // 获取数据
        setUserInfo() {
            this.userInfo = this.G.userInfo;
        }
    },

    mounted() {
        this.setUserInfo();
    }

};
</script>
