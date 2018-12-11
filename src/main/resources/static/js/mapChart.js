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
    
    var siteChangeChart;
    var siteChangeOption = {
    	    xAxis: {
    	        type: 'category',
    	        data: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]
    	    },
    	    yAxis: {
    	        type: 'value'
    	    },
    	    series: [{
    	        data: [],
    	        type: 'line'
    	    }]
    	};
    var siteChangeTable = $("#siteChange-list");
    var siteChangeOps = {
            inited: false,
            showByID:function(id){
            	if (!this.inited) {
            		this.show(id);
            	}else{
            		this.loadData(id);
            	}
            },
            loadData:function(id){
            	mapUtil.link.getData("/site/change", true, {
              		 siteID:id
              	 	}, function(data) {
                       if (data) {
                      	 siteChangeChart.setOption({                           
                             series: [{
                                 name: '单车总数',
                                 data: data
                             }]
                         });
                       }
                   });
            },
            getRow:function(row,ele){
            	siteChangeOps.loadData(row.id);
            },
            show: function(id) {
                if (!this.inited) {
                    console.log('加载折线图');
                    siteChangeChart = echarts.init($("#chart-siteChange")[0]);
                    siteChangeChart.setOption(siteChangeOption);
                    if(id){
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
//                                	 "index":count++,
                                    "id": ele.id,
                                    "name": ele.name,
                                    "type": ele.type,
                                    "volume":ele.volume,
                                });
                            });
                            siteChangeTable.bootstrapTable('load', list);
                        }
                    });
                   
                    
