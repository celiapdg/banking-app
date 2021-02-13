DROP SCHEMA IF EXISTS banking;
CREATE SCHEMA banking;
USE banking;

CREATE TABLE `user` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255),
  `password` VARCHAR(255),
  username VARCHAR(36) UNIQUE,
  PRIMARY KEY (id)
);


CREATE TABLE `role` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255),
  user_id BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES `user` (id)
);


CREATE TABLE `admin` (
  id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `user`(id)
);


CREATE TABLE account_holder (
  id BIGINT NOT NULL,
  birth DATE NOT NULL,
  mailing_city VARCHAR(255),
  mailing_country VARCHAR(255),
  mailing_postal_code INT,
  mailing_street VARCHAR(255),
  primary_city VARCHAR(255),
  primary_country VARCHAR(255),
  primary_postal_code INT,
  primary_street VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `user` (id)
);


CREATE TABLE third_party (
  id BIGINT NOT NULL AUTO_INCREMENT,
  hash_key VARCHAR(6) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE `account` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  balance_amount DECIMAL(19,2),
  balance_currency VARCHAR(255),
  primary_owner_id BIGINT NOT NULL,
  secondary_owner_id BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (secondary_owner_id) REFERENCES account_holder (id),
  FOREIGN KEY (primary_owner_id) REFERENCES account_holder (id)
);


CREATE TABLE student_checking (
  id BIGINT NOT NULL AUTO_INCREMENT,
  secret_key VARCHAR(255),
  `status` VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);


CREATE TABLE checking (
  id BIGINT NOT NULL AUTO_INCREMENT,
  secret_key VARCHAR(255),
  `status` VARCHAR(255),
  below_minimum_balance BOOLEAN NOT NULL,
  last_maintenance_date DATE NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);


CREATE TABLE credit_card (
  id bigint NOT NULL AUTO_INCREMENT,
  credit_limit_amount DECIMAL(19,2),
  credit_limit_currency VARCHAR(255),
  interest_rate DECIMAL(19,2) NOT NULL,
  last_interest_date DATE NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);


CREATE TABLE savings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  below_minimum_balance BOOLEAN NOT NULL,
  interest_rate DECIMAL(19,2) NOT NULL,
  last_interest_date DATE NOT NULL,
  monthly_maintenance_fee_amount DECIMAL(19,2),
  monthly_maintenance_fee_currency VARCHAR(255),
  secret_key VARCHAR(255),
  `status` VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);

CREATE TABLE `transaction` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    origin_id BIGINT,
    destination_id BIGINT,
    transaction_amount DECIMAL(19,2) NOT NULL,
    transaction_currency VARCHAR(255),
    concept VARCHAR(255),
    transaction_date_time DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (origin_id) REFERENCES `account` (id),
    FOREIGN KEY (destination_id) REFERENCES `account` (id)
);

SELECT MAX(t.sum) FROM (SELECT DATE(transaction_date_time) AS transaction_date, SUM(transaction_amount) AS sum FROM transaction WHERE origin_id = 171 GROUP BY transaction_date) AS t;
    
SELECT transaction_date_time, SUM(transaction_amount) AS sum FROM transaction WHERE origin_id = 171 AND transaction_date_time >= NOW() - INTERVAL 1 DAY;

SELECT transaction_date_time, 
SUM(transaction_amount) AS sum 
FROM transaction 
WHERE origin_id = 171 AND transaction_date_time >= NOW() - INTERVAL 1 DAY
GROUP BY transaction_date_time;