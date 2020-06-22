<template>
    <!-- 添加商品到团购弹层 -->
    <el-dialog :close-on-click-modal="false" title="填写团购商品信息" :visible="true" @close="$emit('close')" class="dialogw80">
        <el-row :gutter="20">
            <el-col :span="6">
                <!-- <el-input v-model="formAdd.productName" type="text" auto-complete="off" placeholder="商品名称" :fetch-suggestions="handleAddingSearch"></el-input> -->
                <p>商品</p>
                <el-select
                    v-model="formAdd.productName"
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
            </el-col>
            <el-col :span="6">
                <p>规格</p>
                <el-select v-model="formAdd.specificationId" placeholder="请选择商品规格" :disabled="!formAdd.productName">
                    <el-option v-for="(item,key) in specificationList" :label="item.specificationName" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-col>
            <el-col :span="6">
                <el-button
                type="primary"
                @click.stop="handleAddToActivity" style="margin-top:20px" :disabled="!formAdd.specificationId">加入团购</el-button>
            </el-col>
        </el-row>
        <el-table :data="tableDataAdding" border style="margin-top:20px">
            <el-table-column label="序号">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.sortNo" type="text" auto-complete="off" maxlength="3"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column v-for="(column,key) in columnsAdding" :prop="column.key" :label="column.title" :key="key">
                <template slot-scope="scope">
                    <span>{{scope.row[column.key]}}</span>
                </template>
            </el-table-column>
            <el-table-column label="开团数量">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.openUpNum" type="text" auto-complete="off" maxlength="5"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="最大购买数量">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.maxPurchaseNum" type="text" auto-complete="off" maxlength="5"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="团购单价">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.price" type="text" auto-complete="off" maxlength="5"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="预计发货天数">
                <template slot-scope="scope">
                    <section style="text-align:center">
                        <el-input v-model="scope.row.appointShipDay" type="text" auto-complete="off" maxlength="3"></el-input>
                    </section>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                    type="text"
                    @click.stop="handleAddingRemove(scope.row)">移除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click.stop="handleAddingConfirm">确定</el-button>
        </div>
    </el-dialog>
</template>
<script>
const columnsAdding = [
    { key: 'cityProductName', title: '商品名称' },
    { key: 'citySpecificationName', title: '商品规格' },
    { key: 'oriPrice', title: '商品原单价' }
    // { key: 'quantity', title: '商品库存' }
];
import api from '@/api/groupBuying';
export default {
    props: ['groupBuyingId', 'groupBuyingCode'],
    data() {
        return {
            formAdd: {
                productId: '',
                specificationId: ''
            },
            keywordList: [],
            specificationList: [],

            tableDataAdding: [],
            columnsAdding: columnsAdding
        };
    },
    methods: {
        // 选中搜索下拉选项
        handleAddingSearchSelect(data) {
            this.formAdd.productId = data;
            this.formAdd.specificationId = '';
            const param = {
                productId: data,
                available: 1
            };
            api.productSpecificationSelectSpecification(param).then(res => {
                if (res.code === 100) {
                    this.specificationList = res.content;
                } else {
                    this.specificationList = [];
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
                // warehouseId: this.formAdd.warehouseId,
                available: 1,
                isAppDisplay: 1,
                name: query
            };
            api.productListProduct(param).then(res => {
                if (res.code === 100) {
                    this.keywordList = res.content;
                } else {
                    this.keywordList = [];
                }
            });
        },

        // 选中商品规格后加入团购
        handleAddToActivity() {
            const productId = this.formAdd.productId;
            const specificationId = this.formAdd.specificationId;

            // 装填商户信息
            const productArr = this.keywordList.filter(item => item.id === productId);
            const cityProduct = productArr[0];
            console.log('cityProduct', cityProduct);
            const cityProductId = cityProduct.id;
            const cityProductName = cityProduct.name;

            // 装填规格信息
            const selectedArr = this.specificationList.filter(item => item.id === specificationId);
            const citySpecification = selectedArr[0];
            console.log('citySpecification', citySpecification);
            const citySpecificationId = citySpecification.id;
            const citySpecificationName = citySpecification.specificationName;

            const price = citySpecification.price || 0;

            console.log(cityProductName, citySpecificationName);
            if (!cityProductName) {
                return this.$notify.error('未能获取商品名');
            }
            if (!citySpecificationName) {
                return this.$notify.error('未能获取规格名');
            }

            for (let index = 0; index < this.tableDataAdding.length; index++) {
                const element = this.tableDataAdding[index];
                if (element.citySpecificationId === this.formAdd.specificationId) {
                    return this.$notify.error('已经添加过该商品规格');
                }
            }

            this.tableDataAdding.push({
                sortNo: this.tableDataAdding.length + 1,
                cityProductId,
                cityProductName,
                citySpecificationName,
                citySpecificationId,
                price,
                oriPrice: price,
                openUpNum: '',
                appointShipDay: '',
                maxPurchaseNum: ''
            });
        },
        // 团购添加商品时，暂时移除
        handleAddingRemove(data) {
            this.tableDataAdding = this.tableDataAdding.filter(item => item.sortNo !== data.sortNo);
        },
        // 确定添加商品到团购
        handleAddingConfirm() {
            console.log('handleAddingConfirm')
            const param = this.tableDataAdding.map(item => {
                item.groupBuyingId = this.groupBuyingId;
                item.groupBuyingCode = this.groupBuyingCode;
                return item;
            });
            for (let index = 0; index < param.length; index++) {
                const element = param[index];
                if (element.activityPrice === '') {
                    return this.$notify.error(`第${index + 1}行未填写价格`);
                }
                if (element.stockNum === '') {
                    return this.$notify.error(`第${index + 1}行未填写库存`);
                }
                if (!element.openUpNum) {
                    return this.$notify.error(`第${index + 1}行未填写开团数量`);
                }
                if (isNaN(element.openUpNum) || !/^[0-9]+$/.test(element.openUpNum)) {
                    return this.$notify.error(`第${index + 1}行开团数量非正整数`);
                }
                if (!element.maxPurchaseNum) {
                    return this.$notify.error(`第${index + 1}行未填写最大可购买数量`);
                }
                if (isNaN(element.maxPurchaseNum) || !/^[0-9]+$/.test(element.maxPurchaseNum)) {
                    return this.$notify.error(`第${index + 1}行最大可购买数量非正整数`);
                }
                if (!element.appointShipDay) {
                    return this.$notify.error(`第${index + 1}行未填写预计发货天数`);
                }
                if (isNaN(element.appointShipDay) || !/^[0-9]+$/.test(element.appointShipDay)) {
                    return this.$notify.error(`第${index + 1}行预计发货天数非正整数`);
                }
                if (!element.price) {
                    return this.$notify.error(`第${index + 1}行未填写团购单价`);
                }
                if (isNaN(element.price) || !/^[0-9]{1,9}(\.[0-9]{1,2})?$/.test(element.price)) {
                    return this.$notify.error(`第${index + 1}行团购单价不合法`);
                }
                if (!element.sortNo) {
                    return this.$notify.error(`第${index + 1}行未填写序号`);
                }
                if (isNaN(element.sortNo) || !/^[0-9]+$/.test(element.sortNo)) {
                    return this.$notify.error(`第${index + 1}行序号非正整数`);
                }
            }
            console.log(param);
            api.groupBuyingProductInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                }
            });
        }
    }
};
</script>
