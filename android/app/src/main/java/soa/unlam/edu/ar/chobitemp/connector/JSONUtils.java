package soa.unlam.edu.ar.chobitemp.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import org.json.JSONObject;

/**
 * Created by mcurrao on 06/07/17.
 */

public class JSONUtils {

    private ObjectMapper mapper;

    public JSONUtils() {

        mapper = new ObjectMapper().registerModule(new JsonOrgModule());
    }

    public <T> T asT(JSONObject jsonObject, Class<T> clazz) {
        return mapper.convertValue(jsonObject, clazz);
    }
}
