/*
 Navicat Premium Data Transfer

 Source Server         : zibuyu
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : im_server

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 29/03/2021 18:27:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for im_message
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `message_id` char(25) DEFAULT '' COMMENT 'message_id',
  `from_id` bigint(20) DEFAULT '0' COMMENT '消息发送者',
  `to_id` bigint(20) DEFAULT '0' COMMENT '消息接受者',
  `msg` varchar(10240) DEFAULT '' COMMENT '账号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `un_message_id` (`message_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息记录';

-- ----------------------------
-- Records of im_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `account` varchar(32) DEFAULT '' COMMENT '账号',
  `password` varchar(32) DEFAULT '' COMMENT '账号',
  `name` varchar(16) DEFAULT '' COMMENT '姓名',
  `mobile` char(11) DEFAULT '' COMMENT '手机',
  `last_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT '0' COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT '0' COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `un_account` (`account`) USING BTREE,
  UNIQUE KEY `un_mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- ----------------------------
-- Records of im_user
-- ----------------------------
BEGIN;
INSERT INTO `im_user` VALUES (1, 'zhangsan', '123456', '张三', '13200000001', '2021-03-29 18:26:40', '2021-03-29 18:26:40', 0, '2021-03-29 18:26:40', 0);
INSERT INTO `im_user` VALUES (2, 'lisi', '123456', '李四', '13200000002', '2021-03-29 18:26:40', '2021-03-29 18:26:40', 0, '2021-03-29 18:26:40', 0);
INSERT INTO `im_user` VALUES (3, 'wangwu', '123456', '王五', '13200000003', '2021-03-29 18:26:40', '2021-03-29 18:26:40', 0, '2021-03-29 18:26:40', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
