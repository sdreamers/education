<template>
    <el-dialog :close-on-click-modal="false" title="选择部门" :visible="true" @close="$emit('close')">
        <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        highlight-current
        @node-click="nodeClick"
        :default-checked-keys="treeChecked"></el-tree>
    </el-dialog>
</template>
<script>
export default {
    props: ['treeData'],
    data() {
        return {
            treeProps: {
                children: 'children',
                label: 'name'
            },
            treeChecked: []
        }
    },
    methods: {
        nodeClick(data) {
            console.log(data);
            this.$emit('treeClick', data);
        }
    }
}
</script>
