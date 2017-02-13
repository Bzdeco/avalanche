package weatherCollector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import weatherCollector.entities.Weather;

import java.util.Date;

public interface WeatherRepository extends JpaRepository<Weather, Date>{

}
