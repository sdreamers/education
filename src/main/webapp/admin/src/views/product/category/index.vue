<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row :gutter="20">
                <el-col :span="4">
                    <el-input v-model="searchInput" placeholder="搜索一级品类"></el-input>
                </el-col>
                <el-col :span="4">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleClear">清除</el-button>
                </el-col>
                <el-col :span="16">
                    <el-button style="float: right;margin-left: 10px;" @click="$router.push({name: '/productGroup/index'})">分组设置</el-button>
                    <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_product/productCategory/insert']">添加记录</el-button>
                </el-col>
            </el-row>
            <el-row :gutter="8" class="list-con clearfix">
                <el-col :span="12">
                    <h2>一级品类</h2>
                    <el-table :data="tableData01" border @row-click="rowClick01" v-loading="loading01"
                              @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columnsBD" :prop="column.key" :label="column.title"
                                         :key="key" :sortable="column.sort">
                            <template slot-scope="scope">
                                <img v-if="column.key === 'previewImageURL'" style="width: 60px;height: 60px;"
                                     :src="scope.row.previewImageURL"/>
                                <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                    type="text"
                                    v-if="G.permission['_product/productCategory/update']"
                                    @click.stop="handleEdit(scope.row)">编辑
                                </el-button>
                                <el-button
                                    type="text"
                                    v-if="G.permission['_product/productCategory/delete']"
                                    @click.stop="handleRemove(scope.row)">删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pager-wrapper">
                        <el-pagination
                            @size-change="handleSizeChange01"
                            @current-change="handleCurrentChange01"
                            :current-page.sync="currentPage01"
                            :page-sizes="[2, 5, 10, 20]"
                            :page-size="pageSize01"
                            layout="total, sizes, slot, prev, pager, next"
                            :total="totalPage01">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange01(currentPage01)"><i
                                class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
                </el-col>
                <el-col :span="12">
                    <h2>二级品类</h2>
                    <el-table :data="tableData02" border v-loading="loading02" @sort-change='sortChange'>
                        <el-table-column v-for="(column,key) in columnsLOB" :prop="column.key" :label="column.title"
                                         :key="key" :sortable="column.sort" :width="column.width">
                            <template slot-scope="scope">
                                <img v-if="column.key === 'previewImageURL'" style="width: 60px;height: 60px;"
                                     :src="scope.row.previewImageURL"/>
                                <span v-if="column.key !== 'previewImageURL'">{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="所属分组">
                            <template slot-scope="scope">
                                <el-select v-model="scope.row.selected" placeholder="请选择"
                                           @change="handleListGroupChange(scope.row)">
                                    <el-option
                                        v-for="item in ListGroup"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
                                    </el-option>
                                </el-select>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                    type="text"
                                    @click.stop="handleEdit(scope.row)">编辑
                                </el-button>
                                <el-button
                                    type="text"
                                    @click.stop="handleRemove(scope.row)">删除
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="pager-wrapper">
                        <el-pagination
                            @size-change="handleSizeChange02"
                            @current-change="handleCurrentChange02"
                            :current-page.sync="currentPage02"
                            :page-sizes="[2, 5, 10, 20]"
                            :page-size="pageSize02"
                            layout="total, sizes, slot, prev, pager, next"
                            :total="totalPage02">
                            <span class="el-pagination__total refresh" @click="handleCurrentChange02(currentPage02)"><i
                                class="el-icon-refresh"></i></span>
                        </el-pagination>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <edit
            v-if="dialogFormVisible"
            :form="form"
            :fatherList="fatherList"
            :formType="formType"
            @close="handleCloseEdit"></edit>
    </section>
</template>
<script>
    import api from '@/api/product';
    import apiCommon from '@/api/common';
    import common from '@/components/common';
    import headerList from '@/config/headerList';

    import edit from './edit';

    const columnsBD = [
        {
            key: 'sortNo',
            title: '序号',
            sort: 'custom'
        },
        {
            key: 'previewImageURL',
            title: '预览图片'
        },
        {
            key: 'name',
            title: '一级品类名称',
            sort: 'custom'
        }
    ];
    const columnsLOB = [
        {
            key: 'sortNo',
            title: '序号',
            sort: 'custom'
        },
        {
            key: 'previewImageURL',
            title: '预览图片'
        },
        {
            key: 'name',
            title: '二级品类名称',
            sort: 'custom',
            width: 120
        }
    ];
    const form = {
        sortNo: '',
        categoryNo: '',
        name: '',
        fatherId: '',
        previewImageURL: ''
    };
    export default {
        components: {
            edit,
            common
        },
        data() {
            return {
                headerList: headerList,

                searchInput: '',

                tableData01: [],
                tableData02: [],
                columnsBD: columnsBD,
                columnsLOB: columnsLOB,

                ListGroup: [],

                currentPage01: 1,
                totalPage01: 0,
                pageSize01: 5,

                sortBy: '',
                direction: '',
                currentPage02: 1,
                totalPage02: 0,
                pageSize02: 5,

                loading01: false,
                loading02: false,

                dialogFormVisible: false,
                form: JSON.parse(JSON.stringify(form)),
                formType: '',

                fatherList: []
            };
        },
        methods: {
            handleCloseEdit(data) {
                window.LOG(data);
                let fatherId = 0;
                if (data) {
                    fatherId = data.id;
                }
                this.productCategoryPagers(fatherId);
                this.dialogFormVisible = false;
            },
            handleSearch() {
                this.currentPage01 = 1;
                this.productCategoryPagers();
                this.productGroupListGroup(-99);
            },
            handleClear() {
                this.searchInput = '';
                this.productCategoryPagers();
                this.productGroupListGroup(-99);
            },
            handleAdd() {
                this.formType = 'add';
                this.form = JSON.parse(JSON.stringify(form));
                this.productCategoryBasic();
                this.dialogFormVisible = true;
            },
            // 编辑
            handleEdit(data) {
                this.formType = 'edit';
                this.form = JSON.parse(JSON.stringify(data));
                this.productCategoryBasic();
                this.dialogFormVisible = true;
            },
            // 删除
            handleRemove(data) {
                this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const param = {
                        id: data.id
                    };
                    this.productCategoryDelete(param).then(res => {
                        window.LOG(res);
                        if (data.fatherId) {
                            this.productCategoryPagers(data.fatherId);
                        } else {
                            this.productCategoryPagers();
                        }
                    });
                }).catch(() => {
                    this.$notify({
                        type: 'info',
                        message: '取消'
                    });
                });
            },
            // 修改二级品类的单品分组
            handleListGroupChange(data) {
                const groupId = data.selected;
                const id = data.id;
                this.productCategoryUpdate({ groupId, id });
            },
            // 点击一级品类的行时，获取该品类的二级品类，以及分组
            rowClick01(row) {
                window.LOG(row);
                this.currentPage02 = 1;
                const id = row.id;
                this.productCategoryPagers(id);
                this.productGroupListGroup(id);
            },
            // 一级品类每页显示数改变时
            handleSizeChange01(pageSize) {
                this.pageSize01 = pageSize;
                this.productCategoryPagers();
            },
            // 一级品类翻页时
            handleCurrentChange01(currentPage) {
                this.currentPage01 = currentPage;
                this.productCategoryPagers();
            },
            // 二级品类每页显示数改变时
            handleSizeChange02(pageSize) {
                this.pageSize02 = pageSize;
                this.productCategoryPagers(this.fatherId);
            },
            // 二级品类翻页时
            handleCurrentChange02(currentPage) {
                this.currentPage02 = currentPage;
                this.productCategoryPagers(this.fatherId);
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
            // 获取二级品类的分组
            productGroupListGroup(categoryId) {
                const param = {
                    categoryId
                };
                api.productGroupListGroup(param).then(res => {
                    this.ListGroup = res;
                    if (res.length === 0) {
                        this.tableData02 = [];
                        this.currentPage02 = 1;
                        this.totalPage02 = 0;
                        this.pageSize02 = 5;
                    }
                    this.ListGroup.unshift({ id: '0', name: '请选择分组' });
                });
            },
            // 修改二级品类单品的分组
            productCategoryUpdate(data) {
                return api.productCategoryUpdate(data).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message);
                    } else {
                        this.$notify.warning(res.message || '发生未知错误');
                    }
                    return res;
                });
            },
            // 添加二级品类单品的分组
            productCategoryInsert(data) {
                return api.productCategoryInsert(data).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message);
                    } else {
                        this.$notify.warning(res.message || '发生未知错误');
                    }
                    return res;
                });
            },
            // 删除二级品类单品的分组
            productCategoryDelete(data) {
                return api.productCategoryDelete(data).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message);
                    }
                    return res;
                });
            },
            // 获取一、二级品类列表
            productCategoryPagers(fatherId = 0) {
                if (!this.G.permission['_product/productCategory/pages']) {
                    return this.$notify.error('没有权限');
                }
                const param = {
                    page: 1,
                    limit: 5,
                    fatherId: fatherId
                };
                // 如果 fatherid 不为 0，为二级菜单，否则为 1 级菜单
                if (fatherId) {
                    this.loading02 = true;
                    this.fatherId = fatherId;
                    param.page = this.currentPage02;
                    param.limit = this.pageSize02;
                    param.sortBy = this.sortBy ? this.sortBy : undefined;
                    param.direction = this.direction ? this.direction : undefined;
                } else {
                    this.loading01 = true;
                    this.fatherId = 0;
                    param.page = this.currentPage01;
                    param.limit = this.pageSize01;
                    param.sortBy = this.sortBy ? this.sortBy : undefined;
                    param.direction = this.direction ? this.direction : undefined;
                    if (this.searchInput) {
                        param.categoryName = this.searchInput;
                    }
                }
                api.productCategoryPagers(param).then(res => {
                    if (fatherId) {
                        this.loading02 = false;
                        if (res.records) {
                            this.tableData02 = res.records.map(item => {
                                item.selected = item.groupId ? item.groupId : '请选择分组';
                                return item;
                            });
                        } else {
                            this.tableData02 = [];
                            this.$notify.error('无数据');
                        }
                        this.totalPage02 = res.total;
                    } else {
                        this.loading01 = false;
                        this.tableData01 = res.records;
                        this.totalPage01 = res.total;
                    }
                });
            },
            sortChange(column) {
                console.log(column + '-' + column.prop + '-' + column.order);
                this.sortBy = column.prop;
                this.direction = column.order === 'ascending' ? 'asc' : 'desc';
                this.productCategoryPagers(this.fatherId);
            }
        },
        created() {
            this.productCategoryPagers();
        }
    };
</script>
