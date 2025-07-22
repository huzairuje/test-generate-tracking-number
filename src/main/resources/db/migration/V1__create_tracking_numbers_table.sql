-- V3__create_tracking_number_table.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE tracking_number (
    id BIGSERIAL PRIMARY KEY,  -- use BIGSERIAL for auto-incrementing IDs
    tracking_number VARCHAR(255) UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    origin_country_id VARCHAR(2) NOT NULL,
    destination_country_id VARCHAR(2) NOT NULL,
    weight NUMERIC(6, 3) CHECK (weight <= 999.999),
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_slug VARCHAR(255) NOT NULL
);
CREATE SEQUENCE tracking_number_seq START WITH 1 INCREMENT BY 1;
CREATE UNIQUE INDEX uniq_tracking_num ON tracking_number (tracking_number);