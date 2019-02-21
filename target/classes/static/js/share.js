var share = {};
share.switcher = {
    changeMod: function(id, check) {
        var mod = id.substring(5);
        if (mod.substring(0, 3) == 'map') {
            mod = mod.substring(4, mod.length);
            if (check) {
                mapLayer.addMapFeature(mod);
            } else {
                mapLayer.removeMapFeature(mod);
            }
        } else if (mod.substring(0, 6) == 'marker') {
            var name = mod.substring(7, mod.length);
            if (check) {
                mapLayer.showMarkers(name);
            } else {
                mapLayer.hideMarkers(name);
            }
        } else {
            if (check) {
                mapLayer.showMod(mod);
            } else {
                mapLayer.hide(mod);
            }
        }
    },
    bind: function() {
        var swts = $(".js-switch");
        swts.each(function(index, ele) {
            var swit = new Switchery(ele);
            // ele.checked=true;
        });
        var that = this;
        swts.change(function() {
            console.log(this.id);
            share.switcher.changeMod(this.id, this.checked);

        });
    },
}
share.clockLoader = {
    clock: $("#clockLoader"),

    show: function() {
        this.clock.show();
    },
    hide: function() {
        this.clock.hide();
    },
}
share.area = {
    block: null,
    init: function() {
        // 108.891913, 34.286154, 108.996753, 34.240899
        var southWest = new AMap.LngLat(108.891913, 34.240899)
        var northEast = new AMap.LngLat(108.996753, 34.286154)

        var bounds = new AMap.Bounds(southWest, northEast)

        this.block = new AMap.Rectangle({
            bounds: bounds,
            strokeColor: 'red',
            strokeWeight: 6,
            strokeOpacity: 0.5,
            strokeDasharray: [30, 10],
            // strokeStyle还支持 solid
            strokeStyle: 'solid',
            fillColor: 'blue',
            fillOpacity: 0.5,
            cursor: 'pointer',
            zIndex: 50,
        })

        var rec = this.block;
        rec.hide();
        rec.setMap(map);

        // map.setFitView([ rec ]);
        var rectangleEditor;
        map.plugin(['AMap.RectangleEditor'], function() {
            rectangleEditor = new AMap.RectangleEditor(map, rec);

            rectangleEditor.on('adjust', function(e) {
                console.log(e);
            })

            rectangleEditor.on('end', function(e) {
               
                // event.target 即为编辑后的矩形对象
            })
            rectangleEditor.close();
        })

        $("#btn-showArea").click(function() {
            console.log(this);
            var btn = $(this);
            if (btn.attr("data-show") == "false") {
                btn.text("隐藏");
                rec.show();
                rectangleEditor.open();
                btn.attr("data-show", "true");
            } else {
                btn.text("显示");
                rec.hide();
                rectangleEditor.close();
                btn.attr("data-show", "false");
            }
        });
        $("#btn-resetArea").click(function() {
            var btn = $(this);

        });
        // 缩放地图到合适的视野级别

    },

}
share.params = {
    format: "YYYY_MM_DD HH:00",
    formTime: function(time) {
        return moment(time).format(this.format);
    },
    getSpan: function(start, end) {
        console.log(start);
        var st = moment(start, this.format);
        var en = moment(end, this.format);
        var range = moment.twix(st, en);
        var hour = range.length("hours");
        console.log(hour);
        return hour;
    },
    cluster: function() {
        var btn = $("#clusterDist");
        if (btn) {
            var input = btn.val();
            if (input < 10 || input > 100) {
                return mapLayer.config.clusterDist;
            }
            if (input && input != '') {
                mapLayer.config.setParam("clusterDist", input);
            }
            return mapLayer.config.clusterDist = input;
        }
        return null;
    },
    picker: null,
    setRange: function(start, end) {

        $('#time-picker').data('daterangepicker').setStartDate(
            moment(start).format(this.format));
        $('#time-picker').data('daterangepicker').setEndDate(
            moment(end).format(this.format));
    },
    getPicker: function() {
        return $('#time-picker').data('daterangepicker');
    },
    init: function() {
        $("#clusterDist").val(mapLayer.config.clusterDist);
        $("#inActiveHours").val(mapLayer.config.inactHours);
        mapUtil.link.getData("/map/daterange", "true", {}, function(timerange) {
            var start = mapUtil.utils.parser(timerange[0]);
            var end = mapUtil.utils.parser(timerange[1], 23);

            share.params.picker = $('#time-picker')
                .daterangepicker({
                        "timePicker": true,
                        "timePicker24Hour": true,
                        "opens": "right",
                        "locale": {
                            "format": share.params.format,
                            "separator": "~",
                            "applyLabel": "Apply",
                            "cancelLabel": "Cancel",
                            "fromLabel": "From",
                            "toLabel": "To",
                            "customRangeLabel": "Custom",
                            "weekLabel": "W",
                            "daysOfWeek": ["Su", "Mo", "Tu", "We",
                                "Th", "Fr", "Sa"
                            ],
                            "monthNames": ["1月", "2月", "3月", "4月",
                                "5月", "6月", "7月", "8月", "9月",
                                "10月", "11月", "12月"
                            ],
                            "firstDay": 1
                        },
                        "showCustomRangeLabel": false,
                        "startDate": end,
                        "endDate": end,
                        "minDate": start,
                        "maxDate": end,
                    },
                    function(start, end, label) {
                        console.log('改变了时间参数: ' +
                            start.format(share.params.format) +
                            ' to ' +
                            end.format(share.params.format));
                    });
        });
        $("#submit-param").click(
            function() {
                // 设置聚类距离，获取数据时读取那个数值
                var dist = share.params.cluster();

                // 获取时间范围，
                var pick = share.params.getPicker();
                var start = pick.startDate;
                var end = pick.endDate;
                
                var inact=$("#inActiveHours").val();
                if (start.isSame(end)) {
                    console.log("准备加载单文件数据" + start);
                    mapLayer.config.setParam("startTime", start
                        .format(share.params.format));
                    mapLayer.config.setParam("endTime", start
                        .format(share.params.format));
                    mapLayer.config.setParam("inactHours",inact);
                    mapLayer.resetMods();
                } else {
                    console.log("准备加载区间数据" + start + "--" + end);
                    mapLayer.config.setParam("startTime", start
                        .format(share.params.format));
                    mapLayer.config.setParam("endTime", end
                        .format(share.params.format));
                    mapLayer.resetMods();
                }

            });
    },
}

