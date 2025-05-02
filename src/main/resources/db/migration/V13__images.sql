CREATE TABLE images (
    type VARCHAR(25) NOT NULL,
    title VARCHAR(64) NOT NULL,
    image BYTEA NOT NULL,
    mime_type VARCHAR(255) NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    PRIMARY KEY (type, title)
);