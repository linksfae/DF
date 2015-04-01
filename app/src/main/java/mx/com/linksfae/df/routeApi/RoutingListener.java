package mx.com.linksfae.df.routeApi;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by ariza on 3/12/15.
 */
public interface RoutingListener {
    public void onRoutingFailure();

    public void onRoutingStart();

    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route);
}
