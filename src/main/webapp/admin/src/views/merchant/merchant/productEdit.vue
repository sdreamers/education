<template>
    <el-dialog :close-on-click-modal="false" title="添加 \ 删除 品种" :visible="dialogVisible" width="20%" @close="$emit('close')">
        <el-form :model="form"  ref="ruleForm">
            <el-form-item label="操作类型" label-width="100px" prop="name">
                <el-select v-model="form.type">
                    <el-option v-for="(item,key) in typeArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="品种名称" v-if="form.type === '1'" label-width="100px" prop="name">
                <el-input v-model="form.name" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
            <el-form-item label="品种名称" v-if="form.type === '2' || form.type === '3'" label-width="100px" prop="name">
                <el-select v-model="form.productId">
                    <el-option v-for="(item,key) in productArr" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="新品种名称" v-if="form.type === '3'" label-width="100px" prop="name">
                <el-input v-model="form.newName" type="text" auto-complete="off" maxlength="10"></el-input>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import api from '@/api/merchant';
    import product from '@/api/product';

    const typeArr = [
        { id: '1', name: '增加油料品种' },
        { id: '2', name: '删除油料品种' },
        { id: '3', name: '修改油料品种' }
    ]

    export default {
        props: ['dialogVisible', 'form', 'type'],
        data() {
            return {
                typeArr: JSON.parse(JSON.stringify(typeArr)),

                productArr: []
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                if (!this.form.type) {
                    return this.$notify.error('请选择操作类型');
                }

                if (this.form.type === '1') {
                    if (!this.form.name) {
                        return this.$notify.error('请输入品种名称');
                    }
                    const param = {
                        name: this.form.name
                    };
                    api.insertProduct(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('添加成功');
                            this.$emit('close');
                        }
                    });
                } else if (this.form.type === '2') {
                    if (!this.form.productId) {
                        return this.$notify.error('请选择要删除的商品');
                    }
                    const param = {
                        id: this.form.productId
                    };
                    product.delete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.$emit('close');
                        }
                    })
                } else if (this.form.type === '3') {
                    if (!this.form.productId) {
                        return this.$notify.error('请选择要修改的商品');
                    }
                    if (!this.form.newName) {
                        return this.$notify.error('请输入商品名称');
                    }
                    const param = {
                        id: this.form.productId,
                        name: this.form.newName
                    };
                    product.updateName(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success('删除成功');
                            this.$emit('close');
                        }
                    });
                }
            },

            listAllProduct() {
                product.listAllProduct().then(res => {
                    if (res.code === 100) {
                        this.productArr = res.content;
                    }
                })
            }
        },

        created() {
            this.listAllProduct();
        }
    };
</script>
