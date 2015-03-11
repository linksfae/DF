package mx.com.linksfae.df;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import mx.com.linksfae.df.bean.Estacion;
import mx.com.linksfae.df.utils.Constants;
import mx.com.linksfae.df.utils.Utils;

public class MainActivity extends Activity implements OnMapReadyCallback, LocationListener{
    public static final String ACTIVITY="MainActivity";
    private GoogleMap googleMap;
    private Estacion[] estaciones;
    private boolean cargarEstaciones;

    private class HttpRequestTask extends AsyncTask<Void, Void, Estacion[]> {
        @Override
        protected Estacion[] doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Estacion[] estaciones=restTemplate.getForObject(Constants.ECOBICI_JSON_URL, Estacion[].class);
                setEstaciones(estaciones);
                return estaciones;
            } catch (Exception e) {
                Log.e(ACTIVITY, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Estacion[] estaciones) {
            Toast.makeText(getApplicationContext(), "# Estaciones: " + estaciones.length, Toast.LENGTH_SHORT).show();
            llenarMapa();
        }
    }



    public void btActualizarEstaciones(View view){
        new HttpRequestTask().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    public void init(Bundle savedInstanceState){
        setContentView(R.layout.activity_main);
        MapFragment googleMapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.googleMap);
        googleMapFragment.getMapAsync(this);
        maximizar();
        validarGPS();

        Utils.checkPlayServices(this);
        if(savedInstanceState==null){
            cargarEstaciones=true;
        }
        else{
            cargarEstaciones=false;
            //setEstaciones((Estacion[])savedInstanceState.getSerializable(Constants.STATE_ESTACIONES));
            //llenarEstaciones(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        dibujarEstaciones(true);
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this );
        String bestProvider=locationManager.getBestProvider(new Criteria(), false);
        Location lastKnowLocation=locationManager.getLastKnownLocation(bestProvider);
        if(lastKnowLocation!=null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude())));
        }
    }


    public void dibujarEstaciones(boolean httpRequest){
        Log.i(ACTIVITY, getEstaciones()==null?"estaciones null":"estaciones no null");

         if(cargarEstaciones==true){
             new HttpRequestTask().execute();
         }
        else{
             llenarMapa();
         }
    }
    public void llenarMapa(){
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        if(googleMap!=null){
            googleMap.clear();
            if(estaciones!=null){
                for(Estacion e: estaciones){
                    boundsBuilder.include(new LatLng(Double.valueOf(e.getLat()),Double.valueOf(e.getLon())));
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
                    googleMap.addMarker(marker);
                }
            }
        }
        LatLngBounds bounds = boundsBuilder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, -5));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.STATE_ESTACIONES, getEstaciones());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setEstaciones((Estacion[])savedInstanceState.getSerializable(Constants.STATE_ESTACIONES));
    }


    public void validarGPS(){
        final SharedPreferences preferences=getPreferences(MODE_PRIVATE);
        boolean showMessage=preferences.getBoolean(getString(R.string.preference_dont_ask_again), false);
        if(!showMessage){
            LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                AlertDialog alertDialog=new AlertDialog.Builder(this).setTitle(R.string.activateGPS)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        })
                        .setMultiChoiceItems(R.array.mc, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedIndex, boolean isChecked) {
                                if (selectedIndex == 0) {
                                    SharedPreferences.Editor editor=preferences.edit();
                                    AlertDialog alertDialog = ((AlertDialog) dialog);
                                    if (isChecked) {
                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(R.string.noThanks);
                                        editor.putBoolean(getString(R.string.preference_dont_ask_again), true);

                                    } else {
                                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(R.string.no);
                                        editor.putBoolean(getString(R.string.preference_dont_ask_again), false);
                                    }
                                    editor.commit();
                                }
                            }
                        }).create();

                alertDialog.show();
            }
        }
    }









    public void maximizar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.INVISIBLE
                );

    }

    @Override
    protected void onResume() {
        super.onResume();
        //maximizar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public Estacion[] getEstaciones() {
        return estaciones;
    }

    public void setEstaciones(Estacion[] estaciones) {
        this.estaciones = estaciones;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public static String getActivity() {
        return ACTIVITY;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(ACTIVITY, "location changed");
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(ACTIVITY, "status changed");
    }
    @Override
    public void onProviderEnabled(String provider) {
        Log.i(ACTIVITY, "provider enabled");
    }
    @Override
    public void onProviderDisabled(String provider) {
        Log.i(ACTIVITY, "provider disabled");
    }
}
