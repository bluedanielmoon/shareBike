;
(function(global, undefined) {

    console.log(this);

    var _global;



    var dom = document.getElementById("chart-varia");
    var myChart;
    var inited = false;
    var COLORS = ["#ffffff", "#b4e0f3", "#70b4eb", "#1482e5", "#1c3fbf", "#070093"];

    var variaPiece = [{
            min: 200
        }, // 不指定 max，表示 max 为无限大（Infinity）。
        {
            min: 100,
            max: 200
        }, {
            min: 50,
            max: 100
        }, {
            min: 10,
            max: 50
        }, {
            max: 10
        }
    ];
    var chancePiece = [{
            min: 1.005
        }, // 不指定 max，表示 max 为无限大（Infinity）。
        {
            min: 1.004,
            max: 1.005
        }, {
            min: 1.002,
            max: 1.003
        }, {
            min: 1.001,
            max: 1.002
        }, {
            max: 1.0001
        }
    ];
    var renderItem = function(params, api) {
        var ltPoint = api.coord([api.value(0), api.value(1)]);

        return {
            type: 'rect',
            shape: {
                x: ltPoint[0],
                y: ltPoint[1],
                width: 20,
                height: 20
            },
            style: api.style({
                stroke: 'rgba(0,0,0,0.1)'
            }),
            styleEmphasis: api.styleEmphasis()
        };
    };
    var bMap = {
        center: [108.951771, 34.266582], // 视角中心点
        zoom: 14,
        roam: true,
        mapStyle: {
            styleJson: [{
                'featureType': 'water',
                'elementType': 'all',
                'stylers': {
                    'color': '#d1d1d1'
                }
            }, {
                'featureType': 'land',
                'elementType': 'all',
                'stylers': {
                    'color': '#f3f3f3'
                }
            }, {
                'featureType': 'railway',
                'elementType': 'all',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'highway',
                'elementType': 'all',
                'stylers': {
                    'color': '#999999'
                }
            }, {
                'featureType': 'highway',
                'elementType': 'labels',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'arterial',
                'elementType': 'geometry',
                'stylers': {
                    'color': '#fefefe'
                }
            }, {
                'featureType': 'arterial',
                'elementType': 'geometry.fill',
                'stylers': {
                    'color': '#fefefe'
                }
            }, {
                'featureType': 'poi',
                'elementType': 'all',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'green',
                'elementType': 'all',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'subway',
                'elementType': 'all',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'manmade',
                'elementType': 'all',
                'stylers': {
                    'color': '#d1d1d1'
                }
            }, {
                'featureType': 'local',
                'elementType': 'all',
                'stylers': {
                    'color': '#d1d1d1'
                }
            }, {
                'featureType': 'arterial',
                'elementType': 'labels',
                'stylers': {
                    'visibility': 'off'
                }
            }, {
                'featureType': 'boundary',
                'elementType': 'all',
                'stylers': {
                    'color': '#fefefe'
                }
            }, {
                'featureType': 'building',
                'elementType': 'all',
                'stylers': {
                    'color': '#d1d1d1'
                }
            }, {
                'featureType': 'label',
                'elementType': 'labels.text.fill',
                'stylers': {
                    'color': 'rgba(0,0,0,0)'
                }
            }]
        }
    };
    // // 获取百度地图实例，使用百度地图自带的控件
    // var bmap = chart.getModel().getComponent('bmap').getBMap();
    // bmap.addControl(new BMap.MapTypeControl());




    var initVariaMap = function(time) {
        if (!inited) {

            var bmap = new BMap.Map("chart-varia");
            // 创建地图实例
            var point = new BMap.Point(116.404, 39.915);
            // 创建点坐标
            bmap.centerAndZoom(point, 15);

            myChart = echarts.init(dom);
            console.log("初始化百度地图");

            loadVariaData();
            inited = true;
        }
    };
    var flucData = {
        date: "2018_11_01",
        dist: 200,
        type: 2,
        piece: chancePiece
    }
    var loadVariaData = function() {
        myChart.clear();
        mapUtil.link.getData(
            "/anay/varia", true, {
                time: flucData.date,
                dist: flucData.dist,
                type: flucData.type
            },
            function(data) {
                if (data) {

                    console.log(data);
                    console.log("加载方差波动");
                    var list = [];
                    // data=data.slice(0,10);
                    data.forEach(function(ele, index) {
                        var item = [ele.center.lng, ele.center.lat, ele.fluc];
                        list.push(item);
                    });
                    var option = {
                        tooltip: {},
                        visualMap: {
                            type: 'piecewise',
                            // inverse : true,//颜色面板是否反转
                            top: 0,
                            left: 0,
                            pieces: flucData.piece,
                            borderColor: '#ccc',
                            borderWidth: 2,
                            backgroundColor: '#eee',
                            dimension: 2,
                            inRange: {
                                color: COLORS,
                                opacity: 0.7
                            }
                        },
                        series: [{
                            type: 'custom',
                            coordinateSystem: 'bmap',

                            name: '123',
                            renderItem: renderItem,
                            animation: false,
                            itemStyle: {
                                emphasis: {
                                    color: 'yellow'
                                }
                            },
                            encode: {
                                tooltip: 2
                            },
                            data: list,
                        }],

                        bmap: bMap
                    };
                    if (option && typeof option === "object") {
                        console.log("开始渲染数据");


                        myChart.setOption(option, {
                            notMerge: true
                        });

                    }

                }
            });

    };


    var timeSet = $("#fluc-date").val(function() {
        return moment(flucData.date, "YYYY_MM_DD").format("YYYY-MM-DD");
    }).datetimepicker({
        format: "yyyy-mm-dd",
        autoclose: true,
        minView: 2,
        todayBtn: true,
        initialDate: flucData.date
    });

    timeSet.on('changeDate', function(ev) {

        var input = ev.date;
        var formed = moment(input, "dddd MMMM DD YYYY").format("YYYY_MM_DD");
        flucData.date = formed;
        loadVariaData();
    });

    var typeSet = $('.checkbox input').iCheck({
        checkboxClass: 'icheckbox_square-red',
        radioClass: 'iradio_square-red',
        increaseArea: '20%' // optional
    }).on('ifChecked', function(event) {

        var name = this.id;
        if (name == 'fluc-varia') {
            // 方差
            flucData.type = 1;
            flucData.piece = variaPiece;
        } else {
            // 概率
            flucData.type = 2;
            flucData.piece = chancePiece;
        }
        loadVariaData();
    });

    var distSet = $("#fluc-dist");
    distSet.val(flucData.dist);
    distSet.on("change", function(e) {
        flucData.dist = $(this).val();
        loadVariaData();
    });

    var stackChart;

    var stackOption = {
        title: {
            text: '单车统计图'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        legend: {
            data: ['单车总数']
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        dataZoom: [{

            type: 'inside',
            start: 0,
            end: 10
        }, {
            start: 0,
            end: 10,
            top: '85%',
            handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            handleSize: '80%',
            handleStyle: {
                color: '#fff',
                shadowBlur: 3,
                shadowColor: 'rgba(0, 0, 0, 0.6)',
                shadowOffsetX: 2,
                shadowOffsetY: 2
            }
        }],
        grid: {
            left: '3%',
            right: '4%',
            bottom: '5%',
            containLabel: true
        },
        xAxis: [{

            type: 'category',
            boundaryGap: false,
            data: []
        }],
        yAxis: [{
            type: 'value'
        }],
        series: [{
            name: '单车总数',
            type: 'line',
            stack: '总量',
            areaStyle: {},
            data: []
        }]
    };

    var stackOps = {
        inited: false,
        show: function() {

            if (!this.inited) {
                console.log('加载折线图');
                stackChart = echarts.init($("#chart-stack")[0]);
                stackChart.setOption(stackOption);

                mapUtil.link.getData("/anay/stack", true, {}, function(data) {
                    var timeBar = [];
                    var countList = [];
                    data.forEach(function(element, index) {

                        timeBar.push(element.startTime);
                        countList.push(element.bikeCount);
                    });

                    stackChart.setOption({
                        xAxis: {
                            data: timeBar
                        },
                        series: [{
                            // 根据名字对应到相应的系列
                            name: '单车总数',
                            data: countList
                        }]
                    });


                });
                this.inited = true;
            }

        }

    }


    var dailyChart;

    var dailyOption = {
        title: {
            text: '每日统计图'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        legend: {
            data: []
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [{

            type: 'category',
            boundaryGap: false,
            data: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
        }],
        yAxis: [{
        	 type: 'value',
        	 min:10000,
        	 max:22000,
        }],
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        dataZoom: [{

            type: 'inside',
        }, {
            top: '85%',
            handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
            handleSize: '80%',
            handleStyle: {
                color: '#fff',
                shadowBlur: 3,
                shadowColor: 'rgba(0, 0, 0, 0.6)',
                shadowOffsetX: 2,
                shadowOffsetY: 2
            }
        }],
        series: []
    };

    var dailyOps = {
        inited: false,
        show: function() {

            if (!this.inited) {
                console.log('加载每日统计图');
                dailyChart = echarts.init($("#chart-daily")[0]);
                dailyChart.setOption(dailyOption);

                mapUtil.link.getData("/anay/daily", true, {}, function(data) {
                    console.log(data);
                    var timeBar = [];
                    var countList = [];
                    
                    for(var i=0;i<24;i++){
                    	 timeBar.push(i);
                    }
                    
                    console.log(data[0].length);
                    var lgName=[];
                    
                    for (var i = 0; i < data[i].length; i++) {

                    	
                    	var names=new Array();
                         var ls=new Array();
                         lgName.push(moment(data[0][i].date, "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD"));
                         
                         for(var j=0;j<24;j++){
                        	 if(j==0){
                        		 var name=moment(data[j][i].date, "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD");
                        		 names.push(name);
                        	 }
                        	 
                             ls.push(data[j][i].count);
                         }
                        countList.push({
                            'name':names,
                            'type':'line',
                            'data':ls
                        });

                    }
                    dailyChart.setOption({
                    	legend:{
                    		data:lgName
                    	},
                         xAxis: {
                             data: timeBar
                         },
                         series:countList
                     });


                });
                this.inited = true;
            }

        }

    }


    mapChart = {
        showStack: function() {
            stackOps.show();
        },
        showDaily: function() {
            dailyOps.show();
        },
        show: function(time) {
            initVariaMap();
        },
        setData: function(time, dist) {
            loadVariaData();

        },
    }

    // 最后将插件对象暴露给全局对象
    _global = (function() {
        return this || (0, eval)('this');
    }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = mapChart;
    } else if (typeof define === "function" && define.amd) {
        define(function() {
            return mapChart;
        });
    } else {
        !('mapChart' in _global) && (_global.mapChart = mapChart);
    }
}());