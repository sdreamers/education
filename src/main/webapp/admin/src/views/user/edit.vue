<template>
    <el-dialog :close-on-click-modal="false" title="导入设备" :visible="dialogVisible" width="30%" @close="$emit('close')">
        <el-form :model="form" label-position="left" ref="ruleForm" label-width="90px" :rules="rules">
            <el-form-item label="用户名" prop="name">
                <el-input v-model="form.name" placeholder="请输入用户名">
                </el-input>
            </el-form-item>
            <el-form-item label="账号" prop="account">
                <el-input v-model="form.account" placeholder="请输入账号"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="telephone">
                <el-input v-model="form.telephone" placeholder="请输入手机号码"></el-input>
            </el-form-item> 
            <el-form-item label="角色" prop="nature">
                <el-select v-model="form.nature" filterable placeholder="请选择角色" @change="handleNatureChange">
                        <el-option
                            v-for="item in natures"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
            </el-form-item>
            <el-form-item label="供应商" prop="supplierId" v-if="form.nature === 3">
                <el-select v-model="form.supplierId" filterable placeholder="请选择供应商">
                        <el-option
                            v-for="item in suppliers"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import userApi from '@/api/user'
    import supplierApi from '@/api/supplier'

    const natures = [{ id: 1, name: '管理员' }, { id: 2, name: '系统用户' }, { id: 3, name: '供应商' }]

    export default {
        props: ['dialogVisible', 'form'],
        data() {
            const validateSupplier = (rule, value, callback) => {
                console.log(this.form.supplierId);
                if (this.form.nature === 3 && !this.form.supplierId) {
                    callback(new Error('请选择供应商'));
                }
                callback();
            };

            return {
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    account: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    telephone: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    nature: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    supplierId: [{ validator: validateSupplier, trigger: 'blur' }]
                },
                natures: natures,
                suppliers: []
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        const param = JSON.parse(JSON.stringify(this.form));
                        if (this.form.id) {
                            // 更新
                            param.id = this.form.id;
                            userApi.update(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success('更新成功');
                                    this.close();
                                }
                            });
                        } else {
                            // 新增
                            userApi.insert(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success('新增成功');
                                    this.close();
                                }
                            })
                        }
                    } else {
                        this.$notify({
                            message: '请按要求填写表单',
                            type: 'warning'
                        });
                        return false;
                    }
                });
            },

            close() {
                this.form = {};
                this.dialogVisible = false;
                this.$emit('close');
            },

            handleNatureChange() {
                if (this.form.nature !== 3) {
                    this.form.supplierId = '';
                }
            },

            loadSuppliers() {
                supplierApi.list().then(res => {
                    if (res.code === 100 && res.content) {
                        this.suppliers = res.content;
                    }
                })
            }
        },

        created() {
            this.loadSuppliers();
        }
    };
</script>
