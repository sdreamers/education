<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 修改 记录" :visible="true" @close="$emit('close')">
        <el-form :model="form" :rules="rules" ref="ruleForm">
            <el-form-item label="商品" :label-width="formLabelWidth" prop="cityProductId">
                <el-select
                    v-model="form.cityProductId"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="请输入关键词"
                    :remote-method="handleAddingSearch"
                    @change="handleAddingSearchSelect">
                    <el-option
                        v-for="item in keywordList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="规格" :label-width="formLabelWidth" prop="cityProductSpecificationId">
                <el-select v-model="form.cityProductSpecificationId" placeholder="请选择商品规格" :disabled="!form.cityProductId">
                    <el-option v-for="(item,key) in specificationList" :label="item.specificationName" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                <el-input v-model="form.sortNo" type="text" auto-complete="off" maxlength="9"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
    import api from '@/api/recommend';
    import searchProduct from '@/api/activity';
    import formUtil from '@/libs/form';

    export default {
        props: ['dialogFormVisible', 'formLabelWidth', 'form', 'rules', 'formType', 'eventName'],
        data() {
            return {
                provinceList: [],
                cityList: [],
                keywordList: [],
                specificationList: []
            };
        },
        methods: {
            // 选中搜索下拉选项
            handleAddingSearchSelect(data) {
                this.form.cityProductId = data;
                this.form.cityProductSpecificationId = '';
                const param = {
                    productId: data,
                    available: 1
                };
                searchProduct.productSpecificationSelectSpecification(param).then(res => {
                    if (res.code === 100) {
                        this.specificationList = res.content;
                    }
                });
            },
            handleAddingSearch(query) {
                if (query === '') {
                    return this.keywordList = [];
                }
                if (!query) {
                    return;
                }
                const param = {
                    available: 1,
                    name: query
                };
                searchProduct.productListProduct(param).then(res => {
                    if (res.code === 100) {
                        this.keywordList = res.content;
                    } else {
                        this.keywordList = [];
                    }
                });
            },
            // 提交表单
            handleSubmit() {
                const formType = this.formType;
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        if (formType === 'edit') {
                            const param = JSON.parse(JSON.stringify(this.form));
                            param.isFixed = '1';
                            param.eventName = this.eventName;
                            api.update(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.$emit('close');
                                } else {
                                    // this.$notify.error(res.message || '未知错误');
                                }
                            });
                        } else if (formType === 'add') {
                            const param = this.form;
                            param.isFixed = '1';
                            param.eventName = this.eventName;
                            api.insert(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.$emit('close');
                                    formUtil.initForm(this.form);
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
        }
    };
</script>
