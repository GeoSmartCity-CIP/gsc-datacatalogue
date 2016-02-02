 
CREATE TABLE gscdatacatalogue.gsc_001_organization
(
  id integer primary key  NOT NULL default nextval('gsc_001_organization_id_seq'),
  json json NOT NULL
);


CREATE TABLE gscdatacatalogue.gsc_002_user
(
  id integer primary key  NOT NULL default nextval('gsc_002_user_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_003_role
(
  id integer primary key  NOT NULL default nextval('gsc_003_role_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_004_function
(
  id integer primary key  NOT NULL default nextval('gsc_004_function_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_005_permission
(
  id integer primary key  NOT NULL default nextval('gsc_005_permission_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_006_datasource
(
  id integer primary key  NOT NULL default nextval('gsc_006_datasource_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_007_dataset
(
  id integer primary key  NOT NULL default nextval('gsc_007_dataset_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_008_layer
(
  id integer primary key  NOT NULL default nextval('gsc_008_layer_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_009_grouplayer
(
  id integer primary key  NOT NULL default nextval('gsc_009_grouplayer_id_seq'),
  json json NOT NULL
);

CREATE TABLE gscdatacatalogue.gsc_010_application
(
  id integer primary key  NOT NULL default nextval('gsc_010_application_id_seq'),
  json json NOT NULL
);