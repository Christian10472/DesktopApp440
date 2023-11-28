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
Owner VARCHAR(255) NOT NULL, 
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
Owner VARCHAR(255) NOT NULL, 
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
CREATE PROCEDURE InitializeTestUserInput()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'users', ' (
Username,
Password,
FirstName,
LastName,
Email
)', 
' VALUES ', 
'(
''TestUser'',
''Comp440User2023'',
''Test'',
''User'',
''Test.User@my.csun.edu''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeTestUser2Input()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'users', ' (
Username,
Password,
FirstName,
LastName, 
Email
)', 
' VALUES ', 
'(
''TestUser2'',
''Comp440User2023'',
''Test'',
''User2'',
''Test.User2@my.csun.edu''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$


DELIMITER $$ 
CREATE PROCEDURE InitializeTestUser3Input()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'users', ' (
Username,
Password,
FirstName,
LastName, 
Email
)', 
' VALUES ', 
'(
''TestUser3'',
''Comp440User2023'',
''Test'',
''User3'',
''Test.User3@my.csun.edu''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$


DELIMITER $$ 
CREATE PROCEDURE InitializeTestUser4Input()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'users', ' (
Username,
Password,
FirstName,
LastName, 
Email
)', 
' VALUES ', 
'(
''TestUser4'',
''Comp440User2023'',
''Test'',
''User4'',
''Test.User4@my.csun.edu''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$


DELIMITER $$ 
CREATE PROCEDURE InitializeTestUser5Input()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'users', ' (
Username,
Password,
FirstName,
LastName, 
Email
)', 
' VALUES ', 
'(
''TestUser5'',
''Comp440User2023'',
''Test'',
''User5'',
''Test.User5@my.csun.edu''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeReviewInputs1()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'reviews', ' (
ItemId,
Reviewer,
Quality,
Review,
DatePosted,
Owner
)', 
' VALUES ', 
'(
1,
''TestUser2'',
''excellent'',
''Item1 is excellent'',
''2023-1-1'',
''TestUser1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeReviewInputs2()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'reviews', ' (
ItemId,
Reviewer,
Quality,
Review,
DatePosted,
Owner
)', 
' VALUES ', 
'(
2,
''TestUser2'',
''good'',
''Item2 is good'',
''2023-1-2'',
''TestUser1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeReviewInputs3()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'reviews', ' (
ItemId,
Reviewer,
Quality,
Review,
DatePosted,
Owner
)', 
' VALUES ', 
'(
3,
''TestUser2'',
''fair'',
''Item3 is fair'',
''2023-1-3'',
''TestUser1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeReviewInputs4()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'reviews', ' (
ItemId,
Reviewer,
Quality,
Review,
DatePosted,
Owner
)', 
' VALUES ', 
'(
4,
''TestUser2'',
''poor'',
''Item4 is poor'',
''2023-1-4'',
''TestUser1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ;

DELIMITER $$ 
CREATE PROCEDURE InitializeReviewInputs5()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'reviews', ' (
ItemId,
Reviewer,
Quality,
Review,
DatePosted,
Owner
)', 
' VALUES ', 
'(
5,
''TestUser2'',
''good'',
''Item5 is excellent again'',
''2023-1-5'',
''TestUser1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 
 

DELIMITER $$ 
CREATE PROCEDURE InitializeItemInputs1()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'Items', ' (
Username,
Title,
Category,
Description,
Price,
DatePosted
)', 
' VALUES ', 
'(
''TestUser'',
''Item1'',
''Category1'',
''Description for Item1'',
1000.00,
''2023-1-1''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeItemInputs2()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'Items', ' (
Username,
Title,
Category,
Description,
Price,
DatePosted
)', 
' VALUES ', 
'(
''TestUser'',
''Item2'',
''Category2'',
''Description for Item2'',
2000.00,
''2023-1-2''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeItemInputs3()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'Items', ' (
Username,
Title,
Category,
Description,
Price,
DatePosted
)', 
' VALUES ', 
'(
''TestUser'',
''Item3'',
''Category3'',
''Description for Item3'',
3000.00,
''2023-1-3''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeItemInputs4()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'Items', ' (
Username,
Title,
Category,
Description,
Price,
DatePosted
)', 
' VALUES ', 
'(
''TestUser'',
''Item4'',
''Category3, Category4'',
''Description for Item4'',
4000.00,
''2023-1-4''
)');
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END$$

DELIMITER ; 

