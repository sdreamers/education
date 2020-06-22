<template>
    <el-dialog :close-on-click-modal="false" title="支出" :visible="dialogVisible" width="60%" @close="handleClose">
        <el-form :model="form" :rules="rules" ref="ruleForm" :inline="true">
            <el-row class="mb20" :gutter="20" >
                    <el-form-item label="支出日期" prop="orderTime">
                        <el-date-picker
                            v-model="form.orderTime"
                            type="date"
                            align="right"
                            value-format="yyyy-MM-dd"
                            unlink-panels
                            range-separator="至">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="单号" prop="orderNo">
                        <el-input v-model="form.orderNo" placeholder="单号\支号"></el-input>
                    </el-form-item>
                    <el-form-item label="总账摘要" prop="remark">
                        <el-autocomplete
                            class="inline-input"
                            v-model="form.remark"
                            :fetch-suggestions="handleRemarkSearch"
                            placeholder="总账摘要"
                        ></el-autocomplete>
                    </el-form-item>
                <el-form-item label="">
                    <el-button type="primary" @click="handleAddToTable">添加项目</el-button>
                </el-form-item>
            </el-row>

            <el-table
                :data="form.tableData"
                border
                v-loading="loading">
                <el-table-column prop="merchantName" label="单位名称">
                    <template slot-scope="scope">
                        <el-select v-model="scope.row.merchantId" placeholder="项目名称">
                            <el-option v-for="(item,key) in merchantArr" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="分账摘要">
                    <template slot-scope="scope">
                        <el-input type="text" v-model="scope.row.remark" placeholder="分账摘要"></el-input>
                    </template>
                </el-table-column>

                <el-table-column prop="name" label="项目名称">
                    <template slot-scope="scope">
                        <el-select v-model="scope.row.productId" placeholder="项目名称">
                            <el-option v-for="(item,key) in productArr" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column prop="amount" label="数量" min-width="100px">
                    <template slot-scope="scope">
                        <el-input type="text" v-model="scope.row.amount" placeholder="购买数量"></el-input>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleClose">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>

    import merchant from '@/api/merchant';
    import product from '@/api/product';
    import order from '@/api/order';

    const form = {
        orderNo: '',
        remark: '',
        tableData: []
    };

    const remarkArr = [
        { value: '永兴油料支出' },
        { value: '琛航油料支出' },
        { value: '南沙油料支出' }
    ];

    export default {

        props: ['dialogVisible'],

        data() {
            return {
                form: JSON.parse(JSON.stringify(form)),
                rules: {
                    orderNo: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    remark: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    orderTime: [{ required: true, message: '不能为空', trigger: 'blur' }]
                },
                productArr: [],
                merchantArr: [],
                loading: false
            }
        },

        methods: {
            handleAddToTable() {
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        this.form.tableData.push(this.initRow());
                    }
                });
            },

            initRow() {
                let selectedSpeIndex = this.form.tableData.length;
                selectedSpeIndex++;
                return {
                    merchantId: '',
                    merchantName: '',
                    remark: '',
                    productId: '',
                    productName: '',
                    amount: '',
                    selectedSpeIndex: selectedSpeIndex
                };
            },

            /**
             * 搜索单位
             * */
            querySearchMerchant(merchantName, cb) {
                if (!merchantName) {
                    return;
                }
                const param = { searchStr: merchantName };
                merchant.search(param).then(res => {
                    if (res && res.code === 100) {
                        const merchantArr = [];
                        res.content.forEach(item => {
                            merchantArr.push({ value: item.name, id: item.id });
                        });
                        cb(merchantArr);
                    }
                })
            },

            // 点击删除按钮
            handleRemove(data) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.form.tableData = this.form.tableData.filter(item => item.sid !== data.sid);
                })
            },

            handleSelectMerchant(item, row) {
                row.merchantId = item.id;
            },

            merchantNameChange(row) {
                if (!row.merchantName) {
                    row.merchantId = '';
                }
            },

            handleSubmit() {
                this.$confirm('确定提交？').then(() => {
                    if (!this.form.orderTime) {
                        return this.$notify.error('请选择开支日期');
                    }
                    if (!this.form.orderNo) {
                        return this.$notify.error('请输入单号');
                    }
                    if (!this.form.remark) {
                        return this.$notify.error('请输入摘要');
                    }
                    if (this.form.tableData.length === 0) {
                        return this.$notify.error('请添加商品');
                    }
                    for (let i = 0; i < this.form.tableData.length; i++) {
                        const ele = this.form.tableData[i];
                        if (!ele.merchantId) {
                            return this.$notify.error('未选择单位');
                        }
                        if (!ele.productId) {
                            return this.$notify.error('未选择商品');
                        }
                        if (!ele.amount) {
                            return this.$notify.error('未输入购买数量');
                        }
                        if (isNaN(ele.amount)) {
                            return this.$notify.error('购买数量必须为数字');
                        }
                    }
                    const orderProductSnapshots = [];
                    this.form.tableData.forEach(item => {
                        orderProductSnapshots.push({
                            merchantId: item.merchantId,
                            productId: item.productId,
                            amount: item.amount,
                            remark: item.remark
                        });
                    });
                    const param = {
                        orderNo: this.form.orderNo,
                        remark: this.form.remark,
                        type: '2',
                        orderTimeStr: this.form.orderTime,
                        orderProductSnapshots
                    };
                    order.insert(param).then(res => {
                        window.LOG(res);
                        if (res.code === 100) {
                            this.$notify.success('操作成功');
                            this.handleClose();
                        }
                    });
                }).catch()
            },

            handleClose() {
                this.form = JSON.parse(JSON.stringify(form));
                this.$emit('close');
            },

            listAllProduct() {
                product.listAllProduct().then(res => {
                    if (res.code === 100) {
                        this.productArr = res.content;
                    }
                })
            },

            listAllMerchant() {
                merchant.list().then(res => {
                    if (res.code === 100) {
                        this.merchantArr = res.content;
                    }
                })
            },

            handleRemarkSearch(queryString, cb) {
                cb(remarkArr);
            }
        },

        created() {
            this.listAllProduct();
            this.listAllMerchant();
        }
    }
</script>

<style scoped>

</style>
