package soa.unlam.edu.ar.chobitemp.temp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mcurrao on 04/06/17.
 */

public class TempStatus implements Serializable {

    private static final long serialVersionUID = 5113706811145036468L;

    private BigDecimal temperature;
    private int color;
    private List<Weather> weatherTypes;

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Weather> getWeatherTypes() {
        return weatherTypes;
    }

    public void setWeatherTypes(List<Weather> weatherTypes) {
        this.weatherTypes = weatherTypes;
    }
}
