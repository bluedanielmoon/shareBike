

visualer = (function(map){
	　　　　var recLayer;
	　　　　var initRec = function(){
		　　　　　　  recLayer = Loca.visualLayer({
			container: map,
			type: 'heatmap',
			shape: 'rectangle'
		});

		recLayer.setOptions({
			unit: 'meter',
			style: {
				color: ['#ecda9a', '#efc47e', '#f3ad6a', '#f7945d', '#f97b57', '#f66356', '#ee4d5a'],
				radius: 1500,
				opacity: 0.9,
				gap: 150,
				height: [0, 500000]
			}
		});
	　　　　};
	　　　　var setRecData = function(recData){
		　　　　　　recLayer.setData(recData, {
			lnglat: function (obj) {
				var val = obj.value;
				return [val['lng'], val['lat']]
			},
			value: 'count',
			type: 'json'
		});
	　　　　};
	var showRec=function(){
		recLayer.render();
	};
	
	var test=function(){
		console.log("hi");
	};
	
	　　　　return {
		　　　　　　initRec : initRec,
		　　　　　　setRec	 : setRecData,
		test:test,
	　　　　};






　　})(map);


