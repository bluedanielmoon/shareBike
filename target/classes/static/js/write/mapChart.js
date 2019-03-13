;
(function(global, undefined) {

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
            min: 1.1
        }, // 不指定 max，表示 max 为无限大（Infinity）。
        {
            min: 1.08,
            max: 1.1
        }, {
            min: 1.05,
            max: 1.1
        }, {
            min: 1.01,
            max: 1.05
        }, {
            max: 1.01
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
    
    
    var flowBMap={
            center: [108.890723,34.300889], // 视角中心点
            zoom: 14,
            roam: true,
            mapStyle:{
                styleJson:[{
                    "featureType": "land",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#091220ff"
                    }
                }, {
                    "featureType": "water",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#113549ff"
                    }
                }, {
                    "featureType": "green",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#0e1b30ff"
                    }
                }, {
                    "featureType": "building",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "building",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#ffffffb3"
                    }
                }, {
                    "featureType": "building",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#dadadab3"
                    }
                }, {
                    "featureType": "subwaystation",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#b15454B2"
                    }
                }, {
                    "featureType": "education",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#e4f1f1ff"
                    }
                }, {
                    "featureType": "medical",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#f0dedeff"
                    }
                }, {
                    "featureType": "scenicspots",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "color": "#e2efe5ff"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "weight": 4
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#f7c54dff"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#fed669ff"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#8f5a33ff"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "highway",
                    "elementType": "labels.icon",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "weight": 2
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#d8d8d8ff"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#ffeebbff"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#525355ff"
                    }
                }, {
                    "featureType": "arterial",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "weight": 1
                    }
                }, {
                    "featureType": "local",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#d8d8d8ff"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#979c9aff"
                    }
                }, {
                    "featureType": "local",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "railway",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "weight": 1
                    }
                }, {
                    "featureType": "railway",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#123c52ff"
                    }
                }, {
                    "featureType": "railway",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#12223dff"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on",
                        "weight": 1
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#d8d8d8ff"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#ffffff00"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#979c9aff"
                    }
                }, {
                    "featureType": "subway",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "continent",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "continent",
                    "elementType": "labels.icon",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "continent",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#333333ff"
                    }
                }, {
                    "featureType": "continent",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "city",
                    "elementType": "labels.icon",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "city",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "city",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#454d50ff"
                    }
                }, {
                    "featureType": "city",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "town",
                    "elementType": "labels.icon",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "town",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "town",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#454d50ff"
                    }
                }, {
                    "featureType": "town",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "road",
                    "elementType": "geometry.fill",
                    "stylers": {
                        "color": "#12223dff"
                    }
                }, {
                    "featureType": "poilabel",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "districtlabel",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "road",
                    "elementType": "geometry",
                    "stylers": {
                        "visibility": "on"
                    }
                }, {
                    "featureType": "road",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "road",
                    "elementType": "geometry.stroke",
                    "stylers": {
                        "color": "#ffffff00"
                    }
                }, {
                    "featureType": "district",
                    "elementType": "labels",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "poilabel",
                    "elementType": "labels.icon",
                    "stylers": {
                        "visibility": "off"
                    }
                }, {
                    "featureType": "poilabel",
                    "elementType": "labels.text.fill",
                    "stylers": {
                        "color": "#2dc4bbff"
                    }
                }, {
                    "featureType": "poilabel",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffff00"
                    }
                }, {
                    "featureType": "manmade",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#12223dff"
                    }
                }, {
                    "featureType": "districtlabel",
                    "elementType": "labels.text.stroke",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "entertainment",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#ffffffff"
                    }
                }, {
                    "featureType": "shopping",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#12223dff"
                    }
                }, {
                    "featureType": "background",
                    "elementType": "geometry",
                    "stylers": {
                        "color": "#404a59ff"
                    }
                }]
            }
    }
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
            var point = new BMap.Point(108.951771, 34.266582);
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

    var typeSet = $('#fluc-type .checkbox input').iCheck({
        checkboxClass: 'icheckbox_square-red',
        radioClass: 'iradio_square-red',
        increaseArea: '20%' // optional
    }).on('ifChecked', function(event) {

        var name = this.id;
        if (name == 'fluc-varia') {
            // 方差
            flucData.type = 1;
            flucData.piece = variaPiece;
        } else if (name == 'fluc-chance'){
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
            data: ['单车总数','温度']
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
            type: 'value',
            name: '单车总数',
            scale: true,
            max: 35000,
            min: 5000,
        },{
            type: 'value',
            name: '温度',
            scale: true,
            max: 50,
            min: -30,
        }],
        series: [{
            name: '单车总数',
            type: 'line',
            stack: '总量',
            areaStyle: {},
            data: []
        },{
            name: '温度',
            type: 'line',
            data: [],
            xAxisIndex: 0,
            yAxisIndex: 1,
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
                    console.log(data);
                	var timeBar = [];
                    var countList = [];
                    var tempList=[];
                    data.forEach(function(element, index) {

                        timeBar.push(element.startTime);
                        countList.push(element.bikeCount);
                        tempList.push(element.weather.tempature);
                    });

                    stackChart.setOption({
                        xAxis: {
                            data: timeBar
                        },
                        series: [{
                            // 根据名字对应到相应的系列
                            name: '单车总数',
                            data: countList
                        },{
                            // 根据名字对应到相应的系列
                            name: '温度',
                            data: tempList,
                        }]
                    });


                });
                this.inited = true;
            }

        }

    }

    var siteChangeChart;
    var siteChangeOption = {
        xAxis: {
            type: 'category',
            data: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23]
        },
        yAxis: {
            type: 'value'
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
        visualMap: {
            show: false,
            dimension: 0,
            pieces: [{
                lte: 6,
                color: 'green'
            }, {
                gt: 6,
                lte: 8,
                color: 'red'
            }, {
                gt: 8,
                lte: 14,
                color: 'green'
            }, {
                gt: 14,
                lte: 17,
                color: 'red'
            }, {
                gt: 17,
                color: 'green'
            }]
        },
        series: [{
            data: [],
            type: 'line'
        }]
    };
    var siteChangeTable = $("#siteChange-list");
    var siteChangeOps = {
        inited: false,
        showByID: function(id) {
            if (!this.inited) {
                this.show(id);
            } else {
                this.loadData(id);
            }
        },
        loadData: function(id) {
        	var date=$("#site-change-Date").val();

        	var chosed=$('#site-change-modal option:selected').val();
        	 
        	if(chosed!='all'&&chosed!='predict'){
        		date=moment(date, "YYYY-MM-DD").format("YYYY_MM_DD");
        	}
            mapUtil.link.getData("/site/change", true, {
            	date:date,
            	choose:chosed,
                siteID: id
            }, function(data) {
                if (data) {
                	/**
					 * private int hour; // 1-work ,0-notWork private int
					 * workDay; // 1-canGo,0-cannot Go private int weather; //
					 * 1-[10,25],2-[5-10,25-30],3-[<5,>30] private int temp;
					 */
                	var circumList=data.circum;
                	if(circumList&&circumList.length==24){
                		var workDay=circumList[0].workDay;
                		if(workDay==1){
                			workDay="工作日";
                		}else {
							workDay="非工作日";
						}
                		var canGO=0;
                		var totalTemp=0;
                		circumList.forEach(function(ele,index) {
                			totalTemp+=ele.temp;
                			if(index>=6&&index<=23){
                				if(ele.weather==1){
                    				canGO++;
                    			}
                			}
						});
                		totalTemp=Math.round(totalTemp/24);
                		$("#site-change-workDay").val(workDay);
                		$("#site-change-weather").val(canGO);
                		$("#site-change-temp").val(totalTemp);
                		
                	}
                	var mountType=data.type;
                	var bump=data.bump;
                	var visualStyle=[];
                	if(mountType&&mountType==1){
                		if(bump.bumpType<4){// 小于4即存在淤积
                    		var spans=bump.maxBumpSpan;
                    		spans[0]+=7;
                    		spans[1]+=7;
                    		visualStyle=[{
                                lte: spans[0],
                                color: 'green'
                            }, {
                                gt: spans[0],
                                lte: spans[1],
                                color: 'red'
                            }, {
                                gt: spans[1],
                                color: 'green'
                            }];
                    	}
                	}
                	if(data.list!=null){
                		 siteChangeChart.setOption({
                         	visualMap: {
                                 pieces: visualStyle
                             },
                             series: [{
                                 name: '单车总数',
                                 data: data.list
                             }]
                         });
                	}else {
                		layer.msg('Sorry,暂时没有该日的预测数据');
					}
                	
                   
                }
            });
        },
        getRow: function(row, ele) {
            siteChangeOps.loadData(row.id);
        },
        show: function(id) {
            if (!this.inited) {
                console.log('加载折线图');
                siteChangeChart = echarts.init($("#chart-siteChange")[0]);
                siteChangeChart.setOption(siteChangeOption);
                
                var startDate="2018-12-01";
                var timeSet = $("#site-change-Date").val(startDate)
                .datetimepicker({
                    format: "yyyy-mm-dd",
                    autoclose: true,
                    minView: 2,
                    todayBtn: true,
                    initialDate:startDate
                });
                
                if (id) {
                    this.loadData(id);
                }
                
                siteChangeTable.bootstrapTable({
                    striped: true, // 是否显示行间隔色
                    cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                    sortable: false, // 是否启用排序
                    sortOrder: "asc", // 排序方式
                    sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                    pageSize: 5, // 每页的记录行数（*）
                    pagination: true, // 是否显示分页（*）
                    minimumCountColumns: 2, // 最少允许的列数
                    clickToSelect: true, // 是否启用点击选中行
                    checkboxHeader: false, // 隐藏前面的复选框
                    uniqueId: "name", // 每一行的唯一标识，一般为主键列
                    icons: {
                        refresh: 'glyphicon-refresh icon-refresh',
                        toggle: 'glyphicon-list-alt icon-list-alt',
                        columns: 'glyphicon-th icon-th'
                    },
                    onClickRow: siteChangeOps.getRow,
                    columns: [{
                        field: 'name',
                        title: '站点名称'
                    }, {
                        field: 'volume',
                        title: '容量'
                    }],
                });
                mapUtil.link.getData("/site/all", true, {}, function(data) {
                    if (data) {
                        var list = [];
                        data.forEach(function(ele, index) {
                            list.push({
                                // "index":count++,
                                "id": ele.id,
                                "name": ele.name,
                                "type": ele.type,
                                "volume": ele.volume,
                            });
                        });
                        siteChangeTable.bootstrapTable('load', list);
                    }
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
            min: 10000,
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
                    var timeBar = [];
                    var countList = [];

                    for (var i = 0; i < 24; i++) {
                        timeBar.push(i);
                    }

                    var lgName = [];
                    var days= data[0].length;
                    for (var i = days-20; i < data[0].length; i++) {


                        var names = new Array();
                        var ls = new Array();
                        lgName.push(moment(data[0][i].date, "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD"));

                        for (var j = 0; j < 24; j++) {
                            if (j == 0) {
                                var name = moment(data[j][i].date, "YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DD");
                                names.push(name);
                            }
                            ls.push(data[j][i].count);
                        }
                        countList.push({
                            'name': names,
                            'type': 'line',
                            'data': ls
                        });

                    }
                    dailyChart.setOption({
                        legend: {
                            data: lgName
                        },
                        xAxis: {
                            data: timeBar
                        },
                        series: countList
                    });


                });
                this.inited = true;
            }

        }

    }

    var siteDom = document.getElementById("chart-site");
    var siteChart;
    var siteInited = false;
    var siteSlider = null;

    var scoreData = [];
    var clusterData = [];

    var initSiteMap = function(time) {
        if (!siteInited) {
            var smap = new BMap.Map("chart-site");
            // 创建地图实例
            var point = new BMap.Point(108.951771, 34.266582);
            // 创建点坐标
            smap.centerAndZoom(point, 15);

            siteChart = echarts.init(siteDom);
            var siteInit = {
                rate: 5,
                flucSca: 60,
                countSca: 30,
                poiSca: 10,
                clusterDist: 100,
                maxPack: 50,
                minPack: 2,
            }
            loadSiteScoreData(siteInit);
            loadSiteClusterData(siteInit, setSiteShow);
            siteInited = true;
            siteSlider = $('#site-rate').slider({
            	value: siteInit.rate,
                formatter: function(value) {
                    return 'Current value: ' + value;
                }
            });

            var siteScaleSlider = $("#site-scoreScal").slider({
                min: 0,
                max: 100,
                value: [60, 90],
                labelledby: ['site-scoreScal-1', 'site-scoreScal-2'],
                formatter: function() {
                    return "波动占比:" + arguments[0][0] + "%  最大停放数占比:" +
                        (arguments[0][1] - arguments[0][0]) + "%  POI占比:" + (100 - arguments[0][1] + "%");
                },
            });

            var swts = $(".site-switch");
            var swit;
            swts.each(function(index, ele) {
                swit = new Switchery(ele);
                $(this).change(function() {
                    if (this.checked) {
                        $("#show-site-check label").text("预选点");
                        setSiteShow(scoreData);
                    } else {
                        $("#show-site-check label").text("站点");
                        setSiteShow(clusterData);
                    }
                });
            });

            $("#site-cluster-Dist").val(siteInit.clusterDist);
            $("#site-cluster-Maxpack").val(siteInit.maxPack);
            $("#site-cluster-Minpack").val(siteInit.minPack);

            $("#submit-site").click(function() {
                var slidVal = $(siteSlider).val();
                var scaleVal = $(siteScaleSlider).val().split(',');
                var scale1 = scaleVal[0];
                var scale2 = scaleVal[1] - scale1;
                var scale3 = 100 - scaleVal[1];

                var clusterDist = $("#site-cluster-Dist").val();
                var maxPack = $("#site-cluster-Maxpack").val();
                var minPack = $("#site-cluster-Minpack").val();
               
                var params = {
                    rate: slidVal,
                    flucSca: scale1,
                    countSca: scale2,
                    poiSca: scale3,
                    clusterDist: clusterDist,
                    maxPack: maxPack,
                    minPack: minPack,
                }
                if (swit.element.checked) {
                    loadSiteScoreData(params, setSiteShow);
                    loadSiteClusterData(params);
                } else {
                    loadSiteScoreData(params);
                    loadSiteClusterData(params, setSiteShow);
                }
            });

            $("#submit-siteBase").click(function() {
                var slidVal = $(siteSlider).val();
                var scaleVal = $(siteScaleSlider).val().split(',');
                var scale1 = scaleVal[0];
                var scale2 = scaleVal[1] - scale1;
                var scale3 = 100 - scaleVal[1];
                var clusterDist = $("#site-cluster-Dist").val();
                var maxPack = $("#site-cluster-Maxpack").val();
                var minPack = $("#site-cluster-Minpack").val();
               

                var params = {
                    rate: slidVal,
                    flucSca: scale1,
                    countSca: scale2,
                    poiSca: scale3,
                    clusterDist: clusterDist,
                    maxPack: maxPack,
                    minPack: minPack,
                }
                mapUtil.link.getData("/site/submit", true, params, function(data) {
                    if (data) {
                        console.log(data);

                    }
                });
            });

        }
    };

    var renderItemSite = function(params, api) {
        var ltPoint = api.coord([api.value(0), api.value(1)]);

        return {
            type: 'rect',
            shape: {
                x: ltPoint[0],
                y: ltPoint[1],
                width: 2,
                height: 2
            },
            style: api.style({
                stroke: 'rgba(0,0,0,0.1)'
            }),
            styleEmphasis: api.styleEmphasis()
        };
    };

    var setSiteShow = function(list) {
        var option = {
            tooltip: {},
            visualMap: {
                pieces: [
                    { min: 0.2 },
                    { min: 0.15, max: 0.2 },
                    { min: 0.1, max: 0.15 },
                    { min: 0.08, max: 0.1 },
                    { min: 0.04, max: 0.08 },
                    { min: 0.02, max: 0.04 },
                    { min: 0.01, max: 0.02 },
                    { max: 0.1 }
                ],
                inRange: {
                    color: ['#50a3ba', '#eac736', '#d94e5d']
                },
                textStyle: {
                    color: '#fff'
                }
            },
            series: [{
                type: 'scatter',
                coordinateSystem: 'bmap',
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
            siteChart.setOption(option, {
                notMerge: true,
            });

        }
    }
    var loadSiteScoreData = function(param, showFunc) {

        mapUtil.link.getData("/site/score", true, param, function(data) {
            if (data) {
                var list = [];
                for (var i in data) {
                    var item = {
                        name: [data[i].lng, data[i].lat],
                        value: [data[i].lng, data[i].lat, i],
                    }
                    list.push(item);
                };
                scoreData = list;
                if (showFunc) {
                    showFunc(scoreData);
                }

            }
        });

    };
    var loadSiteClusterData = function(param, showFunc) {

        mapUtil.link.getData("/site/cluster", true, param, function(data) {
            if (data) {
            	console.log(data);
                var list = [];
                data.forEach(function(ele, index) {
                    var item = {
                        name: [ele.name],
                        value: [ele.lnglat[0], ele.lnglat[1]],
                    }
                    list.push(item);
                });
                clusterData = list;
                if (showFunc) {
                    showFunc(clusterData);
                }

            }
        });

    };
    
    var siteFlowChart;
    var flowOps={
    	inited: false,
    	startRatio:0.1,
    	flowData:{
    		citys:[],
    		lines:[]
    	},
          init:function(){
        	   if (!this.inited) {
                   siteFlowChart = echarts.init($("#chart-siteFlow")[0]);
                   siteFlowChart.setOption(siteFlowOption);
                   //1--flowin to site,2--flowout from site
                   this.loadFlowData(1);
                   
                  
                   $('#site-flow-ratio').val(flowOps.startRatio).change(function() {
                	   $('#flow-type .checkbox input').each(function(index,ele) {
                		   if(ele.id=='flow-in'){
                			   flowOps.loadFlowData(1);
                		   }else {
                			   flowOps.loadFlowData(2);
                		   }
                	   });
                   });
                   
                   var typeSet = $('#flow-type .checkbox input').iCheck({
                       checkboxClass: 'icheckbox_square-red',
                       radioClass: 'iradio_square-red',
                       increaseArea: '20%' // optional
                   }).on('ifChecked', function(event) {

                       var name = this.id;
                       console.log(name)
                       if (name == 'flow-in') {
                    	   flowOps.loadFlowData(1);
                       } else if (name == 'flow-out'){
                    	   flowOps.loadFlowData(2);
                       }
                   });
                   this.inited = true;
                   
               }
           },
          
           loadFlowData:function(flag){
        	   $('#site-flow-ratio')
        	   var ratio=$('#site-flow-ratio').val();
        	   if(!ratio||ratio>=1||ratio<0){
        		   ratio=flowOps.startRatio;
        	   }
        	   mapUtil.link.getData("/site/flow", true, {
        		   //1--flowin to site,2--flowout from site
        		   flowRatio:ratio,
        		   flowType:flag
        	   }, function(data) {
                   if (data) {
                	   console.log(data)
                	   var cities=[];
                	   var flows=[];
                	   data.sites.forEach(function(ele, index) {
                		   cities.push({
            			   id:ele.id,
							name:ele.name,
							value: [ele.lng,ele.lat],
                		   })
                	   })
                	   $.map(data.flow,function(items,index){
                		   var fromSite=cities.find(function(elem){
                			     if(elem.id==index){
                			    	 return elem;
            			     	 }
                			 	},index);
                		   for ( var i in items) {
                			   var itemID=items[i]
                			   var toSite=cities.find(function(elem){
                  			     if(elem.id==itemID){
                  			    	 return elem;
                  			     	}
                  			 	},itemID);
                			   flows.push({
                				   fromName:fromSite.name,
            					   toName:toSite.name,
        						   coords:[fromSite.value,toSite.value]
                			   })
            		   	  }
						})
						flowOps.flowData.citys=cities;
                	   flowOps.flowData.lines=flows;
                	  
                	   siteFlowChart.setOption({
                		   series: [{
                               name: '地点',
                               data: cities
                           },{
                               name: '线路',
                               data: flows
                           }]  
                	   });
                   }
               });
           },
    }
    
    var siteFlowOption= {
    	    bmap: flowBMap,
    	    title: {
    	        text: '站点迁移图',
    	        left: 'center',
    	        textStyle: {
    	            color: '#fff'
    	        }
    	    },
    	    legend: {
    	        show: false,
    	        orient: 'vertical',
    	        top: 'bottom',
    	        left: 'right',
    	        data: ['地点', '线路'],
    	        textStyle: {
    	            color: '#fff'
    	        }
    	    },
    	    series: [{
    	        name: '地点',
    	        type: 'effectScatter',
    	        coordinateSystem: 'bmap',
    	        zlevel: 2,
    	        rippleEffect: {
    	            brushType: 'stroke'
    	        },
    	        label: {
    	            emphasis: {
    	                show: true,
    	                position: 'right',
    	                formatter: '{b}'
    	            }
    	        },
    	        symbolSize: 5,
    	        showEffectOn: 'render',
    	        itemStyle: {
    	            normal: {
    	                color: '#F58158'
    	            }
    	        },
    	        data:[]
    	    }, {
    	        name: '线路',
    	        type: 'lines',
    	        coordinateSystem: 'bmap',
    	        zlevel: 2,
    	        large: true,
    	        effect: {
    	            show: true,
    	            constantSpeed: 30,
    	            symbol: 'pin',
    	            symbolSize: 3,
    	            trailLength: 0,
    	        },
    	        lineStyle: {
    	            normal: {
    	                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
    	                    offset: 0,
    	                    color: '#58B3CC'
    	                }, {
    	                    offset: 1,
    	                    color: '#F58158'
    	                }], false),
    	                width: 1,
    	                opacity: 0.2,
    	                curveness: 0.1
    	            }
    	        },
    	        data:[]
    	    }]
    	};
    
    var list = [];
   
    var activeChart = $("#chart-avtiveAna");
    var activeOption = {
        title: {
            text: '单车活跃度',
            subtext:'各个整数代表在某日期前的若干天内，单车位置变动的次数',
            x: 'center'
        },
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20,
        },
        series: [{
            name: '活跃度',
            type: 'pie',
            radius: '55%',
            center: ['40%', '50%'],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };
    var activeOps = {
        inited: false,
        activeDate: "2018-12-03 12",
        days: 3,
        loadData: function(date, days) {
            mapUtil.link.getData("/map/avtive", true, {
                date: date,
                days: days,
            }, function(data) {
                if (data) {
                    var list=[{
                        name: "0",
                        value: data.pies[0],
                    },{
                        name: "1-2",
                        value: data.pies[1],
                    },{
                        name: "3-6",
                        value: data.pies[2],
                    },{
                        name: "7-10",
                        value: data.pies[3],
                    },{
                        name: "10以上",
                        value: data.pies[4],
                    },];

                    data: ["0","1-2","3-6","7-10","10以上"],
                    activeChart.setOption({
                        series: [{
                            name: '活跃度',
                            data: list
                        }]
                    });
                }
            });
        },
        init: function(id) {
            if (!this.inited) {
                console.log('加载活跃度');
                activeChart = echarts.init($("#chart-avtiveAna")[0]);
                activeChart.setOption(activeOption);

                var timeSet = $("#activeAna-date").val(function() {
                    return moment(activeOps.activeDate, "YYYY_MM_DD HH").format("YYYY-MM-DD HH");
                }).datetimepicker({
                    format: "yyyy-mm-dd HH",
                    autoclose: true,
                    minView: 1,
                    todayBtn: true,
                    initialDate: activeOps.activeDate
                });

                $("#activeAna-before").val(activeOps.days);
                var days = $("#activeAna-before").val();
                
                var initDate=moment(activeOps.activeDate, "YYYY-MM-DD HH").format("YYYY_MM_DD HH");
                activeOps.loadData(initDate, days);
                timeSet.on('changeDate', function(ev) {
                    var input = ev.date;
                    var formed = moment(input, "dddd MMMM DD YYYY").format("YYYY_MM_DD HH");
                    console.log(formed);
                    activeOps.activeDate = formed;
                    activeOps.loadData(activeOps.activeDate, days);

                });
                this.inited = true;
            }

        }

    }


    var inactTrendChart;

    var inactTrendOption = {
        title: {
            text: '不活跃单车量统计图'
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
            data: ['单车不活跃数']
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
            end: 100
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
            name: '单车不活跃数',
            type: 'line',
            stack: '总量',
            areaStyle: {},
            data: []
        }]
    };

    var inactTrendOps = {
        inited: false,
        startDate:"2019-1-14 23",
        startDays:2,
        startHours:24,
        setData:function(startTime,startDays,startHours){
        	console.log(startTime)
        	console.log(startDays)
        	console.log(startHours)
        	mapUtil.link.getData("/site/inactiveDays", true, {
            	startTime:startTime,
            	daysBefore:startDays,
        		checkDay:startHours
            }, function(data) {
                var timeBar = [];
                var countList = [];
                for(var key in data){
                	timeBar.push(key);
                	countList.push(data[key]);
            	}
                inactTrendChart.setOption({
                    xAxis: {
                        data: timeBar
                    },
                    series: [{
                        // 根据名字对应到相应的系列
                        name: '单车不活跃数',
                        data: countList
                    }]
                });
            });
        },
        show: function() {
            if (!this.inited) {
                inactTrendChart = echarts.init($("#chart-activeTrend")[0]);
                inactTrendChart.setOption(inactTrendOption);
                
                var timeSet = $("#activeTrend-date").val(function() {
                    return inactTrendOps.startDate;
                }).datetimepicker({
                    format: "yyyy-mm-dd",
                    autoclose: true,
                    minView: 1,
                    todayBtn: true,
                    initialDate: inactTrendOps.startDate
                });
                
                var days=$("#activeTrend-before").val(inactTrendOps.startDays);
                var hours=$("#activeTrend-hour").val(inactTrendOps.startHours);
                
                var time = $("#activeTrend-date").val();
                var initDate=moment(time, "YYYY-MM-DD HH").format("YYYY_MM_DD HH");
                inactTrendOps.setData(initDate,inactTrendOps.startDays,inactTrendOps.startHours)
                
                $("#activeTrend-submit").click(function() {
                	inactTrendOps.startDays=$("#activeTrend-before").val();
                	inactTrendOps.startHours=$("#activeTrend-hour").val();
                    
                    var time = $("#activeTrend-date").val();
                    var initDate=moment(time, "YYYY-MM-DD HH").format("YYYY_MM_DD HH");
                    inactTrendOps.setData(initDate,inactTrendOps.startDays,inactTrendOps.startHours)
				})
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
        showSite: function() {
            initSiteMap();
        },
        showSiteChange: function(id) {
            siteChangeOps.show(id);
        },
        showSiteChangeById: function(id) {
            siteChangeOps.showByID(id);
        },
        showActive: function() {
            activeOps.init();
        },
        showFlow: function(time) {
        	flowOps.init();
        },
        showVaria: function(time) {
            initVariaMap();
        },
        setData: function(time, dist) {
            loadVariaData();
        },
        showInactiveTrend: function() {
        	inactTrendOps.show();
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