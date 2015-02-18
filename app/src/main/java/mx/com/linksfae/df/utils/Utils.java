package mx.com.linksfae.df.utils;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Fabian on 17/02/2015.
 */
public class Utils {
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    public static boolean checkPlayServices(ActionBarActivity context) {
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
    private static void showErrorDialog(int code, ActionBarActivity context) {
        GooglePlayServicesUtil.getErrorDialog(code, context,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }
}
