----------------------
--       FILE       --
----------------------
CREATE TABLE FILE (
    filename        VARCHAR(1024)   NOT NULL PRIMARY KEY,
    size_in_bytes   INTEGER         NOT NULL CHECK (size_in_bytes >= 0),
    folders         VARCHAR(1024)[]
    CONSTRAINT filename_not_blank CHECK (filename <> '')
);