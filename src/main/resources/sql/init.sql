
DROP TABLE `users`;
DROP TABLE `dispatcher`;
DROP TABLE `dispatch_type`;
DROP TABLE `poi`;
DROP TABLE `sites`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(20) DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后登陆时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='登陆系统的用户列表';

INSERT INTO users(id,username,password) VALUES(1,'daniel','123');


CREATE TABLE IF NOT EXISTS `dispatch_type` (
  `id` int(2) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT '调度类型',
  `volume` int(2) DEFAULT NULL COMMENT '调度容量',
  `unit_time` int(5) DEFAULT NULL COMMENT '调度单车所需时间',
  `speed` int(5) DEFAULT NULL COMMENT '移动速度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调度类型列表';

CREATE TABLE IF NOT EXISTS `dispatcher` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT '调度力量名称',
  `dispatch_type` int(2) DEFAULT NULL COMMENT '调度力量类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY(`dispatch_type`) REFERENCES `dispatch_type`(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调度力量列表';

CREATE TABLE IF NOT EXISTS `poi` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT 'POI名称',
  `poi_type` int(2) DEFAULT NULL COMMENT 'POI类型',
  `lng` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `lat` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='POI列表';

CREATE TABLE IF NOT EXISTS `sites` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) DEFAULT NULL COMMENT '站点名称',
  `volume` int(3) DEFAULT NULL COMMENT '站点容量',
  `over_rate` decimal(6,2) DEFAULT NULL COMMENT '站点超载率',
  `lng` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `lat` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='站点列表';


