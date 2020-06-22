<template>
    <el-dialog :close-on-click-modal="false" title="修改售价" :visible="true" @close="$emit('close')">
        <el-form :model="formPrice" >
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formPrice.specificationName" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="当前售价" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formPrice.price" type="text" auto-complete="off" readonly></el-input>
            </el-form-item>
            <el-form-item label="新售价" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formPrice.newPrice" type="text" auto-complete="off"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitPrice">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/productMaintain';
export default {
    props: ['formPrice', 'formLabelWidth', 'warehouseId'],
    methods: {
        handleSubmitPrice() {
            const param = {
                warehouseId: this.warehouseId,
                productList: [{
                    newPrice: this.formPrice.newPrice,
                    oldPrice: this.formPrice.price,
                    productSpecificationId: this.formPrice.productSpecificationId
                }]
            };
            api.priceNoteAdd(param).then(res => {
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
