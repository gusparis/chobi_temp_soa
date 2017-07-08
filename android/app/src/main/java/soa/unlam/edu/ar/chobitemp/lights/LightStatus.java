package soa.unlam.edu.ar.chobitemp.lights;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by mcurrao on 02/07/17.
 */

public class LightStatus implements Serializable {

    private static final long serialVersionUID = 2500976996672942582L;

    private Boolean lightsOn;

    private boolean desiredSwitch;
    private int switchRequest;

    public LightStatus(){}

    public LightStatus(Boolean lightsOn) {
        this.lightsOn = lightsOn;
    }

    public Boolean getLightsOn() {
        return lightsOn;
    }

    public void setLightsOn(Boolean lightsOn) {
        this.lightsOn = lightsOn;
    }

    public boolean isDesiredSwitch() {
        return desiredSwitch;
    }

    public void setDesiredSwitch(boolean desiredSwitch) {
        if(desiredSwitch && !this.desiredSwitch)
            this.switchRequest = new Random().nextInt();
        this.desiredSwitch = desiredSwitch;
    }

    public int getSwitchRequest() {
        return switchRequest;
    }
}
