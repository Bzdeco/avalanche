how to run this puppy

1. run psql as postgres user
2. run script under src/main/resources/psql/createWeatherDb.sql
3. run WeatherApplication.java with VM option '-Dfilename=<filename>'
 for example <filename.=-Dfilename=M-34-100-B-b-2-4-0.las
4. go to localhost:8080/getWeatherData
 youre done, check in psql 'select * from weather;'