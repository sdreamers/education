<template>
        <el-dialog :close-on-click-modal="false" title="填写部门信息" :visible="true" @close="$emit('close')">
            <el-form>
                <el-form-item label="部门名称" :label-width="formLabelWidth" prop="departmentName">
                    <el-input v-model="form.departmentName" type="text" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="上级部门" :label-width="formLabelWidth" prop="departmentPName">
                    <el-input v-model="form.departmentPName" @change="handleDepartmentChange" type="text" auto-complete="off"></el-input>
                    <el-tree :data="treeData" :props="defaultProps" @node-click="handleDepartmentInsertClick" default-expand-all :default-checked-keys="[form.departmentPid]" style="border: 1px solid #eee;" highlight-current></el-tree>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click="$emit('close')">取 消</el-button>
                <el-button type="primary" @click="handleInsertDepartment">确 定</el-button>
            </div>
        </el-dialog>
</template>
<script>
import api from '@/api/member';
export default {
    props: ['userId', 'defaultProps', 'treeData', 'departmentName', 'departmentPName', 'departmentPid', 'departmentId', 'formType'],
    data() {
        return {

            formLabelWidth: '120px',
            cityList: [],
            cityChecked: [],

            dialogDepartment: '',

            form: {
                departmentName: '',
                departmentPName: '',
                departmentPid: '',
                departmentId: ''
            }

        };
    },
    methods: {
        handleDepartmentInsertClick(data) {
            this.form.departmentPid = data.id;
            this.form.departmentPName = data.name;
        },
        // 提交部门，新增或者修改
        handleInsertDepartment() {
            if (this.formType === 'edit') {
                if (!this.G.permission['_account/department/update']) {
                    return this.$notify.error('没有权限');
                }
                const param = {
                    id: this.form.departmentId,
                    pId: this.form.departmentPid,
                    name: this.form.departmentName
                };
                if (param.name === '') {
                    return this.$notify.error('部门名称不能为空');
                }
                if (param.name.length > 30) {
                    return this.$notify.error('部门名称不能大于30个字符');
                }
                if (!param.id) {
                    return this.$notify.error('部门 id 异常');
                }
                if (!param.pId) {
                    return this.$notify.error('上级部门 id 异常');
                }
                api.departmentUpdate(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.$emit('close');
                    }
                });
            } else if (this.formType === 'add') {
                if (!this.G.permission['_account/department/insert']) {
                    return this.$notify.error('没有权限');
                }
                const pId = this.form.departmentPid;
                const departmentName = this.form.departmentName;
                if (!pId) {
                    return this.$notify.error('未选择上级部门');
                }
                if (!departmentName) {
                    return this.$notify.error('未输入部门名称');
                }
                if (departmentName.length > 30) {
                    return this.$notify.error('不得大于30个字符');
                }
                const param = {
                    pId: pId,
                    name: departmentName
                };
                api.departmentInsert(param).then(res => {
                    if (res.code === 100) {
                        this.$notify.success(res.message || '成功');
                        this.$emit('close');
                    }
                });
            }
        },
        handleDepartmentChange() {
            if (!this.form.departmentPName) {
                this.form.departmentPid = '0';
            }
        }
    },
    mounted() {
        this.form.departmentName = this.departmentName;
        this.form.departmentPName = this.departmentPName
        this.form.departmentPid = this.departmentPid;
        this.form.departmentId = this.departmentId;
    }
};
</script>
