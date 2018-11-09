//mapLayer.init.heatmap();
//mapLayer.layer.heatmap.hide(); 隐藏
//mapLayer.layer.heatmap.show(); 显示
//mapLayer.layer.heatmap.setMap(map); 添加
//mapLayer.layer.heatmap.setMap(null); 删除
var mapLayer={

}

// load map and its plugins ----BEGIN
var map = new AMap.Map('container', {
	zoom : 15,// 级别
	// 'bg'（地图背景）、'point'（POI点）、'road'（道路）、'building'（建筑物）
	features:['bg','point','road','building'],
	center : [ 108.916913, 34.265569 ],// 中心点坐标
	viewMode : '2D',// 使用2D视图
//	isHotspot:true,
});

mapLayer={
		// name,mod
		loaded : [],
		icons:{
			point:"image/point.png",
			bus:"image/bus.png",
		},
		styles:{
			massMark:[{
		        url: "image/little.png",
		        anchor: new AMap.Pixel(4, 4),
		        size: new AMap.Size(7, 7)
		        
		    }],
		    clusterMark:[{
		        url: "image/big.png",
		        anchor: new AMap.Pixel(6, 6),
		        size: new AMap.Size(11, 11)
		    }],
		},
		
		cleanMarkers:function(markers){
			if(markers&&markers!=null){
				for(var i=0;i<markers.length;i++){
					if (markers[i]) {
						markers[i].setMap(null);
						markers[i] = null;
			        }
				}			
			}	
		},
		showMarkers:function(markers){
			if(markers&&markers!=null){
				for(var i=0;i<markers.length;i++){
					if (markers[i]) {
						markers[i].show();
			        }
				}			
			}	
		},
		addMapFeature:function(modName){
			var feats=map.getFeatures();
			feats.push(modName);
			map.setFeatures(feats);
		},
		removeMapFeature:function(modName){
			var feats=map.getFeatures();

			for(var i=0;i<feats.length;i++){
				if(feats[i]==modName){
					feats.splice(i,1);
					break;
				}
			}
			map.setFeatures(feats);
		},
		
		show:function(modName){
			var module=mapLayer.getMod(modName);
					
			if(module&&module!=null){
				module.show=true;
				if(module.hasData){
					
					if(mapUtil.utils.isArray(module.mod)){
						for(var i=0;i<module.mod.length;i++){
							if (module.mod[i]) {
								module.mod[i].show();
					        }
						}	
					}else{
						module.mod.show();
					}
				}else{
					mapLayer.data.loadMod(modName);
				}
				
			}
		},
		
		hide:function(modName){
			var module=mapLayer.getMod(modName);
			if(module&&module!=null){
				module.show=false;
				if(mapUtil.utils.isArray(module.mod)){
					for(var i=0;i<module.mod.length;i++){
						if (module.mod[i]) {
							module.mod[i].hide();
				        }
					}	
				}else{
					module.mod.hide();
				}		
			}
		},
		reloadData:function(time){
			for(var i in this.loaded){
				this.data.loadMod(this.loaded[i].name,time);
			}
		},
		showAll:function(){
			for(var i in this.loaded){
				this.show(this.loaded[i].name);
			}
		},
		hideAll:function(){
			for(var i in this.loaded){
				this.hide(this.loaded[i].name);
			}
		},	
		init:{
			busMarkers:function(moduleName){
				mapUtil.link.getData("/map/busStop", true, {
				}, function(data) {
					if(data){
						var markers=[];
						data.forEach(function(marker) {
							marker= new AMap.Marker({
								icon: mapLayer.icons.bus,
								// [116.406315,39.908775]
								position: [marker.location.lng,marker.location.lat],
								offset: new AMap.Pixel(-12,-12),
								zIndex: 2,
								map:map,
								visible:false,
								title: marker.name
						        });
							markers.push(marker);
						});
						
						mapLayer.loaded.push({
							name:moduleName,
							mod:markers,
							hasData:true,
							show:false,
						});
						
					}				

				});
				
				
			},
			massMark:function(moduleName){

				var mass = new AMap.MassMarks([], {
			        opacity: 0.8,
			        zIndex: 1,
			        cursor: 'pointer',
			        style: mapLayer.styles.massMark
			    });

				mass.marker = new AMap.Marker({content: ' ', map: map});

				mass.on('mouseover', function (e) {
					mass.marker.setPosition(e.data.lnglat);
					mass.marker.setLabel({content: e.data.name})
			    });

				mass.setMap(map);
				
				mapLayer.loaded.push({
					name:moduleName,
					mod:mass,
					hasData:false,
					show:true,
				});
				
				console.log("初始化海量点图");
			},
			cluster:function(moduleName){
				var mass = new AMap.MassMarks([], {
			        opacity: 0.8,
			        zIndex: 1,
			        cursor: 'pointer',
			        style: mapLayer.styles.clusterMark
			    });

				mass.marker = new AMap.Marker({content: ' ', map: map});

				mass.on('mouseover', function (e) {
					mass.marker.setPosition(e.data.lnglat);
					mass.marker.setLabel({content: e.data.name})
			    });

				mass.setMap(map);
				mapLayer.loaded.push({
					name:moduleName,
					mod:mass,
					hasData:false,
					show:false,
				});
				console.log("初始化聚类图");
			},
			// 热力图插件同步方式需要window.init方式加载，太别扭
			// 采用把函数传进来的方式，先异步加载插件，然后再去取数据
			heatmap:function(moduleName,getDataFunc){
				map.plugin([ "AMap.Heatmap" ],function(){
					var heatMod= new AMap.Heatmap(map, {
						radius : 25, // 给定半径
						opacity : [ 0, 1 ], 
						gradient:{ 
							0.5: 'blue', 
							0.65: 'rgb(117,211,248)', 
							0.7: 'rgb(0, 255,0)', 
							0.9: '#ffea00', 
							1.0: 'red' 
						}
					});
					mapLayer.loaded.push({
						name:moduleName,
						mod:heatMod,
						hasData:false,
						show:false,
					});
					console.log("初始化热力图");
				});
				
			},
		},
		data:{
			setShow:function(module){
				if(module.show){
					module.mod.show();
				}else{
					module.mod.hide();
				}
			},
			loadMod:function(moduleName,time){
				switch (moduleName) {
				case "":					
					break;
				case "bikePos":
					this.bikePos(moduleName,time);
					break;
				case "cluster":
					this.cluster(moduleName,time);
					break;
				case "heat":
					this.heatmap(moduleName,time);					
					break;
				default:
				}				
			},
			bikePos:function(modName,time){
				if(!time){
					time="latest";
				}
				share.clockLoader.show();
				mapUtil.link.getData("/map/bikePos", true, {
					time : time
				}, function(data) {
					if(data&&data.header){
						// {"lnglat":[116.258446,37.686622],"name":"景县","style":2}
						var mass=mapLayer.getMod(modName);

						var time=mapUtil.utils.parser(data.header.startTime);

						share.timeSetter.set(time);
						mass.mod.setData(data.bikes);
						mass.hasData=true;
						
						mapLayer.data.setShow(mass);
												
						var header=data.header;
						var panel={
								bikesCount:header.bikeCount,
								regionStart:[header.bikeRec.startLng,header.bikeRec.startLat],
								regionEnd:[header.bikeRec.endLng,header.bikeRec.endLat],
								weather:header.weather.weather,
								tempareture:header.weather.tempature
						};
						share.infoPanel.setData(panel);
					}				
					share.clockLoader.hide();
				});
							
			},
			
			cluster:function(modName,time){
				if(!time){
					time="latest";
				}
				share.clockLoader.show();
				mapUtil.link.getData("/map/cluster", true, {
					time : time,
					distance : 20
				}, function(data) {
					if(data){
						console.log("加载聚类图");
						var cluster=mapLayer.getMod(modName);
						
						var time=mapUtil.utils.parser(data.header.startTime);
						share.timeSetter.set(time);
						cluster.mod.setData(data.clusters);
						cluster.hasData=true;
						mapLayer.data.setShow(cluster);
						
					}
					share.clockLoader.hide();
				});
			},
			heatmap:function(modName,time){
				if(!time){
					time="latest";
				}
				share.clockLoader.show();
				mapUtil.link.getData("/map/heat", true, {
					time : time					
				}, function(data) {
					if(data){
						console.log("加载热力图");
						var header = data.header;
						var panel = {
							header : "数据显示",
							body : {
								"单车数量" : header.bikeCount,
								"区域起始点" : new Array(header.bikeRec.startLng,
										header.bikeRec.startLat),
								"区域终止点" : new Array(header.bikeRec.endLng,
										header.bikeRec.endLat),
								"采集时间" : mapUtil.utils.parser(header.time),
								"天气情况" : header.weather.weather,
								"温度" : header.weather.tempature
							}
						};
						// 设置面板信息
						share.infoPanel.setData(panel);
						// 设置热力数据
						
						var heater=mapLayer.getMod(modName);
						heater.hasData=true;
						heater.mod.setDataSet({
							data : data.bikes,
							max : 100
						});				
						mapLayer.data.setShow(heater);
					}
					
					share.clockLoader.hide();
				});
			},
		},
		getMod : function(moduleName) {
			var loads = this.loaded;

			for (var i = 0; i < loads.length; i++) {
				if (loads[i].name == moduleName) {
					return loads[i];
				}
			}
			return null;
		},
		
		util:function(){
			AMap.plugin([ "AMap.LineSearch" ], function() {
				// 实例化公交站点查询类
				var line = new AMap.LineSearch({
					pageIndex : 1, // 页码，默认值为1
					pageSize : 20, // 单页显示结果条数，默认值为20，最大值为50
					city : '西安市', // 限定查询城市，可以是城市名（中文/中文全拼）、城市编码，默认值为『全国』
					extensions : 'all', // 一定要加这个选项，否则不返回站点信息
				});
				mapUtil.link.getData("/map/lines", true, {}, function(data) {
					for ( let i=0;i<data.length;i++) {
						console.log("正在查询："+data[i].name);
						line.search(data[i].name, function(status, result) {
							// 打印状态信息status和结果信息result
							// status：complete 表示查询成功，no_data 为查询无结果，error
							// 代表查询错误。
							console.log(status, result);
							if(status=="complete"){
								var infos=result.lineInfo;
								for ( var j=0;j<infos.length;j++) {

									mapUtil.link.postJson("/map/busLine", false, {						
										id : infos[j].id,
										company : infos[j].company,
										name : infos[j].name,
										distance : infos[j].distance,						
										start_stop : infos[j].start_stop,
										end_stop : infos[j].end_stop,
										stime : infos[j].stime,
										etime : infos[j].etime,
										bounds : infos[j].bounds,					
										via_stops : infos[j].via_stops,
										path : infos[j].path,

									}, function(data) {
										
									});
								}
							}else{
								console.log("fail"+data[i].name);
							}
						});		
					}
				});

				// 执行关键字查询

			});
		},
}