//                    $("#submit-site-change").click(function(){
//                    	 var siteID=$("#site-change-id").val();
//                    	 console.log(siteID);
//                    	 mapUtil.link.getData("/site/change", true, {
//                    		 siteID:siteID
//                    	 }, function(data) {
//                             console.log(data);
//
//                    		 siteChangeChart.setOption({                           
//                                 series: [{
//                                     // 根据名字对应到相应的系列
//                                     name: '单车总数',
//                                     data: data
//                                 }]
//                             });
//
//
//                         });
//                    });
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
    
    var siteDom = document.getElementById("chart-site");
    var siteChart;
    var siteInited=false;
    var siteSlider=null;
    
    var scoreData=[];
    var clusterData=[];
    
    var initSiteMap = function(time) {
        if (!siteInited) {

            var smap = new BMap.Map("chart-site");
            // 创建地图实例
            var point = new BMap.Point(108.951771, 34.266582);
            // 创建点坐标
            smap.centerAndZoom(point, 15);

            siteChart = echarts.init(siteDom);

            var siteInit={
        			rate:1,
        			flucSca:60,
        			countSca:30,
        			poiSca:10,
        			clusterDist:200,
        	}
            loadSiteScoreData(siteInit);
            loadSiteClusterData(siteInit,setSiteShow);
            
           
            
            siteInited = true;
            
            siteSlider=$('#site-rate').slider({
            	formatter: function(value) {
            		return 'Current value: ' + value;
            	}
            });
            
            var siteScaleSlider=$("#site-scoreScal").slider({
            	min: 0,
            	max: 100,
            	value: [60, 90],
            	labelledby: ['site-scoreScal-1', 'site-scoreScal-2'],
            	formatter:function(){
            		return "波动占比:"+arguments[0][0]+"%  最大停放数占比:"
            		+(arguments[0][1]-arguments[0][0])+"%  POI占比:"+(100-arguments[0][1]+"%");
            	},
            });
            
            var swts = $(".site-switch");
            var swit;
            swts.each(function(index, ele) {
                swit = new Switchery(ele);
                $(this).change(function(){
                	if(this.checked){
                		$("#show-site-check label").text("预选点");
                		setSiteShow(scoreData);
                    }else{
                    	$("#show-site-check label").text("站点");
                    	setSiteShow(clusterData);        		
                    }
                });
            });
            
            $("#site-cluster-Dist").val(siteInit.clusterDist);
            
            $("#submit-site").click(function(){
            	var slidVal=$(siteSlider).val();
            	
            	
            	var scaleVal=$(siteScaleSlider).val().split(',');
            	var scale1=scaleVal[0];
            	var scale2=scaleVal[1]-scale1;
            	var scale3=100-scaleVal[1];
            	
            	var clusterDist=$("#site-cluster-Dist").val();
            
            	var params={
            			rate:slidVal,
            			flucSca:scale1,
            			countSca:scale2,
            			poiSca:scale3,
            			clusterDist:clusterDist
            	}
            	if(swit.element.checked){
            		loadSiteScoreData(params,setSiteShow);
            		loadSiteClusterData(params);
            	}else{
            		loadSiteScoreData(params);
            		loadSiteClusterData(params,setSiteShow);
            	}
            });
            
            $("#submit-siteBase").click(function(){
            	var slidVal=$(siteSlider).val();
            	var scaleVal=$(siteScaleSlider).val().split(',');
            	var scale1=scaleVal[0];
            	var scale2=scaleVal[1]-scale1;
            	var scale3=100-scaleVal[1];
            	var clusterDist=$("#site-cluster-Dist").val();
                
            	var params={
            			rate:slidVal,
            			flucSca:scale1,
            			countSca:scale2,
            			poiSca:scale3,
            			clusterDist:clusterDist
            	}
            	mapUtil.link.getData("/site/submit", true,params , function(data) {
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
    
    var setSiteShow=function(list){
    	var option = {
                tooltip: {},
                visualMap: {
                	pieces: [
                	    {min: 0.2},
                	    {min: 0.15, max: 0.2},
                	    {min: 0.1, max: 0.15},
                	    {min: 0.08, max: 0.1},
                	    {min: 0.04, max: 0.08},
                	    {min: 0.02, max: 0.04},
                	    {min: 0.01, max: 0.02},
                	    {max: 0.1}     
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
                siteChart.setOption(option,{
                	notMerge:true,
                });

            }
    }
    var loadSiteScoreData = function(param,showFunc) {

    	mapUtil.link.getData("/site/score", true, param, function(data) {
    		if (data) {
    			var list = [];
    			for(var i in data){
    				var item={
    						name:[data[i].lng,data[i].lat],
    						value:[data[i].lng,data[i].lat,i],
    				}
                    list.push(item);
    				
    			};
    			scoreData=list;
    			if(showFunc){
    				showFunc(scoreData);
    			}
    			
            }
        });

    };
    var loadSiteClusterData = function(param,showFunc) {
    
    	mapUtil.link.getData("/site/cluster", true,param, function(data) {
    		if (data) {
    			var list = [];
    			data.forEach(function(ele,index){
    				var item={
    						name:[ele.name],
    						value:[ele.lnglat[0],ele.lnglat[1]],
    				}
                    list.push(item);
    			});
    			clusterData=list; 
    			if(showFunc){
    				showFunc(clusterData);
    			}
    			
            }
        });

    };

    
    var list = [];
    var colors = [
        '#2c7bb6',
        '#abd9e9',
        '#ffffbf',
        '#fdae61',
        '#d7191c'
    ];
    var colors1 = [
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000000',
        '#000033',
        '#000066',
        '#003366',
        '#000099',
        '#003399',
        '#0000CC',
        '#0033CC',
        '#0000FF',
        '#0033FF',
        '#0066FF',
        '#0099FF',
// '#FFFFFF',
// '#FFFFCC',
// '#FFFF99',
// '#FFCC99',
// '#FFCC66',
// '#FF6600',
        '#FF3300',
        '#FF0000',
    ];
    // 000033,000099,0033FF,9933CC,FFFF99,FF9933 ,FF3366,FF0000
    // 000080,0000FF,00BFFF,00FFFF,FAF0E6,FFFACD ,FF8C00,FF00FF,FF0000
    
// mapUtil.link.getData("/site/score", true, {}, function(data) {
// if (data) {
// for(var i in data){
// list.push({
// coord: [data[i].center.lng,data[i].center.lat],
// value: +data[i].score
// });
// }
// siteLayer.setData(list, {
// lnglat: 'coord'
// });
// siteLayer.setOptions({
// // 单位米
// unit: 'meter',
// style: {
// // 正多边形半径
// radius: 10,
// // 高度为 0 即可贴地面
// height: {
// key: 'value',
// value: [100, 1000]
// },
// // 顶面颜色
// color: {
// key: 'value',
// scale: 'quantile',
// value: colors1
// },
// opacity: 0.85
// },
// selectStyle:{
// radius: 20,
// color: '#ffe30a',
// opacity: 1
// }
// });
//
// siteLayer.render();
//
// }
// });
    
    

    

   

    
    
// var siteMap= new AMap.Map('chart-site', {
// viewMode: '2D',
// zoom: 15,
// features: ['bg','point','road', 'building'],
// center: [108.916913, 34.265569], // 中心点坐标
// });
//    
// mapUtil.link.getData("/site/score", true, {}, function(data) {
// if (data) {
// console.log(data);
// var list = [];
// data.forEach(function(marker) {
// marker = new AMap.Marker({
// icon: "image/point.png",
// position: [marker.location.lng, marker.location.lat],
// offset: new AMap.Pixel(-12, -12),
// zIndex: 2,
// map: siteMap,
// visible: true,
// title: marker.name
// });
// list.push(marker);
// });
//
// }
// });


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
        showVaria: function(time) {
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