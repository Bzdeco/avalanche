-- login as postgres user: sudo su postgres
-- execute this sql:  \i path

DROP DATABASE if EXISTS lawiny_test;
CREATE DATABASE lawiny_test;
DROP USER IF EXISTS lawiny;


CREATE USER lawiny WITH SUPERUSER PASSWORD 'l1234';

ALTER DATABASE lawiny_test OWNER TO lawiny;

\connect lawiny_test;

begin;

DROP TABLE IF EXISTS weather;
CREATE TABLE weather
(
  time                  TIMESTAMP   PRIMARY KEY   NOT NULL,
  temp                  REAL,
  temp_min              REAL,
  temp_max              REAL,
  pressure              REAL,
  sea_level             REAL,
  grnd_level            REAL,
  humidity              REAL,
  cloudiness            REAL,
  wind_speed            REAL,
  wind_deg              REAL,
  rain                  REAL,
  snow                  REAL
);

ALTER TABLE weather OWNER TO lawiny;

commit;