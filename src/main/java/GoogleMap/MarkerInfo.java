package GoogleMap;

import com.google.maps.model.LatLng;

/**
 * Created by syun on 2017/12/12.
 */
public class MarkerInfo {
    String point;
    LatLng position;

    MarkerInfo(String point, LatLng position){
        this.point = point;
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getPoint() {
        return point;
    }
}
