package mx.com.linksfae.df;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import mx.com.linksfae.df.fragment.EcobiciMapFragment;
import mx.com.linksfae.df.task.HttpRequestTask;

public class MainActivity extends ActionBarActivity {
    public static final String ACTIVITY="MainActivity";
    private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private GoogleMap map;
    public static final String FRAGTAG = "EcobiciFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            EcobiciMapFragment fragment = new EcobiciMapFragment();
            transaction.add(R.id.container, fragment);
            transaction.commit();
        }



        /*if(checkPlayServices()){
            if (savedInstanceState == null) {
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                if(map!=null){
                    new HttpRequestTask(getApplicationContext(), map).execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No hay mapa", Toast.LENGTH_SHORT).show();


                }
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //new HttpRequestTask(getApplicationContext()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new HttpRequestTask(getApplicationContext(), map).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

}
