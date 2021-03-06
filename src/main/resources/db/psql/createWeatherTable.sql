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
CREATE TYPE DIR AS ENUM ('NW','N','NE','E','SE','S','SW','W');

DROP TABLE IF EXISTS weather;
CREATE TABLE weather
(
  time                  DATE PRIMARY KEY     NOT NULL,
  temp                  INT,
  temp_desc             VARCHAR(40),
  wind_avg              SMALLINT,
  wind_max              SMALLINT,
  wind_deg              SMALLINT,
  wind_dir              DIR,
  precip                REAL,
  precip_interval       SMALLINT,
  precip_type           VARCHAR(40)
);

ALTER TABLE weather OWNER TO lawiny;

commit;