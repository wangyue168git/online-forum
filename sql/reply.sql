CREATE TABLE `reply` (
  `id` char(20) NOT NULL,
  `noteid` int(11) NOT NULL,
  `title` char(20) DEFAULT NULL,
  `replycontent` char(140) DEFAULT NULL,
  `date` char(20) DEFAULT NULL,
  KEY `id` (`id`),
  KEY `noteid` (`noteid`),
  CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`id`) REFERENCES `lode` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reply_ibfk_2` FOREIGN KEY (`noteid`) REFERENCES `notepad` (`noteid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

