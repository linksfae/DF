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

import mx.com.linksfae.df.fragments.EcobiciMapFragment;
import mx.com.linksfae.df.task.HttpRequestTask;
import mx.com.linksfae.df.utils.Utils;

public class MainActivity extends ActionBarActivity{
    public static final String ACTIVITY="MainActivity";

    public static final String FRAGTAG = "EcobiciFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.checkPlayServices(this);





        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EcobiciMapFragment fragment = new EcobiciMapFragment();
        transaction.add(R.id.container, fragment, FRAGTAG);
        transaction.commit();
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
            ((EcobiciMapFragment)Utils.getFragment(this, FRAGTAG)).cargaEstaciones();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
