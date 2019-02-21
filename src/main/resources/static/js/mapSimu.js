;
(function(global, undefined) {


    var simuMap;
    var simuSet = {
        date: null,
    }

    var startBtn = $("#btn-simu-start");
    var pauseBtn = $("#btn-simu-pause");
    var stopBtn = $("#btn-simu-stop");

    // 0-notStart,1-start,2-pause,3-stop
    var simuState = 0;
    var simuID = null;

    var configs = {
        truckType: null,
        triType: null,
        manType: null,
        truckSpeed: null,
        triSpeed: null,
        manSpeed: null,
        truckCap: null,
        triCap: null,
        manCap: null,
        loadUnitTime: null,
        speedRatio:10,
        loadRatio:200,
        getSpeedByType: function(type) {
        	var initSpeed;
            if (type == this.truckType) {
            	initSpeed=this.truckSpeed;
            } else if (type == this.triType) {
            	initSpeed=this.triSpeed;
            } else {
            	initSpeed=this.manSpeed;
            }
            return initSpeed*this.speedRatio;
        },
        setConfig: function() {
            mapUtil.link.getData("/simu/config", true, {}, function(data) {
                if (data) {
                    configs.truckType = data.truckType;
                    configs.triType = data.tricycleType;
                    configs.manType = data.manType;
                    configs.truckSpeed = data.truckSpeed;
                    configs.triSpeed = data.tricycleSpeed;
                    configs.manSpeed = data.manSpeed;
                    configs.truckCap = data.truckCapacity;
                    configs.triCap = data.tricycleCapacity;
                    configs.manCap = data.manCapacity;
                    configs.loadUnitTime = data.loadUnitTime;
                }
            });
        }(),
    }


    var btnOps = {

        toggleBtn: function(btn) {
            if (!btn) {
                return;
            }
            if (btn.hasClass("disabled")) {
                btn.removeClass("disabled");
            } else {
                btn.addClass("disabled");
            }
        },
        togglePause: function(flag) {
            if (flag) {
                pauseBtn.find(".glyphicon").removeClass("glyphicon-repeat")
                    .addClass("glyphicon-pause");
                pauseBtn.find(".simu-pause").text("暂停");
            } else {
                pauseBtn.find(".glyphicon").removeClass("glyphicon-pause")
                    .addClass("glyphicon-repeat");
                pauseBtn.find(".simu-pause").text("恢复");
            }
        },
        startOp: function() {

            if (simuState == 0) {
                simuID = null;
                btnOps.toggleBtn(startBtn);
                simuState = 1;


                var cars = markerOps.dispats;
                var carPos = [];
                cars.forEach(function(ele, index) {
                    carPos.push({
                        id: ele.id,
                        lng: ele.lng,
                        lat: ele.lat,
                    });
                });
                mapUtil.link.postJson("/simu/start", true, {
                    carPos: carPos
                }, function(data) {
                    if (data) {
                    	console.log(data);
                        simuID = data.uuid;
                        markerOps.initJobs(data.list);
                    }
                });
            }

        },
        pauseSim: function() {
            if (simuState == 1) {

                btnOps.togglePause(false);
                simuState = 2;
                markerOps.togglePause(true);

            } else if (simuState == 2) {
                btnOps.togglePause(true);
                simuState = 1;
                markerOps.togglePause(false);
            }
        },
        endSim: function() {

            if (simuState == 1 || simuState == 2) {

                markerOps.stopMove();
                mapUtil.link.getData("/simu/stop", false, {
                    simuID: simuID
                }, function(data) {
                    if (data) {
                        simuState = 0;
                        btnOps.togglePause(true);
                        btnOps.toggleBtn(startBtn);

                    }
                });

            }
        },

    }

    var init = {
        inited: false,
        intMap: function() {
            if (!this.inited) {
                simuMap = new AMap.Map('simu-map', {
                    viewMode: '2D', // 使用2D视图
                    zoom: 15, // 级别
                    features: ['bg', 'road', 'building'],
                    // features: [],
                    center: [108.916913, 34.265569], // 中心点坐标
                });

                this.initMapMark();
                var timeSet = $("#simu-init-date").val(function() {
                    return moment("2018-11-01", "YYYY_MM_DD").format("YYYY-MM-DD");
                }).datetimepicker({
                    format: "yyyy-mm-dd hh:00",
                    autoclose: true,
                    minView: 1,
                    todayBtn: true,
                    initialDate: "2018-11-01"
                });

                timeSet.on('changeDate', function(ev) {
                	
                    var input = ev.date;
                    console.log(input)
                    var formed = moment(input, "dddd MMMM DD YYYY").format("YYYY_MM_DD");
                    simuSet.date = formed;
                });

                startBtn.click(btnOps.startOp);
                pauseBtn.click(btnOps.pauseSim);
                stopBtn.click(btnOps.endSim);

                console.log("初始化模拟");
                this.initDispPos();

                this.inited = true;
            }

        },
        getMapLngLat: function(ev) {
            var lnglat = ev.lnglat;
            // this代表传入的参数，ev代表地图事件

            var btn = this.btn;
            $(btn).addClass("btn-success");

            var car = this.val;
            car.startPos = lnglat;
            car.lng = lnglat.lng;
            car.lat = lnglat.lat;

            var target = markerOps.dispats.find(function(current, index, arr) {
                if (current.name == this) {
                    return current;
                }
            }, car.name);
            // 上面对列表进行寻找，如果找到，说明在地图上，更新其位置，否则，进行添加
            if (typeof(target) == "undefined") {

                if (car.type == configs.truckType) {
                    markerOps.addMarker(car, markerOps.truckIcon);
                } else if (car.type == configs.triType) {
                    markerOps.addMarker(car, markerOps.tricycleIcon);
                } else {
                    markerOps.addMarker(car, markerOps.manIcon);
                }

                markerOps.dispats.push(car);
            } else {
                target.marker.setPosition([car.lng, car.lat]);
            }

            init.unbindMapEvent(init.getMapLngLat);
            return lnglat;
        },
        bindMapEvent: function(func, param) {
            simuMap.on('click', func, param);

        },
        unbindMapEvent: function(func) {
            simuMap.off('click', func);
        },
        initDispPos: function() {
            var dispTable = $('#simu-disp-pos');

            function StartPosFormatter(value, row, index) {
                return [
                    '<button type="button" class="simu-startPos-set btn btn-primary  btn-sm">设置</button>'
                ].join('');
            }
            window.StartPosEvents = {
                'click .simu-startPos-set': function(e, value, row, index) {
                    // 思路：不让点击上浮，把当前按钮和地图事件传入
                    var param = {
                        btn: this,
                        val: row,
                    }
                    event.stopPropagation();
                    init.bindMapEvent(init.getMapLngLat, param);
                }
            };
            dispTable.bootstrapTable({
                striped: true, // 是否显示行间隔色
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                sortable: false, // 是否启用排序
                sortOrder: "asc", // 排序方式
                sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                pageSize: 10, // 每页的记录行数（*）
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                checkboxHeader: false, // 隐藏前面的复选框
                uniqueId: "name", // 每一行的唯一标识，一般为主键列
                icons: {
                    refresh: 'glyphicon-refresh icon-refresh',
                    toggle: 'glyphicon-list-alt icon-list-alt',
                    columns: 'glyphicon-th icon-th'
                },
                columns: [{
                    field: 'name',
                    title: '用户名'
                }, {
                    field: 'typeName',
                    title: '类型'
                }, {
                    field: 'startPos',
                    title: '初始位置',
                    events: StartPosEvents,
                    formatter: StartPosFormatter
                }],
            });

            mapUtil.link.getData("/disp/all", true, {}, function(data) {
                if (data) {
                    var list = [];
                    data.forEach(function(ele, index) {
                        var typeName;

                        if (ele.type == configs.truckType) {
                            typeName = "卡车";
                        } else if (ele.type == configs.triType) {
                            typeName = "三轮车";
                        } else {
                            typeName = "调度员";
                        }
                        list.push({
                            "id": ele.id,
                            "name": ele.name,
                            "type": ele.type,
                            "typeName": typeName,
                            "startPos": [ele.lng, ele.lat]
                        });
                    });
                    dispTable.bootstrapTable('load', list);
                }
            });

        },
        initMapMark: function() {
            mapUtil.link.getData("/site/allTypes", true, {}, function(data) {
                if (data) {
                    markerOps.addMarkers(data);
                }
            });
        },
    }



    var simuOps = {
        start: function() {},
        end: function() {},
        stop: function() {},
    }

    var markInfo = {
        infoSite:null,
        infoDisp:null,
        siteInfo: function(mark,siteInfo) {
        	var content=[];
        	content.push('<ul class="nav nav-pills nav-stacked">');
        	content.push('<li><i class="fa fa-circle-o text-red"></i> 站点名称<span\
			data-siteLimit class="label label-primary pull-right">'+siteInfo.name+'</span></li>');
        	content.push('<li><i class="fa fa-circle-o text-yellow"></i> 站点类型<span\
    				data-siteLimit class="label label-primary pull-right">'+siteInfo.type+'</span></li>');
        	content.push('<li><i class="fa fa-circle-o text-blue"></i> 站点容量<span\
        			data-siteLimit class="label label-primary pull-right">'+siteInfo.volume+'</span></li>');
        	content.push('<li><i class="fa fa-circle-o text-purple"></i> 站点存量<span\
				data-siteLimit class="label label-primary pull-right">20</span></li>');
        	content.push('</ul>');
        	this.infoSite = new AMap.InfoWindow({
                content: content.join(""),
                offset:new AMap.Pixel(0, -24),
        		size:new AMap.Size(200, 110),
            });
        	this.infoSite.open(simuMap,mark.getPosition());
        },
        dispInfo: function(mark,dispInfo) {
        
        	var content=[];
        	content.push('<ul class="nav nav-pills nav-stacked">');
        	content.push('<li><i class="fa fa-circle-o text-red"></i> 调度名称<span\
        			data-siteLimit class="label label-primary pull-right">'+dispInfo.name+'</span></li>');
        	content.push('<li><i class="fa fa-circle-o text-yellow"></i> 调度类型<span\
    				data-siteLimit class="label label-primary pull-right">'+dispInfo.typeName+'</span></li>');
        	content.push('</ul>');
        	dispInfo.infoDisp = new AMap.InfoWindow({
                content: content.join(""),
                offset:new AMap.Pixel(0, -24),
        		size:new AMap.Size(200, 70),
        		position:mark.getPosition(),
            });
        	dispInfo.infoDisp.open(simuMap);
        },

    }


    var markerOps = {
        dispats: [],
        getNextTask: function(carID) {
            mapUtil.link.getData("/simu/next", true, {
                simuID: simuID,
                dispID: carID
            }, function(data) {
                if (data) {
                    if (data.taskType == 1) { // 移动装卸任务

                        markerOps.setLoadTask(carID, data.path.paths);
                    } else if (data.taskType == 2) { // 其他任务
                        
                    }
                }
            });
        },
        setLoadTask: function(carID, carPath) {
            var carMark = this.getMark(carID);
            var arrayPath = [];

            for (var i in carPath) {
                arrayPath.push([carPath[i].lng, carPath[i].lat]);
            }
            carMark.moveLine = new AMap.Polyline({
                map: simuMap,
                path: arrayPath,
                showDir: true,
                strokeColor: "#28F", // 线颜色
                // strokeOpacity: 0.8, //线透明度
                // strokeWeight: 6, //线宽
                strokeStyle: "solid", // 线样式
            });
            simuMap.add(carMark.moveLine);

            var unitSpeed = configs.getSpeedByType(carMark.type);
            carMark.marker.moveAlong(arrayPath, unitSpeed);


        },
        getMark: function(carID) {
            var carData = this.dispats.find(function(current, index, arr) {
                if (current.id == carID) {
                    return current;
                }
            }, carID);
            return carData;
        },
        initJobs: function(jobList) {
            var cars = this.dispats;
            console.log("初始化工作");
            
            cars.forEach(function(ele, index) {
                var carID = ele.id;
                var carData = jobList.find(function(current, index, arr) {
                    if (current.dispatcher.id == carID) {
                        return current;
                    }
                }, carID);
                
                var carPath = carData.path.paths;
                var carMark = markerOps.getMark(carID);
                markerOps.setLoadTask(carID, carPath);
                carMark.marker.on('movealong', function() {
                    simuMap.remove(carMark.moveLine);
                    console.log("移动结束，准备装载");
                    var loadNum = carData.loadNum;
                    var waitTime = parseInt(loadNum * configs.loadUnitTime * 1000 / configs.loadRatio);
                    setTimeout(function() {
                        console.log("装载结束");
                        markerOps.getNextTask(carID);
                    }, waitTime);

                });
                carMark.marker.on('moving', function(ev) {
                	if(carMark.infoDisp){
                		carMark.infoDisp.setPosition(this.getPosition());
                	}
                });
                
            });
        },
        // 只能选择一个路径展示
        toggleRoute: function() {

            var passedPolyline = new AMap.Polyline({
                map: simuMap,
                strokeColor: "#AF5", // 线颜色
                // strokeOpacity: 1, //线透明度
                // strokeWeight: 20, //线宽
                strokeStyle: "dashed", // 线样式
            });
            carMark.marker.on('moving', function(e) {
                passedPolyline.setPath(e.passedPath);
            });
        },
        togglePause: function(flag) {
            var cars = this.dispats;
            for (var i in cars) {
                if (flag) {
                    cars[i].marker.pauseMove();
                } else {
                    cars[i].marker.resumeMove();
                }

            }
        },
        stopMove: function() {
            var cars = this.dispats;
            for (var i in cars) {
                cars[i].marker.stopMove();
            }
        },
        siteIcon: {
            image: "image/simu/house.png",
            imageSize: new AMap.Size(24, 24),
            offset: new AMap.Pixel(-12, -24)
        },
        redSiteIcon: {
            image: "image/simu/houseRed.png",
            imageSize: new AMap.Size(24, 24),
            offset: new AMap.Pixel(-12, -24)
        },
        truckIcon: {
            image: "image/simu/truck.ico",
            imageSize: new AMap.Size(36, 36),
            offset: new AMap.Pixel(-12, -36)
        },
        tricycleIcon: {
            image: "image/simu/tricycle.ico",
            imageSize: new AMap.Size(36, 36),
            offset: new AMap.Pixel(-12, -36)
        },
        manIcon: {
            image: "image/simu/man.ico",
            imageSize: new AMap.Size(24, 24),
            offset: new AMap.Pixel(-12, -24)
        },
        addMarker: function(item, icon) {
            item.marker = new AMap.Marker({
                icon: new AMap.Icon(icon),
                position: [item.lng, item.lat],
                map: simuMap,
            });
            item.marker.on('click', function (ev) {
                markInfo.dispInfo(this,item);
            });
            
        },

        addMarkers: function(data) {
        	var list=data.sites;
        	var types=data.types;
        	console.log(data);
        	var icon;
        	list.forEach(function(ele,index) {
        		if(types[index]==1){
        			icon=markerOps.siteIcon;
        		}else{
        			icon=markerOps.redSiteIcon;
        		}
        		ele.marker = new AMap.Marker({
                    icon: new AMap.Icon(icon),
                    position: [ele.lng, ele.lat],
                    map: simuMap,
                });
                ele.marker.on('click', function (ev) {
                    markInfo.siteInfo(this,ele);
                });
			});
        },
        clearMarer: function(marker) {
            if (marker) {
                marker.setMap(null);
                marker = null;
            }
        },
    }

    mapSimu = {
    	configs:configs,
        init: function() {
            init.intMap();
        }
    };

    _global = (function() {
        return this || (0, eval)('this');
    }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = mapSimu;
    } else if (typeof define === "function" && define.amd) {
        define(function() {
            return mapSimu;
        });
    } else {
        !('mapSimu' in _global) && (_global.mapSimu = mapSimu);
    }

})();

// var sock=null;
// var sockOps={
// open:function(message){
// var sockjs_url = 'http://localhost:8080/simu';
// sock = new SockJS(sockjs_url);
// sock.onopen = function() {
// console.log('open socket');
// sock.send(message);
// };
//          
// var markGo=false;
// sock.onmessage = function(e) {
// var list=e.data;
// if(list.length>0){
// list=JSON.parse(list);
// if(markGo==false){
// markerOps.moveMarkers(list);
// }
//                  
// markGo=true;
// }
//              
// };
// sock.onclose = function() {
// console.log('close socket');
// };
// },
// close:function(){
// sock.close();
// },
// }