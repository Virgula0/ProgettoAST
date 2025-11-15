DROP DATABASE IF EXISTS testdb;
CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS products;

CREATE TABLE users(
    id INT PRIMARY KEY,
    username varchar(255),
    password varchar(255)
);

CREATE TABLE products(
    id INT PRIMARY KEY,
    sender_id int,
    receivername varchar(255),
    receiverusername varchar(255),
    receiveraddress varchar(255),
    packagetype varchar(255),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);