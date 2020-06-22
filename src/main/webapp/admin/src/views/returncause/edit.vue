<template>
    <el-dialog :close-on-click-modal="false" title="填写设置退货原因" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="退货原因代码" :label-width="formLabelWidth" prop="returnCauseNo">
                <el-input v-model="form.returnCauseNo" type="text" auto-complete="off" maxlength="9" pattern="^[1-9]\d*"></el-input>
            </el-form-item>
            <el-form-item label="退货原因描述" :label-width="formLabelWidth" prop="returnCauseDescription">
                <el-input v-model="form.returnCauseDescription" type="text" auto-complete="off" maxlength="200"></el-input>
            </el-form-item>
            <el-form-item label="申请退货是否需要上传图片" :label-width="formLabelWidth" prop="isNeedImage">
                <el-select v-model="form.isNeedImage" placeholder="请选择">
                    <el-option
                    v-for="item in isNeedImageArr"
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
import api from '@/api/returncause';
import apiCommon from '@/api/common';

export default {
    props: ['form', 'formType'],
    data() {
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) > 0 && /^[1-9]\d*$/.test(value)) {
                callback();
            }
            callback(new Error('序号必须为正整数'));
        };
        const formIsInt = { validator: validateInt, trigger: 'blur' };
        return {
            formLabelWidth: '120px',

            qiniuForm: {},

            isNeedImageArr: [
                { id: '0', name: '否' },
                { id: '1', name: '是' }
            ],

            rules: {
                returnCauseNo: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    formIsInt
                ],
                returnCauseDescription: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                isNeedImage: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        // 图片上传到七牛云之前
        handleBeforeUpload(file) {
            const param = {
                fileName: file.name
            };
            return apiCommon.getQiniuToken()
                .then(res => {
                    if (res.code === 100) {
                        const content = res.content;
                        this.qiniuForm = {
                            key: param.fileName,
                            token: content.uploadToken,
                            domain: content.domain
                        };
                    } else {
                        this.$notify.error('图片上传失败： token 获取失败');
                    }
                })
                .catch(err => {
                    this.$notify.error('图片上传失败： token 获取失败');
                    window.LOG('图片上传失败： token 获取失败', err);
                });
        },
        // 预览图片上传到七牛云成功
        handlePreviewSuccess(data) {
            const fileName = this.qiniuForm.domain + data.key;
            this.$set(this.form, 'previewImageUrl', fileName);
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            const form = JSON.parse(JSON.stringify(this.form));
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    // return console.log(/^[1-9]\d*$/.test(form.returnCauseNo));
                    // if (!/^[1-9]\d*$/.test(form.returnCauseNo)) {
                    //     return this.$notify()
                    // }
                    if (formType === 'edit') {
                        const param = form;
                        api.returncauseUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = form;
                        api.returncauseInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            }
                        });
                    }
                } else {
                    this.$notify({
                        message: '请按要求填写表单',
                        type: 'warning'
                    });
                    return false;
                }
            });
        }
    }
};
</script>
