<template>
    <el-dialog :close-on-click-modal="false" title="导入设备" :visible="dialogVisible" width="30%" @close="$emit('close')">
        <el-form :model="form" label-position="left" ref="ruleForm" label-width="90px" :rules="rules">
            <el-form-item label="包名">
                <el-input
                    v-model="form.packetName">
                </el-input>
            </el-form-item>
            <el-form-item label="供应商">
                <el-input v-model="form.supplierName" placeholder="请输入供应商"></el-input>
            </el-form-item>
            <el-form-item label="类型">
                <el-select v-model="form.type" placeholder="请选择">
                    <el-option
                        v-for="item in types"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="年份">
                <el-input v-model="form.currentYear" placeholder="请输入年份"></el-input>
            </el-form-item>
            <el-form-item label="设备">
                <el-button
                    type="text"><label for="import">导入</label><input id="import" type="file" @change="importExcel" style="display:none"></el-button>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>

    import XLSX from 'xlsx';

    const types = [{ label: '普通', value: '1' }, { label: '信息化', value: '2' }];

    const form = { packetName: '', supplierName: '', type: '', currentYear: '' };

    export default {
        props: ['dialogVisible'],
        data() {
            return {
                rules: {
                    packetName: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    currentYear: [{ required: true, message: '宽度不能为空', trigger: 'blur' },
                        { type: 'number', message: '宽度必须为数字值' }],
                    supplierName: [{ required: true, message: '不能为空', trigger: 'blur' }],
                    type: [{ required: true, message: '不能为空', trigger: 'blur' }]

                },
                supplierForm: {},
                form: JSON.parse(JSON.stringify(form)),
                types: JSON.parse(JSON.stringify(types)),
                deviceImportVOList: []
            };
        },
        methods: {
            // 提交表单
            handleSubmit() {
                this.$refs.ruleForm.validate(valid => {
                    if (valid) {
                        if (!this.deviceImportVOList || this.deviceImportVOList.length < 1) {
                            return this.$notify.error('请导入文件');
                        }
                        const param = {
                            devices: this.deviceImportVOList,
                            packet: this.form.packetName,
                            currentYear: this.form.currentYear,
                            type: this.form.type,
                            supplierName: this.form.supplierName
                        }
                        const url = 'localhost:8767/device/import';
                        axios.post(url, JSON.stringify(param)).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                            } else {
                                this.$notify.success(res.message || '失败');
                            }
                            this.deviceImportVOList = [];
                        })
                    } else {
                        this.$notify({
                            message: '请按要求填写表单',
                            type: 'warning'
                        });
                        return false;
                    }
                });
            },

            async importExcel(obj) {
                this.deviceImportVOList = [];
                // js解析Excel相关操作
                let wb; // 读取完成的数据
                const rABS = false; // 是否将文件读取为二进制字符串

                function fixData(data) { // 文件流转BinaryString
                    let o = '', l = 0;
                    const w = 10240;
                    for (; l < data.byteLength / w; ++l) o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w, l * w + w)));
                    o += String.fromCharCode.apply(null, new Uint8Array(data.slice(l * w)));
                    return o;
                }
                function importExcel(obj) { // 导入
                    if (!obj.files) {
                        return;
                    }
                    const f = obj.files[0];
                    const reader = new FileReader();
                    if (rABS) {
                        reader.readAsArrayBuffer(f);
                    } else {
                        reader.readAsBinaryString(f);
                    }
                    return new Promise(resolve => {
                        reader.onload = function(e) {
                            const data = e.target.result;
                            if (rABS) {
                                wb = XLSX.read(btoa(fixData(data)), { // 手动转化
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
                            const deviceImportVOList = [];
                            for (let i = 0; i < ExcelJson.length; i++) {
                                const row = ExcelJson[i];
                                const rowJson = eval('(' + JSON.stringify(row) + ')');
                                const deviceImportVO = {
                                    name: rowJson.设备名称,
                                    technicalParamter: rowJson.技术参数,
                                    specification: rowJson.规格,
                                    model: rowJson.型号,
                                    unit: rowJson.单位,
                                    num: rowJson.数量,
                                    includingTaxPrice: rowJson.含税单价,
                                    excludingTaxPrice: rowJson.不含税单价,
                                    tax: rowJson.税金,
                                    totalAmount: rowJson.含税合价,
                                    schoolName: rowJson.学校名称
                                };
                                deviceImportVOList.push(deviceImportVO);
                            }
                            resolve(deviceImportVOList);
                        };
                    })
                }
                this.deviceImportVOList = await importExcel(obj.target);
            }
        }
    };
</script>
