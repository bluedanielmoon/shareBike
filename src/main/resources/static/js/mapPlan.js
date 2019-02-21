;
(function(global, undefined) {

    var _global;
    mapPlan = {
       test:function(){
    	   console.log("plan-----");
       }
    }

    // 最后将插件对象暴露给全局对象
    _global = (function() {
        return this || (0, eval)('this');
    }());
    if (typeof module !== "undefined" && module.exports) {
        module.exports = mapPlan;
    } else if (typeof define === "function" && define.amd) {
        define(function() {
            return mapPlan;
        });
    } else {
        !('mapPlan' in _global) && (_global.mapPlan = mapPlan);
    }
}());