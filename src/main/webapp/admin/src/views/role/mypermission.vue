<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row>

                <el-tree
                :data="list"
                :props="props"
                ref="cityTree"
                node-key="id"
                highlight-current></el-tree>
                <!-- <ul>
                    <li v-for="item in list">{{item.text}}</li>
                </ul> -->
            </el-row>
        </el-card>
    </section>
</template>

<script>
    import api from '@/api/role';

    export default {
        data() {
            return {
                list: [],
                props: {
                    children: 'nodes',
                    label: 'text'
                }
            };
        },
        methods: {
            // 获取数据
            getPermissionTree() {
                api.getPermissionTree({}).then(res => {
                    if (res.code === 100) {
                        this.list = res.content;
                    } else {
                        this.$alert('您没有任何权限。只有默认的个人中心。');
                    }
                });
            }
        },

        created() {
            this.getPermissionTree();
        }

    };
</script>