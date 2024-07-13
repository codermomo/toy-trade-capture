-- Create database and user
DO
$create_if_not_exist$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'trade_capture') THEN
        CREATE DATABASE trade_capture;
    END IF;

    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'mysu') THEN
        CREATE USER mysu WITH PASSWORD 'mypassword';
    END IF;

    GRANT ALL PRIVILEGES ON DATABASE trade_capture TO mysu;
END
$create_if_not_exist$;

\c trade_capture;
-- Create enum types and implicit cast from varchar to enum
CREATE TYPE INSTRUMENT_TYPE_ENUM AS ENUM ('STOCK', 'BOND', 'FX');
CREATE OR REPLACE FUNCTION varchar_to_instrument_type_enum(VARCHAR) RETURNS INSTRUMENT_TYPE_ENUM AS
$$
BEGIN
    CASE $1
        WHEN 'STOCK' THEN RETURN 'STOCK'::INSTRUMENT_TYPE_ENUM;
        WHEN 'BOND' THEN RETURN 'BOND'::INSTRUMENT_TYPE_ENUM;
        WHEN 'FX' THEN RETURN 'FX'::INSTRUMENT_TYPE_ENUM;
        ELSE RAISE EXCEPTION 'Invalid value for INSTRUMENT_TYPE_ENUM: %', $1;
    END CASE;
END;
$$ language plpgsql;
CREATE CAST (VARCHAR AS INSTRUMENT_TYPE_ENUM) WITH FUNCTION varchar_to_instrument_type_enum(VARCHAR) AS IMPLICIT;

CREATE TYPE TRADE_STATUS_ENUM AS ENUM ('PENDING', 'FILLED', 'CANCELLED');
CREATE OR REPLACE FUNCTION varchar_to_trade_status_enum(VARCHAR) RETURNS TRADE_STATUS_ENUM AS
$$
BEGIN
    CASE $1
        WHEN 'PENDING' THEN RETURN 'PENDING'::TRADE_STATUS_ENUM;
        WHEN 'FILLED' THEN RETURN 'FILLED'::TRADE_STATUS_ENUM;
        WHEN 'CANCELLED' THEN RETURN 'CANCELLED'::TRADE_STATUS_ENUM;
        ELSE RAISE EXCEPTION 'Invalid value for TRADE_STATUS_ENUM: %', $1;
    END CASE;
END;
$$ language plpgsql;
CREATE CAST (VARCHAR AS TRADE_STATUS_ENUM) WITH FUNCTION varchar_to_trade_status_enum(VARCHAR) AS IMPLICIT;

CREATE TYPE CURRENCY_ENUM AS ENUM ('USD', 'JPY');
CREATE OR REPLACE FUNCTION varchar_to_currency_enum(VARCHAR) RETURNS CURRENCY_ENUM AS
$$
BEGIN
    CASE $1
        WHEN 'USD' THEN RETURN 'USD'::CURRENCY_ENUM;
        WHEN 'JPY' THEN RETURN 'JPY'::CURRENCY_ENUM;
        ELSE RAISE EXCEPTION 'Invalid value for CURRENCY_ENUM: %', $1;
    END CASE;
END;
$$ language plpgsql;
CREATE CAST (VARCHAR AS CURRENCY_ENUM) WITH FUNCTION varchar_to_currency_enum(VARCHAR) AS IMPLICIT;

-- Consider adding new tables and enforce foreign key constraint in the future
CREATE TABLE IF NOT EXISTS trade (
    trade_id UUID PRIMARY KEY,
    trade_date_time TIMESTAMP NOT NULL,
    instrument_id VARCHAR(10) NOT NULL,
    instrument_type INSTRUMENT_TYPE_ENUM NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(8) NOT NULL,
    base_currency CURRENCY_ENUM NOT NULL,
    source_id VARCHAR(10) NOT NULL,
    book_id VARCHAR(10) NOT NULL,
    counterparty_id VARCHAR(10) NOT NULL,
    trade_status TRADE_STATUS_ENUM NOT NULL
);