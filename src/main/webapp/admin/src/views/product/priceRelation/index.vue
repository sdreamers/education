<template>
    <section class="view-content">
        <el-card shadow="none">
            <common :list="headerList"></common>
            <el-row class="list-con clearfix">
                    <el-row class="mb20" :gutter="20">
                        <el-col :span="24">
                            <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_priceRelation/insertPriceRelation']">添加价格关系</el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="tableData" border>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                            <template slot-scope="scope">
                                <span>{{scope.row[column.key]}}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                type="text"
                                v-if="G.permission['_priceRelation/immediateExecutionPriceRelation']"
                                @click.stop="handleExcute(scope.row)">立即执行</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_priceRelation/updatePriceRelation']"
                                @click.stop="handleEdit(scope.row)">编辑</el-button>
                                <el-button
                                type="text"
                                v-if="G.permission['_priceRelation/deletePriceRelation']"
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

        <edit
            v-if="dialogFormVisible"
            :formType="formType"
            :form="form"
            :cityMap="cityMap"
            :computeMap="computeMap"
            :relationScope="relationScope"
            :productMerchantTypeMap="productMerchantTypeMap"
            @close="dialogFormVisible=false,priceRelationPagers()"></edit>
    </section>
</template>
<script>
import api from '@/api/productRelation';
import apiComon from '@/api/common';
import util from '@/libs/util';
import edit from './edit';
import common from '@/components/common';
import headerList from '@/config/headerList';

const columns = [
    { key: 'operatorTime', title: '操作时间' },
    { key: 'cityId_text', title: '生效城市' },
    { key: 'relationCityId_text', title: '对比城市' },
    { key: 'level_text', title: '级别' },
    { key: 'scope_text', title: '生效范围' },
    { key: 'computeMode_text', title: '价格关系' },
    { key: 'compute', title: '计算公式' }
];
const relationScope = {
    city: '城市',
    rootCategory: '一级分类'
};
const computeMap = {
    add: '加',
    sub: '减',
    multiply: '乘',
    divide: '除'
}
const computeSymbolMap = {
    add: '+',
    sub: '-',
    multiply: 'x',
    divide: '÷'
}
const form = {
    level: '',
    cityId: '',
    relationCityId: '',
    computeMode: '',
    value: '',
    relationId: []
};
export default {
    components: {
        edit,
        common
    },
    data() {
        return {
            headerList: headerList,


            areaName: '',

            tableData: [],
            columns: columns,

            currentPage: 1,
            totalPage: 0,
            pageSize: 20,

            dialogFormVisible: false,
            form: JSON.parse(JSON.stringify(form)),
            formType: '',

            relationScope,
            productMerchantTypeList: [],
            productMerchantTypeMap: {},

            computeMap,
            computeSymbolMap,

            cityMap: {}
        };
    },
    methods: {
        handleSearch() {
            this.currentPage = 1;
            this.priceRelationPagers();
        },
        // 清除搜索
        handleClear() {
            this.areaName = '';
            this.currentPage = 1;
            this.priceRelationPagers();
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
            this.form = JSON.parse(JSON.stringify(data));
            this.dialogFormVisible = true;
        },
        handleExcute(data) {
            console.log(data);
            this.$confirm('确定立即执行？', '确认', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id,
                    userId: this.G.userInfo.id,
                    userName: this.G.userInfo.nickname
                }
                api.priceRelationExecute(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.priceRelationPagers();
                    }
                });
            })
        },
        // 点击删除按钮
        handleRemove(data) {
            window.LOG(data);
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                api.priceRelationDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.checkIsLastItemOfLastPage();
                        this.priceRelationPagers();
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
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.priceRelationPagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.priceRelationPagers();
        },
        checkIsLastItemOfLastPage() {
            if (this.tableData.length === 1 && this.currentPage > 1) {
                this.currentPage = this.currentPage - 1;
            }
        },
        // 获取数据
        priceRelationPagers() {
            if (!this.G.permission['_priceRelation/listByPage']) {
                return this.$notify.error('没有列表数据权限');
            }
            const param = {
                page: 1,
                limit: 5
            };
            param.page = this.currentPage;
            param.limit = this.pageSize;
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            if (this.areaName) {
                param.areaName = this.areaName;
            }
            api.priceRelationPagers(param).then(res => {
                window.LOG(res);

                if (res.records && res.records.length) {
                    this.tableData = res.records.map(item => {
                        item.cityId_text = this.cityMap[item.cityId]
                        item.relationCityId_text = this.cityMap[item.relationCityId];
                        item.level_text = this.relationScope[item.level];
                        item.operatorTime = util.formatTime(item.operatorTime ? new Date(item.operatorTime) : '');
                        item.scope_text = item.level === 'city' ? '全部商品' : '';
                        if (item.scope_text === '') {
                            if (item.relationId && (typeof item.relationId === 'number')) {
                                item.relationId = item.relationId.toString();
                                item.scope_text = this.productMerchantTypeMap[item.relationId];
                            }
                        }


                        item.cityId = item.cityId.toString();
                        item.relationCityId = item.relationCityId.toString();

                        item.computeMode_text = this.computeMap[item.computeMode];

                        item.compute = item.cityId_text + '=' + item.relationCityId_text + (computeSymbolMap[item.computeMode] || '未知') + item.value;
                        return item;
                    });
                    this.totalPage = res.total;
                } else {
                    this.tableData = [];
                }
            });
        },
        getCityMap() {
            return apiComon.userCityUserCitys({ userId: this.G.userInfo.id }).then(res => {
                if (res) {
                    const cityMap = {};
                    res.forEach(item => {
                        if (item.children) {
                            item.children.forEach(ele => {
                                cityMap[ele.id] = ele.text;
                            })
                        }
                    });
                    this.cityMap = cityMap;
                }
            })
        },
        productCategoryPagers() {
            return api.productCategoryPagers({ page: 1, limit: 100, fatherId: 0 }).then(res => {
                if (res.records && res.records.length) {
                    this.productMerchantTypeList = res.records;
                    const productMerchantTypeMap = {};
                    res.records.forEach(item => {
                        productMerchantTypeMap[item.id] = item.name;
                    })
                    this.productMerchantTypeMap = productMerchantTypeMap;
                }
            })
        }
    },
    async created() {
        await this.getCityMap();
        await this.productCategoryPagers();
        this.priceRelationPagers();
    }
};
</script>
