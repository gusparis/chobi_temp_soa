package soa.unlam.edu.ar.chobitemp.temp;

import java.io.Serializable;
import java.math.BigDecimal;

import soa.unlam.edu.ar.chobitemp.lights.LightStatus;

/**
 * Created by mcurrao on 06/07/17.
 */

public class CurrentLocalStatus implements Serializable {

    private static final long serialVersionUID = 7118083383082480515L;

    private TempStatus tempStatus;
    private LightStatus lightStatus;
    private TemperatureSource source;
    private BigDecimal humidity;
    private String currentSourceDescription;
    private boolean error;

    private RemoteStatus remoteStatus;

    public TempStatus getTempStatus() {
        return tempStatus;
    }

    public void setTempStatus(TempStatus tempStatus) {
        this.tempStatus = tempStatus;
    }

    public TemperatureSource getSource() {
        return source;
    }

    public void setSource(TemperatureSource source) {
        this.source = source;
    }

    public LightStatus getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(LightStatus lightStatus) {
        this.lightStatus = lightStatus;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public void setHumidity(BigDecimal humidity) {
        this.humidity = humidity;
    }

    public String getCurrentSourceDescription() {
        return currentSourceDescription;
    }

    public void setCurrentSourceDescription(String currentSourceDescription) {
        this.currentSourceDescription = currentSourceDescription;
    }

    public RemoteStatus getRemoteStatus() {
        return remoteStatus;
    }

    public void setRemoteStatus(RemoteStatus remoteStatus) {
        this.remoteStatus = remoteStatus;
    }

    public boolean hasData() {
        return tempStatus != null;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
