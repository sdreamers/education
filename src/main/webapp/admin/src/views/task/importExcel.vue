<template>
    <el-dialog :close-on-click-modal="false" title="导入设备" :visible="dialogVisible" width="30%" @close="$emit('close')">
        <el-form :model="form" label-position="left" ref="ruleForm" label-width="90px" :rules="rules">
            <el-form-item label="包名" prop="packetName">
                <el-input v-model="form.packetName" placeholder="请输入包名">
                </el-input>
            </el-form-item>
            <el-form-item label="供应商" prop="supplierName">
                <el-input v-model="form.supplierName" placeholder="请输入供应商"></el-input>
            </el-form-item>
            <el-form-item label="年份" prop="currentYear">
                <el-input v-model="form.currentYear" placeholder="请输入年份"></el-input>
            </el-form-item>
            <el-form-item label="设备明细">
                <el-upload
                    action="https://jsonplaceholder.typicode.com/posts/"
                    :limit="1"
                    :http-request="modeUpload">
                <el-button size="small" type="primary">上传</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import axios from 'axios';

    const form = { packetName: '', supplierName: '', type: '', currentYear: '' };

    export default {
        props: ['dialogVisible'],
        data() {
            const validateInt = (rule, value, callback) => {
                if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                    callback();
                }
                callback(new Error('必须为正整数'));
            };

            return {
                rules: {
                    packetName: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    currentYear: [{ required: true, message: '不能为空', trigger: 'blur' },
                        { validator: validateInt, trigger: 'blur' }],
                    supplierName: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    type: [{ required: true, message: '不能为空', trigger: 'blur' }]

                },
                supplierForm: {},
                form: JSON.parse(JSON.stringify(form)),
                deviceImportVOList: [],
                excel: {}
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        console.log(this.excel)
                        if (!this.excel) {
                            return this.$notify.error('请导入文件');
                        }
                        const formData = new FormData();
                        formData.append('excelFile', this.excel);
                        formData.append('packet', this.form.packetName);
                        formData.append('currentYear', this.form.currentYear);
                        formData.append('supplierName', this.form.supplierName);
                        const url = 'http://localhost:8767/device/import';
                        axios.post(url, formData).then(res => {
                            console.log(res);
                            if (res.data.code === 100) {
                                this.$notify.success(res.message || '导入成功');
                                this.form = JSON.parse(JSON.stringify(form));
                                this.excel = {};
                                this.$emit('importSuccess')
                            } else {
                                this.$notify.success(res.data.message || '失败');
                            }
                        })
                    } else {
                        this.$notify({
                            message: '请按要求填写表单',
                            type: 'warning'
                        });
                        return false;
                    }
                });
            },

            modeUpload(item) {
                this.excel = item.file;
            }

        }
    };
</script>
