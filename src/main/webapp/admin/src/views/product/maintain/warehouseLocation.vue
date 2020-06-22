<template>

    <el-dialog :close-on-click-modal="false" title="仓位设置" :visible="true" @close="$emit('close')">
        <el-form :model="formWarehouseLocation" >
            <el-form-item label="主仓（如：A-16-0）" label-width="200px">
                <el-col :span="4">
                    <el-input v-model="masterObj.a" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                </el-col>
                <el-col :span="2" style="text-align:center">-</el-col>
                <el-col :span="4">
                    <el-input v-model="masterObj.b" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                </el-col>
                <el-col :span="2" style="text-align:center">-</el-col>
                <el-col :span="4">
                    <el-input v-model="masterObj.c" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                </el-col>
            </el-form-item>
            <el-form-item label="备仓" label-width="200px">
                <div v-for="(item,index) in slaveList">
                    <el-row>
                    <el-col :span="4">
                        <el-input v-model="item.a" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                    </el-col>
                    <el-col :span="2" style="text-align:center">-</el-col>
                    <el-col :span="4">
                        <el-input v-model="item.b" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                    </el-col>
                    <el-col :span="2" style="text-align:center">-</el-col>
                    <el-col :span="4">
                        <el-input v-model="item.c" type="text" auto-complete="off" oninput="if(value.length >2)value = value.slice(0, 2)"></el-input>
                    </el-col>
                    <el-col :span="4" v-if="index > 0" style="margin-left: 10px;">
                        <el-button type="primary" @click="handleRemove(item,index)">移除</el-button>
                    </el-col>
                    </el-row>
                </div>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button type="warning" @click="handleAdd">添加备仓</el-button>
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmitWarehouseLocation">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/productMaintain';
export default {
    props: ['formWarehouseLocation', 'formLabelWidth', 'warehouseId'],
    data() {
        return {
            masterObj: { a: '', b: '', c: '' },
            slaveList: [{ a: '', b: '', c: '' }]
        };
    },
    created() {
        console.log(this.formWarehouseLocation);
        const warehouseLocationMaster = this.formWarehouseLocation.warehouseLocationMaster;
        const warehouseLocationSlave = this.formWarehouseLocation.warehouseLocationSlave;
        if (warehouseLocationMaster) {
            const arr = warehouseLocationMaster.split('-');
            this.masterObj.a = arr[0];
            this.masterObj.b = arr[1];
            this.masterObj.c = arr[2];
        }
        if (warehouseLocationSlave) {
            const arr = warehouseLocationSlave.split(',');
            console.log(arr);
            const slaveList = arr.map(item => {
                const arr = item.split('-');
                return { a: arr[0], b: arr[1], c: arr[2] }
            });
            this.slaveList = slaveList;
        }
    },
    methods: {
        handleAdd() {
            if (this.slaveList.length >= 5) {
                return this.$notify.error('备仓不能多于5个');
            }
            this.slaveList.push({ a: '', b: '', c: '' });
        },
        handleRemove(data, index) {
            this.slaveList.splice(index, 1);
            this.slaveList = this.slaveList;
        },
        handleSubmitWarehouseLocation() {
            const data = this.formWarehouseLocation;
            const param = {
                productId: data.productId,
                productName: data.name,
                productSpecificationId: data.productSpecificationId,
                specificationName: data.specificationName,
                warehouseId: this.$route.params.id
            };
            const masterObj = this.masterObj;
            const reg = /^[A-Z]+$/;
            if (!masterObj.a || !masterObj.b) {
                return this.$notify.error('主仓前两位不能为空');
            }
            if (!reg.test(masterObj.a)) {
                return this.$notify.error('主仓第一位只能写大写字母');
            }
            if (isNaN(masterObj.b)) {
                return this.$notify.error('主仓位第2位必须为数字');
            }
            if (isNaN(masterObj.c)) {
                return this.$notify.error('主仓位第3位必须为数字');
            }
            if (masterObj.c === '') {
                masterObj.c = 0;
            }
            if (masterObj.c < 0 || masterObj.b < 0) {
                return this.$notify.error('主仓信息中请勿输入负数');
            }
            param.warehouseLocationMaster = masterObj.a + '-' + masterObj.b + '-' + masterObj.c;

            const slaveList = this.slaveList;
            const slaveArr = [];
            for (let i = 0; i < slaveList.length; i++) {
                const slave = slaveList[i];
                if (slave.a || slave.b || slave.c) {
                    if (!slave.a || !slave.b) {
                        return this.$notify.error(`第(${i + 1})行备仓请填写完整`);
                    }
                    if (!reg.test(slave.a)) {
                        return this.$notify.error(`第(${i + 1})行备仓第一位只能写大写字母`);
                    }
                    if (isNaN(slave.b)) {
                        return this.$notify.error(`第(${i + 1})行备仓位第2位必须为数字`);
                    }
                    if (isNaN(slave.c)) {
                        return this.$notify.error(`第(${i + 1})行备仓位第3位必须为数字`);
                    }
                    if (slave.c === '') {
                        slave.c = 0;
                    }
                    if (slave.c < 0 || slave.b < 0) {
                        return this.$notify.error(`第(${i + 1})行备仓信息中请勿输入负数`);
                    }
                    const item = slave.a + '-' + slave.b + '-' + slave.c;
                    slaveArr.push(item);
                }
            }
            if (slaveArr.length) {
                param.warehouseLocationSlave = slaveArr.join(',');
            }

            window.LOG(param.warehouseLocationMaster, param.warehouseLocationSlave);
            // window.LOG(this.formWarehouseLocation, param);
            api.warehouseLocationInsert(param).then(res => {
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
