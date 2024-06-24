CREATE USER IF NOT EXISTS 'microservice'@'%' IDENTIFIED BY 'abcdefg';
CREATE DATABASE IF NOT EXISTS `trade_capture`;
GRANT ALL PRIVILEGES ON `trade_capture`.* TO 'microservice'@'%';