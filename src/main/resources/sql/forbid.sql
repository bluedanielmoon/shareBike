CREATE TABLE IF NOT EXISTS `forbid_area` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(20) NOT NULL COMMENT '区域名称',
  `path` varchar(20000) DEFAULT NULL COMMENT '路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='禁停区域';

insert into forbid_area (id,name,path) values (1,"禁停区域001","123.123");