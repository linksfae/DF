package mx.com.linksfae.df.fragments;

import android.app.Fragment;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Map;

import mx.com.linksfae.df.R;
import mx.com.linksfae.df.task.HttpRequestTask;

public class EcobiciMapFragment extends Fragment implements OnMapReadyCallback{
    public static final String TAG = "EcobiciMapFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_ecobicimap, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapFragment googleMap=(MapFragment)getChildFragmentManager().findFragmentById(R.id.googleMap);
        googleMap.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapFragment googleMap=(MapFragment)getFragmentManager().findFragmentById(R.id.googleMap);
        googleMap.getMapAsync(this);



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
        //new HttpRequestTask(getActivity().execute();
    }
}
