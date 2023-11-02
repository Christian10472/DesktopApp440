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

-- Create procedures
DELIMITER // 
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
END; 
DELIMITER; 

DELIMITER // 
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
END;  

-- Drop procedures
DELIMITER //
DROP PROCEDURE ReinitializeItemsTable;
DROP PROCEDURE InitializeItemsTable;

-- Call procedures
CALL ReinitializeItemsTable;
CALL InitializeItemsTable;

-- Check if items table exist
SELECT count(*)
FROM information_schema.tables
WHERE table_schema = 'comp440database'
AND table_name = 'items';
