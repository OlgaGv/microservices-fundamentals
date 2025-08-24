-- Create the resource table
CREATE TABLE IF NOT EXISTS resource
(
    id               SERIAL PRIMARY KEY,
    resource_content BYTEA NOT NULL
);