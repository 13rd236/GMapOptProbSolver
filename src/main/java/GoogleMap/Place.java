package GoogleMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Place {
    String place;
    List<Place> connected;
    List<Edge> edges;

    public Place(){
        this.place =null;
        this.connected = null;
        this.edges = null;
    }

    public Place(String place){
        this.place = place;
        this.connected = new ArrayList<Place>();
        this.edges = new ArrayList<Edge>();
    }

    public Place(Place p){
        this.place = p.getPlace();
        this.connected = p.getConnected();
        this.edges = p.getEdges();
    }

    public void addConnected(Place p){
        this.connected.add(p);
    }

    public void addEdges(Edge e){
        this.edges.add(e);
    }

    public String getPlace() {
        return place;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Place> getConnected() {
        return connected;
    }

    public boolean isSame(String s){
        if(s.equals(place)){
            return true;
        }
        return false;
    }

    public List<Place> placeConnect(List<Edge> edges,List<String> vertex){
        List<Place> places = new ArrayList<>();
        for(int i=0;i<vertex.size();i++) {
            Place p = new Place(vertex.get(i));
            places.add(p);
        }

        for(int i=0;i<edges.size();i++){
            Edge e = edges.get(i);
            String origin = e.origin;
            String dest = e.destination;
            Place originPlace = null,destPlace = null;
            for(int j=0;j<places.size();j++){

                Place p = places.get(j);
                if(p.isSame(origin)){
                    originPlace = p;
                }else if(p.isSame(dest)){
                    destPlace = p;
                }
            }

            originPlace.addConnected(destPlace);
            originPlace.addEdges(e);

            destPlace.addConnected(originPlace);
            destPlace.addEdges(e.reversePlace());
//            if(destPlace.place.equals(dest)) {
//                destPlace.addEdges(e);
//            }else{
//                destPlace.addEdges(e.reversePlace());
//            }
        }
        return places;
    }

    public List<Place> makeGraph(double[] cplexSolve, List<Place> places, Map<String,Integer> map){
        int nodeSize = places.size();
        List<Edge> edges = new ArrayList<>();
        List<Place> result = new ArrayList<>();
        List<String> vertex = new ArrayList<>();

        for(int i = 0;i<places.size();i++) {
            vertex.add(places.get(i).getPlace());
            for (int j = 0; j < places.get(i).getEdges().size(); j++) {
                Edge e = places.get(i).getEdges().get(j);
                int eOriginIdx = map.get(e.getOrigin());
                int eDestIdx = map.get(e.getDestination());
                if(eOriginIdx < eDestIdx &&
                        cplexSolve[nodeSize * eOriginIdx + eDestIdx] == 1){
                    //if(cplexSolve[nodeSize * i + j] == 1){
                    Place p = places.get(i);
                    Edge e2 = p.getEdges().get(j);
                    edges.add(e2);
                }
            }
        }
        //printEdges(edges);
        result = placeConnect(edges,vertex);
        //printPlaces(result);
        return result;
    }

    public List<Edge> makeEdge(double[] cplexSolve, List<Place> places, Map<String,Integer> map){
        int nodeSize = places.size();
        List<Edge> edges = new ArrayList<>();
        List<Place> result = new ArrayList<>();
        List<String> vertex = new ArrayList<>();

        for(int i = 0;i<places.size();i++) {
            vertex.add(places.get(i).getPlace());
            for (int j = 0; j < places.get(i).getEdges().size(); j++) {
                Edge e = places.get(i).getEdges().get(j);
                int eOriginIdx = map.get(e.getOrigin());
                int eDestIdx = map.get(e.getDestination());
                if(eOriginIdx < eDestIdx &&
                        cplexSolve[nodeSize * eOriginIdx + eDestIdx] == 1){
                    //if(cplexSolve[nodeSize * i + j] == 1){
                    Place p = places.get(i);
                    Edge e2 = p.getEdges().get(j);
                    edges.add(e2);
                }
            }
        }
        return edges;
    }

    public void printEdges(List<Edge> edges){
        for(int i=0;i<edges.size();i++){
            System.out.println("origin= "+edges.get(i).getOrigin()+",dest= "+edges.get(i).getDestination()+",distance="+edges.get(i).getDistance());
        }
    }

    public void printPlaces(List<Place> places){
        for(int i=0;i<places.size();i++){
            System.out.println("Number:"+i);
            System.out.println("Place:"+places.get(i).getPlace());
            System.out.print("Connection:");
            for(int j=0;j<places.get(i).edges.size();j++){
                System.out.println(places.get(i).edges.get(j).getDestination()+"Distance:"+places.get(i).edges.get(j).distance);
            }
        }
    }
}
