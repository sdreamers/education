<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 活动" :visible="true" @close="$emit('close')">
        <el-form :model="formActivity" :rules="rules" ref="ruleForm">
            <el-form-item label="活动单价" :label-width="formLabelWidth">
                <el-input v-model="formActivity.activityPrice" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="活动库存" :label-width="formLabelWidth">
                <el-input v-model="formActivity.stockNum" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitActivity">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
import api from '@/api/activity';
export default {
    props: ['dialogActivityVisible', 'formActivity', 'formLabelWidth', 'rules'],
    methods: {

        // 修改活动商品的单价和库存
        handleSubmitActivity() {
            const param = {
                id: this.formActivity.id,
                activityPrice: this.formActivity.activityPrice,
                stockNum: this.formActivity.stockNum
            };
            if (param.activityPrice === '' || param.activityPrice < 0 || isNaN(param.activityPrice)) {
                return this.$notify.error('价格不合法');
            }
            if (param.activityPrice > 999999999.99) {
                this.$notify.error('活动单价不能大于 999999999.99');
                return;
            }
            if (param.stockNum === '' || param.stockNum < 0 || isNaN(param.stockNum)) {
                return this.$notify.error('库存不合法');
            }
            console.log(param.stockNum)
            if (Math.floor(Number(param.stockNum)) !== Number(param.stockNum)) {
                return this.$notify.error('库存必须为正整数');
            }
            api.activityProductUpdate(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        }
    }
};
</script>
