-- Update email column to accept 50 characters
ALTER TABLE verification_codes
ALTER COLUMN email
TYPE VARCHAR(50);