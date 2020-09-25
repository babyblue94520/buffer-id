CREATE DATABASE IF NOT EXISTS `buffer_id` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ----------------------------
-- Table structure for serial
-- ----------------------------
CREATE TABLE `serial` (
  `id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '群組',
  `prefix` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '前綴',
  `number` bigint(20) NOT NULL DEFAULT '0' COMMENT '流水號',
  PRIMARY KEY (`id`,`prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='serial';


