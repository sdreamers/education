<template>
    <section>
        <el-card>
            <el-row class="list-con clearfix">
                <el-row class="mb20" :gutter="20">
                    <el-col :lg="3" :xl="2">
                        <el-input v-model="currentYear" placeholder="当前年份"></el-input>
                    </el-col>
                    <el-col :lg="3" :xl="2">
                        <el-button type="primary" @click="search">搜索</el-button>
                        <el-button @click="clear">清除</el-button>
                    </el-col>
                </el-row>
            </el-row>
            <div id="openAppMain" style="margin-top: 20px; margin-left:auto;margin-right:auto;width: auto;height:300px;"></div>
        </el-card>
    </section>
</template>

<script>

    import echarts from 'echarts';
    import packetApi from '@/api/packet';

    const option = {
        title: {
            text: ''
        },
        color: ['#3398DB'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: [],
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '包',
                type: 'bar',
                barWidth: '60%',
                data: []
            }
        ]
    };

    export default {
        data() {
            return {
                chart: '',
                option: option,

                currentYear: ''
            }
        },

        mounted() {
            this.initEcharts()
        },

        methods: {

            defaultDate() {
                this.currentYear = '2020';
            },

            initEcharts() {
                this.chart = echarts.init(document.getElementById('openAppMain'));
                this.drawLine();
            },

            drawLine() {
                const param = {};
                if (this.currentYear) {
                    param.currentYear = this.currentYear;
                }
                packetApi.numProgress(param).then(res => {
                    if (res.code === 100) {
                        this.setOption(res);
                    } else {
                        this.$notify.warning(res.message || '异常错误');
                    }
                })
            },

            setOption(diskData) {
                const data = diskData.data === undefined ? [] : diskData.data;
                this.option.xAxis.data = diskData.x === undefined ? [] : diskData.x;
                this.option.yAxis.data = diskData.y;
                this.option.series[0].data = data;
                this.chart.setOption(this.option, true);
            },

            search() {
                this.initEcharts();
            },

            clear() {
                this.currentYear = '';
            }
        },

        created() {
            this.defaultDate();
        }
    }
</script>
