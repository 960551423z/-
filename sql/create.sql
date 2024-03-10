
create database partner;
alter database partner character set utf8 collate utf8_general_ci;
use partner;
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment primary key comment 'id',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '手机号',
    email        varchar(512)                       null comment '邮箱',
    userStatus   tinyint  default 0                 not null comment '状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRose     tinyint  default 0                 not null comment '用户角色 0-普通用户  1-管理员',
    tags         varchar(512)                       comment '用户标签'
)
comment '用户';

create table tag
(
    id           bigint auto_increment primary key comment 'id',
    tagName      varchar(256) unique                null comment '标签名称,标签唯一',
    userId       bigint                             null comment '用户id',
    parentId     bigint                             null comment '父标签id',
    isParent     tinyint                            null comment '0-不是父标签, 1-父标签',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
comment '标签'