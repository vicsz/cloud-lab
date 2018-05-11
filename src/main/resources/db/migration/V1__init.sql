CREATE TABLE person (
	id int NOT NULL AUTO_INCREMENT,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	PRIMARY KEY (ID)
);

INSERT INTO person (first_name, last_name) values ('Peter', 'Parker');