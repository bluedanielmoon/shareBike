;
(function(global, undefined) {
    var userTable = $('#table-user');


    var siteTable=$('#table-site');

    var dispTable=$('#table-disp');

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
    				//将数据按类型分开
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
        init: function() {
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
                pageList: [10, 25, 50, 100], // 可供选择的每页的行数（*）
                search: true, // 是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true, // 是否显示所有的列
                showRefresh: true, // 是否显示刷新按钮
                minimumCountColumns: 2, // 最少允许的列数
                clickToSelect: true, // 是否启用点击选中行
                height: 500, // 行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "name", // 每一行的唯一标识，一般为主键列
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
                    title: '站点名称'
                }, {
                    field: 'volume',
                    title: '站点容量'
                }, {
                    field: 'type',
                    title: '站点类型'
                },{
                    field: 'coords',
                    title: '经纬度'
                }],
            });
        },
        loadData: function() {
            mapUtil.link.getData("/site/all", true, {}, function(data) {
            	
                var list = [];
                if (data) {
                     data.forEach(function(element, index) {
                         list.push({
                             "name": element.name,
                             "volume":element.volume,
                             "type": element.type,
                             "coords": [element.lng, element.lat]
                         });
                     });
                }
                siteTable.bootstrapTable('load', list);

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
                }, {
                    field: 'id',
                    title: '调度ID'
                }, {
                    field: 'name',
                    title: '调度名称'
                }, {
                    field: 'type',
                    title: '调度类型'
                }],
            });
        },
        loadData: function() {
            mapUtil.link.getData("/disp/all", true, {}, function(data) {

                var list = [];
                if (data) {
                     data.forEach(function(element, index) {
                         list.push({
                        	 "id": element.id,
                             "name": element.name,
                             "type": element.type
                         });
                     });
                }
                dispTable.bootstrapTable('load', list);

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
            dispOps.init();
            dispOps.loadData();
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