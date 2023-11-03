-- Items table
CREATE TABLE Items (
ItemId INT AUTO_INCREMENT NOT NULL,
Username VARCHAR(255) NOT NULL,
Title varchar(255) NOT NULL,
Category VARCHAR(255) NOT NULL,
Description TEXT,
Price DECIMAL (10,2) NOT NULL,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ItemId PRIMARY KEY(ItemId),
CONSTRAINT Fk_Items_Username FOREIGN KEY(Username) REFERENCES users(Username)
);

CREATE TABLE Reviews (
ReviewId BIGINT AUTO_INCREMENT NOT NULL,
ItemId INT NOT NULL,
Reviewer VARCHAR(255) NOT NULL,
Quality VARCHAR(255) NOT NULL,
Review TEXT,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ReviewId PRIMARY KEY(ReviewId),
CONSTRAINT Fk_Items_ItemId FOREIGN KEY(ItemId) REFERENCES items(ItemId),
CONSTRAINT Fk_Items_Reviewer FOREIGN KEY(Reviewer) REFERENCES users(Username)
);

DROP TABLE reviews;

-- Create procedures
DELIMITER $$ 
CREATE PROCEDURE InitializeItemsTable()
BEGIN
  SET @sql = CONCAT('CREATE TABLE ', 'Items', ' (
ItemId INT AUTO_INCREMENT NOT NULL,
Username VARCHAR(255) NOT NULL,
Title varchar(255) NOT NULL,
Category VARCHAR(255) NOT NULL,
Description TEXT,
Price DECIMAL (10,2) NOT NULL,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ItemId PRIMARY KEY(ItemId),
CONSTRAINT Fk_Items_Username FOREIGN KEY(Username) REFERENCES users(Username)
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE ReinitializeItemsTable()
BEGIN
  DROP TABLE Items;
  SET @sql = CONCAT('CREATE TABLE ', 'Items', ' (
ItemId INT AUTO_INCREMENT NOT NULL,
Username VARCHAR(255) NOT NULL,
Title varchar(255) NOT NULL,
Category VARCHAR(255) NOT NULL,
Description TEXT,
Price DECIMAL (10,2) NOT NULL,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ItemId PRIMARY KEY(ItemId),
CONSTRAINT Fk_Items_Username FOREIGN KEY(Username) REFERENCES users(Username)
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 


DELIMITER $$
CREATE PROCEDURE InitializeReviewsTable()
BEGIN
  SET @sql = CONCAT('CREATE TABLE ', 'Reviews', ' (
ReviewId BIGINT AUTO_INCREMENT NOT NULL,
ItemId INT NOT NULL,
Reviewer VARCHAR(255) NOT NULL,
Quality VARCHAR(255) NOT NULL,
Review TEXT,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ReviewId PRIMARY KEY(ReviewId),
CONSTRAINT Fk_Items_ItemId FOREIGN KEY(ItemId) REFERENCES items(ItemId),
CONSTRAINT Fk_Items_Reviewer FOREIGN KEY(Reviewer) REFERENCES users(Username)
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER // 
CREATE PROCEDURE ReinitializeReviewsTable()
BEGIN
  DROP TABLE reviews;
  SET @sql = CONCAT('CREATE TABLE ', 'Reviews', ' (
ReviewId BIGINT AUTO_INCREMENT NOT NULL,
ItemId INT NOT NULL,
Reviewer VARCHAR(255) NOT NULL,
Quality VARCHAR(255) NOT NULL,
Review TEXT,
DatePosted DATE NOT NULL,
CONSTRAINT Pk_Items_ReviewId PRIMARY KEY(ReviewId),
CONSTRAINT Fk_Items_ItemId FOREIGN KEY(ItemId) REFERENCES items(ItemId),
CONSTRAINT Fk_Items_Reviewer FOREIGN KEY(Reviewer) REFERENCES users(Username)
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$
CREATE PROCEDURE ReinitializeTables()
BEGIN
  DROP TABLE reviews;
  DROP TABLE items;
  CALL InitializeItemsTable;
  CALL InitializeReviewsTable;
END$$
DELIMITER ;

DELIMITER $$ 
CREATE PROCEDURE InitializeTables()
BEGIN
  CALL InitializeItemsTable;
  CALL InitializeReviewsTable;
END$$
DELIMITER ;

-- Drop procedures
DROP PROCEDURE ReinitializeItemsTable;
DROP PROCEDURE InitializeItemsTable;
DROP PROCEDURE InitializeReviewsTable;
DROP PROCEDURE ReinitializeReviewsTable;
DROP PROCEDURE ReinitializeTables;
DROP PROCEDURE InitializeTables;

-- Call procedures
CALL ReinitializeItemsTable;
CALL InitializeItemsTable;
CALL ReinitializeReviewsTable;
CALL InitializeReviewsTable;
CALL ReinitializeTables;
CALL InitializeTables;

-- Check if items table exist
SELECT count(*)
FROM information_schema.tables
WHERE table_schema = 'comp440database'
AND table_name = 'items';

-- Additional scripts
DROP TABLE reviews;
DROP TABLE items;

SELECT * FROM items;