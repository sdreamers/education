<template>
    <el-dialog class="dialogw80" :close-on-click-modal="false" title="填写消息" :visible="true" @close="$emit('close')">
        <el-row>
            <el-col :span="10">
                <el-tree
                v-show="form.type === '3'"
                v-if="G.permission['_department/pagers']"
                :data="roleList"
                :props="defaultProps"
                show-checkbox
                default-expand-all
                node-key="uId"
                ref="tree"
                style="max-height: 400px;overflow-y:auto"
                :default-checked-keys="form.permissionIds">
                    <span class="custom-tree-node" slot-scope="{ node, data }"><span>{{node.label}}</span></span>
                </el-tree>
            </el-col>
            <el-col :span="14">
                <el-form :model="form" :rules="rules" ref="ruleForm">
                    <el-form-item label="消息标题" :label-width="formLabelWidth" prop="title">
                        <el-input v-model="form.title" type="text" auto-complete="off" :disabled="form.type === '3'"></el-input>
                    </el-form-item>
                    <el-form-item label="消息内容" :label-width="formLabelWidth" prop="content">
                        <el-input v-model="form.content" type="textarea" auto-complete="off" :autosize="{ minRows: 4, maxRows: 4}" :maxlength="form.type === '3' ? 70 : 200"></el-input>
                    </el-form-item>
                    <p style="text-align:right" v-show="form.type === '3'">
                        目前还可以输入：{{70 - form.content.length}}
                    </p>
                    <el-form-item label="消息类型" :label-width="formLabelWidth" prop="type" v-if="formType === 'add'">
                        <el-select v-model="form.type" placeholder="默认订阅" @change="typeChange">
                            <el-option v-for="(item,key) in mapDefault" :label="item.name" :value="item.id" :key="key"></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</template>
<script>
import api from '@/api/message';

export default {
    props: ['formType', 'form'],
    data() {
        return {
            formLabelWidth: '140px',

            roleList: [],
            // treeList: [],
            defaultProps: {
                children: 'children',
                label: 'name'
            },

            // 状态列表
            statusList: [{ id: '0', name: '否' }, { id: '1', name: '是' }],

            // 七牛表单
            qiniuForm: {},

            mapDefault: [{ id: '0', name: '系统公告' }, { id: '3', name: '内部消息' }],

            rules: {
                returnCauseNo: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                returnCauseDescription: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ],
                isNeedImage: [
                    { required: true, message: '不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        typeChange(data) {
            if (data === '3') {
                this.form.title = '';
            }
        },
        // 提交基本信息
        handleSubmit() {
            const formType = this.formType;
            this.$refs.ruleForm.validate(valid => {
                if (valid) {
                    const param = JSON.parse(JSON.stringify(this.form));
                    console.log(param);
                    if (param.type === '3') {
                        param.receiverIdList = this.$refs.tree.getCheckedKeys().filter(item => item !== undefined);
                        param.title = '';
                        if (param.receiverIdList.length === 0) {
                            return this.$notify.error('请选择信息接收对象');
                        }
                        if (param.content.length > 70 || param.content.length === 0) {
                            return this.$notify.error('必须输入内容，且长度不得大于70');
                        }
                    } else {
                        if (param.content.length === 0) {
                            return this.$notify.error('必须输入内容');
                        }
                    }

                    if (formType === 'edit') {
                        api.messageUpdate(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
                            }
                        });
                    } else if (formType === 'add') {
                        api.messageInsert(param).then(res => {
                            if (res.code === 100) {
                                this.$notify.success(res.message || '成功');
                                this.$emit('close');
                            } else {
                                this.$notify.warning(res.message || '异常错误');
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
        getDepartmentUserTree() {
            if (!this.G.permission['_account/departmentUser/getDepartmentUserTree']) {
                return this.$notify.error('没有权限');
            }
            const transformTozTreeFormat = function(b, a) {
                let c, d;
                const e = 'id', g = 'pId', k = 'children';
                if (!e || e === '' || !a) { return []; }
                const f = [],
                    h = {};
                for (c = 0,
                d = a.length; c < d; c++) { h[a[c][e]] = a[c]; }
                for (c = 0,
                d = a.length; c < d; c++) {
                    if (h[a[c][g]] && a[c][e] !== a[c][g]) {
                        if (!h[a[c][g]][k]) {
                            h[a[c][g]][k] = [];
                        }
                        h[a[c][g]][k].push(a[c])
                    } else {
                        f.push(a[c]);
                    }
                }
                return f
            }
            api.getDepartmentUserTree().then(res => {
                if (res.code === 100) {
                    const arr = JSON.parse(res.content)
                    console.log(arr);

                    this.roleList = transformTozTreeFormat({
                        idKey: 'id',
                        pIdKey: 'pId',
                        childrenKey: 'children'
                    }, arr);
                    console.log(this.roleList);
                }
            })
        }
    },
    created() {
        this.getDepartmentUserTree();
    }
};
</script>
