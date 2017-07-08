package soa.unlam.edu.ar.chobitemp.intent;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import soa.unlam.edu.ar.chobitemp.ChobiConstants;
import soa.unlam.edu.ar.chobitemp.connector.JSONUtils;
import soa.unlam.edu.ar.chobitemp.connector.URIUtils;
import soa.unlam.edu.ar.chobitemp.connector.VolleyConnector;
import soa.unlam.edu.ar.chobitemp.temp.CurrentLocalStatus;
import soa.unlam.edu.ar.chobitemp.temp.RemoteStatus;

/**
 * Created by mcurrao on 04/06/17.
 */

public class GetRemoteStatusTask {

    private Context context;
    private Response.Listener<RemoteStatus> listener;
    private ErrorListener errorListener;
    private JSONUtils jsonUtils;

    public GetRemoteStatusTask(Context context, Response.Listener<RemoteStatus> listener, ErrorListener errorListener) {
        this.context = context;
        this.listener = listener;
        this.errorListener = errorListener;
        this.jsonUtils = new JSONUtils();
    }

    public void fetch(CurrentLocalStatus localStatus) throws URISyntaxException {
        VolleyConnector connector = VolleyConnector.getInstance(context);
        URI address = URIUtils.buildUrl(context, ChobiConstants.RemoteEndpoint.FETCH_DATA);

        JSONObject parameters = null;
        if (localStatus != null) {
            parameters = new JSONObject();
            try {
                if (localStatus.getSource() != null)
                    parameters.put(ChobiConstants.RemoteEndpoint.Parameter.SOURCE, localStatus.getSource().serverPath());
                if (localStatus.getLightStatus() != null && localStatus.getLightStatus().isDesiredSwitch()) {
                    parameters.put(ChobiConstants.RemoteEndpoint.Parameter.SWITCH_LIGHTS, true);
                    parameters.put(ChobiConstants.RemoteEndpoint.Parameter.SWITCH_LIGHTS_REQUEST_NUMBER, localStatus.getLightStatus().getSwitchRequest());
                }
            } catch (JSONException e) {
                // ESTO SOLAMENTE AFECTABA A DOUBLES INFINITOS. OLVIDALO.
            }
        }

        connector.fetchOne(address, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(jsonUtils.asT(response, RemoteStatus.class));
            }
        }, errorListener);


        // listener.onResponse(jsonUtils.asT(mockJSONObject(), RemoteStatus.class));
    }

    protected JSONObject mockJSONObject() {

        JSONObject jo = null;
        try {
            jo = new JSONObject("{\"ip\":\"xxx.xxx.xxx.xxx\",\"light\":true,\"cloud\":{\"location\":\"Buenos Aires\",\"temperature\":28,\"humidity\":90},\"sensor\":{\"location\":\"Bariloche\",\"temperature\":14,\"humidity\":30}}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
}
