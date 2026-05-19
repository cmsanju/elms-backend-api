DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),

    role VARCHAR(50),

    skills VARCHAR(1000),
    bio VARCHAR(2000),

    phone VARCHAR(50),
    profile_image VARCHAR(500),

    active BOOLEAN,

    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
