CREATE SCHEMA IF NOT EXISTS shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_cart.carts (
    shopping_cart_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    state VARCHAR(20) NOT NULL CHECK (state IN ('ACTIVE', 'DEACTIVATED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_carts_username_state ON shopping_cart.carts(username, state);

CREATE TABLE IF NOT EXISTS shopping_cart.cart_items (
    id UUID PRIMARY KEY,
    shopping_cart_id UUID NOT NULL REFERENCES shopping_cart.carts (shopping_cart_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK ( quantity >= 0 ),
    CONSTRAINT unique_cart_product UNIQUE (shopping_cart_id, product_id)
);