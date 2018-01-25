package GoogleMap;

import com.google.maps.model.TravelMode;

public class PlacesJson {

    public int[] travelMode;
    public String[] places;

    public PlacesJson(String[] places){
        this.places = places;
    }

    public PlacesJson(int[] travelMode,String[] places){
        this.travelMode = travelMode;
        this.places = places;
    }
}
