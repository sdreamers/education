<template>
<div>
    <el-dialog :close-on-click-modal="false" title="流程节点设置" :visible="true" @close="$emit('close')" class="">
        <el-card v-for="(item,key) in formNode" :key="key">
            <el-button type="danger" @click="deleteNode(key)">删除该节点</el-button>
            <el-form :model="item" ref="ruleForm">
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-form-item prop="name">
                            <p>节点名称</p>
                            <el-input v-model="item.nodeName" type="text" auto-complete="off" maxlength="10"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item prop="name">
                            <p>通过触发事件</p>
                            <el-select v-model="item.passedEvent" placeholder="请选择" style="width:100%">
                                <el-option v-for="(item,key) in workflowEvents" :label="item" :value="key" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item prop="name">
                            <p>驳回触发事件</p>
                            <el-select v-model="item.rejectedEvent" placeholder="请选择" style="width:100%">
                                <el-option v-for="(item,key) in workflowEvents" :label="item" :value="key" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item prop="name">
                            <p>处理人类型</p>
                            <el-select v-model="item.handlerType" placeholder="请选择" style="width:100%" @visible-change="visibleChange(item, key, 'handler')" @change="changeType(item, key, 'handler')">
                                <el-option v-for="(item,key) in workflowHandlerTypes" :label="item" :value="key" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item prop="name">
                            <p>处理人</p>
                            <el-input readonly v-model="item.handlerName" type="text" auto-complete="off" maxlength="10" @focus="showHandler(item, key, 'handler')"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item prop="name">
                            <p>抄送人类型</p>
                            <el-select v-model="item.receiverType" placeholder="请选择" style="width:100%" @visible-change="visibleChange(item, key, 'receiver')" @change="changeType(item, key, 'receiver')">
                                <el-option v-for="(item,key) in workflowHandlerTypes" :label="item" :value="key" :key="key"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item prop="name">
                            <p>抄送人</p>
                            <el-input readonly v-model="item.receiverName" type="text" auto-complete="off" maxlength="10" @focus="showHandler(item, key, 'receiver')"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </el-card>
        <div slot="footer" class="dialog-footer">
            <el-button type="success" @click="addNode">添加节点</el-button>
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
    <selectPerson
        @close="dialogSelectPersonVisible=false"
        @submit="handleSelectedSubmit"
        v-if="dialogSelectPersonVisible"></selectPerson>
    <selectRole
        @close="dialogSelectRoleVisible=false"
        @submit="handleSelectedSubmit"
        v-if="dialogSelectRoleVisible"></selectRole>
</div>
</template>
<script>
import selectPerson from './selectPerson';
import selectRole from './selectRole';
import { workflowEvents, workflowHandlerTypes } from '@/api/dict';
import api from '@/api/workflow';

const formNode = {
    workflowId: '',
    nodeSeq: '',
    nodeName: '',
    passedEvent: '',
    rejectedEvent: '',
    handlerType: '',
    handlerName: '',
    handler: '',
    receiverType: '',
    receiverName: '',
    receiver: ''
}
export default {
    props: ['formNode', 'workflowId'],
    components: {
        selectPerson,
        selectRole
    },
    data() {
        return {
            workflowEvents: {},
            workflowHandlerTypes: {},

            currentIndex: 0,
            currentInput: '',


            formLabelWidth: '120px',

            qiniuForm: {},

            dialogSelectPersonVisible: false,
            dialogSelectRoleVisible: false
        };
    },
    methods: {
        deleteNode(index) {
            this.formNode.splice(index, 1);
        },
        addNode() {
            const form = JSON.parse(JSON.stringify(formNode));
            form.nodeSeq = this.formNode.length + 1;
            this.formNode.push(form);
        },
        visibleChange(row, index, input) {
            this.currentIndex = index;
            this.currentInput = input;
        },
        changeType() {
            const formNode = this.formNode;
            formNode[this.currentIndex][this.currentInput + 'Name'] = '';
            formNode[this.currentIndex][this.currentInput] = '';
            this.formNode = formNode;
        },
        handleSelectedSubmit(data) {
            console.log(data);
            const formNode = this.formNode;
            formNode[this.currentIndex][this.currentInput + 'Name'] = data.map(item => item.nickName).join('，');
            formNode[this.currentIndex][this.currentInput] = data.map(item => item.userId).join('&');
            this.formNode = formNode;
            this.dialogSelectPersonVisible = false;
            this.dialogSelectRoleVisible = false;
        },
        showHandler(row, index, input) {
            this.currentIndex = index;
            this.currentInput = input;
            if (row[input + 'Type'] === '1') {
                this.dialogSelectPersonVisible = true;
            } else if (row[input + 'Type'] === '0') {
                this.dialogSelectRoleVisible = true;
            }
        },
        // 提交表单
        handleSubmit() {
            const formNode = this.formNode;
            const param = {
                workflowId: this.workflowId,
                nodeName: formNode.map(item => item.nodeName),
                nodeSeq: formNode.map(item => item.nodeSeq),
                handlerType: formNode.map(item => item.handlerType),
                handler: formNode.map(item => item.handler),
                receiverType: formNode.map(item => item.receiverType),
                receiver: formNode.map(item => item.receiver),
                passedEvent: formNode.map(item => item.passedEvent),
                rejectedEvent: formNode.map(item => item.rejectedEvent)
            }

            const loading = this.$loading();
            api.workflowNodeInsertOrUpdate(param).then(res => {
                loading.close();
                if (res.code === 100) {
                    this.$notify.success(res.message || '成功');
                    this.$emit('close');
                } else {
                    this.$notify.error(res.message || '成功');
                }
            });
        },
        getWorkflowEvents() {
            workflowEvents().then(res => {
                if (res.code === 100) {
                    const obj = res.content;
                    obj[''] = '可不选';
                    this.workflowEvents = obj;
                }
            })
        },
        getWorkflowHandlerTypes() {
            workflowHandlerTypes().then(res => {
                if (res.code === 100) {
                    const obj = res.content;
                    obj[''] = '请选择';
                    this.workflowHandlerTypes = obj;
                }
            })
        }
    },
    created() {
        this.getWorkflowEvents();
        this.getWorkflowHandlerTypes();
    }
};
</script>