DELIMITER $$ 
CREATE PROCEDURE InitializeItemInputs5()
BEGIN
  SET @sql = CONCAT('INSERT INTO ', 'Items', ' (
Username,
Title,
Category,
Description,
Price,
DatePosted
)', 
' VALUES ', 
'(
''TestUser'',
''Item5'',
''Category5, Category4'',
''Description for Item5'',
5000.00,
''2023-1-5''
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
  DELETE FROM users WHERE Username='TestUser';
  DELETE FROM users WHERE Username='TestUser2';
  DELETE FROM users WHERE Username='TestUser3';
  DELETE FROM users WHERE Username='TestUser4';
  DELETE FROM users WHERE Username='TestUser5';
  CALL InitializeTestUserInput;
  CALL InitializeTestUser2Input;
  CALL InitializeTestUser3Input;
  CALL InitializeTestUser4Input;
  CALL InitializeTestUser5Input;
  CALL InitializeItemsTable;
  CALL InitializeItemInputs1; 
  CALL InitializeItemInputs2;
  CALL InitializeItemInputs3;
  CALL InitializeItemInputs4;
  CALL InitializeItemInputs5;
  CALL InitializeReviewsTable;
  CALL InitializeReviewInputs1;
  CALL InitializeReviewInputs2;
  CALL InitializeReviewInputs3;
  CALL InitializeReviewInputs4;
  CALL InitializeReviewInputs5;
END$$
DELIMITER ;

DELIMITER $$ 
CREATE PROCEDURE InitializeTables()
BEGIN
  CALL InitializeTestUserInput;
  CALL InitializeTestUser2Input;
  CALL InitializeTestUser3Input;
  CALL InitializeTestUser4Input;
  CALL InitializeTestUser5Input;
  CALL InitializeItemsTable;
  CALL InitializeItemInputs1; 
  CALL InitializeItemInputs2;
  CALL InitializeItemInputs3;
  CALL InitializeItemInputs4;
  CALL InitializeItemInputs5;
  CALL InitializeReviewsTable;
  CALL InitializeReviewInputs1;
  CALL InitializeReviewInputs2;
  CALL InitializeReviewInputs3;
  CALL InitializeReviewInputs4;
  CALL InitializeReviewInputs5;
END$$
DELIMITER ;

-- Drop procedures
DROP PROCEDURE ReinitializeItemsTable;
DROP PROCEDURE InitializeItemsTable;
DROP PROCEDURE InitializeReviewsTable;
DROP PROCEDURE ReinitializeReviewsTable;
DROP PROCEDURE ReinitializeTables;
DROP PROCEDURE InitializeTables;
DROP PROCEDURE InitializeItemInputs1;
DROP PROCEDURE InitializeItemInputs2;
DROP PROCEDURE InitializeItemInputs3;
DROP PROCEDURE InitializeItemInputs4;
DROP PROCEDURE InitializeItemInputs5;
DROP PROCEDURE InitializeReviewInputs1;
DROP PROCEDURE InitializeReviewInputs2;
DROP PROCEDURE InitializeReviewInputs3;
DROP PROCEDURE InitializeReviewInputs4;
DROP PROCEDURE InitializeReviewInputs5;
DROP PROCEDURE InitializeTestUserInput; 
DROP PROCEDURE InitializeTestUser2Input;
DROP PROCEDURE InitializeTestUser3Input;
DROP PROCEDURE InitializeTestUser4Input;
DROP PROCEDURE InitializeTestUser5Input;


-- Call procedures
CALL ReinitializeItemsTable;
CALL InitializeItemsTable;
CALL ReinitializeReviewsTable;
CALL InitializeReviewsTable;
CALL InitializeItemInputs1; 
CALL InitializeItemInputs2;
CALL InitializeItemInputs3;
CALL InitializeItemInputs4;
CALL InitializeItemInputs5;
CALL InitializeReviewInputs1;
CALL InitializeReviewInputs2;
CALL InitializeReviewInputs3;
CALL InitializeReviewInputs4;
CALL InitializeReviewInputs5;
CALL ReinitializeTables;
CALL InitializeTables;
CALL InitializeTestUserInput;
CALL InitializeTestUser2Input;
CALL InitializeTestUser3Input;
CALL InitializeTestUser4Input;
CALL InitializeTestUser5Input;

-- Check if items table exist
SELECT count(*)
FROM information_schema.tables
WHERE table_schema = 'comp440database'
AND table_name = 'items';

-- Additional scripts
DROP TABLE reviews;
DROP TABLE items;

SELECT * FROM Users;

SELECT * FROM items;

SELECT * FROM reviews;

DELETE FROM users WHERE Username='TestUser';
DELETE FROM users WHERE Username='TestUser2';
DELETE FROM users WHERE Username='TestUser3';
DELETE FROM users WHERE Username='TestUser4';
DELETE FROM users WHERE Username='TestUser5';