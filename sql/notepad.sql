CREATE TABLE `notepad` (
  `id` char(20) NOT NULL,
  `noteid` int(11) NOT NULL AUTO_INCREMENT,
  `title` char(20) DEFAULT NULL,
  `content` char(140) DEFAULT NULL,
  `date` char(20) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`noteid`),
  KEY `id` (`id`),
  CONSTRAINT `notepad_ibfk_1` FOREIGN KEY (`id`) REFERENCES `lode` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;

