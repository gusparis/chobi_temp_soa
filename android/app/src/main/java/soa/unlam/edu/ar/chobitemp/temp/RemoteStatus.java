package soa.unlam.edu.ar.chobitemp.temp;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by mcurrao on 05/07/17.
 */

public class RemoteStatus implements Serializable {

    private static final long serialVersionUID = 1571938916220719140L;

    private RemoteSourceData cloud;
    private RemoteSourceData sensor;

    private Boolean light;
    private String ip;

    public static class RemoteSourceData implements Serializable {

        private static final long serialVersionUID = -7240947066585983825L;

        private BigDecimal temperature;
        private BigDecimal humidity;
        private String location;

        public BigDecimal getTemperature() {
            return temperature;
        }

        public void setTemperature(BigDecimal temperature) {
            this.temperature = temperature;
        }

        public BigDecimal getHumidity() {
            return humidity;
        }

        public void setHumidity(BigDecimal humidity) {
            this.humidity = humidity;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public RemoteSourceData getCloud() {
        return cloud;
    }

    public void setCloud(RemoteSourceData cloud) {
        this.cloud = cloud;
    }

    public RemoteSourceData getSensor() {
        return sensor;
    }

    public void setSensor(RemoteSourceData sensor) {
        this.sensor = sensor;
    }

    public Boolean getLight() {
        return light;
    }

    public void setLight(Boolean light) {
        this.light = light;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
