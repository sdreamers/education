<template>
    <el-dialog :close-on-click-modal="false" title="减少库存" :visible="true" @close="$emit('close')">
        <el-form :model="formWarehousing" :rules="rules" ref="ruleForm">
            <el-form-item label="规格名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formWarehousing.specificationName" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="现库存" :label-width="formLabelWidth" prop="name">
                <el-input v-model="formWarehousing.quantity" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="类型" :label-width="formLabelWidth" prop="name">
                <el-select v-model="formWarehousing.type" placeholder="请选择分组">
                    <el-option v-for="(item,key) in wareHousesingListDesc" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="出库数量" :label-width="formLabelWidth" prop="num">
                <el-input v-model="formWarehousing.num" type="text" auto-complete="off" maxlength="8"></el-input>
            </el-form-item>
            <el-form-item label="备注" :label-width="formLabelWidth">
                <el-input v-model="formWarehousing.comment" type="text" auto-complete="off" maxlength="100"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitWarehousing">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

import api from '@/api/productMaintain';
const wareHousesingListDesc = [
    { id: 'out_inventory_loss', name: '盘亏' }
];
export default {
    props: ['formWarehousing', 'warehouseId', 'formLabelWidth'],
    data() {
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        };
        const validateInt = (rule, value, callback) => {
            if (!Number.isNaN(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('必须为大于等于0的正整数'));
        };
        const formRequire = { required: true, message: '不能为空', trigger: 'blur' };
        const formIsNaN = { validator: validateNaN, trigger: 'blur' };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        return {
            wareHousesingListDesc: wareHousesingListDesc,
            rules: {
                num: [formRequire, formIsNaN, formIsInt]
            }
        };
    },
    methods: {

        // 提交表单
        handleSubmitWarehousing() {
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = {
                        type: '2',
                        warehouseId: this.$route.params.id,
                        subType: this.formWarehousing.type,
                        productList: [{
                            productBasicInfoId: this.formWarehousing.productBasicInfoId,
                            productBasicSpecificationId: this.formWarehousing.productBasicSpecificationId,
                            productSpecificationId: this.formWarehousing.productSpecificationId,
                            num: this.formWarehousing.num,
                            comment: this.formWarehousing.comment,
                            cost: this.formWarehousing.cost
                        }]
                    };
                    api.adjustStockForInStock(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.$emit('close');
                        }
                    });
                }
            })
        }
    }
};
</script>
