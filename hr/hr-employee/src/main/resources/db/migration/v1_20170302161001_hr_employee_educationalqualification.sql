CREATE TABLE egeis_educationalQualification (
	id BIGINT NOT NULL,
	qualification CHARACTER VARYING(250) NOT NULL,
	majorSubject CHARACTER VARYING(250),
	yearOfPassing INTEGER NOT NULL,
	university CHARACTER VARYING(250),
	documents CHARACTER VARYING(250),
	createdBy BIGINT NOT NULL,
	createdDate DATE NOT NULL,
	lastModifiedBy BIGINT,
	lastModifiedDate DATE,
	tenantId CHARACTER VARYING(250) NOT NULL,

	CONSTRAINT pk_egeis_educationalQualification PRIMARY KEY (Id)
);

CREATE SEQUENCE seq_egeis_educationalQualification
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;