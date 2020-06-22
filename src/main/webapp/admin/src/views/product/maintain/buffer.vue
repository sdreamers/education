<template>
    <el-dialog :close-on-click-modal="false" title="修改缓冲值" :visible="true" @close="$emit('close')">
        <el-form :model="formBuffer" >
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formBuffer.specificationName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="缓冲值" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formBuffer.bufferQuantity" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitBuffer">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/productMaintain';
import Util from '@/libs/util';
export default {
    props: ['formBuffer', 'formLabelWidth', 'warehouseId'],
    methods: {
        handleSubmitBuffer() {
            if (isNaN(Number(this.formBuffer.bufferQuantity))) {
                return this.$notify.error('请输入数字')
            }
            const param = {
                specificationId: this.formBuffer.productSpecificationId,
                cityId: Util.getCookie('cityId'),
                bufferQuantity: this.formBuffer.bufferQuantity
            };
            if (!param.bufferQuantity) {
                this.$notify.error('缓冲值不能为空');
                return;
            }
            if (parseFloat(param.bufferQuantity) < 0) {
                this.$notify.error('缓冲值不能小于0');
                return;
            }
            if (parseFloat(param.bufferQuantity) > 999999999) {
                this.$notify.error('缓冲值不能大于999999999');
                return;
            }
            api.updateBufferQuantity(param).then(res => {
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
