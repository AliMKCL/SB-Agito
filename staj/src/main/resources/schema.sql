CREATE TABLE IF NOT EXISTS `product` (
    `product_id` int AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(100) NOT NULL,
    `category` varchar(100) NOT NULL,
    `stock` int NOT NULL,
    `price` double NOT NULL
    );