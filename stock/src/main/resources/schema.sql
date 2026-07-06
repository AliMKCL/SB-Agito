CREATE TABLE IF NOT EXISTS `stock` (
    `code` varchar(100) PRIMARY KEY,
    `quantity` int NOT NULL DEFAULT 0
);