package GoogleMap;

import com.google.maps.model.LatLng;

import java.util.List;

public class ResultJson {
    List<MarkerInfo> infos;
    List<LatLng> latLngs;
    List<String> route;

    public ResultJson(List<MarkerInfo> infos,List<LatLng> latLngs,List<String> route){
        this.infos = infos;
        this.latLngs = latLngs;
        this.route = route;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public List<MarkerInfo> getInfos() {
        return infos;
    }

    public List<String> getRoute() { return route; }
}
