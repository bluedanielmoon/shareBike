;
(function(global, undefined) {


	var _global;
	var mapLayer;

	var config = {
		clusterDist: 100,
		maxPack:50,
		minPack:10,
		startTime: "latest",
		endTime: "latest",
		inactHours: 24,
		// share.config.setParam("clusterDist", 20);
		setParam: function(param, value) {
			this[param] = value;
		},
		getParam: function(param) {
			return this[param];
		},
	};

	// name,mod
	var loaded = [];
	var icons = {
		point: "image/point.png",
		bus: "image/bus.png",
	};

	var styles = {
		massMark: [{
			url: "image/little.png",
			anchor: new AMap.Pixel(4, 4),
			size: new AMap.Size(7, 7)

		},{
			url: "image/littleRed.png",
			anchor: new AMap.Pixel(4, 4),
			size: new AMap.Size(7, 7)

		}],
		clusterMark: [{
			url: "image/big.png",
			anchor: new AMap.Pixel(6, 6),
			size: new AMap.Size(11, 11)
		}],
	};

	var markers = {
		bus: null,
		init: {
			bus: function() {
				mapUtil.link.getData("/map/busStop", true, {}, function(data) {
					if (data) {
						console.log("bus");
						var list = [];
						data.forEach(function(marker) {
							marker = new AMap.Marker({
								icon: icons.bus,
								position: [marker.location.lng, marker.location.lat],
								offset: new AMap.Pixel(-12, -12),
								zIndex: 2,
								map: map,
								visible: false,
								title: marker.name
							});
							list.push(marker);
						});
						markers.bus = {
							marker: list,
							show: false,
						}

					}
					// if ends
				});
			},
			// bus init ends
		},
	};

	var init = {

		massMark: function(moduleName) {

			var mass = new AMap.MassMarks([], {
				opacity: 0.8,
				zIndex: 1,
				cursor: 'pointer',
				style: styles.massMark
			});

			mass.marker = new AMap.Marker({
				content: ' ',
				map: map
			});

			mass.on('mouseover', function(e) {
				mass.marker.setPosition(e.data.lnglat);
				mass.marker.setLabel({
					content: e.data.name
				})
			});

			mass.setMap(map);

			loaded.push({
				name: moduleName,
				mod: mass,
				hasData: false,
				show: false,
			});

			console.log("初始化海量点图");
		},
		cluster: function(moduleName) {
			var mass = new AMap.MassMarks([], {
				opacity: 0.8,
				zIndex: 1,
				cursor: 'pointer',
				style: styles.clusterMark
			});

			mass.marker = new AMap.Marker({
				content: ' ',
				map: map
			});

			mass.on('mouseover', function(e) {
				mass.marker.setPosition(e.data.lnglat);
				mass.marker.setLabel({
					content: e.data.name
				})
			});

			mass.setMap(map);
			loaded.push({
				name: moduleName,
				mod: mass,
				hasData: false,
				show: false,
			});
			console.log("初始化聚类图");
		},
		// 热力图插件同步方式需要window.init方式加载，太别扭
		// 采用把函数传进来的方式，先异步加载插件，然后再去取数据
		heatMap: function(moduleName, getDataFunc) {
			map.plugin(["AMap.Heatmap"], function() {
				var heatMod = new AMap.Heatmap(map, {
					radius: 25, // 给定半径
					opacity: [0, 1],
					gradient: {
						0.5: 'blue',
						0.65: 'rgb(117,211,248)',
						0.7: 'rgb(0, 255,0)',
						0.9: '#ffea00',
						1.0: 'red'
					}
				});
				loaded.push({
					name: moduleName,
					mod: heatMod,
					hasData: false,
					show: false,
				});
				console.log("初始化热力图");
			});

		},
	};

	var addMapFeature = function(modName) {
		var feats = map.getFeatures();
		feats.push(modName);
		map.setFeatures(feats);
	};
	var removeMapFeature = function(modName) {
		var feats = map.getFeatures();

		for (var i = 0; i < feats.length; i++) {
			if (feats[i] == modName) {
				feats.splice(i, 1);
				break;
			}
		}
		map.setFeatures(feats);
	};

	var cleanMarkers = function(markers) {
		if (markers && markers != null) {
			for (var i = 0; i < markers.length; i++) {
				if (markers[i]) {
					markers[i].setMap(null);
					markers[i] = null;
				}
			}
		}
	};
	var getMarkerMod = function(modName) {
		switch (modName) {
			case "busStop":
				return markers.bus;
			default:
				break;
		}
	};
	var showMarkers = function(modName) {
		var mod=getMarkerMod(modName);
		if (mod && mod != null) {
			if (mod.marker) {
				mod.show = true;
				for (var i = 0; i < mod.marker.length; i++) {
					if (mod.marker[i]) {
						mod.marker[i].show();
					}
				}
			}

		}
	};
	var hideMarkers = function(modName) {
		var mod=getMarkerMod(modName);
		if (mod && mod != null) {
			if (mod.marker) {
				mod.show = false;
				for (var i = 0; i < mod.marker.length; i++) {
					if (mod.marker[i]) {
						mod.marker[i].hide();
					}
				}
			}

		}
	};


	var task = {
		tasks: [],
		addTask: function(task) {
			this.tasks.push(task);
		},
		removeTask: function(name) {
			for (var i in this.tasks) {
				if (name == this.tasks[i]) {
					this.tasks.splice(i, 1);
				}
			}
		},
		isDone: function() {
			return this.tasks.length == 0;
		}
	};

	var resetMods = function() {

		if (!task.isDone()) {
			console.log("tasks not done");
			return;
		}
		// 以记录任务数和定时读取任务完成情况来控制加载插件,最多等待十秒钟
		var timesRun = 0;
		var interval = setInterval(function() {
			timesRun += 1;
			if (task.isDone() || timesRun == 100) {
				share.clockLoader.hide();
				clearInterval(interval);
			}
		}, 100);

		for (var i in loaded) {
			var modName = loaded[i].name;
			if (loaded[i].show == true) {
				task.addTask(modName);
				loadAndShow(modName, {
					show: true,
					hideLoader: false,
					removeTask: true
				});
			} else {
				loadAndShow(modName, {
					show: false
				});
			}

		}
		hideAll();
		share.clockLoader.show();
	};

	var showMod = function(modName) {
		var module = getMod(modName);

		if (module && module != null) {
			if (module.hasData) {
				module.show = true;
				if (mapUtil.utils.isArray(module.mod)) {
					for (var i = 0; i < module.mod.length; i++) {
						if (module.mod[i]) {
							module.mod[i].show();
						}
					}
				} else {
					module.mod.show();
				}
			} else {
				share.clockLoader.show();
				loadAndShow(modName, {
					show: true,
					hideLoader: true
				});
			}

		}
	};
	var loadAndShow = function(modName, options) {
		data.loadMod(modName, function(modName, header) {
			if (options.show) {
				showMod(modName);
			}
			if (options.hideLoader) {
				share.clockLoader.hide();
			}
			if (options.removeTask) {
				task.removeTask(modName);
			}

		});
	};
	var hide = function(modName) {
		var module = getMod(modName);
		if (module && module != null) {
			module.show = false;
			if (mapUtil.utils.isArray(module.mod)) {
				for (var i = 0; i < module.mod.length; i++) {
					if (module.mod[i]) {
						module.mod[i].hide();
					}
				}
			} else {
				module.mod.hide();
			}
		}
	};
	var hideAll = function() {
		for (var i in loaded) {
			hide(loaded[i].name);
		}
	};

	var data = {
			loadMod: function(moduleName, func) {
				switch (moduleName) {
					case "":
						break;
					case "bikePos":
						this.bikePos(moduleName, func);
						break;
					case "cluster":
						this.cluster(moduleName, func);
						break;
					case "heat":
						this.heatmap(moduleName, func);
						break;
					default:
				}
			},
			checkTime: function(time) {

				if (!time || time == null || time == '') {
					time = "latest";
				}
				return time;
			},
			bikePos: function(modName, func) {
				var start = config.getParam("startTime");
				var end = config.getParam("endTime");
				
				var inAct = config.getParam("inactHours");
				start = this.checkTime(start);
				end = this.checkTime(end);
				if (start == "latest" || end == "latest" || start == end) {
					
					mapUtil.link.getData("/map/bikePosInact", true, {
						time: start,
						inAct:inAct
					}, function(data) {
						if (data && data.header) {
							// {"lnglat":[116.258446,37.686622],"name":"景县","style":2}
							var mass = getMod(modName);
							mass.mod.setData(data.bikes);
							mass.hasData = true;
							var header = data.header;
							func(modName);
							var panel = {
								bikesCount: header.bikeCount,
								startTime: share.params.formTime(header.startTime),
								endTime: share.params.formTime(header.startTime),
								timeSpan: "0",
								regionStart: [header.bikeRec.startLng, header.bikeRec.startLat],
								regionEnd: [header.bikeRec.endLng, header.bikeRec.endLat],
								weather: header.weather.weather,
								tempareture: header.weather.tempature
							};
							share.infoPanel.setData(panel);
							share.params.setRange(header.startTime, header.startTime);
						}
						
					});
				} else {
					mapUtil.link.getData("/map/rangebikePos", true, {
						start: start,
						end: end,
					}, function(data) {
						if (data) {
							console.log(data);
							var list = [];
							for (var i in data) {
								if (data[i] && data[i].bikes) {
									list = list.concat(data[i].bikes);
								}
							}
							// {"lnglat":[116.258446,37.686622],"name":"景县","style":2}
							var mass = getMod(modName);
							mass.mod.setData(list);
							mass.hasData = true;
							// var header=data.header;
							func(modName);
							console.log(data.header);
							var header = data.header;
							var panel = {
								bikesCount: header.bikesCount,
								startTime: share.params.formTime(start),
								endTime: share.params.formTime(end),
								timeSpan: share.params.getSpan(start, end),
								regionStart: [header.bikeRec.startLng, header.bikeRec.startLat],
								regionEnd: [header.bikeRec.endLng, header.bikeRec.endLat],
								weather: "无",
								tempareture: header.temperature
							};
							share.infoPanel.setData(panel);
							share.params.setRange(start, end);
						}

					});
				}


			},

			cluster: function(modName, func) {
				var start = config.getParam("startTime");
				var end = config.getParam("endTime");
				start = this.checkTime(start);
				end = this.checkTime(end);
				console.log("加载聚类图");
				console.log(start);
				console.log(end);
				console.log("聚类距离" + config.clusterDist);
				if (start == "latest" || end == "latest" || start == end) {
					mapUtil.link.getData("/map/cluster", true, {
						time: start,
						distance: config.clusterDist,
						maxPack:config.maxPack,
						minPack:config.minPack,
					}, function(data) {
						if (data) {
							var cluster = getMod(modName);

							cluster.mod.setData(data);
							cluster.hasData = true;
							func(modName);

						}
					});
				} else {
					mapUtil.link.getData("/map/rangecluster", true, {
						start: start,
						end: end,
						distance: config.clusterDist,
						maxPack:config.maxPack,
						minPack:config.minPack,
					}, function(data) {
						if (data) {
							console.log("加载区间聚类图");
							console.log(data);
							var list = [];
							for (var i in data) {
								list = list.concat(data[i]);
							}
							var cluster = getMod(modName);
							cluster.mod.setData(list);
							cluster.hasData = true;
							func(modName);
						}

					});
				}
			},
			heatmap: function(modName, func) {
				var start = config.getParam("startTime");
				var end = config.getParam("endTime");
				start = this.checkTime(start);
				end = this.checkTime(end);
				console.log("加载热力图");
				if (start == "latest" || end == "latest" || start == end) {
					mapUtil.link.getData("/map/heat", true, {
						time: start
					}, function(data) {
						if (data) {
							var heater = getMod(modName);
							heater.hasData = true;
							heater.mod.setDataSet({
								data: data.bikes,
								max: 100
							});
							func(modName);
						}
					});
				} else {
					mapUtil.link.getData("/map/rangeheat", true, {
						start: start,
						end: end,
					}, function(data) {
						if (data) {
							var list = [];
							for (var i in data) {
								list = list.concat(data[i].bikes);
							}
							var heater = getMod(modName);
							heater.hasData = true;
							heater.mod.setDataSet({
								data: list,
								max: 100
							});
							func(modName);
						}

					});
				}
			},
		};
		var getMod = function(moduleName) {
			var loads = loaded;

			for (var i = 0; i < loads.length; i++) {
				if (loads[i].name == moduleName) {
					return loads[i];
				}
			}
			return null;
		};



	mapLayer = {
		config: config,
		icons: icons,

		initBus: function() {
			markers.init.bus();
		},
		initMass: function(modName) {
			init.massMark(modName);
		},
		initCluster: function(modName) {
			init.cluster(modName);
		},
		initHeatMap: function(modName) {
			init.heatMap(modName);
		},
		showMod: function(modName, func) {
			data.showMod(modName, func);
		},
		resetMods: function() {
			resetMods();
		},
		showMarkers: function(modName) {
			showMarkers(modName);
		},
		hideMarkers: function(modName) {
			hideMarkers(modName);
		},
		showMod: function(modName) {
			showMod(modName);
		},
		hide: function(modName) {
			hide(modName);
		},
		addMapFeature: function(modName) {
			addMapFeature(modName);
		},
		removeMapFeature: function(modName) {
			removeMapFeature(modName);
		},
	};

	_global = (function() {
		return this || (0, eval)('this');
	}());
	if (typeof module !== "undefined" && module.exports) {
		module.exports = mapLayer;
	} else if (typeof define === "function" && define.amd) {
		define(function() {
			return mapLayer;
		});
	} else {
		!('mapLayer' in _global) && (_global.mapLayer = mapLayer);
	}

})();