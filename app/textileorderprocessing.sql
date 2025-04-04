-- MySQL dump 10.13  Distrib 8.0.41, for Linux (x86_64)
--
-- Host: localhost    Database: textileorderprocessing
-- ------------------------------------------------------
-- Server version	8.0.41-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Bills`
--

DROP TABLE IF EXISTS `Bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Bills` (
  `BillNo` int NOT NULL AUTO_INCREMENT,
  `OrderNo` int NOT NULL,
  `billing_price` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`BillNo`),
  KEY `OrderNo` (`OrderNo`),
  CONSTRAINT `Bills_ibfk_1` FOREIGN KEY (`OrderNo`) REFERENCES `Orders` (`OrderNo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Bills`
--

LOCK TABLES `Bills` WRITE;
/*!40000 ALTER TABLE `Bills` DISABLE KEYS */;
INSERT INTO `Bills` VALUES (1,2,155.00,'Paid'),(2,5,80.00,'Unpaid'),(3,6,950.00,'Unpaid');
/*!40000 ALTER TABLE `Bills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Customers`
--

DROP TABLE IF EXISTS `Customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customers` (
  `CustomerId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) NOT NULL,
  `contact` varchar(20) NOT NULL,
  PRIMARY KEY (`CustomerId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customers`
--

LOCK TABLES `Customers` WRITE;
/*!40000 ALTER TABLE `Customers` DISABLE KEYS */;
INSERT INTO `Customers` VALUES (1,'John Doe','123 Main St, Springfield','555-1234'),(2,'Jane Smith','456 Elm St, Shelbyville','555-5678'),(3,'Acme Corp','789 Oak St, Capital City','555-9012');
/*!40000 ALTER TABLE `Customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Items`
--

DROP TABLE IF EXISTS `Items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Items` (
  `ItemNo` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `size` varchar(10) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `stock_level` int DEFAULT NULL,
  `reorder_threshold` int DEFAULT NULL,
  PRIMARY KEY (`ItemNo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Items`
--

LOCK TABLES `Items` WRITE;
/*!40000 ALTER TABLE `Items` DISABLE KEYS */;
INSERT INTO `Items` VALUES (1,'Widget A','Widgets','Small',10.00,0,20),(2,'Widget B','Widgets','Medium',15.00,150,30),(3,'Gadget X','Gadgets','Large',25.00,50,10);
/*!40000 ALTER TABLE `Items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `OrderNo` int NOT NULL AUTO_INCREMENT,
  `ItemNo` int NOT NULL,
  `CustomerId` int NOT NULL,
  `quantity` int NOT NULL,
  `transport_costs` decimal(10,2) NOT NULL,
  `item_costs` decimal(10,2) NOT NULL,
  `total_costs` decimal(10,2) NOT NULL,
  `order_date` date DEFAULT (curdate()),
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`OrderNo`),
  KEY `CustomerId` (`CustomerId`),
  KEY `ItemNo` (`ItemNo`),
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`CustomerId`) REFERENCES `Customers` (`CustomerId`),
  CONSTRAINT `Orders_ibfk_2` FOREIGN KEY (`ItemNo`) REFERENCES `Items` (`ItemNo`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES (1,1,1,5,2.50,10.00,52.50,'2023-10-04','Pending'),(2,2,2,10,5.00,15.00,155.00,'2023-10-05','Pending'),(3,3,3,2,3.00,25.00,53.00,'2023-10-06','Pending'),(5,1,2,3,50.00,10.00,80.00,'2025-04-02','Shipped'),(6,1,1,95,0.00,10.00,950.00,'2025-04-02','Shipped'),(7,1,1,1,0.00,10.00,10.00,'2025-04-02','Pending');
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Quotations`
--

DROP TABLE IF EXISTS `Quotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Quotations` (
  `QuotationNo` int NOT NULL AUTO_INCREMENT,
  `ItemNo` int NOT NULL,
  `CustomerId` int NOT NULL,
  `quantity` int NOT NULL,
  `transport_costs` decimal(10,2) NOT NULL,
  `item_costs` decimal(10,2) NOT NULL,
  `total_costs` decimal(10,2) NOT NULL,
  `date_created` date NOT NULL,
  PRIMARY KEY (`QuotationNo`),
  KEY `CustomerId` (`CustomerId`),
  KEY `ItemNo` (`ItemNo`),
  CONSTRAINT `Quotations_ibfk_1` FOREIGN KEY (`CustomerId`) REFERENCES `Customers` (`CustomerId`),
  CONSTRAINT `Quotations_ibfk_2` FOREIGN KEY (`ItemNo`) REFERENCES `Items` (`ItemNo`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Quotations`
--

LOCK TABLES `Quotations` WRITE;
/*!40000 ALTER TABLE `Quotations` DISABLE KEYS */;
INSERT INTO `Quotations` VALUES (2,2,2,20,10.00,15.00,310.00,'2025-04-02'),(3,3,3,5,7.50,25.00,132.50,'2025-04-02');
/*!40000 ALTER TABLE `Quotations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transport_Charges`
--

DROP TABLE IF EXISTS `Transport_Charges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transport_Charges` (
  `TransportId` int NOT NULL AUTO_INCREMENT,
  `transport_name` varchar(50) NOT NULL,
  `charge_per_distance` decimal(10,2) NOT NULL,
  PRIMARY KEY (`TransportId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transport_Charges`
--

LOCK TABLES `Transport_Charges` WRITE;
/*!40000 ALTER TABLE `Transport_Charges` DISABLE KEYS */;
INSERT INTO `Transport_Charges` VALUES (1,'Standard Delivery',1.00),(2,'Express Delivery',2.50),(3,'Overnight Delivery',5.00);
/*!40000 ALTER TABLE `Transport_Charges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Users` (
  `UserId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`UserId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Users`
--

LOCK TABLES `Users` WRITE;
/*!40000 ALTER TABLE `Users` DISABLE KEYS */;
INSERT INTO `Users` VALUES (1,'admin','hashed_password_1','admin'),(2,'manager','hashed_password_2','manager'),(3,'staff','hashed_password_3','staff');
/*!40000 ALTER TABLE `Users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-04 10:02:14
