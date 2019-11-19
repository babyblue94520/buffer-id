CREATE DATABASE IF NOT EXISTS `buffer_id` CHARACTER SET utf8 COLLATE utf8_general_ci;
-- ----------------------------
-- Table structure for serial
-- ----------------------------
CREATE TABLE IF NOT EXISTS `serial` (
  `group` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '群組',
  `prefix` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '前綴',
  `number` bigint(20) unsigned DEFAULT '0' COMMENT '流水號',
  PRIMARY KEY (`id`,`prefix`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;