package mx.com.linksfae.df;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import mx.com.linksfae.df.task.HttpRequestTask;
import mx.com.linksfae.df.utils.Utils;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback{
    public static final String ACTIVITY="MainActivity";

    private GoogleMap map;
    public static final String FRAGTAG = "EcobiciFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SupportMapFragment fragment = new SupportMapFragment();
            fragment.getMapAsync(this);
            transaction.add(R.id.container, fragment);
            transaction.commit();

        Utils.checkPlayServices(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new HttpRequestTask(getApplicationContext(), map).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(ACTIVITY, "map ready");

        map = googleMap;
        new HttpRequestTask(getApplicationContext(), map).execute();
    }
}
