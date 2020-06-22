<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <common :list="headerList"></common>
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd">添加记录</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="search.name" placeholder="搜索关键字"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :width="column.width">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleViewProduct(scope.row)">查看商品</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleAddProduct(scope.row)">添加商品</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                                type="text"
                                @click.stop="handleRemove(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="pager-wrapper">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-sizes="[2, 5, 10, 20]"
                        :page-size="pageSize"
                        layout="total, sizes, slot, prev, pager, next"
                        :total="totalPage">
                        <span class="el-pagination__total refresh" @click="handleCurrentChange(currentPage)"><i class="el-icon-refresh"></i></span>
                    </el-pagination>
                </div>
            </el-row>
        </el-card>
        <!-- 修改活动 -->
        <edit
            v-if="dialogFormVisible"
            @close="dialogFormVisible = false,activityPagers()"
            :formType="formType"
            :formLabelWidth="formLabelWidth"
            :form="form"
            :rules="rules"></edit>
        <!-- 活动商品列表 -->
        <productList
            @close="dialogProductVisible = false,activityPagers()"
            v-if="dialogProductVisible"
            :formLabelWidth="formLabelWidth"
            :rules="rules"
            :product="product"></productList>
        <addProduct
            @close="dialogProductAddVisible = false,activityPagers()"
            v-if="dialogProductAddVisible"
            :formLabelWidth="formLabelWidth"
            :activityId="activityId"
            :activityCode="activityCode"
            :rules="rules"
        ></addProduct>
    </section>
</template>
<script>
    import api from '@/api/activity';
    import Util from '@/libs/util';

    // 编辑弹层
    import edit from './edit';
    // 查看商品
    import productList from './productList';
    // 添加商品
    import addProduct from './addProduct';

    const columns = [
        { key: 'id', title: 'ID', width: '100px' },
        { key: 'name', title: '活动名称', width: '200px' },
        { key: 'code', title: '活动代码', width: '100px' },
        { key: 'provinceName', title: '省份', width: '100px' },
        { key: 'cityName', title: '城市', width: '100px' },
        { key: 'beginTime', title: '开始时间' },
        { key: 'endTime', title: '结束时间' }
    ];
    const form = {
        date: []
    };
    export default {
        components: {
            edit,
            productList,
            addProduct
        },
        data() {
            const validateNaN = (rule, value, callback) => {
                if (isNaN(value)) {
                    callback(new Error('请输入数字'));
                }
                callback();
            };
            const validateInt = (rule, value, callback) => {
                if (Number.isInteger(Number(value)) && Number(value) >= 0) {
                    callback();
                }
                callback(new Error('序号必须为大于等于0的正整数'));
            };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                search: {
                    name: ''
                },

                tableData: [],
                columns: columns,

                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                product: {},

                activityId: '',
                activityCode: '',

                // 修改活动
                dialogFormVisible: false,
                // 活动列表
                dialogProductVisible: false,
                // 添加商品
                dialogProductAddVisible: false,
                formLabelWidth: '120px',
                form: JSON.parse(JSON.stringify(form)),
                formType: '',
                rules: {
                    name: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    code: [{ required: true, message: '不能为空', trigger: 'blur' }, formIsNaN, formIsInt],
                    provinceId: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    cityId: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    formDate: [{ required: true, message: '不能为空', trigger: 'blur' }]
                }
            };
        },
        methods: {
            // 搜索
            handleSearch() {
                this.currentPage = 1;
                this.activityPagers();
            },
            // 清除搜索
            handleClear() {
                this.search.name = '';
                this.currentPage = 1;
                this.activityPagers();
            },
            // 点击添加按钮
            handleAdd() {
                this.formType = 'add';
                this.form = JSON.parse(JSON.stringify(form));
                this.dialogFormVisible = true;
            },
            // 点击编辑按钮
            handleEdit(data) {
                this.formType = 'edit';
                data.formDate = [data.beginTime, data.endTime];
                this.form = JSON.parse(JSON.stringify(data));
                this.dialogFormVisible = true;
            },
            // 点击删除按钮
            handleRemove(data) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: data.id
                    };
                    api.activityDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.activityPagers();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }).catch(err => {
                    window.LOG(err);
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },
            handleViewProduct(data) {
                this.product.provinceId = data.provinceId;
                this.product.cityId = data.cityId;
                this.product.code = data.code;
                this.dialogProductVisible = true;
            },
            // 添加商品
            handleAddProduct(data) {
                this.activityId = data.id;
                this.activityCode = data.code;
                this.dialogProductAddVisible = true;
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.activityPagers();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.activityPagers();
            },
            // 获取数据
            activityPagers() {
                const param = {
                    page: this.currentPage,
                    limit: this.pageSize
                };
                if (this.search.name) {
                    param.name = this.search.name;
                }
                api.activityPagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                            item.beginTime = Util.parseTimeStr(item.beginTime);
                            item.endTime = Util.parseTimeStr(item.endTime);
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
            }
        },
        created() {
            this.activityPagers();
        }
    };
</script>
