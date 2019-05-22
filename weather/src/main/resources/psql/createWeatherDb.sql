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