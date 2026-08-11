-- Insert Stocks
INSERT INTO `stock` (`code`, `quantity`, `unit_sale_price`, `threshold`) VALUES ('0001', 5, 20.00, 6);
INSERT INTO `stock` (`code`, `quantity`, `unit_sale_price`, `threshold`) VALUES ('0002', 2, 5.00, 1);
INSERT INTO `stock` (`code`, `quantity`, `unit_sale_price`, `threshold`) VALUES ('0003', 3, 10.00, 4);

-- Insert Stock Entries (Bulk imports at different times / different unit prices)
-- Product 1 (Added total = 20, cost total = 160. Weighted average purchase = 8.00)
INSERT INTO `stock_entry` (`id`, `code`, `quantity_added`, `unit_price`, `total_price_paid`, `vendor`, `created_at`)
VALUES (1, '0001', 10, 10.00, 100.00, 'Vendor A', CURRENT_TIMESTAMP);
INSERT INTO `stock_entry` (`id`, `code`, `quantity_added`, `unit_price`, `total_price_paid`, `vendor`, `created_at`)
VALUES (2, '0001', 10, 6.00, 60.00, 'Vendor B', CURRENT_TIMESTAMP);

-- Product 2 (Added total = 5, cost total = 50. Average purchase = 10.00)
INSERT INTO `stock_entry` (`id`, `code`, `quantity_added`, `unit_price`, `total_price_paid`, `vendor`, `created_at`)
VALUES (3, '0002', 5, 10.00, 50.00, 'Vendor C', CURRENT_TIMESTAMP);

-- Product 3 (Added total = 5, cost total = 25. Average purchase = 5.00)
INSERT INTO `stock_entry` (`id`, `code`, `quantity_added`, `unit_price`, `total_price_paid`, `vendor`, `created_at`)
VALUES (4, '0003', 5, 5.00, 25.00, 'Vendor A', CURRENT_TIMESTAMP);


-- Insert Stock Sales (Old ones sold)
-- Product 1 (Sold total = 15, revenue total = 300)
INSERT INTO `stock_sale` (`id`, `code`, `payment`, `amount`, `buyer_name`, `sold_at`)
VALUES (1, '0001', 300.00, 15, 'Buyer A', CURRENT_TIMESTAMP);

-- Product 2 (Sold total = 3, revenue total = 15)
INSERT INTO `stock_sale` (`id`, `code`, `payment`, `amount`, `buyer_name`, `sold_at`)
VALUES (2, '0002', 15.00, 3, 'Buyer C', CURRENT_TIMESTAMP);

-- Product 3 (Sold total = 2, revenue total = 20)
INSERT INTO `stock_sale` (`id`, `code`, `payment`, `amount`, `buyer_name`, `sold_at`)
VALUES (3, '0003', 20.00, 2, 'Buyer A', CURRENT_TIMESTAMP);
