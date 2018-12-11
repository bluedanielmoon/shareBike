CREATE TABLE IF NOT EXISTS `routes` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `from_id` int(5) NOT NULL COMMENT '起点站点id',
  `to_id` int(5) NOT NULL COMMENT '终点站点id',
  `distance` int(10) DEFAULT NULL COMMENT '路径距离',
  `duration` int(5) DEFAULT NULL COMMENT '估计时间',
  `path` varchar(21800) DEFAULT NULL COMMENT '路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='站点线路';

insert into routes (from_id,to_id,distance,duration,path) values (1,2,12,23,"123.123");