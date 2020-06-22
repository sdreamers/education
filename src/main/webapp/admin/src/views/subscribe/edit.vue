<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="订阅标题" :label-width="formLabelWidth" prop="title">
                <el-input v-model="form.title" type="text" auto-complete="off" maxlength="30"></el-input>
            </el-form-item>
            <el-form-item label="订阅代码" :label-width="formLabelWidth" prop="code">
                <el-input v-model="form.code" type="text" auto-complete="off" maxlength="30"></el-input>
            </el-form-item>
            <el-form-item label="推送模式" :label-width="formLabelWidth" prop="pushType">
                <el-select v-model="form.pushType" placeholder="推送模式">
                    <el-option v-for="(item,key) in mapPush" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="发送短信" :label-width="formLabelWidth" prop="isSendSms" v-show="form.pushType === '1'">
                <el-select v-model="form.isSendSms" placeholder="发送短信">
                    <el-option v-for="(item,key) in mapSms" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="用户是否可选" :label-width="formLabelWidth" prop="userEditFlag">
                <el-select v-model="form.userEditFlag" placeholder="用户是否可选" @change="handleEditChange">
                    <el-option v-for="(item,key) in mapEdit" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="默认订阅" :label-width="formLabelWidth" prop="defaultFlag">
                <el-select v-model="form.defaultFlag" placeholder="默认订阅" :disabled="form.userEditFlag === '0'">
                    <el-option v-for="(item,key) in mapDefault" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="允许订阅的用户类型" :label-width="formLabelWidth" prop="allowUserType">
                <el-checkbox-group v-model="form.allowUserType" @change="handleUserChange">
                    <el-checkbox label="所有用户"></el-checkbox>
                    <el-checkbox label="商户" :disabled="form.allowUserType.includes('所有用户')"></el-checkbox>
                    <el-checkbox label="管理员" :disabled="form.allowUserType.includes('所有用户')"></el-checkbox>
                    <el-checkbox label="销售员" :disabled="form.allowUserType.includes('所有用户')"></el-checkbox>
                    <el-checkbox label="配送员" :disabled="form.allowUserType.includes('所有用户')"></el-checkbox>
                </el-checkbox-group>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/subscribe';
const map = {
    0: '所有用户',
    1: '商户',
    2: '管理员',
    3: '销售员',
    4: '配送员'
}
const mapRevert = {
    所有用户: 0,
    商户: 1,
    管理员: 2,
    销售员: 3,
    配送员: 4
}
export default {
    props: ['formType', 'form'],
    data() {
        return {
            formLabelWidth: '120px',

            // 状态列表
            statusList: [{ id: '0', name: '否' }, { id: '1', name: '是' }],

            // 七牛表单
            qiniuForm: {},

            mapPush: [{ id: '1', name: '单播推送' }, { id: '0', name: '广播推送' }],
            mapSms: [{ id: '1', name: '发送' }, { id: '0', name: '不发送' }],
            mapEdit: [{ id: '1', name: '可选' }, { id: '0', name: '不可选' }],
            mapDefault: [{ id: '1', name: '是' }, { id: '0', name: '否' }],

            rules: {
                returnCauseNo: [
                    { required: true, message: '不能为空', trigger: 'blur' }
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
        handleUserChange(data) {
            console.log(data);
            if (data.includes('所有用户')) {
                this.form.allowUserType = ['所有用户'];
            }
        },
        handleEditChange(data) {
            console.log(data);
            if (data === '0') {
                this.form.defaultFlag = '1';
            }
        },
        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = JSON.parse(JSON.stringify(this.form));
                    param.allowUserType = param.allowUserType.filter(item => item !== undefined && item != null).map(item => {
                        item = mapRevert[item];
                        return item;
                    }).join(',');
                    if (formType === 'edit') {
                        api.subscribeUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        api.subscribeInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
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
    },
    created() {
        this.form.allowUserType = this.form.allowUserType.split(',');
        this.form.allowUserType = this.form.allowUserType.map(item => {
            item = map[item];
            return item;
        })
        console.log(this.form.allowUserType);
    }
};
</script>
