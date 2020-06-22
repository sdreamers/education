<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 活动" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="活动名称" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off" maxlength="30"></el-input>
            </el-form-item>
            <el-form-item label="活动代码" :label-width="formLabelWidth" prop="code">
                <el-input v-model="form.code" type="text" auto-complete="off" maxlength="5"></el-input>
            </el-form-item>
<!--            <el-form-item label="省份" :label-width="formLabelWidth" prop="provinceId">
                <el-select v-model="form.provinceId" placeholder="请选择省份" @change="handleProvinceChange" :disabled="formType === 'edit'">
                    <el-option v-for="(item,key) in provinceList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="城市" :label-width="formLabelWidth" prop="cityId">
                <el-select v-model="form.cityId" placeholder="请选择城市" :disabled="formType === 'edit'">
                    <el-option v-for="(item,key) in cityList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>-->
            <el-form-item label="开始时间" :label-width="formLabelWidth" prop="formDate">
                <el-date-picker
                    v-model="form.formDate"
                    type="daterange"
                    align="right"
                    unlink-panels
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期">
                </el-date-picker>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import apiCommon from '@/api/common';
import api from '@/api/activity';
import Util from '@/libs/util';

export default {
    props: ['dialogFormVisible', 'formLabelWidth', 'form', 'rules', 'formType'],
    data() {
        return {
            provinceList: [],
            cityList: []
        };
    },
    methods: {
        handleProvinceChange(data) {
            this.form.provinceId = data;
            this.getCity();
        },
        getCity() {
            const param = {
                provinceId: this.form.provinceId
            };
            return apiCommon.userCity(param).then(res => {
                if (res.code === 100) {
                    this.cityList = res.content;
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
                return res;
            });
        },
        getProvince() {
            return apiCommon.userProvince().then(res => {
                if (res.code === 100) {
                    this.provinceList = res.content;
                    if (res.content.length) {
                        if (!this.form.provinceId) {
                            this.form.provinceId = res.content[0].id;
                        }
                        this.getCity();
                    } else {
                        this.form.province = '';
                    }
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
                return res;
            });
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = JSON.parse(JSON.stringify(this.form));
                        param.beginTime = Util.formatTime(new Date(this.form.formDate[0]));
                        param.endTime = Util.formatTime(new Date(this.form.formDate[1]));
                        window.LOG(param);
                        api.activityUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        param.beginTime = Util.formatTime(new Date(this.form.formDate[0]));
                        param.endTime = Util.formatTime(new Date(this.form.formDate[1]));
                        param.cityId = Util.getCookie('cityId');
                        param.provinceId = Util.getCookie('provinceId');
                        api.activityInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
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
        this.getProvince();
    }
};
</script>
