# 1. How to run this puppy

1. Run `psql` as postgres user
2. Run script under `src/main/resources/psql/createWeatherDb.sql`
3. Run WeatherApplication.java with VM option `-Dfilename=<filename>`
 for example `-Dfilename=M-34-100-B-b-2-4-0.las`
4. Go to `localhost:8080/getWeatherData`

 You're done, run `select * from weather;` query in `psql`.
 
# 2. How not to let this puppy kill you computer

Building `Terrain` with relatively big size and precision (e.g. 1000x1000 cells) is a very
memory-heavy process. To succeed without getting `OutOfMemoryError` you need to increase the
JVM's heap-size before running the serialization (`TerrainFormatter.serialize`). To do this,
in IntelliJ IDEA go to `Run > Edit Configurations` and pass additional argument to VM options:
`-Xmx<number>g`, where `<number>` is the amount of the GB used for JVM heap. The exact number
depends on `Terrain` resolution specified via `TerrainSettings`.