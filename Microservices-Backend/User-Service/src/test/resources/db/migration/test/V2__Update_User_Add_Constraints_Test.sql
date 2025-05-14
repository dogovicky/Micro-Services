-- Add new Boolean columns to users table
ALTER TABLE users ADD COLUMN is_enabled BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users ADD COLUMN is_credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users ADD COLUMN is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users ADD COLUMN is_account_non_expired BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE users ADD COLUMN created_at TIMESTAMP NOT NULL;
