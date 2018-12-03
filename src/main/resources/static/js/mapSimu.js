;
(function(global, undefined) {

	
	var simuMap;
	var simuSet={
			date:null,
	}
	
	var startBtn=$("#btn-simu-start");
	var pauseBtn=$("#btn-simu-pause");
	var stopBtn=$("#btn-simu-stop");
	
	//0-notStart,1-start,2-pause,3-stop
	var simuState=0;
	var simuID=null;
	
	var btnOps={
			
			toggleBtn:function(btn){
				if(!btn){
					return ;
				}
				if(btn.hasClass("disabled")){
					btn.removeClass("disabled");
				}else{
					btn.addClass("disabled");	
				}
			},
			togglePause:function(flag){
				if(flag){
					pauseBtn.find(".glyphicon").removeClass("glyphicon-repeat")
					.addClass("glyphicon-pause");
					pauseBtn.find(".simu-pause").text("暂停");					
				}else{
					pauseBtn.find(".glyphicon").removeClass("glyphicon-pause")
					.addClass("glyphicon-repeat");
					pauseBtn.find(".simu-pause").text("恢复");
				}
			},
			startOp:function(){
				
				if(simuState==0){
					simuID=null;
					btnOps.toggleBtn(startBtn);
					simuState=1;
					mapUtil.link.getData("/simu/start",true,{},function(data){				
						if(data){
							simuID=data.uuid;
							
							markerOps.initJobs(data.list);
						}						
					});
				}
				
			},
			pauseSim:function(){
				if(simuState==1){
					
					btnOps.togglePause(false);
					simuState=2;
					markerOps.togglePause(true);
					
				}else if(simuState==2){
					btnOps.togglePause(true);
					simuState=1;
					markerOps.togglePause(false);
				}
			},
			endSim:function(){
				
				if(simuState==1||simuState==2){
					
					markerOps.stopMove();
					mapUtil.link.getData("/simu/stop",false,{
						simuID:simuID
					},function(data){				
						if(data){
							simuState=0;
							btnOps.togglePause(true);
							btnOps.toggleBtn(startBtn);
							
						}						
					});	
					
				}
			},
			
	}
	
	var init={
			inited:false,
			intMap:function(){
				if(!this.inited){
					 simuMap= new AMap.Map('simu-map', {
					        viewMode: '2D', // 使用2D视图
					        zoom: 15, // 级别
					        features: ['bg','road', 'building'],	
					        //features: [],	
					        center: [108.916913, 34.265569], // 中心点坐标
					    });
					
					 this.initMapMark();
					 var timeSet = $("#simu-init-date").val(function() {
					        return moment("2018-11-01", "YYYY_MM_DD").format("YYYY-MM-DD");
					    }).datetimepicker({
					        format: "yyyy-mm-dd",
					        autoclose: true,
					        minView: 2,
					        todayBtn: true,
					        initialDate: "2018-11-01"
					    });

					    timeSet.on('changeDate', function(ev) {

					        var input = ev.date;
					        var formed = moment(input, "dddd MMMM DD YYYY").format("YYYY_MM_DD");
					        simuSet.date=formed;
					    });
					    
					    startBtn.click(btnOps.startOp);					    
					    pauseBtn.click(btnOps.pauseSim);    
					    stopBtn.click(btnOps.endSim);
					    			    
					    console.log("初始化模拟");
					 this.inited=true;
				}
				 
			},
			initMapMark:function(){
				mapUtil.link.getData("/site/all",true,{},function(data){				
					if(data){
						markerOps.addMarkers(data,markerOps.siteIcon);
					}						
				});
				
				mapUtil.link.getData("/disp/all",true,{},function(data){				
					if(data){
						data[0].lng=108.925175;
						data[0].lat=34.260033;
						
						data[1].lng=108.922158;
						data[1].lat=34.286243;
						
						data[2].lng=108.923188;
						data[2].lat=34.259361;
						
						data[3].lng=108.910314;
						data[3].lat=34.241483;
						
						data[4].lng=108.995629;
						data[4].lat=34.268725;
						
//						data[5].lng=108.972283;
//						data[5].lat=34.250848;
//						
//						data[6].lng=108.947393;
//						data[6].lat=34.269718;		
						
						for(var i in data){
							if(data[i].type==1){			
								markerOps.addMarker(data[i],markerOps.truckIcon);
							}else if(data[i].type==2){
								markerOps.addMarker(data[i],markerOps.tricycleIcon);
							}else if(data[i].type==3){
								markerOps.addMarker(data[i],markerOps.manIcon);
							}
						}
						markerOps.dispats=data;
					}						
				});
			},
	}
	
	
	
	var simuOps={
			start:function(){},
			end:function(){},
			stop:function(){},
	}
	
	var markerOps={
			dispats:[],
			initJobs:function(jobList){
				var cars=this.dispats;
				for(var i in cars){
					var carID=cars[i].id;
					var carMark=cars[i];
					var carData=jobList.find(function(current, index, arr){
						if(current.dispatcher.id==carID){
							return current;
						}
					},carID);
					console.log(carData);
					var carPath=carData.path.paths;
					var arrayPath=[];
					for(var i in carPath){
						arrayPath.push([carPath[i].lng,carPath[i].lat]);
					}
					var polyline = new AMap.Polyline({
				        map: simuMap,
				        path: arrayPath,
				        showDir:true,
				        strokeColor: "#28F",  //线颜色
				        //strokeOpacity: 0.8,     //线透明度
				       // strokeWeight: 6,      //线宽
				        strokeStyle: "solid",  //线样式
					});

					carMark.marker.moveAlong(arrayPath, 1000);
					
					carMark.marker.on('movealong', function(){
						console.log("11end!!!!!!!!");
					});
				}
			},
			//只能选择一个路径展示
			toggleRoute:function(){
				
	
			    var passedPolyline = new AMap.Polyline({
			        map: simuMap,
			        strokeColor: "#AF5",  //线颜色
			       // strokeOpacity: 1,     //线透明度
			        //strokeWeight: 20,      //线宽
			        strokeStyle: "dashed",  //线样式
			    });
			    carMark.marker.on('moving', function (e) {
			    	passedPolyline.setPath(e.passedPath);
				});
			},
			togglePause:function(flag){
				var cars=this.dispats;
				for(var i in cars){
					if(flag){
						cars[i].marker.pauseMove();
					}else{
						cars[i].marker.resumeMove();
					}
					
				}
			},
			stopMove:function(){
				var cars=this.dispats;
				for(var i in cars){
					cars[i].marker.stopMove();	
				}
			},
			siteIcon:{
		        image: "image/simu/house.ico",
		        imageSize: new AMap.Size(24, 24),
		        offset: new AMap.Pixel(-12, -24)
		    },
		    truckIcon:{
		        image: "image/simu/truck.ico",
		        imageSize: new AMap.Size(36, 36),
		        offset: new AMap.Pixel(-12, -36)
		    },
		    tricycleIcon:{
		        image: "image/simu/tricycle.ico",
		        imageSize: new AMap.Size(36, 36),
		        offset: new AMap.Pixel(-12, -36)
		    },
		    manIcon:{
		        image: "image/simu/man.ico",
		        imageSize: new AMap.Size(24, 24),
		        offset: new AMap.Pixel(-12, -24)
		    },
			addMarker:function(item,icon){
				item.marker = new AMap.Marker({
		            icon: new AMap.Icon(icon),          
		            position: [item.lng,item.lat],
		            map: simuMap,
		        });		
			},
		
			addMarkers:function(list,icon){	
		
				for(var i in list){
					list[i].marker = new AMap.Marker({
			            icon: new AMap.Icon(icon),          
			            position: [list[i].lng,list[i].lat],
			            map: simuMap,
			        });			
				}
			},	
			clearMarer:function(marker){
				 if (marker) {
			            marker.setMap(null);
			            marker = null;
			        }
			},
	}

	mapSimu = {
		init:function(){
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

//var sock=null;
//var sockOps={
//		open:function(message){
//			var sockjs_url = 'http://localhost:8080/simu';
//			sock = new SockJS(sockjs_url);
//			sock.onopen = function() {					
//			    console.log('open socket');
//			    sock.send(message);
//			};
//			
//			var markGo=false;
//			sock.onmessage = function(e) {
//				var list=e.data;
//				if(list.length>0){
//					list=JSON.parse(list);
//					if(markGo==false){
//						markerOps.moveMarkers(list);
//					}
//					
//					markGo=true;
//				}
//				
//			 };
//			 sock.onclose = function() {
//			     console.log('close socket');
//			 };
//		},
//		close:function(){
//			sock.close();
//		},
//}