;
(function(global, undefined) {
    var userTable = $('#table-user');


    var siteTable=$('#table-site');

    var dispTable=$('#table-disp');

    var forbidTable=$('#table-forbid');
    
    var userOps = {
        init: function() {
            userTable.bootstrapTable({
                toolbar: '#toolbar-user', // 工具按钮用哪个容器
                toolbarAlign: 'left',
                striped: true, // 是否显示行间隔色
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, // 是否显示分页（*）
                sortable: false, // 是否启用排序
                sortOrder: "asc", // 排序方式
                sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                // queryParams----返回的参数里必须有 pageSize, pageNumber, searchText,
                // sortName, sortOrder.
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                search: true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true, // 是否显示所有的列
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
                cardView: false, // 是否显示详细视图
                detailView: false, // 是否显示父子表
                icons: {
                    refresh: 'glyphicon-refresh icon-refresh',
                    toggle: 'glyphicon-list-alt icon-list-alt',
                    columns: 'glyphicon-th icon-th'
                },
                columns: [{
                    checkbox: true
                }, {
                    field: 'name',
                    title: '用户名'
                }, {
                    field: 'createTime',
                    title: '创建时间'
                }, {
                    field: 'updateTime',
                    title: '最后登陆时间'
                }],
            });
        },
        loadData: function() {
            mapUtil.link.getData("/user/all", true, {}, function(data) {

                var list = [];
                if (data) {
                    data.forEach(function(element, index) {
                        list.push({
                            "name": element.userName,
                            "createTime": element.createTime,
                            "updateTime": element.updateTime
                        });

                    });
                }
                userTable.bootstrapTable('load', list);

            });
        },

        bindControl: function() {
            var addUser = $("#modal-add-user");
            addUser.on('show.bs.modal', function() {
                $("#user-form")[0].reset();
            });
            addUser.find("#modal-user-submit").click(function() {

                var username = addUser.find("#modal-userName").val();
                var password = addUser.find("#modal-userPass").val();
                
                console.log(username);
                console.log(password);
                mapUtil.link.getData("/user/add", true, {
                    userName: username,
                    password: password
                }, function(data) {
                    if (data && data == true) {
                        userOps.loadData();
                    }
                });

            });

            $("#btn-delete-user").click(function() {
                var list = userTable.bootstrapTable('getSelections');
                if (list.length > 0) {
                    var delList = [];
                    list.forEach(function(element, index) {
                        delList.push(element.name);

                    });
                    mapUtil.link.postList("/user/delete", true, {
                        'names': delList
                    }, function(data) {
                        if (data && data == true) {
                            userOps.loadData();
                        }

                    });

                }
            });
        },
    }; // user表操作

    var poiOps = {
    	poimap:null,
    	poiTree:null,
    	list:[],
    	inited:false,
    	init:function(){
    		if(this.inited){
    			return ;
    		}
    		this.poimap = new AMap.Map('poi-map', {
                viewMode: '2D',
                zoom: 15, 
                features: ['bg','point','road', 'building'],
                center: [108.916913, 34.265569], // 中心点坐标
            });
    		
    		var poiTree=this.poiTree;
    		var poiList=this.list;
    		mapUtil.link.getData("/poi/all",true,{},function(data){
    			if(data){
    				var types = new Set(); // 空Set
    				console.log(data);
    				// 将数据按类型分开
    				data.forEach(function(ele, index) {
    					var nameType=null;
    					if(ele.type==1){
                    		nameType="公交站";
                    	}else if(ele.type==2){
                    		nameType="地铁站";
                    	}
                        if(types.has(nameType)){
                        	var theType=poiList.find(function(current, index, arr){
                        		
                        		return current.text==this;
                        	},nameType);
                        	theType.nodes.push({
                        		text:ele.name
                        	});
                        }else{
                        	var another=new Array();
                        	another.push({
                        		text:ele.name
                        	});
                        	types.add(nameType);
                        	poiList.push({
                        		text: nameType,
                        	    nodes:another
                        	});
                        }
                    });
    				
    				console.log(poiList);
    				poiTree=$('#poi-list').treeview({data: poiList});
    			}
    			
    		});
    		this.inited=true;
    	},
    		
    }

    var siteOps = {
		init:function(){
			this.initTable();
			this.initMap();
		},
		sitemap:null,
    	inited:false,
    	siteIcon: {
            image: "image/simu/house.ico",
            imageSize: new AMap.Size(24, 24),
            offset: new AMap.Pixel(-12, -24)
        },
        siteList:null,
        updateSite:null,
		initMap:function(){
			if(this.inited){
    			return ;
    		}
    		this.sitemap = new AMap.Map('siteMan-map', {
                viewMode: '2D',
                zoom: 13, 
                features: ['bg','point','road'],
                center: [108.946971,34.264399], // 中心点坐标
            });
    		this.loadData();
    		
    		this.inited=true;
		},
		
        initTable: function() {
        	function siteManFormatter(value, row, index) {
                return [
                    '<button type="button" class="siteMan-pos btn btn-primary  btn-sm">定位</button>',
                    '<button type="button" class="siteMan-repos btn btn-primary  btn-sm">移动</button>',
                    '<button type="button" data-toggle="modal" data-target="#modal-update-site" class="siteMan-change btn btn-primary  btn-sm">修改</button>',
                    '<button type="button" class="siteMan-anay btn btn-primary  btn-sm">分析</button>'
                ].join('');
            }
            window.siteManEvents = {
                'click .siteMan-pos': function(e, value, row, index) {
                	 var target = siteOps.siteList.find(function(current, index, arr) {
                         if (current.id == this) {
                             return current;
                         }
                     }, row.id);
                	 
                	 // console.log(index);
                	 
// $table.on('click-row.bs.table', function (e, row, $element) {
// $('.success').removeClass('success');
// $($element).addClass('success');
// });
                	 
                	 siteOps.sitemap.setZoomAndCenter(15, [target.lng, target.lat]); 
                    event.stopPropagation();
                },
                'click .siteMan-repos': function(e, value, row, index) {
                	var target = siteOps.siteList.find(function(current, index, arr) {
                        if (current.id == this) {
                            return current;
                        }
                    }, row.id);
                	siteOps.sitemap.setZoomAndCenter(15, [target.lng, target.lat]); 
                	target.marker.setDraggable(true);
                	target.marker.on('dragend',function(ev){
                		console.log(ev.lnglat);
                		target.marker.setDraggable(false);
                		mapUtil.link.getData("/site/update", true, {
                			id:row.id,
            				lng:ev.lnglat.lng,
            				lat:ev.lnglat.lat,
                		}, function(data) {
                            if (data) {
                            	siteOps.loadData();
                            }
                		});
                	});
                	 event.stopPropagation();
                },
                'click .siteMan-change': function(e, value, row, index) {
                	siteOps.updateSite=row;
                },
                'click .siteMan-anay': function(e, value, row, index) {
                	console.log(window.location);
                	console.log(row.id);
                	window.location='/index.html#anaylyze_site';
                	
                	$('#siteChange-Tab').tab('show');
                	mapChart.showSiteChangeById(row.id);
                }
            };
            siteTable.bootstrapTable({
            	toolbar: '#toolbar-site', // 工具按钮用哪个容器
                toolbarAlign: 'left',
                striped: true, // 是否显示行间隔色
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, // 是否显示分页（*）
                sortable: false, // 是否启用排序
                sortOrder: "asc", // 排序方式
                sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                strictSearch: true,
                showColumns: true, // 是否显示所有的列
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                uniqueId: "id", // 每一行的唯一标识，一般为主键列
                detailView: false, // 是否显示父子表
                icons: {
                    refresh: 'glyphicon-refresh icon-refresh',
                    toggle: 'glyphicon-list-alt icon-list-alt',
                    columns: 'glyphicon-th icon-th'
                },
                columns: [{
                	checkbox:true,
                },{
                    field: 'name',
                    title: '名称'
                }, {
                    field: 'volume',
                    title: '容量'
                }, {
                    field: 'type',
                    title: '类型'
                },{
                    field: 'coords',
                    title: '操作',
                    width: '160px',
                    events: siteManEvents,
                    formatter: siteManFormatter
                }],
            });
        },
        lightTableRow:function(id,index){
        	var page=parseInt(index/10)+1; 
			siteTable.bootstrapTable('selectPage', page);
			siteTable.find('.success').each(function(){
				 $(this).removeClass('success');
			 });
			siteTable.find('[data-uniqueid='+id+']').addClass('success');
        },
        loadData: function() {
        	
        	if(this.siteList){
				// 去掉地图上原来的标记
				var origins=this.siteList;
				if(origins.length>0){
					for (var i in origins) {
						origins[i].marker.setMap(null);
					}
				}
			}
            mapUtil.link.getData("/site/all", true, {}, function(data) {
            	
                var list = [];
                if (data) {
                	siteOps.siteList=data;
                	var count=0;
                     data.forEach(function(element, index) {
                    	 element.marker = new AMap.Marker({
                             icon: new AMap.Icon(siteOps.siteIcon),
                             position: [element.lng, element.lat],
                             map: siteOps.sitemap,
                         });
                    	 element.marker.on('click', function (ev) {
                    		 siteOps.lightTableRow(element.id,element.tableIndex);
 						 });
                    	 element.tableIndex=count;
                         list.push({
                        	 "index":count++,
                        	 "id":element.id,
                             "name": element.name,
                             "volume":element.volume,
                             "type": element.type,
                             // "coords": [element.lng, element.lat]
                         });
                     });
                }
                siteTable.bootstrapTable('load', list);

            });
        },

        bindControl: function() {
    	    var addSite = $("#modal-add-site");
// addSite.on('show.bs.modal', function() {
// $("#siteAdd-form")[0].reset();
// });
    	    addSite.find("#modal-sitePosAdd").click(function() {
    	    	console.log("111");
    	    	var btn=this;
    	    	function checkAddPos(ev){
    	    		$(btn).val(ev.lnglat);
    	    		$(btn).text("选定的坐标:["+ev.lnglat+"]");
    	    		
    	    		siteOps.sitemap.off('click',checkAddPos);
    	    		
    	    		$('#modal-add-site').modal("show");
    	    	}
    	    	siteOps.sitemap.on('click',checkAddPos);
    	    });
    	    addSite.find("#modal-siteAdd-submit").click(function() {
    	    	 var siteName = addSite.find("#modal-siteNameAdd").val();
                 var siteLimit = addSite.find("#modal-siteLimitAdd").val();
                 var siteType = addSite.find("#modal-siteTypeAdd").val();
                 var sitePos = addSite.find("#modal-sitePosAdd").val().split(",");
                 if(siteType=='调度站'){
                 	siteType=1;
                 }else{
                 	siteType=2;
                 }
                 
                 mapUtil.link.getData("/site/add", true, {
                	 name:siteName,
                	 volume:siteLimit,
                	 type:siteType,
                	 lng:sitePos[0],
                	 lat:sitePos[1],
                 }, function(data) {
                     if (data && data == true) {
                     	siteOps.loadData();
                     }
                 });
                 
               
    	    });
            var updateSite = $("#modal-update-site");
            updateSite.on('show.bs.modal', function() {
                $("#siteUpdate-form")[0].reset();
            });
            updateSite.find("#modal-siteUpdate-submit").click(function() {

                var siteName = updateSite.find("#modal-siteName").val();
                var siteLimit = updateSite.find("#modal-siteLimit").val();
                var siteType = updateSite.find("#modal-siteType").val();
                if(siteType=='调度站'){
                	siteType=1;
                }else{
                	siteType=2;
                }
                
                mapUtil.link.getData("/site/updateInfo", true, {
                    id:siteOps.updateSite.id,
                    siteName:siteName,
                    siteLimit:siteLimit,
                    siteType:siteType,
                    
                }, function(data) {
                    if (data && data == true) {
                    	siteOps.loadData();
                    }
                });

            });

            $("#btn-delete-site").click(function() {
                var list = siteTable.bootstrapTable('getSelections');
                if (list.length > 0) {
                    var delList = [];
                    list.forEach(function(element, index) {
                        delList.push(element.id);

                    });
                    mapUtil.link.postList("/site/delete", true, {
                        'ids': delList
                    }, function(data) {
                        if (data && data == true) {
                        	siteOps.loadData();
                        }

                    });

                }
            });
        },
    }


    var forbidOps = {
        init:function(){
            this.initTable();
            this.initMap();
        },
        forbidmap:null,
        inited:false,
        forbidList:null,
        areaList:[],
        updateItem:null,
        initMap:function(){
            if(this.inited){
                return ;
            }
            this.forbidmap = new AMap.Map('forbid-map', {
                viewMode: '2D',
                zoom: 13, 
                features: ['bg','point','road'],
                center: [108.946971,34.264399], // 中心点坐标
            });
           // this.loadData();
            
            this.inited=true;
        },
        
        initTable: function() {
            function forbidFormatter(value, row, index) {
                return [
                    '<button type="button" class="forbid-pos btn btn-primary  btn-sm">定位</button>',
                    '<button type="button" data-toggle="modal" data-target="#modal-update-forbid" class="forbid-change btn btn-primary  btn-sm">重命名</button>'
                ].join('');
            }
            window.forbidEvents = {
                'click .forbid-pos': function(e, value, row, index) {
                     var target = forbidOps.areaList.find(function(current, index, arr) {
                         if (current.id == this) {
                             return current;
                         }
                     }, row.id);
                     console.log(target);
                     
                     forbidOps.forbidmap.setZoomAndCenter(17, [target.path[0].lng, target.path[0].lat]); 
                     event.stopPropagation();
                },
                'click .forbid-change': function(e, value, row, index) {
                    var target = forbidOps.areaList.find(function(current, index, arr) {
                        if (current.id == this) {
                            return current;
                        }
                    }, row.id);
                    forbidOps.updateItem=target;
                    // forbidOps.forbidmap.setZoomAndCenter(17,
					// [target.path[0].lng, target.path[0].lat]);
                    
                    
//                    target.marker.setDraggable(true);
//                    target.marker.on('dragend',function(ev){
//                        console.log(ev.lnglat);
//                        target.marker.setDraggable(false);
//                        mapUtil.link.getData("/site/update", true, {
//                            id:row.id,
//                            lng:ev.lnglat.lng,
//                            lat:ev.lnglat.lat,
//                        }, function(data) {
//                            if (data) {
//                                siteOps.loadData();
//                            }
//                        });
//                    });
                },
                'click .siteMan-change': function(e, value, row, index) {
                    siteOps.updateSite=row;
                }
            };
            forbidTable.bootstrapTable({
                toolbar: '#toolbar-forbid', // 工具按钮用哪个容器
                toolbarAlign: 'left',
                striped: true, // 是否显示行间隔色
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, // 是否显示分页（*）
                sortable: false, // 是否启用排序
                sortOrder: "asc", // 排序方式
                sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                strictSearch: true,
                showColumns: true, // 是否显示所有的列
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                uniqueId: "id", // 每一行的唯一标识，一般为主键列
                detailView: false, // 是否显示父子表
                icons: {
                    refresh: 'glyphicon-refresh icon-refresh',
                    toggle: 'glyphicon-list-alt icon-list-alt',
                    columns: 'glyphicon-th icon-th'
                },
                columns: [{
                    checkbox:true,
                },{
                    field: 'name',
                    title: '名称'
                }, {
                    field: 'coords',
                    title: '操作',
                    width: '160px',
                    events: forbidEvents,
                    formatter: forbidFormatter
                }],
            });
        },
        lightTableRow:function(id,index){
            var page=parseInt(index/10)+1; 
            siteTable.bootstrapTable('selectPage', page);
            siteTable.find('.success').each(function(){
                 $(this).removeClass('success');
             });
            siteTable.find('[data-uniqueid='+id+']').addClass('success');
        },
        
        addAreas:function(list){
        	
        	for(var i=0;i<forbidOps.areaList.length;i++){
        		forbidOps.areaList[i].drawArea.setMap(null);
        	}
        	forbidOps.areaList=[];
        	list.forEach(function(ele,index){
        		ele.path= JSON.parse(ele.path);
        		var paths=[];
        		for(var i=0;i<ele.path.length;i++){
        			paths.push([ele.path[i].lng,ele.path[i].lat]);
        		}
        		ele.drawArea=new AMap.Polygon({
    				path:paths,
            		map:forbidOps.forbidmap,
            		strokeStyle:'solid',
            		strokeColor:'#000000',
            		strokeWeight:3,
            		fillColor:'#B0C4DE',
            		fillOpacity:0.8,
            		
                });
        		
        		forbidOps.areaList.push(ele);
        	});
        },
        loadData: function() {
            mapUtil.link.getData("/forbid/all", true, {}, function(data) {
                var list = [];
                if (data) {
                   forbidOps.addAreas(data);
                     data.forEach(function(element, index) {
                         list.push({
                             "id":element.id,
                             "name": element.name,
                         });
                     });
                }
                forbidTable.bootstrapTable('load', list);
            });
        },
        drawArea:function(drawResult){
        	var panel=this.forbidmap;
        	var drawPath=[];
        	var inited=false;
        	var drawLine;
        	var drawArea;
        	function initLine(ev){
        		console.log("绘制");
        		var clockPoint=ev.lnglat;
        		drawPath.push([clockPoint.lng,clockPoint.lat]);
        		if(!inited){
        			drawLine = new AMap.Polyline({
                		map:forbidOps.forbidmap,
                		// strokeDasharray:'',
                		strokeStyle:'solid',
                		strokeColor:'#000000',
                		strokeWeight:3,
                		path:drawPath,
                    });
            		inited=true;
        		}else{
        			drawLine.setPath(drawPath);
        		}
        		
        	}
        	function finishLine(ev){
        		if(drawPath.length>=3){
        			drawPath.push(drawPath[0]);
        			drawLine.setPath(drawPath);
        			
        			drawArea = new AMap.Polygon({
        				path:drawPath,
                		map:forbidOps.forbidmap,
                		// strokeDasharray:'',
                		strokeStyle:'solid',
                		strokeColor:'#000000',
                		strokeWeight:3,
                		fillColor:'#B0C4DE',
                		fillOpacity:0.8,
                		
                    });
        		}
        		forbidOps.forbidmap.setDefaultCursor("pointer");
        		drawLine.setMap(null);
        		console.log("完成绘制");
        		panel.off('click',initLine);
        		panel.off('rightclick',finishLine);
        		for(var i=0;i<drawPath.length-1;i++){
        			if(i==0){
        				drawResult.push([drawPath[i].lng,drawPath[i].lat]);
        			}else{
        				drawResult.push(drawPath[i]);
        			}
        			
        		}
        		$('#modal-add-forbid').modal("show");
        		
        	}
        	
        	forbidOps.forbidmap.setDefaultCursor("crosshair");
        	panel.on('click',initLine);
        	panel.on('rightclick',finishLine);
        },

        bindControl: function() {
        	
            var addForbid = $("#modal-add-forbid");
// addSite.on('show.bs.modal', function() {
// $("#siteAdd-form")[0].reset();
// });
            var drawResult=[];
            addForbid.find("#modal-forbidAreaAdd").click(function() {
                console.log("111");
                
                forbidOps.drawArea(drawResult);
	    		
            });
            addForbid.find("#modal-forbidAdd-submit").click(function() {
      	    	 var forbidName = addForbid.find("#modal-forbidNameAdd").val();
                   console.log(drawResult);
                 var list=[];
                 for(var i=0;i<drawResult.length;i++){
                	 list.push({
                		 lng:drawResult[i][0],
                		 lat:drawResult[i][1],
                	 });
                 }
                 
                 mapUtil.link.postList("/forbid/add", true, {
                	 name:forbidName,
                	 paths:drawResult,
                 }, function(data) {
                     if (data && data == true) {
                    	 forbidOps.loadData();
                     }
                 });
            });
            
                   
            
			 var updateForbid = $("#modal-update-forbid");
             updateForbid.on('show.bs.modal', function() {
            	 $("#forbidUpdate-form")[0].reset();
			 });
             updateForbid.find("#modal-forbidUpdate-submit").click(function() {
			
			 var name = updateForbid.find("#modal-forbidName").val();
			 mapUtil.link.getData("/forbid/update", true, {
					 id:forbidOps.updateItem.id,
					 name:name,
				 }, 
				 function(data) {
					 if (data && data == true) {
						 forbidOps.loadData();
					 }
				 });
			
			 });

            $("#btn-delete-forbid").click(function() {
            	
				 var list = forbidTable.bootstrapTable('getSelections');
				 if (list.length > 0) {
					 var delList = [];
					 list.forEach(function(element, index) {
						 delList.push(element.id);
					 });
					 mapUtil.link.postList("/forbid/delete", true, {
						 'ids': delList
					 }, function(data) {
						 if (data && data == true) {
							 forbidOps.loadData();
						 }
					
					 });
				
				 }
            });
        },
    }

    var dispOps = {
        init: function() {
            dispTable.bootstrapTable({
                toolbar: '#toolbar-disp', // 工具按钮用哪个容器
                toolbarAlign: 'left',
                striped: true, // 是否显示行间隔色
                cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true, // 是否显示分页（*）
                sortable: false, // 是否启用排序
                sortOrder: "asc", // 排序方式
                sidePagination: "client", // 分页方式：client///server,大爷的，如果服务器分页，数据要放到rows
                pageNumber: 1, // 初始化加载第一页，默认第一页
                pageSize: 10, // 每页的记录行数（*）
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                search: true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true, // 是否显示所有的列
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "ID", // 每一行的唯一标识，一般为主键列
                showToggle: true, // 是否显示详细视图和列表视图的切换按钮
                cardView: false, // 是否显示详细视图
                detailView: false, // 是否显示父子表
                icons: {
                    refresh: 'glyphicon-refresh icon-refresh',
                    toggle: 'glyphicon-list-alt icon-list-alt',
                    columns: 'glyphicon-th icon-th'
                },
                columns: [{
                    checkbox: true
                },{
                    field: 'name',
                    title: '调度名称'
                }, {
                    field: 'typeName',
                    title: '调度类型'
                }],
            });
        },
        loadData: function() {
            mapUtil.link.getData("/disp/all", true, {}, function(data) {

                var list = [];
                if (data) {
                	
                     data.forEach(function(element, index) {
                    	 var typeName;
                    	 if (element.type == mapSimu.configs.truckType) {
                             typeName = "卡车";
                         } else if (element.type == mapSimu.configs.triType) {
                             typeName = "三轮车";
                         } else {
                             typeName = "调度员";
                         }
                         list.push({
                        	 "id": element.id,
                             "name": element.name,
                             "type": element.type,
                             "typeName": typeName
                         });
                     });
                }
                dispTable.bootstrapTable('load', list);

            });
        },

        bindControl: function() {
            var addDisp = $("#modal-add-disp");
            addDisp.on('show.bs.modal', function() {
                $("#disp-form")[0].reset();
            });
            addDisp.find("#modal-disp-submit").click(function() {
                var dispName = addDisp.find("#modal-dispName").val();
                var dispType = addDisp.find("#modal-dispType").val();
                if(dispType=='卡车'){
                	dispType=mapSimu.configs.truckType;
                }else if(dispType=='三轮车'){
                	dispType=mapSimu.configs.triType;
                }else if(dispType=='调度员'){
                	dispType=mapSimu.configs.manType;
                }
                
                console.log(dispType);
                mapUtil.link.getData("/disp/add", true, {
                	dispName: dispName,
                	dispType: dispType
                }, function(data) {
                    if (data && data == true) {
                    	dispOps.loadData();
                    }
                });

            });

            $("#btn-delete-disp").click(function() {
                var list = dispTable.bootstrapTable('getSelections');
                if (list.length > 0) {
                    var delList = [];
                    list.forEach(function(element, index) {
                        delList.push(element.name);

                    });
                    mapUtil.link.postList("/disp/deletePatch", true, {
                        'names': delList
                    }, function(data) {
                        if (data && data == true) {
                        	dispOps.loadData();
                        }

                    });

                }
            });
        },
    }



    var testGo = function() {
        console.log("hello, ehtr");
    };


    manage = {
        test: testGo,

        init: function() {
            userOps.init();
            userOps.loadData();
            userOps.bindControl();
            poiOps.init();
            siteOps.init();
            siteOps.loadData();
            siteOps.bindControl();
            
            dispOps.init();
            dispOps.loadData();
            dispOps.bindControl();
            forbidOps.init();
            forbidOps.loadData();
            forbidOps.bindControl();
        },
        initUser: function() {
            userOps.init();
            userOps.loadData();
            userOps.bindControl();
        },
        initPOI: function() {
            poiOps.init();
            // poiOps.loadData();
            // poiOps.bindControl();
        },
    };

    _global = (function() {
        return this || (0, eval)('this');
    }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = manage;
    } else if (typeof define === "function" && define.amd) {
        define(function() {
            return manage;
        });
    } else {
        !('mapLayer' in _global) && (_global.manage = manage);
    }

})();