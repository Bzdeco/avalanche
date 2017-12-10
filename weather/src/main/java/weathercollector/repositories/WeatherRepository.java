package weathercollector.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import weathercollector.entities.Weather;

import java.util.Date;

public interface WeatherRepository extends JpaRepository<Weather, Date>{

}
