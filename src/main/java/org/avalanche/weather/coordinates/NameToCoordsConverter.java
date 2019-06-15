package org.avalanche.weather.coordinates;

public interface NameToCoordsConverter
{
    Coords convert(String filename);
}
