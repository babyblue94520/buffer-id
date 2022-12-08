-- ----------------------------
-- Table structure for serial
-- ----------------------------
CREATE TABLE IF NOT EXISTS `serial` (
  `id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'group',
  `prefix` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'prefix',
  `number` bigint(20) NOT NULL DEFAULT '0' COMMENT 'current number',
  PRIMARY KEY (`id`,`prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='serial';


