package soa.unlam.edu.ar.chobitemp;

/**
 * Created by mcurrao on 04/06/17.
 */

public interface ChobiConstants {

    String DEFAULT_ENDPOINT = "192.168.1.14";
    String CURRENT_TEMPERATURE_URL = "/temperature/";
    String SWITCH_LIGHT_STATUS = "/switchLights";

    float DEFAULT_MIN_TEMP = 14;

    float DEFAULT_MAX_TEMP = 34;
    int SELFT_REFRESH_RATE = 8000;

    public interface RemoteEndpoint {
        String FETCH_DATA = "/status";

        public interface Parameter {
            String SOURCE = "source";
            String SWITCH_LIGHTS = "switchLightStatus";
            String SWITCH_LIGHTS_REQUEST_NUMBER = "switchLightRequest";
        }
    }

    public static class SharedPreferences {
        public static final String CHOBI_PREFERENCES = "CHOBI_SHARED_PREFERENCES";
        public static final String CHOBI_SERVICE_ENDPOINT = "CHOBI_SERVICE_ENDPOINT";
        public static final String CHOBI_TEMPERATURE_MIN = "CHOBI_TEMPERATURE_MIN";
        public static final String CHOBI_TEMPERATURE_MAX = "CHOBI_TEMPERATURE_MAX";
        public static final String TEMPERATURE_SOURCE = "APP_TEMPERATURE_SOURCE";
    }
}

