-- ELMS Agentic AI Platform — MySQL Database Setup
-- Run this script to create the database

CREATE DATABASE IF NOT EXISTS elms_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE elms_db;

-- Users table (Spring Boot auto-creates from JPA, this is for reference)
-- Hibernate will run DDL automatically with spring.jpa.hibernate.ddl-auto=update

-- Insert demo data (only if tables are empty — Spring DataInitializer handles this)
-- But you can use this as reference or for manual inserts

-- Verify setup
SELECT 'ELMS Database initialized successfully!' AS status;
SELECT VERSION() AS mysql_version;
