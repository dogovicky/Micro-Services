
-- Create users table
CREATE TABLE users (
 user_id UUID PRIMARY KEY,
 full_name VARCHAR(255) NOT NULL UNIQUE,
 email VARCHAR(255) NOT NULL UNIQUE,
 phone_number VARCHAR(20) NOT NULL UNIQUE,
 password VARCHAR(255) NOT NULL
);

-- Create enum type for role
CREATE TYPE role_type AS ENUM ('USER', 'ADMIN');

-- Create user_profiles table
CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY,
    nationality VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    identification_number VARCHAR(100) NOT NULL UNIQUE,

    -- Embedded address fields
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,

    role role_type NOT NULL DEFAULT 'USER',


    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);