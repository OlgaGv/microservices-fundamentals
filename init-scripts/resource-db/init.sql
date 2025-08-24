-- Create the resource table
CREATE TABLE resource
(
    id          SERIAL PRIMARY KEY,
    s3_location VARCHAR(255) NOT NULL
);