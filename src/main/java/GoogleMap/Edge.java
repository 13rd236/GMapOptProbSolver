package GoogleMap;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

import java.util.*;

public class Edge {
    String  origin;
    String  destination;
    double distance;
    //DirectionsResult direct;
    List<LatLng> path;

    public Edge(){
        this.origin = null;
        this.destination = null;
        this.distance = 0;
        this.path = null;
    }

    public Edge(String origin, String destination, double distance,List<LatLng> path){
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.path = path;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getOrigin(){
        return this.origin;
    }

    public String getDestination(){
        return this.destination;
    }

    public  double getDistance(){
        return this.distance;
    }

//    public DirectionsResult getDirect() {
//        return direct;
//    }


    public List<LatLng> getPath() {
        return path;
    }

    public Edge reversePlace(){
        List<LatLng> rList = new ArrayList<>(path);
        Collections.reverse(rList);
        return new Edge(destination,origin,distance,rList);
    }

    public List<Edge> reverseDistance(List<Edge> edges){
        Collections.sort(edges, new MyComparator());
        List<Edge> reverseEdges = new ArrayList<>();
        int edgesSize = edges.size();
        double maxDistance = edges.get(edgesSize - 1).distance;
        for(int i=0;i<edgesSize;i++){
            Edge e = edges.get(i);
            double iDistance = maxDistance - edges.get(i).distance;
            Edge re = new Edge(e.origin,e.destination,iDistance,e.path);
            reverseEdges.add(re);
        }
        return reverseEdges;
    }

    static class MyComparator implements Comparator {
        @Override
        public int compare (Object arg0, Object arg1) {
            Edge x = (Edge) arg0;
            Edge y = (Edge) arg1;

            if (x.getDistance() > y.getDistance()) {
                return 1;
            } else if (x.getDistance() < y.getDistance()) {
                return -1;
            } else{
                return 0;
            }
        }
    }
}
