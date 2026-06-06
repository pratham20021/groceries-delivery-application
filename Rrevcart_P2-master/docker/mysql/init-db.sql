-- Create databases for all microservices
CREATE DATABASE IF NOT EXISTS revcart_auth;
CREATE DATABASE IF NOT EXISTS revcart_users;
CREATE DATABASE IF NOT EXISTS revcart_products;
CREATE DATABASE IF NOT EXISTS revcart_orders;
CREATE DATABASE IF NOT EXISTS revcart_payments;
CREATE DATABASE IF NOT EXISTS revcart_delivery;
CREATE DATABASE IF NOT EXISTS revcart_admin;

-- Grant privileges to root from any host
GRANT ALL PRIVILEGES ON revcart_auth.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_users.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_products.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_orders.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_payments.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_delivery.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revcart_admin.* TO 'root'@'%';

FLUSH PRIVILEGES;
