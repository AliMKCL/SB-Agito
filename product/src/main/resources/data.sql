-- SEED DATA

-- Level 1 (Roots)
INSERT INTO category (id, name, parent_id) VALUES (1, 'Electronics', NULL);

-- Level 2
INSERT INTO category (id, name, parent_id) VALUES (2, 'Computers', 1);

-- Level 3 (Leafs)
INSERT INTO category (id, name, parent_id) VALUES (3, 'Laptops', 2);
INSERT INTO category (id, name, parent_id) VALUES (4, 'Desktops', 2);
