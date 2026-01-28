-- liquibase formatted sql
-- changeset Sameh.Adel:1-initial-schema

-- Create initial database schema
CREATE TABLE IF NOT EXISTS databasechangeloglock (
    id int not null primary key,
    locked boolean not null,
    lockgranted timestamp,
    lockedby varchar(255),
    lockcomment varchar(255)
);

-- Create a sample table for testing
CREATE TABLE IF NOT EXISTS sample_table (
    id serial primary key,
    name varchar(100) not null,
    created_at timestamp default current_timestamp
);
