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
            <el-row class="list-con clearfix">
                <el-radio-group v-model="type">
                    <el-radio-button label="包"></el-radio-button>
                    <el-radio-button label="学校"></el-radio-button>
                </el-radio-group>
            </el-row>
            <div id="openAppMain" style="margin-top: 20px; margin-left:auto;margin-right:auto;width: auto;height:500px;"></div>
        </el-card>
    </section>
</template>

<script>

    import echarts from 'echarts';
    import packetApi from '@/api/packet';
    import schoolApi from '@/api/school';

    const app = {};

    const posList = [
        'left', 'right', 'top', 'bottom',
        'inside',
        'insideTop', 'insideLeft', 'insideRight', 'insideBottom',
        'insideTopLeft', 'insideTopRight', 'insideBottomLeft', 'insideBottomRight'
    ];

    app.configParameters = {
        rotate: {
            min: -90,
            max: 90
        },
        align: {
            options: {
                left: 'left',
                center: 'center',
                right: 'right'
            }
        },
        verticalAlign: {
            options: {
                top: 'top',
                middle: 'middle',
                bottom: 'bottom'
            }
        },
        position: {
            options: echarts.util.reduce(posList, (map, pos) => {
                map[pos] = pos;
                return map;
            }, {})
        },
        distance: {
            min: 0,
            max: 100,
            splitNumber: 20
        }
    };

    app.config = {
        rotate: 90,
        align: 'left',
        verticalAlign: 'middle',
        position: 'insideBottom',
        distance: 15,
        onChange: () => {
            const labelOption = {
                normal: {
                    rotate: app.config.rotate,
                    align: app.config.align,
                    verticalAlign: app.config.verticalAlign,
                    position: app.config.position,
                    distance: app.config.distance
                }
            };
            myChart.setOption({
                series: [{
                    label: labelOption
                }, {
                    label: labelOption
                }, {
                    label: labelOption
                }, {
                    label: labelOption
                }]
            });
        }
    };

    const labelOption = {
        show: true,
        position: app.config.position,
        distance: app.config.distance,
        align: app.config.align,
        verticalAlign: app.config.verticalAlign,
        rotate: app.config.rotate,
        formatter: '{c}  {name|{a}}',
        fontSize: 16,
        rich: {
            name: {
                textBorderColor: '#fff'
            }
        }
    };

    const option = {
        color: ['#006699', '#ce2828'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: []
        },
        toolbox: {
            show: true,
            orient: 'vertical',
            left: 'right',
            top: 'center',
            feature: {
                mark: { show: true },
                dataView: { show: true, readOnly: false },
                magicType: { show: true, type: ['line', 'bar', 'stack', 'tiled'] },
                restore: { show: true },
                saveAsImage: { show: true }
            }
        },
        xAxis: [
            {
                type: 'category',
                axisTick: { show: false },
                data: ['2012', '2013', '2014', '2015', '2016']
            }
        ],
        yAxis: [
            {
                type: 'value',
                min: 0,
                max: 100,
                splitNumber: 10,
                axisLabel: {
                    formatter: value => value + '%'
                }
            }
        ],
        series: [
            {
                name: 'Forest',
                type: 'bar',
                barGap: 0,
                label: labelOption,
                data: [320, 332, 301, 334, 390]
            },
            {
                name: 'Steppe',
                type: 'bar',
                label: labelOption,
                data: [220, 182, 191, 234, 290]
            }
        ]
    };

    const packetLegend = ['任务进度', '金额进度'];
    const schoolLegend = ['任务进度', '金额进度'];


    export default {
        data() {
            return {
                chart: '',
                option: option,

                currentYear: '',
                type: '包',

                packetLegend: packetLegend,
                schoolLegend: schoolLegend
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
                if (this.type === '包') {
                    packetApi.numProgress(param).then(res => {
                        if (res.code === 100) {
                            this.setOption(res);
                        } else {
                            this.$notify.warning(res.message || '异常错误');
                        }
                    })
                } else {
                    schoolApi.numProgress(param).then(res => {
                        if (res.code === 100) {
                            this.setOption(res);
                        } else {
                            this.$notify.warning(res.message || '异常错误');
                        }
                    })
                }
            },

            setOption(diskData) {
                const data = diskData.data === undefined ? [] : diskData.data;
                this.option.xAxis[0].data = diskData.x === undefined ? [] : diskData.x;
                this.option.legend.data = this.type === '包' ? this.packetLegend : this.schoolLegend;
                this.option.series[0].name = this.option.legend.data[0];
                this.option.series[1].name = this.option.legend.data[1];
                this.option.series[0].data = data[0].value;
                this.option.series[1].data = data[1].value;
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
