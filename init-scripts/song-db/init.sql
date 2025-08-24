-- Create the song table
CREATE TABLE IF NOT EXISTS song
(
    id       INTEGER      NOT NULL PRIMARY KEY,
    album    VARCHAR(255) NOT NULL,
    artist   VARCHAR(255) NOT NULL,
    duration VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    year     VARCHAR(255) NOT NULL
);