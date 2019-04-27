package weatherCollector;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import weatherCollector.entities.Weather;

import java.sql.SQLException;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackageClasses = Weather.class)
public class WeatherApplication {

	public static void main(String[] args) {
	    //System.setProperty("filename", "M-34-101-A-b-3-3-1.las");
        //System.out.println("filename: "+System.getProperty("filename"));
        SpringApplication.run(WeatherApplication.class, args);
	}

	/**
	 * @link https://www.baeldung.com/spring-boot-access-h2-database-multiple-apps
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseServer() throws SQLException {
		return Server.createTcpServer(
				"-tcp",
				"-tcpAllowOthers",
				"-tcpPort", "9090"
		);
	}
}
