package soa.unlam.edu.ar.chobitemp.connector;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.net.URI;
import java.net.URISyntaxException;

import soa.unlam.edu.ar.chobitemp.ChobiConstants;

/**
 * Created by mcurrao on 05/07/17.
 */

public class URIUtils {

    public static URI buildUrl(Context context, String subPath) throws URISyntaxException {
        URI serviceRoot = fetchServiceRoot(context);
        try {
            return serviceRoot.resolve(subPath);
        } catch (Exception e) {
            throw new URISyntaxException(subPath, "Could not parse subpath");
        }
    }

    private static URI fetchServiceRoot(Context context) throws URISyntaxException {
        SharedPreferences prefs = context.getSharedPreferences(ChobiConstants.SharedPreferences.CHOBI_PREFERENCES, Application.MODE_PRIVATE);
        String setUrl = prefs.getString(ChobiConstants.SharedPreferences.CHOBI_SERVICE_ENDPOINT, ChobiConstants.DEFAULT_ENDPOINT);
        if (setUrl != null && !setUrl.trim().isEmpty()) {
            return new URI(setUrl);
        } else {
           throw new URISyntaxException(setUrl, "Root address was null");
        }
    }
}
