-- Add role types in roles table

ALTER TABLE user_profiles
DROP COLUMN role;


CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_type VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);

INSERT INTO roles (role_type) VALUES ('USER'), ('ADMIN');
