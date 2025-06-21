-- Drop tables if they exist (optional, you can remove these if you want to keep existing data)
DROP TABLE IF EXISTS spot;
DROP TABLE IF EXISTS sector;
DROP TABLE IF EXISTS parking_session;
DROP TABLE IF EXISTS revenue;

-- Create sector table
CREATE TABLE IF NOT EXISTS sector (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    base_price DOUBLE PRECISION NOT NULL,
    max_capacity INTEGER NOT NULL,
    open_hour TIME NOT NULL,
    close_hour TIME NOT NULL,
    duration_limit_minutes INTEGER NOT NULL
    );

-- Create spot table
CREATE TABLE IF NOT EXISTS spot (
    id BIGSERIAL PRIMARY KEY,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    sector_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (sector_name) REFERENCES sector(name)
);

-- Create parking_session table
CREATE TABLE IF NOT EXISTS parking_session (
    id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(255) NOT NULL,
    entry_time TIMESTAMP,
    parked_time TIMESTAMP,
    exit_time TIMESTAMP,
    spot_lat DOUBLE PRECISION,
    spot_lng DOUBLE PRECISION,
    price DOUBLE PRECISION
);

-- Create revenue table
CREATE TABLE IF NOT EXISTS revenue (
    id BIGSERIAL PRIMARY KEY,
    sector VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);
