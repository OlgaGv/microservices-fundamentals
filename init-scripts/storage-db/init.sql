-- Create the storage table
CREATE TABLE IF NOT EXISTS storages
(
    id           SERIAL PRIMARY KEY,
    storage_type VARCHAR(50)  UNIQUE NOT NULL ,
    bucket       VARCHAR(255) NOT NULL,
    path         VARCHAR(255) NOT NULL
);

-- Insert default storage entries
INSERT INTO storages (storage_type, bucket, path)
VALUES ('STAGING', 'staging-bucket', '/files')
ON CONFLICT (storage_type) DO NOTHING;

INSERT INTO storages (storage_type, bucket, path)
VALUES ('PERMANENT', 'permanent-bucket', '/files')
ON CONFLICT (storage_type) DO NOTHING;