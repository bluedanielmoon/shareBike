//mapLayer.init.heatmap();
//mapLayer.layer.heatmap.hide(); 隐藏
//mapLayer.layer.heatmap.show(); 显示
//mapLayer.layer.heatmap.setMap(map); 添加
//mapLayer.layer.heatmap.setMap(null); 删除
var mapLayer={
		config:{
				clusterDist:20,
				startTime:"latest",
				endTime:"latest",
				// share.config.setParam("clusterDist", 20);
				setParam:function(param,value){
					this[param]=value;
				},
				getParam:function(param){
					return  this[param];
				},
		},
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
		
		markers:{
			bus:null,
			init:{
				bus:function(){
					mapUtil.link.getData("/map/busStop", true, {}, function(data) {
						if(data){
							var markers=[];
							data.forEach(function(marker) {
								marker= new AMap.Marker({
									icon: mapLayer.icons.bus,
									position: [marker.location.lng,marker.location.lat],
									offset: new AMap.Pixel(-12,-12),
									zIndex: 2,
									map:map,
									visible:false,
									title: marker.name
							        });
								markers.push(marker);
							});
							mapLayer.markers.bus={
									marker:markers,
									show:false,
							}
							
						}				
						// if ends
					});
				},
				// bus init ends
			},
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
		getMarkerMod:function(modName){
			switch (modName) {
			case "busStop":
				return this.markers.bus;
			default:
				break;
			}
		},
		showMarkers:function(mod){
			if(mod&&mod!=null){
				if(mod.marker){
					mod.show=true;
					for(var i=0;i<mod.marker.length;i++){
						if (mod.marker[i]) {
							mod.marker[i].show();
				        }
					}
				}
							
			}	
		},
		hideMarkers:function(mod){
			if(mod&&mod!=null){
				if(mod.marker){
					mod.show=false;
					for(var i=0;i<mod.marker.length;i++){
						if (mod.marker[i]) {
							mod.marker[i].hide();
				        }
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
		task:{
			tasks:[],
			addTask:function(task){
					this.tasks.push(task);
			},
			removeTask:function(name){
				for(var i in this.tasks){
					if(name==this.tasks[i]){
						this.tasks.splice(i,1);
					}
				}
			},
			isDone:function(){
				return this.tasks.length==0;
			}
		},
	
		resetMods:function(){
		
			if(!mapLayer.task.isDone()){
				console.log("tasks not done");
				return ;
			}
			// 以记录任务数和定时读取任务完成情况来控制加载插件,最多等待十秒钟
			var timesRun = 0;
			var interval = setInterval(function(){
				timesRun += 1;
				if(mapLayer.task.isDone()||timesRun==100){
					share.clockLoader.hide();
					clearInterval(interval);
				}
			}, 100);
			 
			 for(var i in this.loaded){
				 var modName=this.loaded[i].name;
				 if(this.loaded[i].show==true){
					 this.task.addTask(modName);
					 this.loadAndShow(modName,{show:true,hideLoader:false,removeTask:true});
				 }else{
					 this.loadAndShow(modName,{show:false});
				 }
				 
			 }
			 this.hideAll();
			 share.clockLoader.show();
		},
		
		showMod:function(modName){
			var module=mapLayer.getMod(modName);
					
			if(module&&module!=null){
				if(module.hasData){
					module.show=true;
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
					share.clockLoader.show();
					this.loadAndShow(modName,{show:true,hideLoader:true});
				}
				
			}
		},
		loadAndShow:function(modName,options){
			mapLayer.data.loadMod(modName,function(modName,header){
				if(options.show){
					mapLayer.showMod(modName);
				}
				if(options.hideLoader){
					share.clockLoader.hide();
				}
				if(options.removeTask){
					mapLayer.task.removeTask(modName);
				}
				
			});
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
		getShowOnMap:function(){
			var list=[];
			for(var i in this.loaded){
				if(this.loaded[i].show){
					list.push(this.loaded[i].name);
				}
			}
			return list;
		},
		hideAll:function(){
			for(var i in this.loaded){
				this.hide(this.loaded[i].name);
			}
		},	
		init:{
			
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
					show:false,
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
			loadMod:function(moduleName,func){
				switch (moduleName) {
				case "":					
					break;
				case "bikePos":
					this.bikePos(moduleName,func);
					break;
				case "cluster":
					this.cluster(moduleName,func);
					break;
				case "heat":
					this.heatmap(moduleName,func);					
					break;
				default:
				}				
			},
			checkTime:function(time){
				
				if(!time||time==null||time==''){
					time="latest";
				}
				return time;
			},
			bikePos:function(modName,func){
				var start=mapLayer.config.getParam("startTime");
				var end=mapLayer.config.getParam("endTime");
				start=this.checkTime(start);
				end=this.checkTime(end);
				if(start=="latest"||end=="latest"||start==end){
					mapUtil.link.getData("/map/bikePos", true, {
						time : start
					}, function(data) {
						if(data&&data.header){
							// {"lnglat":[116.258446,37.686622],"name":"景县","style":2}
							var mass=mapLayer.getMod(modName);
							mass.mod.setData(data.bikes);
							mass.hasData=true;
							var header=data.header;
							func(modName);
							console.log(header);
							var panel={
									bikesCount:header.bikeCount,
									startTime:share.params.formTime(header.startTime),
									endTime:share.params.formTime(header.startTime),
									timeSpan:"0",
									regionStart:[header.bikeRec.startLng,header.bikeRec.startLat],
									regionEnd:[header.bikeRec.endLng,header.bikeRec.endLat],
									weather:header.weather.weather,
									tempareture:header.weather.tempature
							};
							share.infoPanel.setData(panel);
							share.params.setRange(header.startTime,header.startTime);	
						}				
					});
				}else{
					mapUtil.link.getData("/map/rangebikePos", true, {
						start : start,
						end:end,
					}, function(data) {
						if(data){
							console.log(data);
							var list=[];
							for(var i in data){
								if(data[i]&&data[i].bikes){
									list=list.concat(data[i].bikes);	
								}
							}
							// {"lnglat":[116.258446,37.686622],"name":"景县","style":2}
							 var mass=mapLayer.getMod(modName);
							 mass.mod.setData(list);
							 mass.hasData=true;
							// var header=data.header;
							 func(modName);
							 console.log(data.header);
							 var header=data.header;
							 var panel={
										bikesCount:header.bikesCount,
										startTime:share.params.formTime(start),
										endTime:share.params.formTime(end),
										timeSpan:share.params.getSpan(start,end),
										regionStart:[header.bikeRec.startLng,header.bikeRec.startLat],
										regionEnd:[header.bikeRec.endLng,header.bikeRec.endLat],
										weather:"无",
										tempareture:header.temperature
								};
								share.infoPanel.setData(panel);
								share.params.setRange(start,end);
						}				
						
					});
				}
				
							
			},
			
			cluster:function(modName,func){
				var start=mapLayer.config.getParam("startTime");
				var end=mapLayer.config.getParam("endTime");
				start=this.checkTime(start);
				end=this.checkTime(end);
				console.log("加载聚类图");
				console.log(start);
				console.log(end);
				console.log("聚类距离"+mapLayer.config.clusterDist);
				if(start=="latest"||end=="latest"||start==end){
					mapUtil.link.getData("/map/cluster", true, {
						time : start,
						distance : mapLayer.config.clusterDist,
					}, function(data) {
						if(data){
							var cluster=mapLayer.getMod(modName);
							
							cluster.mod.setData(data);
							cluster.hasData=true;
							func(modName);
							
						}				
					});
				}else{
					mapUtil.link.getData("/map/rangecluster", true, {
						start : start,
						end:end,
						distance : mapLayer.config.clusterDist,
					}, function(data) {
						if(data){
							console.log("加载区间聚类图");
							console.log(data);
							var list=[];
							for(var i in data){
								list=list.concat(data[i]);
							}
							var cluster=mapLayer.getMod(modName);
							cluster.mod.setData(list);
							cluster.hasData=true;
							func(modName);
						}				
						
					});
				}
			},
			heatmap:function(modName,func){
				var start=mapLayer.config.getParam("startTime");
				var end=mapLayer.config.getParam("endTime");
				start=this.checkTime(start);
				end=this.checkTime(end);
				console.log("加载热力图");
				if(start=="latest"||end=="latest"||start==end){
					mapUtil.link.getData("/map/heat", true, {
						time : start
					}, function(data) {
						if(data){
							var heater=mapLayer.getMod(modName);
							heater.hasData=true;
							heater.mod.setDataSet({
								data : data.bikes,
								max : 100
							});		
							func(modName);
						}				
					});
				}else{
					mapUtil.link.getData("/map/rangeheat", true, {
						start : start,
						end:end,
					}, function(data) {
						if(data){
							var list=[];
							for(var i in data){
								list=list.concat(data[i].bikes);
							}
							 var heater=mapLayer.getMod(modName);
							 heater.hasData=true;
							 heater.mod.setDataSet({
								 data : list,
								 max : 100
							 });
							func(modName);
						}				
						
					});
				}
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
