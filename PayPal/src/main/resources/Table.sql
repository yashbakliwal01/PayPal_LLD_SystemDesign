CREATE DATABASE paypal;
use paypal;

select * from users;
INSERT INTO users(id, email, name, phone) VALUES (1001, 'abcd@gmail.com', 'abcd', '123456790');
INSERT INTO users(id, email, name, phone) VALUES (101, 'yash@gmail.com', 'yash', '6547891230');
UPDATE users SET upi_id = 'yash01@okaxis.com' WHERE id = 101;

INSERT INTO users(id, email, name, phone, upi_id) VALUES (102, 'pqr@gmail.com', 'pqr', '4444444444', 'pqr@okaxis.com');
INSERT INTO users(id, email, name, phone, upi_id) VALUES (103, 'mno@gmail.com', 'mno', '1111111111', 'mno@okaxis.com');
INSERT INTO users(id, email, name, phone, upi_id) VALUES (104, 'xyz@gmail.com', 'xyz', '3698521470', 'xyz@okaxis.com');

select * from wallet;
ALTER TABLE wallet DROP COLUMN user_user_id;


select * from payee;
select * from users;
select * from wallet;
select * from transactions;
DROP TABLE wallet;

#WALLET
INSERT INTO wallet (wallet_id, balance, user_id) VALUES (1, 1000.00, 101);
INSERT INTO wallet (wallet_id, balance, user_id) VALUES (2, 1055.00, 102);
INSERT INTO wallet (wallet_id, balance, user_id) VALUES (3, 50000.00, 103);
INSERT INTO wallet (wallet_id, balance, user_id) VALUES (4, 5789.00, 104);

#PAYEE
INSERT INTO payee (id, account_identifier, name, upi_id, email, phone_number) VALUES (1004, '123800963741', 'mohan', 'mohan@okhdfc.com', 'mohan@gmail.com', '9638527410');
INSERT INTO payee (id, account_identifier, name, upi_id, email, phone_number) VALUES (1005, '673829110201', 'karan', 'karan@okhdfc.com', 'karan@gmail.com', '7777777777');
INSERT INTO payee (id, account_identifier, name, upi_id, email, phone_number) VALUES (1006, '09876543212', 'mayank', 'mayank@okhdfc.com', 'mayank@gmail.com', '7879464520');
INSERT INTO payee (id, account_identifier, name, upi_id, email, phone_number) VALUES (1002, '22222222222', 'Roshan', 'rosh_an@okaxis.com', 'roshan@gmail.com', '1111111111');


select wallet from users where id=101;
ALTER TABLE payee DROP COLUMN payee_id;

