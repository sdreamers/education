<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" ref="ruleForm">
            <el-col :span="12">
                <el-form-item label="商品名称" :label-width="formLabelWidth" prop="productName">
                    <el-input v-model="form.productName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="商品规格名称" :label-width="formLabelWidth" prop="specificationName">
                    <el-input v-model="form.specificationName" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="售价" :label-width="formLabelWidth" prop="price">
                    <el-input v-model="form.price" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="上一次进货价" :label-width="formLabelWidth" prop="lastStockPrice">
                    <el-input v-model="form.lastStockPrice" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="成本价" :label-width="formLabelWidth" prop="cost">
                    <el-input v-model="form.cost" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="库存数量" :label-width="formLabelWidth" prop="quantity">
                    <el-input v-model="form.quantity" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="库存缓冲值" :label-width="formLabelWidth" prop="bufferQuantity">
                    <el-input v-model="form.bufferQuantity" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="上下架状态" :label-width="formLabelWidth" prop="available">
                    <el-input v-model="form.available" type="text" auto-complete="off" readonly></el-input>
                </el-form-item>
            </el-col>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
        </div>
    </el-dialog>
</template>
<script>
export default {
    props: ['formType', 'form'],
    data() {
        return {
            formLabelWidth: '120px'

        };
    }
};
</script>
