--login as postgres user: sudo su postgres
-- execute this sql:  \i path

DROP DATABASE if EXISTS lawiny_test;
CREATE DATABASE lawiny_test;
DROP USER IF EXISTS lawiny;


CREATE USER lawiny WITH SUPERUSER PASSWORD 'l1234';

ALTER DATABASE lawiny_test OWNER TO lawiny;

\connect lawiny_test;

begin;

DROP TYPE IF EXISTS DIR CASCADE;
-- CREATE TYPE DIR AS ENUM ('NW','N','NE','E','SE','S','SW','W');

DROP TABLE IF EXISTS weather;
CREATE TABLE weather
(
  time                  TIMESTAMP PRIMARY KEY     NOT NULL,
  temp                  REAL,
  temp_desc             VARCHAR(40),
  wind_avg              SMALLINT,
  wind_max              SMALLINT,
  wind_dir_deg          SMALLINT,
  wind_dir              INTEGER,
  precip_amount         REAL,
  precip_interval       SMALLINT,
  precip_type           VARCHAR(40),
  cloud_level           SMALLINT,
  cloud_sum             SMALLINT,
  cloud_low             SMALLINT,
  snow_level            SMALLINT
);

ALTER TABLE weather OWNER TO lawiny;

commit;