<template>
<div class="view-content">
    <el-row :gutter="10">
            <el-card class="box-card" style="margin-bottom: 10px;">
                    <span></i>hello</span>
            </el-card>
    </el-row>
</div>
</template>
<script>
    // import { appRouter } from '@/router/router';
    export default {
        name: 'home',
        created() {
            this.$router.push({ name: '/account/merchant/index' })
        }
    }
</script>
