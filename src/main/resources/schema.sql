DROP TABLE IF EXISTS TB_USER;

DROP TABLE IF EXISTS TB_USER_REV;

DROP TABLE IF EXISTS REV_INFO;

CREATE TABLE TB_USER (
    ID VARCHAR_IGNORECASE(100) PRIMARY KEY,
    LOGIN VARCHAR_IGNORECASE(100) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
	SALARY NUMBER(11,2) NOT NULL,
	START_DATE DATE NOT NULL,
	VERSION NUMBER(9),
	CREATED_BY VARCHAR(255),
	CREATED_DATE TIMESTAMP,
	UPDATED_BY VARCHAR(255),
	UPDATED_DATE TIMESTAMP
);


ALTER TABLE TB_USER ADD UNIQUE (LOGIN);

create table TB_USER_REV (
		REVISION_ID integer NOT NULL,
		REVISION_TYPE tinyint NOT NULL,
        ID VARCHAR_IGNORECASE(100) NOT NULL,
	    LOGIN VARCHAR_IGNORECASE(100) NOT NULL,
	    NAME VARCHAR(255) NOT NULL,
		SALARY NUMBER(11,2) NOT NULL,
		START_DATE DATE NOT NULL,
		CREATED_BY VARCHAR(255),
		CREATED_DATE TIMESTAMP,
		UPDATED_BY VARCHAR(255),
		UPDATED_DATE TIMESTAMP,
		primary key (REVISION_ID, ID)
);

create table REV_INFO (
   	REVISION_ID integer generated by default as identity,
    REV_TIMESTAMP bigint,
    primary key (REVISION_ID)
);