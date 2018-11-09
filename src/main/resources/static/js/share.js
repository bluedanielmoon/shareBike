//share,map
var share = {
	init : null,
	console : null,
	datePicker : null,
	infoPanel : {
		setData : null,
	},
	timeSetter : {

	},
	clockLoader : {
		show : null,
		hide : null,
	},
}
share.switcher = {
	bind : function() {
		var swts = $(".js-switch");
		swts.each(function(index, ele) {
			var swit = new Switchery(ele);
			// ele.checked=true;
		});
		swts.change(function() {
			var mod = this.id.substring(5);

			if (mod.substring(0, 3) == 'map') {
				var len = mod.length;
				mod = mod.substring(4, mod.length);
				console.log(mod);
				if (this.checked) {
					mapLayer.addMapFeature(mod);
				} else {
					mapLayer.removeMapFeature(mod);
				}
			} else {
				if (this.checked) {
					mapLayer.show(mod);
				} else {
					mapLayer.hide(mod);
				}
			}

		});
	}(),
}

share.timeSetter = {
	set : function(time) {
		$("#dataTime").val(time);
	},
	bind : function() {
		dataTime
		var btn = $("#Setter-Time");
		btn.click(function() {
			// 2018-11-16 03:00:00

			var time = $("#dataTime").val();
			var date = new Date(time);
			var y = date.getFullYear();
			var m = date.getMonth() + 1;

			if (y == 2018) {
				if (m >= 11) {
					mapLayer.reloadData(time);
				} else {
					layer.alert('请选择2018年11月1日起至今的日期=-=！');
				}
			} else if (y >= 2019) {
				layer.alert('未来的数据还没出来=-=！');
			} else {
				layer.alert('请选择2018年11月1日起至今的日期=-=！');
			}
		});

	}(),
}
share.clockLoader = {
	clock : $("#clockLoader"),

	show : function() {
		this.clock.show();
	},
	hide : function() {
		this.clock.hide();
	},
}

// load map and its plugins ----END
// init time picker ----BEGIN
share.datePicker = $('#time-picker').daterangepicker(
		{
			"timePicker" : true,
			"timePicker24Hour" : true,
			"opens":"right",
			"locale" : {
				"format" : "YYYY-MM-DD HH:00",
				"separator" : " ~ ",
				"applyLabel" : "Apply",
				"cancelLabel" : "Cancel",
				"fromLabel" : "From",
				"toLabel" : "To",
				"customRangeLabel" : "Custom",
				"weekLabel" : "W",
				"daysOfWeek" : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ],
				"monthNames" : [ "1月", "2月", "3月", "4月",
						"5月", "6月", "7月", "8月", "9月",
						"10月", "11月", "12月" ],
				"firstDay" : 1
			},
			"showCustomRangeLabel" : false,
			"startDate" : "2018-11-09",
			"endDate" : "2018-11-15",
			"minDate" : "2018-10-01",
			"maxDate" : "2018-12-20"
		},
		function(start, end, label) {
			console.log('New date range selected: '
					+ start.format('YYYY-MM-DD') + ' to '
					+ end.format('YYYY-MM-DD') + ' (predefined range: ' + label
					+ ')');
		});

// .datetimepicker({
// format : "yyyy-mm-dd hh:ii",
// weekStart : 1,
// todayBtn : 1,
// autoclose : 1,
// todayHighlight : 1,
// minView : 1,
// startView : 2,
// forceParse : 0,
// showMeridian : 1
// });

// init time picker ----END
share.infoPanel = {
	startMark : null,
	endMark : null,
	bindMark : function(btn, mark, cords) {
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
				icon : mapLayer.icons.point,
				// [116.406315,39.908775]
				position : [ cords[0], cords[1] ],
				offset : new AMap.Pixel(-12, -12),
				zIndex : 2,
				map : map,
				visible : true,
				title : "区域起点",
			});
			$(btn).css("background-color", "#3c8dbc");
		}
		console.log(cords[0]);
		// map.setCenter([lng, lat]); //设置地图中心点
		map.setCenter([ cords[0], cords[1] ]);
		return mark;
	},
	setData : function(panInfo) {
		/*
		 * var pan={ header:"1111", body:{ name1:1 } }
		 */
		var panel = $("#data-panel");

		panel.find("span[data-bikesCount]").text(panInfo.bikesCount);

		var startRe = panel.find("button[data-regionStart]");
		startRe.attr("data-coords", panInfo.regionStart);
		startRe.click(function() {
			var cords = $(this).attr("data-coords").split(",");
			var mark = share.infoPanel.startMark;
			share.infoPanel.startMark = share.infoPanel.bindMark(this, mark,
					cords);
		});

		var endRe = panel.find("button[data-regionEnd]")
		endRe.attr("data-coords", panInfo.regionEnd);
		endRe.click(function() {
			var cords = $(this).attr("data-coords").split(",");
			var mark = share.infoPanel.endMark;
			share.infoPanel.endMark = share.infoPanel.bindMark(this, mark,
					cords);
		});

		panel.find("span[data-weather]").text(panInfo.weather)
		panel.find("span[data-temprature]").text(panInfo.temperature)
	},

};

share.console = {
	names : {
		map : "map",
		chart : "chart",
	},
	show : function(consoleName) {
		$(".console").hide();
		$("#console-" + consoleName).show();
	},
	change : function(time) {
		var link = window.location.hash;
		if (link && link != "") {
			link = link.substring(1, link.length);
		}
		if (link == "anaylyze_move") {
			share.console.show(share.console.names.chart);
		} else {
			if (link == "") {
				link = "bikePos";
			}
			share.console.show(share.console.names.map);
		}
	},
};

// *************************初始化开始*************************

$(document).ready(function() {
	share.init = function() {
		// 绑定hashchange的变换console时间
		// $(window).bind("hashchange", function() {
		// share.console.change();
		// });
		// // 加载location页面
		// share.console.change();
		// mapLayer.init.busMarkers("busStop");

		share.console.change();
		mapLayer.init.busMarkers("busStop");
		mapLayer.init.massMark("bikePos");
		mapLayer.init.cluster("cluster");
		mapLayer.init.heatmap("heat");

		mapLayer.show("bikePos");

	}();
	layer.msg('hello,欢迎来到shareBIKE,这里有关于共享单车的一切！');
});

map.on('hotspotclick', function(argus) {
	console.log(argus);
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
