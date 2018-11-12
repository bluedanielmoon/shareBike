~(function() {
	var mapUtil = mapUtil || {};
	mapUtil.utils = function() {

		return {
			// 对于没有对象，只有属性（属性可以是数组）的对象，这样就够了
			deepCopy : function(source) {
				var result = {};
				for ( var key in source) {
					if (this.isObject(source[key])) {
						if (this.isArray(source[key])) {
							result[key] = [];
							for ( var i in source[key]) {
								result[key][i] = source[key][i];
							}
						} else {
							result[key] = this.deepCopy(source[key]);
						}
					} else {
						result[key] = source[key];
					}
				}
				return result;
			},
			isObject : function(value) {
				return (typeof value === 'object');
			},
			isNumber : function(value) {
				return (typeof value === 'number');
			},
			isString : function(value) {
				return (typeof value === 'string');
			},
			isBoolean : function(value) {
				return (typeof value === 'boolean');
			},
			isArray : function(value) {
				if (value && typeof value.length != "undefined") {
					return true;
				}
				return false;
			},
			isSupportCanvas : function() {
				var elem = document.createElement('canvas');
				return !!(elem.getContext && elem.getContext('2d'));
			},
			parser : function(inputTime,setHour) {
				var date = new Date(inputTime);
				var y = date.getFullYear();
				var m = date.getMonth() + 1;
				m = m < 10 ? ('0' + m) : m;
				var d = date.getDate();
				d = d < 10 ? ('0' + d) : d;
				var h = date.getHours();
				if(setHour){
					h=setHour;
				}
				h = h < 10 ? ('0' + h) : h;
				var minute = date.getMinutes();
				var second = 0;//date.getSeconds();
				minute = minute < 10 ? ('0' + minute) : minute;
				return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':'
						+ second;
			},
			decodeTime:function(time){
				//2018-11-01T15:33:51.998+0000
				var times=time.split("T");
				var date=times[0];
				var hour=times[1].split(":")[0];
				return date+" "+hour;
			},
		}
	}();
	mapUtil.link = function() {

		return {
			getData : function(url, async, data, succFunc) {
				$.ajax({
					url : url,
					async : async,
					data : data,
					type : "GET",
					success : function(result, status, xhr) {
						succFunc(result);
					}
				});
			},
			postJson:function(url, async, data, succFunc){
				$.ajax({
					url : url,
					async : async,
					//要通过这个函数转化
					data : JSON.stringify(data),
					type : "POST",
					//向服务器提交的数据形式
					contentType: "application/json; charset=utf-8",
					success : function(result, status, xhr) {
						succFunc(result);
					}
				});
			}
			

		}
	}();
	window.mapUtil = mapUtil;
})();