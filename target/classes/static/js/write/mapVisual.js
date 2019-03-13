;
var visualer = {
    loca: null,
    recLayer: null,

    initRec: function() {


    },

    setRecData: function() {
        mapUtil.link.getData("/anay/varia", true, {
            time: "123",
            dist: 1
        }, function(data) {

            if (data) {
                console.log(data);
                visualer.loca = Loca.create(map);

                visualer.recLayer = Loca.visualLayer({
                    container: visualer.loca,
                    type: 'heatmap',
                    shape: 'normal',
                });



                var list = [];
                data.forEach(function(val, index, arr) {
                    list.push({
                        'coordinate': [val.center.lng, val.center.lat],
                        'count': val.viria * 1000,
                    });
                });
                visualer.recLayer.setData(list, {
                    lnglat: 'coordinate',
                    value: 'count'
                });

                visualer.recLayer.setOptions({
                    unit: 'meter',
                    style: {
                        color: {
                            0.5: '#2c7bb6',
                            0.65: '#abd9e9',
                            0.7: '#ffffbf',
                            0.9: '#fde468',
                            1.0: '#d7191c'
                        },
                        //'#ecda9a', '#efc47e', '#f3ad6a', '#f7945d', '#f97b57', '#f66356', '#ee4d5a'],
                        radius: 20,
                        opacity: 0.9,
                        gap: 10,
                        height: [0, 500000]
                    }
                });
                visualer.recLayer.render();
            }

        });



    },
    showRec: function() {

    },


};