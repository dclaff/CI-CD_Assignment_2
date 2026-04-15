-- Seed customers
INSERT OR IGNORE INTO customers(id, name, email, phone, created_at)
    VALUES (1, 'Alice Johnson', 'alice@example.com', '555-0101', '2025-01-15');
INSERT OR IGNORE INTO customers(id, name, email, phone, created_at)
    VALUES (2, 'Bob Smith', 'bob@example.com', '555-0102', '2025-03-20');
INSERT OR IGNORE INTO customers(id, name, email, phone, created_at)
    VALUES (3, 'Carol Davis', 'carol@example.com', '555-0103', '2025-06-10');

-- Seed orders
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (1, 1, 'Laptop',     999.99, '2025-02-01', 'COMPLETED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (2, 1, 'Mouse',       29.99, '2025-03-15', 'COMPLETED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (3, 1, 'Keyboard',    79.99, '2025-04-10', 'PENDING');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (4, 2, 'Monitor',    349.99, '2025-04-20', 'COMPLETED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (5, 2, 'Headphones',  59.99, '2025-05-05', 'SHIPPED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (6, 3, 'Webcam',      89.99, '2025-07-01', 'PENDING');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (7, 1, 'USB Hub',     24.99, '2025-08-12', 'PENDING');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (8, 2, 'Desk Lamp',   44.99, '2025-09-03', 'COMPLETED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (9, 3, 'Chair Mat',   39.99, '2025-10-18', 'SHIPPED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (10, 3, 'Notebook',   12.99, '2025-11-25', 'PENDING');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (11, 1, 'HDMI Cable',  9.99, '2025-12-02', 'COMPLETED');
INSERT OR IGNORE INTO orders(id, customer_id, description, amount, order_date, status)
    VALUES (12, 2, 'Mousepad',   14.99, '2026-01-10', 'PENDING');
