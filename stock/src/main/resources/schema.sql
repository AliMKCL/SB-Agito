CREATE TABLE IF NOT EXISTS `stock` (
    `code` varchar(100) PRIMARY KEY,
    `quantity` int NOT NULL DEFAULT 0,
    `unit_sale_price` decimal(10, 2) NOT NULL,
    `threshold` int NOT NULL DEFAULT 0
);


CREATE TABLE IF NOT EXISTS `stock_entry` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `code` varchar(100) NOT NULL,
    `quantity_added` int NOT NULL,
    `unit_price` decimal(10, 2) NOT NULL,
    `total_price_paid` decimal(10, 2) NOT NULL,
    `vendor` varchar(255),
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`code`) REFERENCES `stock`(`code`) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS `stock_sale` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `code` varchar(100) NOT NULL,
    `payment` decimal(10, 2) NOT NULL,
    `amount` int NOT NULL,
    `buyer_name` varchar(255),
    `sold_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`code`) REFERENCES `stock`(`code`) ON DELETE CASCADE
    );
