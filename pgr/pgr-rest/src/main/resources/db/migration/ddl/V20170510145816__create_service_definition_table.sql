CREATE TABLE service_definition (
    code character varying(20),
    tenantid character varying(256) NOT NULL,
    version bigint DEFAULT 0
);

ALTER TABLE service_definition ADD CONSTRAINT service_definition_pkey
PRIMARY KEY(code, tenantid);

CREATE TABLE attribute_definition (
    code VARCHAR(50) NOT NULL,
    variable CHAR NOT NULL DEFAULT 'N',
    datatype VARCHAR(100) NOT NULL,
    required CHAR NOT NULL DEFAULT 'N',
    datatypedescription VARCHAR(200),
    ordernum INTEGER,
    description VARCHAR(300),
    service_code VARCHAR (256) NOT NULL,
    tenantid character varying(256) NOT NULL
);

ALTER TABLE attribute_definition ADD CONSTRAINT attribute_definition_pkey
PRIMARY KEY(service_code, code, tenantid);

ALTER TABLE attribute_definition ADD CONSTRAINT attribute_definition_fkey
FOREIGN KEY (service_code, tenantid) REFERENCES service_definition (code, tenantid);

CREATE TABLE values_definition (
    service_code VARCHAR (256) NOT NULL,
    attribute_code VARCHAR(50) NOT NULL,
    key VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    tenantid character varying(256) NOT NULL
);

ALTER TABLE values_definition ADD CONSTRAINT values_definition_pkey
PRIMARY KEY(attribute_code, key, tenantid);

ALTER TABLE values_definition ADD CONSTRAINT values_definition_attribute_definition_fkey
FOREIGN KEY (service_code, attribute_code, tenantid) REFERENCES attribute_definition (service_code, code, tenantid);