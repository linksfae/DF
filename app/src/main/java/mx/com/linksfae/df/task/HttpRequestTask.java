package mx.com.linksfae.df.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import mx.com.linksfae.df.bean.Estacion;
import mx.com.linksfae.df.R;

public class HttpRequestTask extends AsyncTask<Void, Void, Estacion[]> {
    public static final String ACTIVITY="HttpRequestTask";
    private static final String ECOBICI_JSON_URL="https://www.ecobici.df.gob.mx/availability_map/getJsonObject";
    private Context context;
    private GoogleMap map;
    public HttpRequestTask(Context context, GoogleMap map){
        this.context=context;
        this.map=map;
    }

    @Override
    protected Estacion[] doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            return restTemplate.getForObject(ECOBICI_JSON_URL, Estacion[].class);
        } catch (Exception e) {
            Log.e(ACTIVITY, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Estacion[] estaciones) {
        Toast.makeText(context, "# Estaciones: "+estaciones.length, Toast.LENGTH_SHORT).show();
        map.clear();
        for(Estacion e: estaciones){
            MarkerOptions marker=new MarkerOptions().position(new LatLng(Double.valueOf(e.getLat()), Double.valueOf(e.getLon()))).title(e.getName()).snippet("Disponibles: "+e.getBikes()+"\nLibres: "+e.getSlots());
            if(e.getStatus().equals("CLS")){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.no_operativa));
            }
            else
            if(e.getBikes()>=5){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.disponible));
            }
            else
            if(e.getBikes()==0){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.vacia));
            }
            else
            if(e.getBikes()<5){
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.medio_disponibles));
            }
            map.addMarker(marker);
        }
    }
}
