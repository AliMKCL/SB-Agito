CREATE TABLE IF NOT EXISTS `category` (
    `id` int AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(100) NOT NULL UNIQUE,
    `parent_id` int,
    FOREIGN KEY (`parent_id`) REFERENCES `category`(`id`)
);

CREATE TABLE IF NOT EXISTS `product` (
    `product_id` int AUTO_INCREMENT PRIMARY KEY,
    `code` varchar(100) NOT NULL,
    `name` varchar(100) NOT NULL,
    `category_id` int NOT NULL,
    `price` double NOT NULL,
    `comm_completed` boolean,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
);

