package org.avalanche.weather.repository;

import org.avalanche.weather.entities.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface WeatherRepository extends JpaRepository<Weather, Date>
{
}