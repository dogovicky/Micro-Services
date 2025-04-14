-- Add new Boolean columns to users table
ALTER TABLE users
ADD COLUMN is_enabled Boolean NOT NULL DEFAULT TRUE,
ADD COLUMN is_credentials_non_expired Boolean NOT NULL DEFAULT TRUE,
ADD COLUMN is_account_non_locked Boolean NOT NULL DEFAULT TRUE,
ADD COLUMN is_account_non_expired Boolean NOT NULL DEFAULT TRUE,
ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL;


-- Remove role column from user_profiles table
ALTER TABLE user_profiles
DROP COLUMN role;

-- Create roles table
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_type VARCHAR(50) UNIQUE NOT NULL
);

-- Create user_roles join table for many-to-many relationship
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);