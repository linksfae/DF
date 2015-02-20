package mx.com.linksfae.df.fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import mx.com.linksfae.df.task.HttpRequestTask;

/**
 * Created by Fabian on 19/02/2015.
 */
public class EcobiciMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    public EcobiciMapFragment(){
        getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        cargaEstaciones();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        String bestProvider=locationManager.getBestProvider(new Criteria(), true);
        Location lastKnowLocation=locationManager.getLastKnownLocation(bestProvider);
    }
    public void cargaEstaciones(){

        new HttpRequestTask(getActivity(), getMap()).execute();
    }
}
