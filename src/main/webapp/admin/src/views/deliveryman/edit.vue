<template>

    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 配送员基本信息" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" :label-width="formLabelWidth" prop="telephone">
                <el-input v-model="form.telephone" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="性别" :label-width="formLabelWidth" prop="sex">
                <el-select v-model="form.sex" placeholder="性别">
                    <el-option v-for="(item,key) in sexArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="年龄" :label-width="formLabelWidth" prop="age">
                <el-input v-model="form.age" type="text" auto-complete="off"></el-input>
            </el-form-item>

            <el-form-item label="省份" :label-width="formLabelWidth" prop="provinceId">
                <el-select v-model="form.provinceId" placeholder="省份" @change="provinceChange">
                    <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="城市" :label-width="formLabelWidth" prop="cityId">
                <el-select v-model="form.cityId" placeholder="城市" @change="cityChange">
                    <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="地区" :label-width="formLabelWidth" prop="districtId">
                <el-select v-model="form.districtId" placeholder="地区">
                    <el-option
                        v-for="(item,key) in districtArr"
                        :label="item.name"
                        :value="item.id"
                        :key="key">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item v-if="formType == 'edit'" label="密码维护" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.loginPassReset" placeholder="密码维护">
                    <el-option v-for="(item,key) in pwdArr" :label="item.name" :value="item.id" :key="key"></el-option>
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
import { deliverymanSex } from '@/api/dict';
import api from '@/api/deliveryman';
import apiCommon from '@/api/common';
import regx from '@/libs/regx';
const pwdArr = [{ id: '0', name: '维持当前密码' }, { id: '1', name: '重置密码' }];
export default {
    props: ['formType', 'form', 'formLabelWidth'],
    data() {
        const validateInt = (rule, value, callback) => {
            if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                callback();
            }
            callback(new Error('请输入整数'));
        };
        const validateLen = (rule, value, callback) => {
            if (Number(value) <= 200 || Number(value) > 0) {
                callback();
            }
            callback(new Error('请输入正确年龄'));
        };
        const validateNaN = (rule, value, callback) => {
            if (isNaN(value)) {
                callback(new Error('请输入数字'));
            }
            callback();
        }
        return {
            sexArr: [],
            sexMap: {},

            pwdArr: pwdArr,

            provinceArr: [],
            cityArr: [],
            districtArr: [],
            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                telephone: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: regx.checkMobile, trigger: 'blur' }
                ],
                age: [
                    { required: true, message: '不能为空', trigger: 'blur' },
                    { validator: validateInt, trigger: 'blur' },
                    { validator: validateLen, trigger: 'blur' },
                    { validator: validateNaN, trigger: 'blur' }
                ],
                sex: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                provinceId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                cityId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                districtId: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        cityChange() {
            this.form.districtId = '';
            this.getDistrict();
        },
        provinceChange() {
            this.form.cityId = '';
            this.form.districtId = '';
            this.getCity();
        },
        getDistrict() {
            if (!this.form.cityId) {
                return;
            }
            const param = {
                cityId: this.form.cityId
            };
            return apiCommon.availableAreasDistricts(param).then(res => {
                if (res.code === 100) {
                    this.districtArr = res.content;
                }
            });
        },
        getCity() {
            if (!this.form.provinceId) {
                return;
            }
            const param = {
                provinceId: this.form.provinceId
            };
            return apiCommon.userCity(param).then(res => {
                if (res.code === 100) {
                    this.cityArr = res.content;
                }
            });
        },
        getProvince() {
            return apiCommon.userProvince().then(res => {
                if (res.code === 100) {
                    this.provinceArr = res.content;
                }
            });
        },
        deliverymanSex() {
            return deliverymanSex().then(res => {
                if (res.code === 100) {
                    const sexMap = res.content;
                    this.sexMap = sexMap;
                    const sexArr = [];
                    for (const item in sexMap) {
                        if (sexMap.hasOwnProperty(item)) {
                            const ele = sexMap[item];
                            sexArr.push({ id: item, name: ele });
                        }
                    }
                    this.sexArr = sexArr;
                }
            });
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const age = Number(this.form.age);
                    if (age < 0 || age > 200) {
                        return this.$notify.error('输入错误，年龄只能在0-200之间');
                    }
                    if (formType === 'edit') {
                        const param = this.form;
                        api.deliverymanUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.deliverymanInsert(param).then(res => {
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
        this.deliverymanSex();
        this.getProvince();
        this.getCity();
        this.getDistrict();
    }
};
</script>
