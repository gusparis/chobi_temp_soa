package soa.unlam.edu.ar.chobitemp.connector;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URI;

/**
 * Created by mcurrao on 05/07/17.
 */

public class VolleyConnector {

    private static VolleyConnector connector;

    private RequestQueue queue;

    private VolleyConnector(Context context) {
        this.queue = Volley.newRequestQueue(context);
    }

    public void fetchOne(URI address, JSONObject parameters, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, address.toString(), parameters, responseListener, errorListener);
        request.setTag(VolleyConnector.class);
        queue.add(request);
    }

    public static synchronized VolleyConnector getInstance(Context context) {
        if (connector == null)
            connector = new VolleyConnector(context);
        return connector;
    }

    public static void cancelAll() {
        if(connector != null) {
            connector.queue.cancelAll(VolleyConnector.class);
        }
    }
}
