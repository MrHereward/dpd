ALTER TABLE IF EXISTS FILE
DROP COLUMN FOLDERS;

----------------------
--      FOLDER      --
----------------------
CREATE TABLE FOLDER (
    name            VARCHAR(1024)   NOT NULL PRIMARY KEY,
    CONSTRAINT name_not_blank CHECK (name <> '')
);

CREATE TABLE FILE_FOLDERS (
    file_filename   VARCHAR(1024)   NOT NULL,
    folders_name VARCHAR(1024)  NOT NULL,
    FOREIGN KEY (file_filename) REFERENCES file(filename),
    FOREIGN KEY (folders_name) REFERENCES folder(name) ON DELETE CASCADE
);

CREATE TABLE FOLDER_SUB_FOLDERS (
    folder_name     VARCHAR(1024)   NOT NULL,
    sub_folders_name VARCHAR(1024)  NOT NULL UNIQUE,
    FOREIGN KEY (sub_folders_name) REFERENCES folder(name) ON DELETE CASCADE,
    FOREIGN KEY (folder_name) REFERENCES folder(name)
);