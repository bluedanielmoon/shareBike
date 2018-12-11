
DROP TABLE `users`;
DROP TABLE `dispatcher`;
DROP TABLE `poi`;
DROP TABLE `sites`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NOW() COMMENT '注册时间',
  `update_time` datetime DEFAULT NOW() ON UPDATE NOW() COMMENT '最后登陆时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='登陆系统的用户列表';

INSERT INTO users(id,username,password) VALUES(1,'daniel','123');

CREATE TABLE IF NOT EXISTS `dispatcher` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT '调度力量名称',
  `dispatch_type` int(2) DEFAULT NULL COMMENT '调度力量类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调度力量列表';

CREATE TABLE IF NOT EXISTS `poi` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT 'POI名称',
  `poi_type` int(2) DEFAULT NULL COMMENT 'POI类型',
  `lng` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `lat` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='POI列表';

INSERT INTO poi(name,poi_type,lng,lat) VALUES('daniel',1,129.123123,45.123123);

CREATE TABLE IF NOT EXISTS `sites` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT '站点名称',
  `volume` int(5) DEFAULT NULL COMMENT '站点容量',
  `site_type` int(2) DEFAULT NULL COMMENT '站点类型',
  `lng` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `lat` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='站点列表';




