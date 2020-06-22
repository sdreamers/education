<template>
    <el-dialog class="dialogw80" :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" ref="ruleForm">
            <el-col :span="12">
                <el-form-item label="商户名称" :label-width="formLabelWidth">
                    <el-input v-model="form.merchantName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="手机号" :label-width="formLabelWidth">
                    <el-input v-model="form.telephone" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="商户认证状态" :label-width="formLabelWidth">
                    <el-input v-model="form.qualificationStatus" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="是否开通货到付款" :label-width="formLabelWidth">
                    <el-input v-model="form.codPrivilage" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="是否被加入黑名单" :label-width="formLabelWidth">
                    <el-input v-model="form.disabled" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="注册时间" :label-width="formLabelWidth">
                    <el-input v-model="form.registerTime" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">关闭</el-button>
        </div>
    </el-dialog>
</template>
<script>
export default {
    props: ['form'],
    data() {
        return {
            formLabelWidth: '120px',

            // 状态列表
            statusList: [{ id: '0', name: '否' }, { id: '1', name: '是' }]
        };
    }
};
</script>
