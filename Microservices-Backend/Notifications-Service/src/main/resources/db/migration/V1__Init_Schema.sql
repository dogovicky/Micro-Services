-- Initialize schema and create first table
CREATE TABLE verification_codes (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(25) NOT NULL,
    code VARCHAR(25),
    is_used BOOLEAN NOT NULL,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    channel VARCHAR(25)
);