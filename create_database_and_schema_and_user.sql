CREATE ROLE cstorage LOGIN PASSWORD 'cstorage';

CREATE DATABASE cloudstorage;

GRANT ALL PRIVILEGES ON DATABASE cloudstorage TO cstorage;

CREATE SCHEMA cloudstorage.cs AUTHORIZATION cstorage;
