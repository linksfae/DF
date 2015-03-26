package mx.com.linksfae.df.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Fabian on 17/02/2015.
 */
public class Utils {
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    public static boolean checkPlayServices(Activity context) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status, context);
            } else {
                Toast.makeText(context, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                context.finish();
            }
            return false;
        }
        return true;
    }
    private static void showErrorDialog(int code, Activity context) {
        GooglePlayServicesUtil.getErrorDialog(code, context,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    public static Object getFragment(ActionBarActivity a, String tag){
        return a.getSupportFragmentManager().findFragmentByTag(tag);
    }

    public static LatLng locationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static double calculateDistanceLatLng(LatLng origen, LatLng destino) {
        return calculateDistance(origen.longitude, origen.latitude, destino.longitude, destino.latitude);
    }
    public static double calculateDistance(double fromLong, double fromLat, double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }
}
