package mx.com.linksfae.df;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mx.com.linksfae.df.bean.Estacion;
import mx.com.linksfae.df.bean.Estaciones;
import mx.com.linksfae.df.routeApi.Route;
import mx.com.linksfae.df.routeApi.Routing;
import mx.com.linksfae.df.routeApi.RoutingListener;
import mx.com.linksfae.df.utils.Constants;
import mx.com.linksfae.df.utils.Utils;

public class MainActivity extends Activity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, RoutingListener{
    public static final String ACTIVITY="MainActivity";
    private GoogleMap googleMap;
    private Estacion[] estacionesEcobici;
    private View mDecorView;
    private SlidingUpPanelLayout mLayout;
    private boolean gpsActivo;
    private boolean drawRoute;

    private Location lastKnowLocation;
    private Marker selectedMarker;
    private Polyline selectedRoute;
    private LatLng[] routePoints;

    private Routing routing;

    private LatLng start;
    private LatLng end;

    LocationManager locationManager;



    private class HttpRequestTask extends AsyncTask<Void, Void, Estacion[]> {
        @Override
        protected Estacion[] doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                estacionesEcobici=restTemplate.getForObject(Constants.ECOBICI_JSON_URL, Estacion[].class);
                return estacionesEcobici;
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
        dibujarEstaciones(true);
    }
    public void btHideStations(View view){
        for(Estacion e: estacionesEcobici){
            e.getMarker().setVisible(!e.getMarker().isVisible());
        }
    }
    public void btGetLocationToStationRoute(View view){
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        Log.d(ACTIVITY, "btGetLocationToStationRout");
        if(lastKnowLocation!=null && selectedMarker!=null){
            start=Utils.locationToLatLng(lastKnowLocation);
            end=selectedMarker.getPosition();
            routing = new Routing(Routing.TravelMode.WALKING);
            routing.registerListener(this);
            routing.execute(start, end);
        }
    }
    public void btGetShortestRoute(View view){
        Log.d(ACTIVITY, "LastknowLocation: "+lastKnowLocation);

        if(gpsActivo){

        }
        else{
            Toast.makeText(getApplicationContext(), getText(R.string.gui_innactive_gps), Toast.LENGTH_SHORT).show();
            if(lastKnowLocation!=null && estacionesEcobici!=null){
                for(Estacion e: estacionesEcobici){
                    e.setDistancia(Utils.calculateDistance(lastKnowLocation.getLongitude(), lastKnowLocation.getLatitude(), e.getLon(), e.getLat()));
                }
                List<Estacion> estacionesList=Arrays.asList(estacionesEcobici);
                Collections.sort(estacionesList);

                for (Estacion e: estacionesList){
                    Log.d(ACTIVITY, "Distancia: "+e.getDistancia()+" Nombre"+e.getName());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }
    public void init(Bundle savedInstanceState){
        setContentView(R.layout.activity_main);
        mDecorView=getWindow().getDecorView();
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        //mLayout.setParalaxOffset(500);
        //mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        //mLayout.setAnchorPoint(.3F);
        //mLayout.setEnableDragViewTouchEvents(false);
        //mLayout.setDragView(R.id.bt_map);
        //mLayout.setTouchEnabled(false);
        //mLayout.setEnabled(false);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //Log.i(ACTIVITY, "onPanelSlide, offset " + slideOffset);


            }
            @Override
            public void onPanelExpanded(View panel) {
                Log.i(ACTIVITY, "onPanelExpanded");
            }
            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(ACTIVITY, "onPanelCollapsed");
            }
            @Override
            public void onPanelAnchored(View panel) {
                Log.i(ACTIVITY, "onPanelAnchored");
            }
            @Override
            public void onPanelHidden(View panel) {
                Log.i(ACTIVITY, "onPanelHidden");
            }
        });


        maximizar();


        MapFragment googleMapFragment=(MapFragment)getFragmentManager().findFragmentById(R.id.googleMap);
        googleMapFragment.getMapAsync(this);



        Utils.checkPlayServices(this);
        if(savedInstanceState==null){
            //validarGPS();
        }
        else{
            //  cargarEstaciones=false;
            //setEstaciones((Estacion[])savedInstanceState.getSerializable(Constants.STATE_ESTACIONES));
            //llenarEstaciones(false);
        }
    }
    public void dibujarEstaciones(boolean actualizar){
        if(actualizar || estacionesEcobici==null){
            new HttpRequestTask().execute();
        }
        else{
            llenarMapa();
        }
    }
    public void llenarMapa(){
        LatLng position=null;
        if(googleMap!=null){
            googleMap.clear();
            if(estacionesEcobici!=null){
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for(Estacion e: estacionesEcobici){
                    position=new LatLng(e.getLat(),e.getLon());
                    boundsBuilder.include(position);


                    MarkerOptions marker=new MarkerOptions().position(position).title(e.getName()).snippet(getString(R.string.gui_ecobici_bicis_disponibles) + e.getBikes() + getString(R.string.gui_ecobici_espacios_vacios) + e.getSlots());

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


                    e.setMarker(googleMap.addMarker(marker));
                }
                LatLngBounds bounds = boundsBuilder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
            }
        }
    }
    public void drawRoute(LatLng[] points){
        Log.d(ACTIVITY, "drawing route");
        if(selectedRoute!=null){
            selectedRoute.remove();
        }
        PolylineOptions polylineOptions=new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.add(points);

        Log.d(ACTIVITY, "googleMap: "+googleMap);
        if(googleMap!=null){

            selectedRoute=googleMap.addPolyline(polylineOptions);
        }
    }

    public void maximizar(){
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private Location getLastKnowLocation(){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(ACTIVITY, "location changed");
        lastKnowLocation=location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(ACTIVITY, "status changed");
    }
    @Override
    public void onProviderEnabled(String provider) {
        if(provider.equals(LocationManager.GPS_PROVIDER)){
            gpsActivo=true;
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
        if(provider.equals(LocationManager.GPS_PROVIDER)){
            gpsActivo=false;
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            maximizar();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.STATE_ESTACIONES, estacionesEcobici);
        LatLng[] as=selectedRoute.getPoints().toArray(new LatLng[0]);

        outState.putParcelableArray(Constants.STATE_ROUTE, as);
        Log.d(ACTIVITY, "saveSize: "+as.length+" "+as[0].latitude);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        estacionesEcobici=(Estacion[])savedInstanceState.getSerializable(Constants.STATE_ESTACIONES);
        routePoints=(LatLng[])savedInstanceState.getParcelableArray(Constants.STATE_ROUTE);
        drawRoute=true;
        Log.d(ACTIVITY, "restoreSize: "+routePoints.length+" "+routePoints[0].latitude);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        dibujarEstaciones(false);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(ACTIVITY, "infowindowsclick marker");
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setMyLocationEnabled(true);


        if(drawRoute){
            drawRoute(routePoints);
        }

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        lastKnowLocation=getLastKnowLocation();

        if(lastKnowLocation!=null){
            //Log.d(ACTIVITY, "lastKnowLocation: "+lastKnowLocation);
        }
        else{
            //Log.e(ACTIVITY, "lastKnowLocation: null");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(ACTIVITY, "clicked marker");
        selectedMarker=marker;
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(ACTIVITY, "onMapClick");
        selectedMarker=null;
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onRoutingFailure() {
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        drawRoute(mPolyOptions.getPoints().toArray(new LatLng[0]));
    }
}


