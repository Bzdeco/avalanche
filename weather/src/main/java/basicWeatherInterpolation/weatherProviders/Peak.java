package basicWeatherInterpolation.weatherProviders;

import lombok.Builder;
import lombok.Data;
import weatherCollector.entities.Weather;

@Data
@Builder
class Peak
{
    private String name;
    private String url;
    private float height;
    private float latitude;
    private float longitude;
    private Weather weather;
}
