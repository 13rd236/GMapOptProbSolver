package GoogleMap;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OneStrokeMaker {
    List<String> vertex;
    List<Edge> edges;
    List<String> route = new ArrayList<>();

    public OneStrokeMaker(){
    }

    public OneStrokeMaker(List<String> vertex, List<Edge> edges) {
        this.vertex = vertex;
        this.edges = edges;
    }

    public List<String> getRoute() {
        return route;
    }

    public List<LatLng> makeOneStroke(Place place, Place from){
        List<LatLng> points = new ArrayList<LatLng>();
        List<LatLng> list = new ArrayList<LatLng>();
        List<LatLng> result = new ArrayList<LatLng>();

        //System.out.println(place.getPlace());
        for (int i=0;i<place.connected.size();i++) {
            if(from == null) Collections.reverse(points);
            if(place.connected.get(i) == from) {
                list = place.getEdges().get(i).path;
                Collections.reverse(list);
            }else{
                points.addAll(place.getEdges().get(i).path);
                result = makeOneStroke(place.connected.get(i),place);
//                if(place.connected.size() == 1) {
//                    Collections.reverse(result);
//                }
                points.addAll(result);
            }
        }
        //points.addAll(list);
        //System.out.print(place.getPlace() + "->" );
        return points;
    }

    public List<LatLng> makeOneStroke(Place place, Place from,List<LatLng> re){
        List<LatLng> points = new ArrayList<LatLng>();
        List<LatLng> list = new ArrayList<LatLng>();
        List<LatLng> result = new ArrayList<LatLng>();
        //List<LatLng> rere = re;
        points.addAll(re);

        //System.out.println(place.getPlace());
        for (int i=0;i<place.connected.size();i++) {
            if(from == null) Collections.reverse(points);
            if(place.connected.get(i) == from) {
                list = place.getEdges().get(i).path;
                Collections.reverse(list);
            }else{
                list = place.getEdges().get(i).path;
                //points.addAll(re);
                result = makeOneStroke(place.connected.get(i),place,list);
                Collections.reverse(list);
                result.addAll(list);
                points.addAll(result);
            }
        }
        //points.addAll(list);
        //System.out.print(place.getPlace() + "->" );
        return points;
    }

    //距離判別のためAがBとCと隣り合わせの時、AB間とAC間の距離がまた区同じだった場合にバグが出る
//    public List<LatLng> makeOneStrokeTSP(Place place,double distance){
//        List<LatLng> points = new ArrayList<LatLng>();
//        List<LatLng> list = new ArrayList<LatLng>();
//        List<LatLng> result = new ArrayList<LatLng>();
//
//
//        while(place.connected.size()!=0){
////            System.out.println("Place=" + place.getPlace());
////            System.out.println(place.connected.size());
//            Place nextPlace = place.connected.get(0);
//            Edge nextEdge = place.getEdges().get(0);
//            if(distance == nextEdge.distance) {
//                place.connected.remove(0);
//                place.getEdges().remove(0);
//            }else{
//                list = nextEdge.path;
//                points.addAll(list);
//                place.connected.remove(0);
//                place.getEdges().remove(0);
//                result = makeOneStrokeTSP(nextPlace,nextEdge.distance);
//                points.addAll(result);
//            }
//        }
//        return points;
//    }

    public List<LatLng> makeOneStrokeTSP(Place place,String from){
        List<LatLng> points = new ArrayList<LatLng>();
        List<LatLng> list = new ArrayList<LatLng>();
        List<LatLng> result = new ArrayList<LatLng>();

        while(place.connected.size()!=0){
            Place nextPlace = place.connected.get(0);
            Edge nextEdge = place.getEdges().get(0);
            String placeName = place.getPlace();
            if(place.connected.get(0).getPlace().equals(from)) {
                    place.connected.remove(0);
                    place.getEdges().remove(0);
                }else{
                    list = nextEdge.path;
                    route.add(place.getPlace());
                    points.addAll(list);
                    place.connected.remove(0);
                    place.getEdges().remove(0);
                    result = makeOneStrokeTSP(nextPlace,placeName);
                    points.addAll(result);
                }
        }
        return points;
    }

    public List<LatLng> direct2Path(DirectionsResult direct){
        List<LatLng> points = new ArrayList<LatLng>();
        for (DirectionsLeg leg : direct.routes[0].legs) {
            for (DirectionsStep step : leg.steps) {
                points.addAll(step.polyline.decodePath());
            }
        }
        return points;
    }

    public void printRoute(){
        for(int i=0;i<route.size();i++){
            System.out.println("No:"+(i+1)+" Place:"+route.get(i));
        }
    }
}