// init time picker ----END

share.infoPanel = {
    inited: false,
    startMark: null,
    endMark: null,
    bindMark: function(btn, mark, cords) {
        if (mark && mark != null) {
            if (mark.Je.visible) {
                mark.hide();
                $(btn).css("background-color", "#f4f4f4");
            } else {
                mark.show();

                $(btn).css("background-color", "#3c8dbc");
            }
        } else {
            mark = new AMap.Marker({
                icon: mapLayer.icons.point,
                // [116.406315,39.908775]
                position: [cords[0], cords[1]],
                offset: new AMap.Pixel(-12, -12),
                zIndex: 2,
                map: map,
                visible: true,
                title: "区域起点",
            });
            $(btn).css("background-color", "#3c8dbc");
        }
        console.log(cords[0]);
        // map.setCenter([lng, lat]); //设置地图中心点
        map.setCenter([cords[0], cords[1]]);
        return mark;
    },
    setData: function(panInfo) {
        /*
         * var pan={ header:"1111", body:{ name1:1 } }
         */
        var panel = $("#data-panel");

        if (panInfo.bikesCount) {
            panel.find("span[data-count]").text(panInfo.bikesCount);
        }
        if (panInfo.startTime) {
            panel.find("span[data-startTime]").text(panInfo.startTime);

        }
        if (panInfo.endTime) {
            panel.find("span[data-endTime]").text(panInfo.endTime);

        }
        if (panInfo.timeSpan) {
            panel.find("span[data-timeSpan]").text(panInfo.timeSpan);

        }

        var startRe = panel.find("button[data-regionStart]");
        startRe.attr("data-coords", panInfo.regionStart);
        var endRe = panel.find("button[data-regionEnd]")
        endRe.attr("data-coords", panInfo.regionEnd);

        if (!this.inited) {
            startRe.click(function() {
                var cords = $(this).attr("data-coords").split(",");
                var mark = share.infoPanel.startMark;
                share.infoPanel.startMark = share.infoPanel.bindMark(this,
                    mark, cords);
            });
            endRe.click(function() {
                var cords = $(this).attr("data-coords").split(",");
                var mark = share.infoPanel.endMark;
                share.infoPanel.endMark = share.infoPanel.bindMark(this, mark,
                    cords);
            });
            this.inited = true;
        }
        if (panInfo.weather) {
            panel.find("span[data-weather]").html(panInfo.weather);
        }

        if (panInfo.tempareture) {
            var temperature = panInfo.tempareture;
            panel.find("span[data-temperature]").html(temperature);
        }

    },

};

