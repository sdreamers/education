<style lang="less">
@import "./charts.less";
</style>
<template>
    <section class="echarts-line mt20">
        <div class="echarts-wrapper" id="echartsWrapper"></div>
    </section>
</template>

<script>
import echarts from 'echarts/lib/echarts';
    // 引入柱状图
require('echarts/lib/chart/bar');
require('echarts/lib/chart/line');
// 引入提示框和标题组件
require('echarts/lib/component/tooltip');
require('echarts/lib/component/title');
require('echarts/lib/component/toolbox');

export default {
    data() {
        return {
            option: {
                title: {
                    text: '每日销量趋势图'
                },
                tooltip: {
                    trigger: 'item'
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {
                            show: true
                        },
                        dataView: {
                            show: true,
                            readOnly: true
                        },
                        magicType: {
                            show: true,
                            type: ['line', 'bar']
                        },
                        restore: {
                            show: true
                        },
                        saveAsImage: {
                            show: true
                        }
                    }
                },
                calculable: true,
                // title: {
                //     text: '每月销量趋势图'
                // },
                grid: {
                    // top: '10%',
                    left: '10%',
                    right: '10%',
                    bottom: '10%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    name: '日期',
                    data: []
                },
                yAxis: [{
                    type: 'value',
                    name: '件'
                }],
                series: [{
                    name: '销量',
                    type: 'bar',
                    data: []
                }]
            },
            myChart: {}
        };
    },
    props: ['series', 'xaxis', 'title'],
    watch: {
        series(data) {
            this.option.series = data.map(item => {
                item.type = 'bar';
                return item;
            });
            this.option.xAxis.data = this.xaxis;
            this.option.title.text = this.title || '销量趋势图';

            // this.option.legend = this.legend;
            console.log(this.option);
            this.myChart.setOption(this.option);
        }
    },
    mounted() {
        this.myChart = echarts.init(document.getElementById('echartsWrapper'));
    }
};
</script>
