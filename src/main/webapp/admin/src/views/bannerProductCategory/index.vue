<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd">添加商品分类广告</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">

                </el-row>
                <el-table :data="tableData" border v-loading="loading" @row-click="rowClick">
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                        <template slot-scope="scope">
                            <img  v-if="column.key === 'imageUrl'" style="width: 160px;height: 90px;" :src="scope.row.imageUrl" />
                            <span v-if="column.key !== 'imageUrl'">{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="是否永久" width="80px">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                            <span v-if="scope.row.expiryDaysFlag === '1'">是</span>
                            <span v-if="scope.row.expiryDaysFlag === '0'">否</span>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="宽" width="60px" align="center">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                            <span>{{scope.row.width}}</span>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="高" width="60px" align="center">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                            <span>{{scope.row.height}}</span>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="类型" width="80px">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <span v-if="scope.row.type === '1'">List</span>
                                <span v-if="scope.row.type === '2'">WebView</span>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100px">
                        <template slot-scope="scope">
                            <section style="text-align:center">
                                <el-switch
                                    v-model="scope.row.status === '1'"
                                    active-color="#13ce66"
                                    inactive-color="#ff4949">
                                </el-switch>
                            </section>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" >
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                                type="text"
                                v-if="scope.row.type === '1'"
                                @click.stop="handleAddProduct(scope.row.id)">关联商品</el-button>
                            <el-button
                                type="text"
                                v-if="scope.row.type === '2'"
                                @click.stop="handleAddURL(scope.row)">关联URL</el-button>
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
        <!-- 活动商品列表 -->
        <el-dialog :close-on-click-modal="false" title="添加 \ 修改 商品分类广告" :visible.sync="dialogFormVisible"  @close="$refs.ruleForm.resetFields()">
            <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="一级品类" :label-width="formLabelWidth" prop="productCategoryId">
                    <el-select v-model="form.productCategoryId" placeholder="请选择一级品类" >
                        <el-option v-for="(item,key) in fatherList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="序号" :label-width="formLabelWidth" prop="sortNo">
                    <el-col :span="6">
                        <el-input v-model="form.sortNo" type="text" auto-complete="off" placeholder="请输入整数" maxlength="8"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="广告类型" :label-width="formLabelWidth" prop="type">
                    <el-select v-model="form.type" placeholder="请选择广告类型">
                        <el-option v-for="(item,key) in type" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="广告状态" :label-width="formLabelWidth" prop="status">
                    <el-select v-model="form.status" placeholder="请选择广告状态">
                        <el-option v-for="(item,key) in status" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="广告预览图" :label-width="formLabelWidth" prop="imageUrl">
                    <el-upload
                        class="avatar-uploader"
                        action="https://upload-z2.qiniup.com/"
                        :show-file-list="false"
                        :data="qiniuForm"
                        :on-success="handlePreviewSuccess"
                        :before-upload="handleBeforeUpload">
                        <img style="width: 150px;height: 150px;" v-if="form.imageUrl" :src="form.imageUrl || ''" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>
                <el-form-item label="图片的宽" :label-width="formLabelWidth" prop="width">
                    <el-col :span="6">
                    <el-input v-model.number="form.width" type="text" auto-complete="off" placeholder="请输入图片的宽" maxlength="8"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="图片的高" :label-width="formLabelWidth" prop="height">
                    <el-col :span="6">
                    <el-input v-model.number="form.height" type="text" auto-complete="off" placeholder="请输入图片的高" maxlength="8"></el-input>
                    </el-col>
                </el-form-item>
                <el-form-item label="永久生效" :label-width="formLabelWidth" prop="expiryDaysFlag">
                    <el-select @change="handleGroupChange(form.expiryDaysFlag)" v-model="form.expiryDaysFlag" placeholder="请选择广告时间">
                        <el-option v-for="(item,key) in bannerExpiryDaysFlag" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="定义时间" :label-width="formLabelWidth" v-show="settingTime">
                    <el-date-picker
                        align="right"
                        unlink-panels
                        v-model="bannerDateTime"
                        type="datetimerange"
                        range-separator="至"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间">
                    </el-date-picker>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>
        <!-- 添加商品到活动弹层 -->
        <el-dialog :close-on-click-modal="false" title="填写广告关联商品信息" :visible.sync="dialogProductAddVisible" @close="bannerProductClose">
            <el-row :gutter="20">
                <el-col :span="16">
                    <el-autocomplete
                        style="width:100%"
                        class="inline-input"
                        v-model="productForm.productId"
                        :fetch-suggestions="handleAddingSearch"
                        placeholder="请输入内容"
                        :trigger-on-focus="false"
                        @select="handleAddingSearchiem"
                    ></el-autocomplete>
                </el-col>
                <el-col :span="6">
                    <el-button
                        type="primary"
                        @click.stop="handleAddToBanner">加入到列表</el-button>
                </el-col>
            </el-row>
            <el-table :data="tableDataAddingShow" border v-loading="loading" style="margin-top:20px">
                <el-table-column label="商品id">
                    <template slot-scope="scope">
                             <span>{{scope.row.productId}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="商品名称">
                    <template slot-scope="scope">
                        <span>{{scope.row.productName}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            @click.stop="handleAddingRemove(scope.$index,scope.row)">移除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pager-wrapper">
                <el-pagination
                    @size-change="handleSizeChangeProduct"
                    @current-change="handleCurrentChangeProduct"
                    :current-page.sync="currentPageProduct"
                    :page-sizes="[2, 5, 10, 20]"
                    :page-size="pageSizeproduct"
                    layout="total, sizes, slot, prev, pager, next"
                    :total="productTotalPage">
                        <span class="el-pagination__total refresh" @click="handleAddProduct(bannerId)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="bannerProductCancel">取消</el-button>
                <el-button type="primary" @click.stop="bannerProductCommit">确定</el-button>
            </div>
        </el-dialog>
        <!-- 商品分类广告跳转地址 -->
        <el-dialog :close-on-click-modal="false" title="添加 \ 修改 商品分类广告跳转地址" :visible.sync="dialogURLFormVisible"  @close="$refs.ruleURLForm.resetFields()">
            <el-form :model="bannerURLForm" :rules="bannerRules" ref="ruleURLForm">
                <el-form-item label="URL地址" :label-width="formLabelWidth">
                    <el-input v-model="bannerURLForm.detailUrl" type="text" auto-complete="off" placeholder="请输入关联的地址，以  https://  开头" maxlength = "255"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="CloseHandleAddURL">取 消</el-button>
                <el-button type="primary" @click="handleAddUpdateBannerURL">确 定</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
    import apiCommon from '@/api/common';
    import api from '@/api/bannerProductCategory';
    import util from '@/libs/util';
    import { bannerExpiryDaysFlagMap, bannerProductCategoryTypeMap, bannerStatusMap } from '@/api/dict';

    const columns = [
        {
            key: 'productCategoryName',
            title: '品类名称'
        },
        {
            key: 'sortNo',
            title: '序号',
            sort: 'custom'
        },
        {
            key: 'imageUrl',
            title: '广告预览图'
        },
        {
            key: 'detailUrl',
            title: '广告跳转地址'
        },
        {
            key: 'beginTime',
            title: '开始时间'
        },
        {
            key: 'endTime',
            title: '结束时间'
        }
    ];
    const form = {
        settingTime: false,
        expiryDaysFlag: '',
        status: '',
        // 设置时间的显示状态
        cityId: util.getCookie('cityId')
        // 设置时间的显示状态
    };
    const bannerURLForm = {};
    const productForm = {
        productId: ''
    };
    export default {
        data() {
            const validateSortNo = function(rule, value, callback) {
                if (value.toString().length > 3) {
                    callback(new Error('不得超过3位'));
                } else {
                    callback();
                }
            };
            const validateNaN = (rule, value, callback) => {
                if (isNaN(value) || !/^[0-9]\d*$/.test(value)) {
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
            const formValidateSortNo = { validator: validateSortNo, trigger: 'blur' };
            const formIsNaN = { validator: validateNaN, trigger: 'blur' };
            const formIsInt = { validator: validateInt, trigger: 'blur' };
            return {
                bannerId: '',
                tableData: [],
                // 一级品类列表
                fatherList: [],
                // 定义时间列表（开始时间、结束时间）
                bannerDateTime: [],
                // 关键字列表
                keywordList: [],
                // 规格列表
                specificationList: [],
                // 商品列表数据
                tableDataAdding: [],
                tableDataAddingShow: [],
                columns: columns,
                // 设置时间的显示状态
                settingTime: '',
                // 商品列表分页
                currentPageProduct: 1,
                productTotalPage: 0,
                pageSizeproduct: 5,
                currentData: [],
                // 商品分类广告列表分页
                currentPage: 1,
                totalPage: 0,
                pageSize: 5,

                loading: false,
                // 添加、修改商品分类广告窗口
                dialogFormVisible: false,
                // 修改关联URL地址窗口
                dialogURLFormVisible: false,
                // 添加广告商品列表
                dialogProductAddVisible: false,
                formLabelWidth: '120px',
                bannerURLForm: JSON.parse(JSON.stringify(bannerURLForm)),
                form: JSON.parse(JSON.stringify(form)),
                // 广告商品列表
                productForm: JSON.parse(JSON.stringify(productForm)),
                formType: '',
                rules: {
                    status: [
                        { required: true, message: '状态不能为空', trigger: 'blur' }
                    ],
                    imageUrl: [
                        { required: true, message: '广告预览图不能为空', trigger: 'blur' }
                    ],
                    width: [
                        { required: true, message: '宽度不能为空', trigger: 'blur' },
                        { type: 'number', message: '宽度必须为数字值' }
                    ],
                    height: [
                        { required: true, message: '高度不能为空', trigger: 'blur' },
                        { type: 'number', message: '宽度必须为数字值' }
                    ],
                    expiryDaysFlag: [
                        { required: true, message: '有效时间不能为空', trigger: 'blur' }
                    ],
                    type: [
                        { required: true, message: '类型不能为空', trigger: 'blur' }
                    ],
                    productCategoryId: [
                        { required: true, message: '一级品类不能为空', trigger: 'blur' }
                    ],
                    sortNo: [
                        { required: true, message: '序号不能为空', trigger: 'blur' },
                        formValidateSortNo,
                        formIsInt,
                        formIsNaN
                    ]
                },
                bannerRules: {
                    detailUrl: [
                        { required: true, message: 'URL地址不能为空', trigger: 'blur' }
                    ]
                },
                bannerExpiryDaysFlag: [],
                status: [],
                type: [],
                // 七牛表单
                qiniuForm: {},
                cityList: []
            };
        },
        methods: {
            // 定义时间的显示状态
            handleGroupChange(data) {
                if (data === '0') {
                    this.settingTime = true;
                } else {
                    this.settingTime = false;
                }
            },
            // search() {
            //     this.currentPage = 1;
            //     this.bannerProductCategoryPagers();
            // },
            // // 清除搜索
            // clear() {
            //     this.areaName = '';
            //     this.currentPage = 1;
            //     this.bannerProductCategoryPagers();
            // },
            // 提交表单
            handleSubmit() {
                const formType = this.formType;
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        if (formType === 'edit') {
                            const param = this.form;
                            if (Number(param.width) <= 0 || Number(param.height) <= 0) {
                                return this.$notify.error('图片的尺寸不得小于0');
                            }
                            if (!Number.isInteger(Number(param.width)) || !Number.isInteger(Number(param.height))) {
                                return this.$notify.error('图片的尺寸不得为小数');
                            }
                            if (param.expiryDaysFlag === '0') {
                                if (!this.bannerDateTime || this.bannerDateTime.length === 0 || this.bannerDateTime.some(res => res === '')) {
                                    return this.$notify.error('请选择定义时间');
                                }
                                if (this.bannerDateTime[0] === this.bannerDateTime[1]) {
                                    return this.$notify.error('起止时间不能一样');
                                }
                                param.beginTime = util.formatTime(new Date(this.bannerDateTime[0]));
                                param.endTime = util.formatTime(new Date(this.bannerDateTime[1]));
                            } else {
                                param.beginTime = '';
                                param.endTime = '';
                            }
                            if (param.type === '2') {
                                api.getBannerProduct({ bannerId: param.id }).then(res => {
                                    if (res.code === 100) {
                                        if (res.content.length > 0) {
                                            return this.$notify.error('广告存在关联商品,不能变更广告类型');
                                        } else {
                                            this.bannerProductCategoryUpdate(param);
                                        }
                                    }
                                });
                            } else {
                                this.bannerProductCategoryUpdate(param);
                            }
                        } else if (formType === 'add') {
                            const param = this.form;
                            if (Number(param.width) <= 0 || Number(param.height) <= 0) {
                                return this.$notify.error('图片的尺寸不得小于0');
                            }
                            if (!Number.isInteger(Number(param.width)) || !Number.isInteger(Number(param.height))) {
                                return this.$notify.error('图片的尺寸不得为小数');
                            }
                            if (param.expiryDaysFlag === '0') {
                                if (!this.bannerDateTime || this.bannerDateTime.length === 0 || this.bannerDateTime.some(res => res === '')) {
                                    return this.$notify.error('请选择定义时间');
                                }
                                if (this.bannerDateTime[0] === this.bannerDateTime[1]) {
                                    return this.$notify.error('起止时间不能一样');
                                }
                                param.beginTime = util.formatTime(new Date(this.bannerDateTime[0]));
                                param.endTime = util.formatTime(new Date(this.bannerDateTime[1]));
                            }
                            api.bannerProductCategoryInsert(param).then(res => {
                                if (res.code === 100) {
                                    this.$notify.success(res.message || '成功');
                                    this.dialogFormVisible = false;
                                    this.bannerProductCategoryPagers();
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
            // 取消表单
            handleCancel() {
                this.form = JSON.parse(JSON.stringify(form));
                this.dialogFormVisible = false;
                this.$refs.ruleForm.resetFields();
            },
            // 点击添加按钮
            handleAdd() {
                this.formType = 'add';
                this.form = JSON.parse(JSON.stringify(form));
                // 有效时间默认永久有效
                this.bannerDateTime = [];
                this.form.expiryDaysFlag = '1';
                this.form.status = '1';
                this.settingTime = false;
                this.dialogFormVisible = true;
            },
            // 点击编辑按钮
            handleEdit(data) {
                this.formType = 'edit';
                const formData = JSON.parse(JSON.stringify(data));
                if (formData.expiryDaysFlag === '0') {
                    this.settingTime = true;
                } else {
                    this.settingTime = false;
                }
                this.form = JSON.parse(JSON.stringify(formData));

                if (this.form.beginTime.trim() || this.form.endTime.trim()) {
                    this.bannerDateTime = [this.form.beginTime, this.form.endTime];
                }
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
                    api.bannerProductCategoryDelete(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.bannerProductCategoryPagers();
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
            // 更新商品分类广告
            bannerProductCategoryUpdate(param) {
                api.bannerProductCategoryUpdate(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogFormVisible = false;
                        this.bannerProductCategoryPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            // 点击状态直接修改当前广告的状态
            rowClick(row, event, column) {
                if (column.label === '状态') {
                    row.status = row.status === '1' ? '0' : '1';
                    const param = {
                        id: row.id,
                        status: row.status
                    };
                    api.bannerProductCategoryUpdate(param).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.bannerProductCategoryPagers();
                        } else {
                            // this.$notify.error(res.message || '未知错误');
                        }
                    });
                }
            },
            // 每页显示数改变时
            handleSizeChange(pageSize) {
                this.pageSize = pageSize;
                this.bannerProductCategoryPagers();
            },
            // 翻页时
            handleCurrentChange(currentPage) {
                this.currentPage = currentPage;
                this.bannerProductCategoryPagers();
            },
            // 关联商品列表翻页相关
            handleSizeChangeProduct(pageSize) {
                this.pageSizeproduct = pageSize;
                this.handleListPager();
            },
            handleCurrentChangeProduct(currentPage) {
                const all = this.tableDataAdding;
                const from = (currentPage - 1) * this.pageSizeproduct;
                this.tableDataAddingShow = all.slice(from, from + this.pageSizeproduct);
            },
            // 图片上传到七牛云之前
            handleBeforeUpload() {
                const param = {
                    // fileName: uuid.v1() + '-' + file.name
                    fileName: new Date().getTime() + '_' + Math.random().toString(32).substring(2)
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
                this.$set(this.form, 'imageUrl', fileName);
            },
            // 确定添加商品到广告
            bannerProductCommit() {
                let productIdList = '';
                let productNameList = '';
                this.tableDataAdding.forEach(item => {
                    productIdList += item.productId + ',';
                    productNameList += item.productName + ',';
                });
                const param = {
                    productIdList: productIdList,
                    bannerId: this.bannerId,
                    productNameList: productNameList
                };
                api.insertBannerProduct(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogProductAddVisible = false;
                        this.tableDataAdding = [];
                        this.tableDataAddingShow = [];
                        this.keywordList = [];
                    }
                });
            },
            // 取消商品到广告列表
            bannerProductCancel() {
                this.dialogProductAddVisible = false;
                this.tableDataAdding = [];
                this.tableDataAddingShow = [];
            },
            // 关联商品到广告关闭操作
            bannerProductClose() {
                this.tableDataAdding = [];
                this.tableDataAddingShow = [];
                this.keywordList = [];
            },
            // 添加商品到广告商品列表中
            handleAddToBanner() {
                if (!this.productForm.productSelected.id) {
                    return this.$notify.error('请选择');
                }
                let has = false;
                this.tableDataAdding.forEach(item => {
                    if (item.productId === this.productForm.productSelected.id) {
                        this.$notify.error('该商品已存在，请不要重复添加！');
                        has = true;
                    }
                });
                if (has) {
                    return false;
                }
                this.keywordList.forEach(item => {
                    if (item.id === this.productForm.productSelected.id) {
                        this.productForm.productSelected.value = item.name;
                    }
                });
                const param = {
                    productId: this.productForm.productSelected.id,
                    productName: this.productForm.productSelected.value
                };
                this.tableDataAdding.push(param);
                this.handleListPager();
                this.productForm.productSelected = {};
                this.productForm.productId = '';
            },
            // 1、将对应数据插入到当前列表；2、通过改变总数来改变总页码
            handleListPager() {
                const pageLimit = this.pageSizeproduct;
                const totalNum = this.tableDataAdding.length;
                const currentPage = this.currentPageProduct;
                const from = (currentPage - 1) * pageLimit;
                this.tableDataAddingShow = this.tableDataAdding.slice(from, from + pageLimit);
                this.productTotalPage = totalNum;
            },
            // 移除关联列表的商品
            handleAddingRemove(index, data) {
                // this.tableDataAddingShow.splice(data, 1);
                this.tableDataAddingShow = this.tableDataAddingShow.filter(item => item.productId !== data.productId);
                this.tableDataAdding = this.tableDataAdding.filter(item => item.productId !== data.productId);
                if (this.tableDataAddingShow.length === 0) {
                    this.currentPageProduct = this.currentPageProduct - 1;
                }
                this.handleListPager();
            },
            // 获取商品分类广告数据Pagers
            bannerProductCategoryPagers() {
                const param = {
                    page: 1,
                    limit: 5
                };
                param.page = this.currentPage;
                param.limit = this.pageSize;

                api.bannerProductCategoryPagers(param).then(res => {
                    if (res.records) {
                        this.tableData = res.records.map(item => {
                        // 开始、结束时间改写
                            item.beginTime = util.parseTimeStr(item.beginTime);
                            item.endTime = util.parseTimeStr(item.endTime);
                            return item;
                        });
                    } else {
                        this.tableData = [];
                        this.$notify.info('无数据');
                    }
                    this.totalPage = res.total;
                });
            },
            // 添加URL关联的广告
            handleAddURL(data) {
                const formData = JSON.parse(JSON.stringify(data));
                this.bannerURLForm = JSON.parse(JSON.stringify(formData));
                this.dialogURLFormVisible = true;
            },
            // 更新广告关联的URL
            handleAddUpdateBannerURL() {
                const param = {
                    id: this.bannerURLForm.id,
                    detailUrl: this.bannerURLForm.detailUrl
                };
                if (param.detailUrl.indexOf('http') === -1) {
                    return this.$notify.error('地址必须以 http 开头');
                }
                api.bannerProductCategoryUpdate(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.dialogURLFormVisible = false;
                        this.bannerProductCategoryPagers();
                    } else {
                        // this.$notify.error(res.message || '未知错误');
                    }
                });
            },
            // 关闭关联URL的窗口
            CloseHandleAddURL() {
                this.$refs.ruleURLForm.resetFields();
                this.dialogURLFormVisible = false;
            },
            // 添加商品关联的广告
            handleAddProduct(data) {
                this.bannerId = data;
                const param = {
                    bannerId: data
                };
                this.productForm = JSON.parse(JSON.stringify(productForm));
                api.getBannerProduct(param).then(res => {
                    if (res.code === 100) {
                        this.tableDataAdding = res.content;
                        this.handleListPager();
                    }
                });
                this.dialogProductAddVisible = true;
            },
            // 获取父级品类列表
            productCategoryBasic() {
                const param = {
                    fatherId: 0
                };
                apiCommon.productCategoryBasic(param).then(res => {
                    if (res.code === 100) {
                        this.fatherList = res.content;
                    } else {
                        this.$notify.error('服务器似乎开了点小差');
                    }
                });
            },
            handleAddingSearchiem(data) {
                this.productForm.productSelected = data;
            },
            // 获取商品列表
            handleAddingSearch(query, cb) {
                if (!query) {
                    return;
                }
                const param = {
                    keyword: query
                };
                api.cityProductList(param).then(res => {
                    if (res.code === 100) {
                        if (res.content) {
                            this.keywordList = res.content.map(item => {
                                item.value = item.name;
                                return item;
                            });
                        }
                    } else {
                        this.keywordList = [];
                    }
                    cb(this.keywordList)
                });
            },
            getBannerExpiryDaysFlag() {
                return bannerExpiryDaysFlagMap().then(res => {
                    if (res.code === 100) {
                        const resContentMap = res.content;
                        for (const item in resContentMap) {
                            if (resContentMap.hasOwnProperty(item)) {
                                const ele = resContentMap[item];
                                this.bannerExpiryDaysFlag.push({ id: item, name: ele });
                            }
                        }
                    }
                });
            },
            getBannerProductCategoryType() {
                return bannerProductCategoryTypeMap().then(res => {
                    if (res.code === 100) {
                        const resContentMap = res.content;
                        for (const item in resContentMap) {
                            if (resContentMap.hasOwnProperty(item)) {
                                const ele = resContentMap[item];
                                this.type.push({ id: item, name: ele });
                            }
                        }
                    }
                });
            },
            getBannerStatus() {
                return bannerStatusMap().then(res => {
                    if (res.code === 100) {
                        const resContentMap = res.content;
                        for (const item in resContentMap) {
                            if (resContentMap.hasOwnProperty(item)) {
                                const ele = resContentMap[item];
                                this.status.push({ id: item, name: ele });
                            }
                        }
                    }
                });
            }
        },
        async created() {
            await Promise.all([
                this.getBannerExpiryDaysFlag(),
                this.getBannerProductCategoryType(),
                this.getBannerStatus()
            ]);
            this.bannerProductCategoryPagers();
            this.productCategoryBasic();
        }
    };
</script>