share.chart = {
    bindTab: function() {
        $("#console-chart a[href='#tab-stack']").click(function(event) {
        	mapChart.showStack();
        });
        $("#console-chart a[href='#tab-site']").click(function(event) {
        	mapChart.showSite();
        });
        $("#console-active a[href='#tab-duration']").click(function(event) {
            mapChart.showInactiveTrend();
        });
        $("#console-active a[href='#tab-avtivePie']").click(function(event) {
            mapChart.showActive();
        });
        
        $("#console-site a[href='#tab-siteChange']").click(function(event) {
        	mapChart.showSiteChange();
        });
        $("#console-chart a[href='#tab-daily']").click(function(event) {
        	mapChart.showDaily();
        });
 		$("#console-site a[href='#tab-siteFlow']").click(function(event) {
        	mapChart.showFlow();
        });  
 		
 		

    }
}
share.console = {

    set: {
        map: [],
        chart: null,
    },

    saveSet: function(module) {

        var swts = $(".js-switch");
        var that = this;
        swts.each(function(index, ele) {
            if (ele.checked) {
                that.set[module].push(ele.id);
            }
        });
        console.log(this.set);

    },
    showSet: function() {
        var swts = $(".js-switch");
        var that = this;
        this.set.map.forEach(function(value) {
            share.switcher.changeMod(value, true);
        });
        this.set.map = [];
    },
    show: function(consoleName) {
        $(".console").hide();
        $("#console-" + consoleName).show();
    },
    change: function() {
        var link = window.location.hash;
        
        
        if (link == "") {
            this.show("map");
            $("#modelTitle").text("控制台");
        }  else { // 其他页面
            link = link.substring(1, link.length);
           if(link == "manage"){
            	this.show("manage");
            	$("#modelTitle").text("资源管理");
            	manage.init();
            }else if (link == "anaylyze_data") {
                this.show("chart");
                $("#modelTitle").text("数据分析");
                mapChart.showVaria();
            }else if (link == "anaylyze_active") {
                this.show("active");
                $("#modelTitle").text("活跃度分析");
                mapChart.showInactiveTrend();
            }else if (link == "anaylyze_site") {
                this.show("site");
                $("#modelTitle").text("站点分析");
                mapChart.showSite();
            }else if(link == "simuler"){
            	this.show("simuler");
            	$("#modelTitle").text("调度模拟");
            	mapSimu.init();
            }else if(link == "plan"){
            	this.show("plan");
            	mapPlan.test();
            }
        }
    },
};

// *************************初始化开始*************************

$(document).ready(function() {
    // 先有界面后有天,反手神仙也难堪
    share.console.change();
    
    //设置用户名字
    var userName=window.location.search;
    userName=userName.substring(6,userName.length);
    $("#userProfile .info p").text(userName);
    
   

    // 地图加载放到这里，放在别的地方在2d时速度快，在3d时就出现问题
    var map = new AMap.Map('container', {
        viewMode: '3D', // 使用2D视图
        zoom: 15, // 级别
        //pitch: 50,
        //mapStyle: 'amap://styles/twilight',
        // 'bg'（地图背景）、'point'（POI点）、'road'（道路）、'building'（建筑物）
        features: ['bg','point','road', 'building'],
        center: [108.916913, 34.265569], // 中心点坐标
    });

    AMap.plugin(['AMap.ControlBar'], function() { //异步同时加载多个插件


        var controlBar = new AMap.ControlBar({
            position: {
                "right": '20px',
                "top": '20px'
            },
        });
        map.addControl(controlBar);

    });

    window.map = map;

    share.params.init();
    // 绑定hashchange的变换console时间
    $(window).bind("hashchange", function() {
        share.console.change();
    });
    share.area.init();

    //地图元素切换
    share.switcher.bind();

    //初始化地图要素
    mapLayer.initBus();
    mapLayer.initMass("bikePos");
    mapLayer.initCluster("cluster");
    mapLayer.initHeatMap("heat");

    mapLayer.showMod("bikePos");

    share.chart.bindTab();
    
    mapChart.showSiteChange();


    map.on('hotspotclick', function(argus) {
        console.log(argus);
    });

    layer.msg('hello,欢迎来到shareBIKE,这里有关于共享单车的一切！');

});

// *************************初始化结束*************************

// *************************获取数据开始***********************

// var trafficLayer = new AMap.TileLayer.Traffic({
// zIndex: 10
// });
//
// trafficLayer.setMap(map);
// console.log(trafficLayer);

// AMap.plugin([ "AMap.StationSearch" ], function() {
// // 实例化公交站点查询类
// var station = new AMap.StationSearch({
// pageIndex : 5, // 页码，默认值为1
// pageSize : 10, // 单页显示结果条数，默认值为20，最大值为50
// city : '029' // 限定查询城市，可以是城市名（中文/中文全拼）、城市编码，默认值为『全国』
// });
//
// // 执行关键字查询
// station.search('105街坊', function(status, result) {
// // 打印状态信息status和结果信息result
// // status：complete 表示查询成功，no_data 为查询无结果，error 代表查询错误。
// console.log(status, result);
// });
// });

// share.clockLoader.show();
// share.clockLoader.hide();
//
// share.clockLoader.setProgress(80);
// mapLayer.changeModule("test");