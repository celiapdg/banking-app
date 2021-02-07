DROP SCHEMA IF EXISTS banking;
CREATE SCHEMA banking;
USE banking;

CREATE TABLE `user` (
  id BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255),
  `password` VARCHAR(20),
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
  id BIGINT NOT NULL,
  balance_amount DECIMAL(19,2),
  balance_currency VARCHAR(255),
  penalty_fee_amount DECIMAL(19,2),
  penalty_fee_currency VARCHAR(255),
  primary_owner_id BIGINT NOT NULL,
  secondary_owner_id BIGINT,
  PRIMARY KEY (id),
  FOREIGN KEY (secondary_owner_id) REFERENCES account_holder (id),
  FOREIGN KEY (primary_owner_id) REFERENCES account_holder (id)
);


CREATE TABLE student_checking (
  id BIGINT NOT NULL,
  secret_key VARCHAR(4),
  `status` VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);


CREATE TABLE checking (
  id BIGINT NOT NULL,
  below_minimum_balance BOOLEAN NOT NULL,
  last_maintenance_date DATE NOT NULL,
  monthly_maintenance_fee_amount DECIMAL(19,2),
  monthly_maintenance_fee_currency VARCHAR(255),
  minimum_balance_amount DECIMAL(19,2),
  minimum_balance_currency VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES student_checking (id)
);


CREATE TABLE credit_card (
  id bigint NOT NULL,
  credit_limit_amount DECIMAL(19,2),
  credit_limit_currency VARCHAR(255),
  interest_rate DECIMAL(19,2) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);


CREATE TABLE savings (
  id BIGINT NOT NULL,
  below_minimum_balance BOOLEAN NOT NULL,
  interest_rate DECIMAL(19,2) NOT NULL,
  last_interest_date DATE NOT NULL,
  monthly_maintenance_fee_amount DECIMAL(19,2),
  monthly_maintenance_fee_currency VARCHAR(255),
  secret_key VARCHAR(4),
  `status` VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES `account` (id)
);
