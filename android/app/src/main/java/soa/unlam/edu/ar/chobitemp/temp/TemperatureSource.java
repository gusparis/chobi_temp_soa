package soa.unlam.edu.ar.chobitemp.temp;

import soa.unlam.edu.ar.chobitemp.R;

/**
 * Created by mcurrao on 17/06/17.
 */

public enum TemperatureSource {
    CHOBI(R.id.current_state, "chobi", R.drawable.watch, R.string.temp_source_chobi), CLOUD(R.id.future_state, "cloud", R.drawable.cloud, R.string.temp_source_weather);

    public final int drawableResource;
    private final int drawerId;
    private final String serverPath;
    private final int description;

    TemperatureSource(int drawerId, String serverPath, int drawableResource, int description) {
        this.drawerId = drawerId;
        this.serverPath = serverPath;
        this.drawableResource = drawableResource;
        this.description = description;
    }

    public int getDrawerId() {
        return this.drawerId;
    }

    public static TemperatureSource findByDrawerId(int drawerId) {
        for (TemperatureSource ts : TemperatureSource.values())
            if (ts.drawerId == drawerId)
                return ts;
        return null;
    }

    public String serverPath() {
        return serverPath;
    }

    public int description() {
        return description;
    }
}