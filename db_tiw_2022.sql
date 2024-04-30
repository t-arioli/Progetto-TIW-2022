DROP SCHEMA `db_tiw_2022`;
CREATE SCHEMA IF NOT EXISTS `db_tiw_2022`;
USE `db_tiw_2022`;
CREATE TABLE `user` (
	`username` VARCHAR(255) PRIMARY KEY,
    `password` VARCHAR(255) NOT NULL,
    `isClient` BOOLEAN NOT NULL,
    `isEmployee` BOOLEAN NOT NULL,
    CHECK( `isClient` != `isEmployee`)
    );
CREATE TABLE `product` (
	`code` INT(127) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `imageUrl` VARCHAR(255)
    );
CREATE TABLE `option` (
	`code` INT(127) NOT NULL,
    `product` INT(127) NOT NULL REFERENCES `product`(`code`)
								ON DELETE CASCADE
                                ON UPDATE CASCADE,
    `name` VARCHAR(255) NOT NULL,
    `isNormal` BOOLEAN NOT NULL,
    `isOnOffer` BOOLEAN NOT NULL,
    PRIMARY KEY(`code`, `product`)
    );
CREATE TABLE `quote` (
	`id` INT(127) PRIMARY KEY AUTO_INCREMENT,
    `price` NUMERIC(8,2),
    `dateCreation` DATE NOT NULL,
    `dateValidation` DATE,
    `client` VARCHAR(255) NOT NULL REFERENCES `user`(`username`)
									ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    `employee` VARCHAR(255) REFERENCES `user`(`username`)
									ON DELETE SET NULL
                                    ON UPDATE CASCADE,
    `product` INT(127) NOT NULL REFERENCES `product`(`code`)
									ON DELETE CASCADE
                                    ON UPDATE CASCADE,
    CHECK(`client` != `employee`)
    );
CREATE TABLE `optionQuote` (
	`quote` INT(127) NOT NULL REFERENCES `quote`(`id`)
									ON DELETE CASCADE
                                    ON UPDATE CASCADE,
	`product` INT(127) NOT NULL,
    `option` INT(127) NOT NULL,
    PRIMARY KEY(`quote`, `product`, `option`),
    FOREIGN KEY(`option`, `product`) REFERENCES `option`(`code`, `product`)
									ON DELETE CASCADE
                                    ON UPDATE CASCADE
    );
INSERT INTO `user`(`username`, `password`, `isClient`, `isEmployee`)
VALUES ("admin", "toor", false, true);
INSERT INTO `product`(`code`, `name`, `imageUrl`)
VALUES (1, "prod1", null);
INSERT INTO `product`(`code`, `name`, `imageUrl`)
VALUES (2, "prod2", null);
INSERT INTO `option`(`code`, `product`, `name`, `isNormal`, `isOnOffer`)
VALUES (1, 1, "opzione 1", true, false);
INSERT INTO `option`(`code`, `product`, `name`, `isNormal`, `isOnOffer`)
VALUES (2, 1, "opzione 2", false, true);
INSERT INTO `option`(`code`, `product`, `name`, `isNormal`, `isOnOffer`)
VALUES (1, 2, "opzione 1", true, false);
INSERT INTO `option`(`code`, `product`, `name`, `isNormal`, `isOnOffer`)
VALUES (2, 2, "opzione 2", false, true);

    
    
    
    
