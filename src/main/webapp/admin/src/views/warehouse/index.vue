<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
                <el-button style="float: right;margin-left: 10px;" @click="handleAdd" v-if="G.permission['_stock/warehouse/insert']">添加冻库</el-button>
            </div>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :span="4">
                        <el-input v-model="searchString" placeholder="查询"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleClear">清除</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" border v-loading="loading" @sort-change='sortChange'>
                    <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key" :sortable="column.sort" >
                        <template slot-scope="scope">
                            <span>{{scope.row[column.key]}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="覆盖片区">
                        <template slot-scope="scope">
                            <el-button
                                type="text"
                                @click.stop="currentWarehouseId = scope.row.id,dialogCoverAreaVisible = true,warehouseCoverageAreaPagers(scope.row.id)">设置</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="库存快照" v-if="G.permission['_warehouse/warehouseSnapshotExcel']">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleSnapClick(scope.row)">导出</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="复制商品" v-if="G.permission['_stock/warehouse/copyWarehouseProduct']">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            @click.stop="handleCopy(scope.row)">复制</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_stock/warehouseCoverageArea/update']"
                            @click.stop="handleEdit(scope.row)">编辑</el-button>
                            <el-button
                            type="text"
                            v-if="G.permission['_stock/warehouse/delete']"
                            @click.stop="handleRemove(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="商品管理">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_product/maintain']"
                            @click.stop="handleMangeWarehouse(scope.row.id, 1)">管理</el-button>
                        </template>
                    </el-table-column>
                    <el-table-column label="库存">
                        <template slot-scope="scope">
                            <el-button
                            type="text"
                            v-if="G.permission['_warehouse/currentStockExcel']"
                            @click.stop="currentWarehouseId = scope.row.id,dialogExportVisible = true">导出</el-button>
                            <el-button
                            type="text" v-if="G.permission['_stock/warehouse/stockImport']"><label for="import">导入</label><input id="import" type="file" @change="importf" style="display:none"></el-button>
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
        <el-dialog :close-on-click-modal="false" title="添加 \ 修改 仓库基本信息" :visible.sync="dialogFormVisible" @close="$refs.ruleForm.resetFields()">
            <el-form :model="form" :rules="rules" ref="ruleForm">
                <el-form-item label="仓库名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" maxlength="200" type="text" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="省份" :label-width="formLabelWidth" prop="provinceId">
                    <el-select v-model="form.provinceId" placeholder="省份" @change="provinceChange" :disabled="formType === 'edit'">
                        <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="城市" :label-width="formLabelWidth" prop="cityId">
                    <el-select v-model="form.cityId" placeholder="城市" @change="cityChange" :disabled="formType === 'edit'">
                        <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="地区" :label-width="formLabelWidth" prop="districtId">
                    <el-select v-model="form.districtId" placeholder="地区" @change="districtChange">
                        <el-option v-for="(item,key) in districtArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="街道" :label-width="formLabelWidth">
                    <el-select v-model="form.streetId" placeholder="选择街道">
                        <el-option v-for="(item,key) in streetArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="地址" :label-width="formLabelWidth">
                    <el-input v-model="form.address" maxlength="200" type="text" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" @click="handleSubmit">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :close-on-click-modal="false" title="覆盖片区" :visible.sync="dialogCoverAreaVisible" >
                <el-button style="float: right;margin-left: 10px;margin-bottom: 20px" type="primary" @click="handleAddCoverageArea">添加覆盖片区</el-button>
            <el-table :data="tableDataCoverageArea" border v-loading="loading" >
                <el-table-column v-for="(column,key) in columnsCoverageArea" :prop="column.key" :label="column.title" :key="key">
                    <template slot-scope="scope">
                        <span>{{scope.row[column.key]}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            @click.stop="handleCoverageAreaRemove(scope.row)">移除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pager-wrapper">
                <el-pagination
                    @size-change="handleSizeChangeCoverageArea"
                    @current-change="handleCurrentChangeCoverageArea"
                    :current-page.sync="currentPageCoverageArea"
                    :page-sizes="[2, 5, 10, 20]"
                    :page-size="pageSizeCoverageArea"
                    layout="total, sizes, slot, prev, pager, next"
                    :total="totalPageCoverageArea">
                        <span class="el-pagination__total refresh" @click="handleCurrentChangeCoverageArea(currentPageCoverageArea)"><i class="el-icon-refresh"></i></span>
                </el-pagination>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="dialogCoverAreaVisible=false">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :close-on-click-modal="false" title="添加 覆盖片区" :visible.sync="dialogSettingCoverAreaVisible" @close="$refs.ruleFormCoverageArea.resetFields()">
            <el-form :model="formCoverageArea" :rules="rulesCoverageArea" ref="ruleFormCoverageArea">
                <el-form-item label="省份" :label-width="formLabelWidth" prop="provinceId">
                    <el-select v-model="formCoverageArea.provinceId" placeholder="省份" @change="coverProvinceChange">
                        <el-option v-for="(item,key) in provinceArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="城市" :label-width="formLabelWidth" prop="cityId">
                    <el-select v-model="formCoverageArea.cityId" placeholder="城市" @change="coverCityChange">
                        <el-option v-for="(item,key) in cityArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="地区" :label-width="formLabelWidth" prop="districtId">
                    <el-select v-model="formCoverageArea.districtId" placeholder="地区" >
                        <el-option v-for="(item,key) in districtArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogSettingCoverAreaVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitCoverageArea">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :close-on-click-modal="false" title="库存快照导出" :visible.sync="dialogInventoryExportVisible">
            <el-form :model="inventoryForm">
                <el-form-item label="导出日期" :label-width="formLabelWidth">
                    <el-date-picker
                        v-model="inventoryForm.date"
                        :editable="false"
                        :clearable="false"
                        type="date"
                        placeholder="选择日期"
                        :picker-options="pickerOption">
                    </el-date-picker>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogInventoryExportVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitInventoryExport">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog :close-on-click-modal="false" title="导出库存" :visible.sync="dialogExportVisible">
            <el-form :model="exportForm">
                <el-form-item label="商品上下架状态" :label-width="formLabelWidth">
                    <el-select v-model="exportForm.id" placeholder="商品上下架状态">
                        <el-option v-for="(item,key) in exportForm.statusArr" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogExportVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitExport">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog :close-on-click-modal="false" title="复制商品" :visible.sync="dialogCopyVisible">
            <el-form :model="copyForm">
                <el-form-item label="来源冻库" :label-width="formLabelWidth">
                    <el-select v-model="copyForm.id" placeholder="来源冻库">
                        <el-option v-for="(item,key) in copyForm.warehouseList" :label="item.name" :value="item.id" :key="key"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogCopyVisible=false">取 消</el-button>
                <el-button type="primary" @click="handleSubmitCopy">确 定</el-button>
            </div>
        </el-dialog>
    </section>
</template>
<script>
import api from '@/api/warehouse';
import apiCommon from '@/api/common';
import { warehouseByUserCurrentCity } from '@/api/dict';
import Util from '@/libs/util';

const columns = [
    { key: 'name', title: '冻库名称', sort: 'custom' },
    { key: 'provinceName', title: '省' },
    { key: 'cityName', title: '市' },
    { key: 'districtName', title: '区' },
    { key: 'streetName', title: '街道' },
    { key: 'address', title: '冻库地址', sort: 'custom' }
];
const columnsBackup = [
    { key: 'id', title: '冻库编号' },
    { key: 'name', title: '冻库名称' },
    { key: 'cityName', title: '所在城市' },
    { key: 'districtName', title: '所在区' },
    { key: 'streetName', title: '所在街道' },
    { key: 'address', title: '地址' }
];
const columnsCoverageArea = [
    { key: 'id', title: '冻库编号' },
    { key: 'warehouseName', title: '冻库名称' },
    { key: 'provinceName', title: '省份' },
    { key: 'cityName', title: '城市' },
    { key: 'districtName', title: '区域' }
];
const form = {
    name: '',
    provinceId: '',
    cityId: '',
    districtId: '',
    streetId: '',
    address: ''
};
const formCoverageArea = {
    warehouseId: '',
    provinceId: '',
    cityId: '',
    districtId: '',
    streetId: ''
};
const yesterday = new Date();
yesterday.setTime(yesterday.getTime() - 24 * 60 * 60 * 1000);
const yesterdayStr = yesterday.getFullYear() + '-' + (yesterday.getMonth() + 1) + '-' + yesterday.getDate();
export default {
    data() {
        return {
            pickerOption: {
                disabledDate(time) {
                    return time.getTime() > (Date.now() - 24 * 60 * 60 * 1000)
                }
            },
            searchString: '',

            tableData: [],
            columns: columns,

            tableDataBackup: [],
            tableDataCoverageArea: [],
            columnsBackup: columnsBackup,
            columnsCoverageArea: columnsCoverageArea,
            streetArr: [],
            districtArr: [],
            cityArr: [],
            provinceArr: [],
            totalPageCoverageArea: 0,
            currentPageCoverageArea: 1,
            pageSizeCoverageArea: 5,
            currentPage: 1,
            totalPage: 0,
            pageSize: 5,
            sortBy: '',
            direction: '',
            currentPageBackup: 1,
            totalPageBackup: 0,
            pageSizeBackup: 5,

            loading: false,

            dialogFormVisible: false,
            dialogInventoryExportVisible: false,
            dialogSettingCoverAreaVisible: false,
            dialogCoverAreaVisible: false,
            dialogLatestVisible: false,
            dialogBackupVisible: false,
            dialogExportVisible: false,
            dialogCopyVisible: false,

            formLabelWidth: '120px',
            form: JSON.parse(JSON.stringify(form)),
            formCoverageArea: JSON.parse(JSON.stringify(formCoverageArea)),
            inventoryForm: {
                date: new Date(yesterdayStr)
            },
            latestForm: {
                warehouseArr: []
            },
            backupForm: {
                selected: [],
                cityId: ''
            },
            exportForm: {
                id: '',
                statusArr: [
                    { id: '', name: '全部' },
                    { id: '0', name: '已下架,或未上架' },
                    { id: '1', name: '已上架' }
                ]
            },
            copyForm: {
                id: '',
                warehouseList: []
            },
            formType: '',
            rules: {
                name: [
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
            },
            rulesCoverageArea: {
                provinceId: [
                    { required: true, message: '请选择省份', trigger: 'blur' }
                ],
                cityId: [
                    { required: true, message: '请选择城市', trigger: 'blur' }
                ],
                districtId: [
                    { required: true, message: '请选择区域', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        coverProvinceChange() {
            this.formCoverageArea.cityId = '';
            this.formCoverageArea.districtId = '';
            this.getCity(this.formCoverageArea.provinceId);
        },
        coverCityChange() {
            this.formCoverageArea.districtId = '';
            this.getCoverageAreaDistrict();
        },
        provinceChange() {
            this.form.cityId = '';
            this.form.districtId = '';
            this.form.streetId = '';
            this.getCity();
        },
        cityChange() {
            this.form.districtId = '';
            this.form.streetId = '';
            this.getDistrict();
        },
        districtChange() {
            this.form.streetId = '';
            this.getStreet();
        },
        handleSnapClick(data) {
            this.currentWarehouseId = data.id;
            this.inventoryForm.date = new Date(yesterdayStr);
            this.dialogInventoryExportVisible = true;
        },
        handleSubmitCopy() {
            const param = {
                slaveWarehouseId: this.copyForm.id,
                masterWarehouseId: this.currentWarehouseId
            };
            api.copyWarehouseProduct(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogCopyVisible = false;
                } else {
                    // this.$notify.error(res.message || '失败');
                }
            })
        },
        handleCopy(data) {
            this.copyForm.id = '';
            this.currentWarehouseId = data.id;

            const param = {
                page: this.currentPage,
                limit: 100,
                cityId: Util.getCookie('cityId')
            };
            warehouseByUserCurrentCity(param).then(res => {
                if (res.code === 100) {
                    const arr = [];
                    const list = res.content;
                    for (const item in list) {
                        if (list.hasOwnProperty(item)) {
                            const element = list[item];
                            arr.push({ id: item, name: element });
                        }
                    }
                    this.copyForm.warehouseList = arr.filter(item => item.id !== data.id);
                    this.dialogCopyVisible = true;
                } else {
                    this.$notify.error('获取冻库列表异常');
                }
            });
            // console.log(this.copyForm.warehouseList)
        },
        handleSearch() {
            this.currentPage = 1;
            this.warehousePagers();
        },
        // 清除搜索
        handleClear() {
            this.searchString = '';
            this.currentPage = 1;
            this.warehousePagers();
        },
        importf(obj) {
            console.log(1);
                    // js解析Excel相关操作
            let wb; // 读取完成的数据
            const rABS = false; // 是否将文件读取为二进制字符串

            function fixdata(data) { // 文件流转BinaryString
                let o = '', l = 0;
                const w = 10240;
                for (; l < data.byteLength / w; ++l) o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w, l * w + w)));
                o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w)));
                return o;
            }
            function importf(obj) { // 导入
                if (!obj.files) {
                    return;
                }
                const f = obj.files[0];
                const reader = new FileReader();
                reader.onload = function(e) {
                    const data = e.target.result;
                    if (rABS) {
                        wb = XLSX.read(btoa(fixdata(data)), { // 手动转化
                            type: 'base64'
                        });
                    } else {
                        wb = XLSX.read(data, {
                            type: 'binary'
                        });
                    }
                // wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
                // wb.Sheets[Sheet名]获取第一个Sheet的数据
                // JSON.stringify( XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]));  转换成json字符串
                    const ExcelJson = XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]); // 将Excel转换成一个数组
                // 创建一个参数对象，传给后台
                    const stockImportVOList = [];
                    for (let i = 0; i < ExcelJson.length; i++) {
                        const row = ExcelJson[i];
                        const rowJson = eval('(' + JSON.stringify(row) + ')');
                        const StockImportVO = {
                            productId: rowJson.商品id,
                            productName: rowJson.商品名称,
                            specificationId: rowJson.规格id,
                            specificationName: rowJson.规格名称,
                            quantity: rowJson.库存件数,
                            cost: rowJson.平均成本价
                        };
                        stockImportVOList.push(StockImportVO);
                    }
                    const stockImportParams = {
                        stockImportVOList: stockImportVOList,
                        warehouseId: this.warehouseId
                    };
                // 发送导入请求
                    const url = '/stock/warehouse/stockImport';
                    axios.post(url, JSON.stringify(stockImportParams)).then(res => {
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                        } else {
                            this.$notify.success(res.message || '失败');
                        }
                    })
                };
                if (rABS) {
                    reader.readAsArrayBuffer(f);
                } else {
                    reader.readAsBinaryString(f);
                }
            }
            return importf(obj.target);
        },
        handleSubmitExport() {
            console.log('handleSubmitExport')
            if (this.exportForm.id === null) {
                return this.$notify.error('请选择商品上下架状态');
            }
            location.href = vars.URLApiBase + '/stock/warehouse/exportCurrentStockExcel?available=' + this.exportForm.id + '&warehouseId=' + this.currentWarehouseId;
            this.dialogExportVisible = false;
            this.$notify.success('加载中，请稍候');
        },
        handleSubmitBackup() {
            const param = {
                masterWarehouseId: this.currentWarehouseId,
                slaveWarehouseIds: this.backupForm.selected.map(item => item.slaveWarehouseId || item.id).join(',')
            };
            window.LOG(param);
            api.warehouseMasterSlaveInsert(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.dialogBackupVisible = false;
                    this.warehousePagers();
                }
            });
        },
        rowClickBackup(row) {
            window.LOG(row);
            let has = false;
            this.backupForm.selected.forEach(item => {
                if (row.name === item.slaveWarehouseName) {
                    has = true;
                }
            });
            if (!has) {
                row.slaveWarehouseName = row.name;
                this.backupForm.selected.push(row);
            }
        },
        handleClickBackup(data) {
            window.LOG(data);
            this.backupForm.selected = this.backupForm.selected.filter(item => item.slaveWarehouseId !== data.slaveWarehouseId);
        },
        // 获取最新商品
        handleSubmitLatest() {
            const param = {
                masterWarehouseId: this.currentWarehouseId,
                slaveWarehouseId: this.latestForm.slaveWarehouseId,
                // priceRatio: this.latestForm.priceRatio,
                costRatio: this.latestForm.costRatio
            };
            window.LOG(param);
            api.copyNewProduct(param).then(res => {
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.warehousePagers();
                    this.dialogLatestVisible = false;
                } else {
                    // this.$notify.error(res.message || '未知错误');
                }
            });
        },
        // 导出库存快照
        handleSubmitInventoryExport() {
            const param = {
                snapshotDate: Util.formatTime(new Date(this.inventoryForm.date)).replace(/-/g, '').split(' ')[0],
                warehouseId: this.currentWarehouseId
            };
            this.dialogInventoryExportVisible = false;
            this.$notify.success('加载中，请稍候');
            location.href = vars.URLApiBase + '/stock/warehouse/exportWarehouseSnapshotExcel?snapshotDate=' + param.snapshotDate + '&warehouseId=' + this.currentWarehouseId;
        },
        // 提交表单
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    if (formType === 'edit') {
                        const param = this.form;
                        api.warehouseUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.warehousePagers();
                            } else {
                                // this.$notify.error(res.message || '未知错误');
                            }
                        });
                    } else if (formType === 'add') {
                        const param = this.form;
                        api.warehouseInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.dialogFormVisible = false;
                                this.warehousePagers();
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
        async handleAdd() {
            this.formType = 'add';
            this.form = JSON.parse(JSON.stringify(form));
            await this.getProvince();
            await this.getStreet();
            await this.getDistrict();
            await this.getCity();
            this.dialogFormVisible = true;
        },
        // 提交覆盖片区表单
        handleSubmitCoverageArea() {
            this.$refs.ruleFormCoverageArea.validate(valid => {
                if (valid) {
                    const param = this.formCoverageArea;
                    this.formCoverageArea.warehouseId = this.currentWarehouseId;
                    api.insertWarehouseCoverageArea(param).then(res => {
                        console.log(res)
                        if (res.code === 100) {
                            this.$notify.success(res.message || '成功');
                            this.dialogSettingCoverAreaVisible = false;
                            this.warehouseCoverageAreaPagers(this.currentWarehouseId);
                        }
                    });
                } else {
                    this.$notify({
                        message: '请按要求填写表单',
                        type: 'warning'
                    });
                    return false;
                }
            });
        },
        // 取消覆盖片区表单
        handleCancelCoverageArea() {
            this.formCoverageArea = JSON.parse(JSON.stringify(formCoverageArea));
            this.dialogSettingCoverAreaVisible = false;
            this.$refs.ruleForm.resetFields();
        },
        // 点击添加覆盖片区
        async handleAddCoverageArea() {
            this.formCoverageArea = JSON.parse(JSON.stringify(formCoverageArea));
            await this.getProvince();
            await this.getCity();
            this.dialogSettingCoverAreaVisible = true;
        },
        // 获取覆盖片区的区域
        getCoverageAreaDistrict() {
            if (!this.formCoverageArea.cityId) {
                return;
            }
            const param = {
                cityId: this.formCoverageArea.cityId
            };
            return apiCommon.availableAreasDistricts(param).then(res => {
                if (res.code === 100) {
                    this.districtArr = res.content;
                }
            });
        },
        // 点击编辑按钮
        async handleEdit(data) {
            this.formType = 'edit';
            const $data = JSON.parse(JSON.stringify(data));
            for (const item in $data) {
                if ($data.hasOwnProperty(item)) {
                    const element = $data[item];
                    if (element === null) {
                        $data[item] = '';
                    }
                }
            }
            this.form = $data;
            await this.getProvince();
            await this.getStreet();
            await this.getDistrict();
            await this.getCity();
            this.dialogFormVisible = true;
        },
        // 点击移除覆盖片区按钮
        handleCoverageAreaRemove(data) {
            this.$confirm('删除以后将无法恢复，确定要删除吗？', '确认删除', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                const param = {
                    id: data.id
                };
                window.LOG(param);
                api.warehouseCoverageAreaDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.warehouseCoverageAreaPagers(this.currentWarehouseId);
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
                api.warehouseDelete(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.warehousePagers();
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
        // 每页显示数改变时(覆盖片区)
        handleSizeChangeCoverageArea(pageSizeCoverageArea) {
            this.pageSizeCoverageArea = pageSizeCoverageArea;
            this.warehouseCoverageAreaPagers(this.currentWarehouseId);
        },
        // 翻页时(覆盖片区)
        handleCurrentChangeCoverageArea(currentPageCoverageArea) {
            this.currentPageCoverageArea = currentPageCoverageArea;
            this.warehouseCoverageAreaPagers(this.currentWarehouseId);
        },
        // 每页显示数改变时
        handleSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.warehousePagers();
        },
        // 翻页时
        handleCurrentChange(currentPage) {
            this.currentPage = currentPage;
            this.warehousePagers();
        },
        // 每页显示数改变时
        handleSizeChangeBackup(pageSize) {
            this.pageSizeBackup = pageSize;
            this.warehousePagersExclude();
        },
        // 翻页时
        handleCurrentChangeBackup(currentPage) {
            this.currentPageBackup = currentPage;
            this.warehousePagersExclude();
        },
        getStreet() {
            if (!this.form.districtId) {
                return;
            }
            const param = {
                districtId: this.form.districtId
            };
            return apiCommon.availableAreasStreets(param).then(res => {
                if (res.code === 100) {
                    this.streetArr = res.content;
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
                    this.districtArr = res.content;
                }
            });
        },
        getCity(id) {
            if (!this.form.provinceId && !id) {
                return;
            }
            const param = {
                provinceId: this.form.provinceId || id
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
        getByMasterWarehouseId() {
            const param = {
                masterWarehouseId: this.currentWarehouseId
            };
            api.getByMasterWarehouseId(param).then(res => {
                if (res.code === 100) {
                    this.backupForm.selected = res.content;
                }
            });
        },
        warehousePagersExclude() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize,
                cityId: this.backupForm.cityId
            };
            param.excludeWarehouseId = this.currentWarehouseId;
            if (!this.G.permission['_stock/warehouse/pages']) {
                return this.$notify.error('没有列表权限');
            }
            api.warehousePagers(param).then(res => {
                window.LOG(res);

                this.tableDataBackup = res.records;
                this.totalPageBackup = res.total;
            });
        },
        warehouseList() {
            api.warehouseList().then(res => {
                if (res.code === 100) {
                    this.latestForm.warehouseArr = res.content;
                    this.latestForm.warehouseId = this.currentWarehouseId;
                }
            });
        },
        // 获取覆盖片区数据
        warehouseCoverageAreaPagers(data) {
            const param = {
                page: this.currentPageCoverageArea,
                limit: this.pageSizeCoverageArea,
                warehouseId: data
            };
            window.LOG(param);
            api.warehouseCoverageAreaPagers(param).then(res => {
                window.LOG(res);
                this.tableDataCoverageArea = res.records;
                this.totalPageCoverageArea = res.total;
            });
        },
        // 获取数据
        warehousePagers() {
            const param = {
                page: this.currentPage,
                limit: this.pageSize,
                cityId: Util.getCookie('cityId')
            };
            if (this.searchString) {
                param.searchString = this.searchString;
            }
            param.sortBy = this.sortBy ? this.sortBy : undefined;
            param.direction = this.direction ? this.direction : undefined;
            window.LOG(param);
            if (!this.G.permission['_stock/warehouse/pages']) {
                return this.$notify.error('没有列表权限');
            }
            api.warehousePagers(param).then(res => {
                window.LOG(res);

                this.tableData = res.records;
                this.totalPage = res.total;
            });
        },
        sortChange(column) {
            console.log(column + '-' + column.prop + '-' + column.order);
            this.sortBy = column.prop;
            this.direction = column.order === 'ascending' ? 'asc' : 'desc';
            this.warehousePagers();
        },

        /**
         * 跳转到商品管理界面
         * */
        handleMangeWarehouse(warehouseId, type) {
            if (!warehouseId) {
                return this.$notify.info('冻库异常,ID不能为空')
            }
            api.getCityRelatedWarehouseList().then(res => {
                if (res.code === 100) {
                    const mate = res.content.some(item => Number(item.id) === Number(warehouseId));
                    if (mate) {
                        if (type === 1) {
                            this.$router.push({ name: '/product/maintain', params: { id: warehouseId } });
                        } else if (type === 0) {
                            location.href = `${vars.URLApiBase}/product/maintain?warehouseId=${warehouseId}&wherePage=1`;
                        }
                    } else {
                        return this.$notify.error('该冻库尚未配置覆盖片区,不能进入商品管理');
                    }
                }
            });
        }
    },
    created() {
        this.warehousePagers();
    }
};
</script>
