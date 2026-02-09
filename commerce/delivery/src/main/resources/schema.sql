CREATE SCHEMA IF NOT EXISTS delivery_service;

CREATE TABLE IF NOT EXISTS delivery_service.addresses (
    address_id uuid PRIMARY KEY,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    house VARCHAR(255),
    flat VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS delivery_service.deliveries (
    delivery_id uuid PRIMARY KEY,
    order_id uuid NOT NULL,
    total_weight DOUBLE PRECISION NOT NULL,
    total_volume DOUBLE PRECISION NOT NULL,
    fragile BOOLEAN NOT NULL,
    delivery_state VARCHAR(20) NOT NULL,
    from_address_id uuid REFERENCES delivery_service.addresses(address_id) ON DELETE CASCADE,
    to_address_id uuid REFERENCES delivery_service.addresses(address_id) ON DELETE CASCADE
);