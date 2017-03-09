CREATE TABLE egeis_serviceHistory (
	id BIGINT NOT NULL,
	serviceInfo CHARACTER VARYING(250) NOT NULL,
	serviceFrom DATE NOT NULL,
	remarks CHARACTER VARYING(250),
	orderNo CHARACTER VARYING(250),
	documents CHARACTER VARYING(250),
	createdBy BIGINT NOT NULL,
	createdDate DATE NOT NULL,
	lastModifiedBy BIGINT,
	lastModifiedDate DATE,
	tenantId CHARACTER VARYING(250) NOT NULL,

	CONSTRAINT pk_egeis_serviceHistory PRIMARY KEY (Id)
);

CREATE SEQUENCE seq_egeis_serviceHistory
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;