CREATE TABLE IF NOT EXISTS `category` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `parent_id` int,
    FOREIGN KEY (`parent_id`) REFERENCES `category`(`id`)
);

CREATE TABLE IF NOT EXISTS `category_translation` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `category_id` int NOT NULL,
    `language_code` varchar(10) NOT NULL,
    `name` varchar(100) NOT NULL,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_category_lang` (`category_id`, `language_code`)
);

CREATE TABLE IF NOT EXISTS `product` (
    `product_id` int AUTO_INCREMENT PRIMARY KEY,
    `code` varchar(100) NOT NULL UNIQUE,
    `category_id` int NOT NULL,
    `price` double NOT NULL,
    `comm_completed` boolean,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
);

CREATE TABLE IF NOT EXISTS `product_translation` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `product_id` int NOT NULL,
    `language_code` varchar(10) NOT NULL,
    `name` varchar(100) NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`product_id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_product_lang` (`product_id`, `language_code`)
);
