-- Active: 1742379510396@@127.0.0.1@3306@textileorderprocessing
-- ==================================================
-- Create Users Table
-- ==================================================
CREATE TABLE Users (
  UserId INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

-- ==================================================
-- Create Customers Table
-- ==================================================
CREATE TABLE Customers (
  CustomerId INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(255) NOT NULL,
  contact VARCHAR(20) NOT NULL
);

-- ==================================================
-- Create Items Table
-- ==================================================
CREATE TABLE Items (
  ItemNo INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  category VARCHAR(50) NOT NULL,
  size VARCHAR(10),
  cost_price DECIMAL(10,2),
  stock_level INT,
  reorder_threshold INT
);

-- ==================================================
-- Create Quotations Table
-- ==================================================
CREATE TABLE Quotations (
    QuotationNo INT PRIMARY KEY AUTO_INCREMENT,
    ItemNo INT NOT NULL,
    CustomerId INT NOT NULL,
    quantity INT NOT NULL,
    transport_costs DECIMAL(10, 2) NOT NULL,
    item_costs DECIMAL(10, 2) NOT NULL,
    total_costs DECIMAL(10,2) NOT NULL,
    date_created DATE NOT NULL,
    FOREIGN KEY (CustomerId) REFERENCES Customers(CustomerId),
    FOREIGN KEY (ItemNo) REFERENCES Items(ItemNo)
);


-- ==================================================
-- Create Orders Table
-- ==================================================
CREATE TABLE Orders (
    OrderNo INT PRIMARY KEY AUTO_INCREMENT,
    ItemNo INT NOT NULL,
    CustomerId INT NOT NULL,
    quantity INT NOT NULL,
    transport_costs DECIMAL(10, 2) NOT NULL,
    item_costs DECIMAL(10, 2) NOT NULL,
    total_costs DECIMAL(10,2) NOT NULL,
    order_date DATE DEFAULT (CURRENT_DATE()),
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (CustomerId) REFERENCES Customers(CustomerId),
    FOREIGN KEY (ItemNo) REFERENCES Items(ItemNo)
);

-- ==================================================
-- Create Bill Table
-- ==================================================
CREATE TABLE Bills(
  BillNo INT PRIMARY KEY AUTO_INCREMENT,
  OrderNo INT NOT NULL,
  billing_price DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  FOREIGN KEY(OrderNo) REFERENCES Orders(OrderNo)
);

-- ==================================================
-- Create Transport_Charges Table
-- ==================================================
CREATE TABLE Transport_Charges (
  TransportId INT PRIMARY KEY AUTO_INCREMENT,
  transport_name VARCHAR(50) NOT NULL,
  charge_per_distance DECIMAL(10,2) NOT NULL
);

-- ==================================================
-- Insert Statements
-- ==================================================
INSERT INTO Users (username, password_hash, role) VALUES
('admin', 'hashed_password_1', 'admin'),
('manager', 'hashed_password_2', 'manager'),
('staff', 'hashed_password_3', 'staff');

INSERT INTO Customers (name, address, contact) VALUES
('John Doe', '123 Main St, Springfield', '555-1234'),
('Jane Smith', '456 Elm St, Shelbyville', '555-5678'),
('Acme Corp', '789 Oak St, Capital City', '555-9012');

INSERT INTO Items (name, category, size, cost_price, stock_level, reorder_threshold) VALUES
('Widget A', 'Widgets', 'Small', 10.00, 100, 20),
('Widget B', 'Widgets', 'Medium', 15.00, 150, 30),
('Gadget X', 'Gadgets', 'Large', 25.00, 50, 10);

INSERT INTO Quotations (ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, date_created) VALUES
(1, 1, 10, 5.00, 100.00, 105.00, '2023-10-01'),
(2, 2, 20, 10.00, 300.00, 310.00, '2023-10-02'),
(3, 3, 5, 7.50, 125.00, 132.50, '2023-10-03');

INSERT INTO Orders (ItemNo, CustomerId, quantity, transport_costs, item_costs, total_costs, order_date, status) VALUES
(1, 1, 5, 2.50, 50.00, 52.50, '2023-10-04', 'Pending'),
(2, 2, 10, 5.00, 150.00, 155.00, '2023-10-05', 'Shipped'),
(3, 3, 2, 3.00, 50.00, 53.00, '2023-10-06', 'Shipped');

INSERT INTO Transport_Charges (transport_name, charge_per_distance) VALUES
('Standard Delivery', 1.00),
('Express Delivery', 2.50),
('Overnight Delivery', 5.00);

INSERT INTO Bills(BillNo, OrderNo, billing_price, status) VALUES
(1, 2, 155, "Paid");