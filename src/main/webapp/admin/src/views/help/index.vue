<template>
    <section class="view-content">
        <el-card shadow="none">
            <div slot="header" class="card-title">
                <i class="el-icon-menu"></i>
                {{G.pageTitle}}
            </div>
            <el-row class="list-con clearfix">
                <el-card  v-for="(item,key) in tableData" :key="key" style="margin-bottom:20px">
                    <h4 style="margin:0;margin-bottom:10px;">{{item.name}}</h4>
                    <el-table :data="item.list" border>
                        <el-table-column v-for="(column,key) in columns" :prop="column.key" :label="column.title" :key="key">
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                    type="text"
                                    @click.stop="handlePlay(scope.row)">播放</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-row>
        </el-card>
    </section>
</template>

<script>
import videoList from '@/config/helpVideoList'
export default {
    data() {
        return {
            tableData: videoList.map(item => {
                item.list.map(ele => {
                    ele.url = ele.url + '?v=2018092601';
                    return ele;
                });
                return item;
            }),
            columns: [
                    { key: 'name', title: '名称' }
            ]
        };
    },
    methods: {
        handlePlay(data) {
            window.open(data.url);
        }
    }

};
</script>

<style scoped>

</style>
