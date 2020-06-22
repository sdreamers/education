<template>
    <div >
        <el-row class="list-con clearfix">
            <el-button id="save" style="float: right;margin-left: 10px;" @click="$router.push({name: '/distribution/index'})" >返回</el-button>
        </el-row>
        <div id="map" style="width: 100%;height: 850px;"></div>

    </div>
</template>
<script>
    import api from '@/api/distribution';

    export default {
        watch: {
            $route() {
                console.log('router change');
                this.init();
            }
        },
        data() {
            return {
                isEdit: true,
                map: {},
                mapStyle: {
                    width: '100%',
                    height: '1000px',
                    'padding-right': '100px'
                },
                dialogVisible: false,
                form: {},
                rules: {
                    name: [
                        { required: true, message: '不能为空', trigger: 'blur' }
                    ]
                },

                currentMode: '',
                // 多边形对象
                overlay: {},
                // 多边形对象数组，以便清除
                overlays: [],
                // 多边形点坐标集合
                pointList: [],
                // 存放所有的标注点，以便清除
                markerArr: [],
                // 存放矩形覆盖物对象
                pointArr: []
            }
        },
        methods: {
            // 回调获得覆盖物信息
            overlaycomplete(e) {
                console.log(this.$route.params);
                console.log('overlay', e.overlay);
                this.overlay = e.overlay;
                // 用于清除所有的矩形
                this.overlays.push(e.overlay);
                this.currentMode = e.drawingMode;

                if (e.drawingMode === BMAP_DRAWING_POLYGON) {
                    // 设置多边形可以编辑
                    e.overlay.enableEditing();
                }
                this.pointArr.push(e.overlay);
            },
            clearAll() {
                this.map.clearOverlays();
                this.clearPolygon();
            },
            clearPolygon() {
                this.pointList = [];
                this.pointArr = [];
            },
            setScope(coordinates) {
                console.log('params', this.$route.params);
                this.isEdit = false;
                const polygonPointList = JSON.parse(coordinates);
                this.map.clearOverlays();
                for (let i = 0; i < polygonPointList.length; i++) {
                    const polygon = new BMap.Polygon(polygonPointList[i], this.styleOptions);
                    this.map.addOverlay(polygon);
                }
            },
            getScope() {
                api.distributionScopeGet({ id: this.$route.params.id }).then(res => {
                    if (res.code === 100) {
                        this.form = res.content;
                        this.setScope(res.content.coordinates);
                    }
                })
            },
            init() {
                this.getScope();
            }
        },
        mounted() {
            this.map = new BMap.Map('map', { enableMapClick: false });
            const poi = new BMap.Point(110.327138, 20.042465);
            this.map.centerAndZoom(poi, 16);
            this.map.enableScrollWheelZoom();


            // 实例化鼠标绘制工具

            this.styleOptions = {
                // 边线颜色。
                strokeColor: 'red',
                // 填充颜色。当参数为空时，圆形将没有填充效果
                fillColor: 'red',
                strokeWeight: 3,
                strokeOpacity: 0.8,
                fillOpacity: 0.6,
                strokeStyle: 'solid'
            };

            const drawingManager = new BMapLib.DrawingManager(this.map, {
                // 是否开启绘制模式
                isOpen: false,
                // 是否显示工具栏
                enableDrawingTool: true,
                drawingToolOptions: {
                    anchor: BMAP_ANCHOR_TOP_RIGHT,
                    offset: new BMap.Size(5, 5),
                    // 工具栏缩放比例
                    scale: 0.8,
                    // 工具栏的内容
                    drawingModes: [
                        BMAP_DRAWING_POLYGON
                    ]
                },
                // 多边形的样式
                polygonOptions: this.styleOptions
            });
            // 添加鼠标绘制工具监听事件，用于获取绘制结果
            drawingManager.addEventListener('overlaycomplete', this.overlaycomplete);

            this.init();
        }
    }

</script>
