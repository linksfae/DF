package mx.com.linksfae.df.routeApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ariza on 3/12/15.
 */
public class PlaceRouting extends AbstractRouting<String>{
    public PlaceRouting(AbstractRouting.TravelMode mTravelMode) {
        super(mTravelMode);
    }

    protected String constructURL(String... points) {
        final StringBuffer mBuf = new StringBuffer(AbstractRouting.DIRECTIONS_API_URL);
        String from=null,to=null;
        try {
            from = URLEncoder.encode(points[0], "utf-8");
            to = URLEncoder.encode(points[1],"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        mBuf.append("origin=");
        mBuf.append(from);
        mBuf.append("&destination=");
        mBuf.append(to);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());

        return mBuf.toString();
    }
}