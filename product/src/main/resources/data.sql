-- SEED DATA

-- Level 1 (Roots)
INSERT INTO category (id, parent_id) VALUES (1, NULL);
INSERT INTO category_translation (category_id, language_code, name) VALUES (1, 'en', 'Electronics');
INSERT INTO category_translation (category_id, language_code, name) VALUES (1, 'tr', 'Elektronik');

-- Level 2
INSERT INTO category (id, parent_id) VALUES (2, 1);
INSERT INTO category_translation (category_id, language_code, name) VALUES (2, 'en', 'Computers');
INSERT INTO category_translation (category_id, language_code, name) VALUES (2, 'tr', 'Bilgisayarlar');

-- Level 3 (Leafs)
INSERT INTO category (id, parent_id) VALUES (3, 2);
INSERT INTO category_translation (category_id, language_code, name) VALUES (3, 'en', 'Laptops');
INSERT INTO category_translation (category_id, language_code, name) VALUES (3, 'tr', 'Dizüstü Bilgisayarlar');

INSERT INTO category (id, parent_id) VALUES (4, 2);
INSERT INTO category_translation (category_id, language_code, name) VALUES (4, 'en', 'Desktops');
INSERT INTO category_translation (category_id, language_code, name) VALUES (4, 'tr', 'Masaüstü Bilgisayarlar');
