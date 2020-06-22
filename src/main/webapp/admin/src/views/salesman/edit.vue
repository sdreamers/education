<template>

    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 业务员基本信息" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="姓名" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.telephone" type="text" auto-complete="off"></el-input>
            </el-form-item>
            <el-form-item label="性别" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.sex" placeholder="性别">
                    <el-option v-for="(item,key) in sexArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="年龄" :label-width="formLabelWidth" prop="name">
                <el-input v-model="form.age" type="text" auto-complete="off"></el-input>
            </el-form-item>

            <el-form-item label="省份" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.provinceId" placeholder="省份" @change="getCity">
                    <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="城市" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.cityId" placeholder="城市" @change="getDistrict">
                    <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="地区" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.districtId" placeholder="地区">
                    <el-option v-for="(item,key) in districtArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="业务员角色" :label-width="formLabelWidth" prop="name">
                <el-select v-model="form.role" placeholder="业务员角色">
                    <el-option v-for="(item,key) in salesmanRoleArr_formatted" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="上级业务员" :label-width="formLabelWidth" prop="name">
                <!-- <el-input v-model="form.leaderName" type="text" auto-complete="off" readonly ></el-input> -->
                {{form.leaderName}} <span @click="handleShowLeader">修改</span>
            </el-form-item>
            <el-form-item v-if="formType==='edit'" label="密码维护" :label-width="formLabelWidth" prop="name">
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
import apiCommon from '@/api/common';
import api from '@/api/salesman';
const pwdArr = [{ id: '0', name: '维持当前密码' }, { id: '1', name: '重置密码' }];
export default {
    props: ['form', 'salesmanRoleArr', 'formType', 'sexArr', 'statusMap', 'orderStatusMap'],
    data() {
        return {
            pwdArr: pwdArr,
            formLabelWidth: '120px',
            rules: {
                name: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            },

            salesmanRoleArr_formatted: [],


            provinceArr: [],
            cityArr: [],
            districtArr: []

        };
    },
    methods: {

        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        api.salesmanUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.salesmanInsert(param).then(res => {
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
                    let has = false;
                    this.districtArr = res.content.map(item => {
                        if (item.id === this.form.districtId) {
                            has = true;
                        }
                        return item;
                    });
                    if (has === false) {
                        this.form.districtId = this.districtArr[0].id;
                    }
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
                    let has = false;
                    this.cityArr = res.content.map(item => {
                        if (item.id === this.form.cityId) {
                            has = true;
                        }
                        return item;
                    });
                    if (has === false) {
                        this.form.cityId = this.cityArr[0].id;
                    }
                }
            });
        },
        getProvince() {
            return apiCommon.userProvince().then(res => {
                if (res.code === 100) {
                    this.provinceArr = res.content;
                }
            });
        }
    },
    async created() {
        await this.getProvince();
        await this.getCity();
        await this.getDistrict();
        this.salesmanRoleArr_formatted = this.salesmanRoleArr.filter(item => item.name !== '所有');
        console.log(this.salesmanRoleArr_formatted);
    }
};
</script>
