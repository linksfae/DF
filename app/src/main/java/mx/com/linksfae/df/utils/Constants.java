package mx.com.linksfae.df.utils;

/**
 * Created by Fabian on 03/03/2015.
 */
public interface Constants {
    String ECOBICI_JSON_URL="https://www.ecobici.df.gob.mx/availability_map/getJsonObject";
    String DISTANCE_HTTP_REQUEST="https://maps.googleapis.com/maps/api/directions/json?" +
            "origin="+
            "mode=walking" +
            "&units=metric" +
            "&key=";

    String STATE_ESTACIONES="stateEstaciones";
    String STATE_ROUTE="stateRoute";
}
