CREATE TABLE `http_service_firewall` (
`id`  bigint NULL AUTO_INCREMENT ,
`ip`  varchar(64) NULL COMMENT 'IP' ,
`userName`  varchar(128) NULL COMMENT '用户名' ,
`pwd`  varchar(128) NULL COMMENT '密码' ,
`status`  smallint NULL COMMENT '状态' ,
`timeCreate`  datetime NULL ,
PRIMARY KEY (`id`)
)
;

CREATE TABLE `http_service_commit` (
`id`  bigint NULL AUTO_INCREMENT ,
`host`  varchar(128) NULL COMMENT '应用' ,
`packagez`  varchar(128) NULL COMMENT '模块' ,
`action`  varchar(128) NULL COMMENT '功能' ,
`method`  varchar(128) NULL COMMENT '方法' ,
`uri`  varchar(1024) NULL COMMENT '请求uri' ,
`seqId`  bigint NULL COMMENT '请求ID' ,
`timeCreate`  datetime NULL COMMENT '创建时间' ,
`countReSend`  tinyint NULL DEFAULT 0 COMMENT '重新提交次数' ,
`errorCode`  varchar(2048) NULL COMMENT '返回的错误代码，累加以|隔开' ,
PRIMARY KEY (`id`)
)
;


ALTER TABLE `http_service_commit`
ADD COLUMN `localObjJson` text  NULL COMMENT '本地对象json' AFTER `errorCode`,
ADD COLUMN `localObjClassName`  varchar(1024) NULL COMMENT '本地对象类名' AFTER `localObjJson`;


