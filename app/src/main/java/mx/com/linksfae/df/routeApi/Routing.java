package mx.com.linksfae.df.routeApi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ariza on 3/12/15.
 */
public class Routing extends AbstractRouting<LatLng>{
    public Routing(TravelMode mTravelMode) {
        super(mTravelMode);
    }

    protected String constructURL(LatLng... points) {
        LatLng start = points[0];
        LatLng dest = points[1];

        final StringBuffer mBuf = new StringBuffer(AbstractRouting.DIRECTIONS_API_URL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());

        return mBuf.toString();
    }
}
