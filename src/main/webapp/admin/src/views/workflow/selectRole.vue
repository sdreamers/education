<template>
<div>
    <el-dialog :close-on-click-modal="false" title="选择指定的人员" :visible="true" @close="$emit('close')">
        <el-row style="margin-top:20px">
            <el-row>
                <el-select v-model="selectedRole" multiple placeholder="请选择">
                    <el-option v-for="(item, key) in rolesList" :label="item.name" :value="item.id" :key="key"></el-option>
                </el-select>
            </el-row>
        </el-row>
        <div slot="footer" class="dialog-footer">
            <el-button @click="$emit('close')">取 消</el-button>
            <el-button type="primary" @click="handleSubmit">确 定</el-button>
        </div>
    </el-dialog>
</div>
</template>
<script>
import { roles } from '@/api/dict';
export default {
    props: [],
    data() {
        return {
            rolesList: [],
            rolesMap: {},

            selectedRole: [],

            currentPage: 1,
            totalPage: 0,
            pageSize: 5
        };
    },
    methods: {
        // 提交表单
        handleSubmit() {
            const arr = [];
            const rolesMap = this.rolesMap;
            this.selectedRole.forEach(item => {
                arr.push({ userId: item, nickName: rolesMap[item] });
            })
            this.$emit('submit', arr);
        },
        getRoles() {
            roles().then(res => {
                if (res.code === 100) {
                    const arr = [];
                    const data = res.content;
                    this.rolesMap = data;
                    for (const item in data) {
                        if (data.hasOwnProperty(item)) {
                            const element = data[item];
                            arr.push({ id: item, name: element });
                        }
                    }
                    this.rolesList = arr;
                }
            })
        }
    },
    created() {
        this.getRoles();
    }
};
</script>
