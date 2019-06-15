package org.avalanche.weather.provider;

import lombok.Builder;
import lombok.Data;
import org.avalanche.weather.entities.Weather;

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
