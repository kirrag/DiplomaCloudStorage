psql -h localhost -d postgres -U postgres -W

CREATE ROLE csadmin LOGIN PASSWORD 'Welcome@1';

CREATE DATABASE cloudstorage;

GRANT ALL PRIVILEGES ON DATABASE cloudstorage TO csadmin;

\q

psql -h localhost -d cloudstorage -U csadmin -W


CREATE SCHEMA cs AUTHORIZATION csadmin;

ALTER USER csadmin SET search_path TO cs;

INSERT INTO user_entity(id,username,password) VALUES(1, 'user1', '$2y$10$hbFDQnt257hsZlqLkkaFd.xdVWd7xpm.1XX2LPgULOhirXyilkvOK');

INSERT INTO user_entity(id,username,password) VALUES(2, 'ten@otux.ru', '$2y$10$hbFDQnt257hsZlqLkkaFd.xdVWd7xpm.1XX2LPgULOhirXyilkvOK');



