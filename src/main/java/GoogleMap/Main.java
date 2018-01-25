package GoogleMap;

import GoogleMap.tsp.TspOneTree;
import com.google.gson.Gson;
import com.google.maps.model.*;
import com.google.maps.model.LatLng;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {

    private static ArrayList<Place> places;

    public static void main(String[] args) {
        // Google Maps Directions API の APIキーをここに入力
        ApiUtil util = new ApiUtil("************************");
//
//        int a = 45;
//        int count = 0;
//
//        while(count < 1000) {
//            count = 0;
//            for (int i = 0; i < a; i++) {
//                for(int j=i+1;j<a-1;j++) {
//                    count++;
//                }
//            }
//            System.out.println(count);
//            a++;
//        }
        //System.out.println(a);

        List<DirectionsResult> resultList = new ArrayList<>();

        Gson gson = new Gson();
        Main m = new Main();
        // ↑ main関数内ではstaticな関数しか用いることができないので、
        // (getDirect関数がstaticではない)
        // 一時的にMainクラスをインスタンス化しているが、
        // getDirect関数を別のクラス(名前は ApiCallDriver とか？)に
        // 記述すべきである。

//        DirectionsResult direct = m.getDirect(util.getContext(),"東京","大阪");
//        // DirectionsResultから得られる情報は
//        // https://googlemaps.github.io/google-maps-services-java/v0.2.3/javadoc/
//        // を参照し、左下の欄の DirectionResultを見ると書いてある。
//
        port(2070);
        /* 最小スパニングツリー問題 */
        post("/", (req, res) -> {
            DirectionsResult direct = null;
            List<MarkerInfo> infos = new ArrayList<>();
            List<Edge> edges = new ArrayList<Edge>();//辺のリスト、集合
            List<String> vertex = new ArrayList<String>(); //頂点のリスト、集合
            Place p = new Place(); //Placeのインスタンス

            String s = URLDecoder.decode(req.body(), "UTF-8");
            System.out.println(s);
            PlacesJson json = gson.fromJson(s, PlacesJson.class);
            TravelMode travelMode = jsonCheckTravelMode(json.travelMode[0]);

            for (int i = 0; i < json.places.length; i++) {
                vertex.add(json.places[i]);
                for (int j = i + 1; j < json.places.length; j++) {
                    direct = util.getDirect(util.getContext(), json.places[i], json.places[j],travelMode);
                    resultList.add(direct);
                    Edge e = new Edge(json.places[i], json.places[j], direct.routes[0].legs[0].distance.inMeters / 1000, direct2Path(direct));
                    edges.add(e);
                }
                if(i == json.places.length -1){
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].endLocation);
                    infos.add(mi);
                }else {
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].startLocation);
                    infos.add(mi);
                }
            }

            //ここがSpannigTree
            Collections.sort(edges, new MyComparator());
            for (int i = 0; i < edges.size(); i++) {
                System.out.println(edges.get(i).origin + ":" + edges.get(i).destination + ":" + edges.get(i).distance + "km");
            }

            SpaningTreeDriver std = new SpaningTreeDriver(edges, vertex);
            List<Edge> answerEdgesList = std.run();

            OneStrokeMaker maker = new OneStrokeMaker(vertex, answerEdgesList);//maker.convert();
            List<Place> places = p.placeConnect(answerEdgesList, vertex);

            List<LatLng> result = new ArrayList<LatLng>();
            //result = maker.makeOneStroke(places.get(0),null);
            List<LatLng> re = new ArrayList<>();
            result = maker.makeOneStroke(places.get(0),null,re);

            ResultJson rj = new ResultJson(infos,result,maker.getRoute());
            String resultJson = gson.toJson(rj);

            //String resultJson = gson.toJson(result);

            return resultJson;
        });

        post("/tsp",(req,res) -> {
            List<MarkerInfo> infos = new ArrayList<>();
            DirectionsResult direct = null;
            List<Edge> edges = new ArrayList<Edge>();//辺のリスト、集合
            List<String> vertex = new ArrayList<String>(); //頂点のリスト、集合
            Place placeInstance = new Place();
            List<Place> result = new ArrayList<Place>();
            List<LatLng> resultPath = new ArrayList<LatLng>();

            String s = URLDecoder.decode(req.body(), "UTF-8");
            System.out.println(s);
            PlacesJson json = gson.fromJson(s, PlacesJson.class);
            TravelMode travelMode = jsonCheckTravelMode(json.travelMode[0]);

            for (int i = 0; i < json.places.length; i++) {
                vertex.add(json.places[i]);
                for (int j = i + 1; j < json.places.length; j++) {

                    direct = util.getDirect(util.getContext(), json.places[i], json.places[j],travelMode);
                    resultList.add(direct);
                    Edge e = new Edge(json.places[i], json.places[j], direct.routes[0].legs[0].distance.inMeters / 1000, direct2Path(direct));
                    edges.add(e);
                }
                if(i == json.places.length -1){
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].endLocation);
                    infos.add(mi);
                }else {
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].startLocation);
                    infos.add(mi);
                }
            }
            //List<Place> places = placeInstance.placeConnect(edges, vertex);
            //TspOneTree tot = new TspOneTree(places);

            TspOneTree tot = new TspOneTree(vertex,edges);
            result = tot.run();
            placeInstance.printPlaces(result);

            OneStrokeMaker maker = new OneStrokeMaker();
            resultPath = maker.makeOneStrokeTSP(result.get(0),null);
            maker.printRoute();
            //maker.printRoute();
            ResultJson rj = new ResultJson(infos,resultPath,maker.getRoute());
            String resultJson = gson.toJson(rj);
            return resultJson;
        });

        post("/tdp",(req,res) -> {
            List<MarkerInfo> infos = new ArrayList<>();
            DirectionsResult direct = null;
            List<Edge> edges = new ArrayList<Edge>();//辺のリスト、集合
            List<String> vertex = new ArrayList<String>(); //頂点のリスト、集合
            Place placeInstance = new Place();
            Edge edgeInstance =new Edge();
            List<Place> result = new ArrayList<Place>();
            List<LatLng> resultPath = new ArrayList<LatLng>();

            String s = URLDecoder.decode(req.body(), "UTF-8");
            System.out.println(s);
            PlacesJson json = gson.fromJson(s, PlacesJson.class);
            TravelMode travelMode = jsonCheckTravelMode(json.travelMode[0]);

            for (int i = 0; i < json.places.length; i++) {
                vertex.add(json.places[i]);
                for (int j = i + 1; j < json.places.length; j++) {

                    direct = util.getDirect(util.getContext(), json.places[i], json.places[j],travelMode);
                    resultList.add(direct);
                    Edge edge = new Edge(json.places[i], json.places[j], direct.routes[0].legs[0].distance.inMeters / 1000, direct2Path(direct));
                    edges.add(edge);
                }
                if(i == json.places.length -1){
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].endLocation);
                    infos.add(mi);
                }else {
                    MarkerInfo mi = new MarkerInfo(json.places[i], direct.routes[0].legs[0].startLocation);
                    infos.add(mi);
                }
            }

            List<Edge> reverseEdges = new ArrayList<>();
            reverseEdges = edgeInstance.reverseDistance(edges);
            //List<Place> places = placeInstance.placeConnect(reverseEdges, vertex);
            //TspOneTree tot = new TspOneTree(places);

            TspOneTree tot = new TspOneTree(vertex,reverseEdges);
            result = tot.run();
            placeInstance.printPlaces(result);

            OneStrokeMaker maker = new OneStrokeMaker();
            resultPath = maker.makeOneStrokeTSP(result.get(0),null);
            maker.printRoute();
            //maker.printRoute();
            ResultJson rj = new ResultJson(infos,resultPath,maker.getRoute());
            String resultJson = gson.toJson(rj);
            return resultJson;
        });
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

    static public List<LatLng> direct2Path(DirectionsResult direct){
        List<LatLng> points = new ArrayList<LatLng>();
        for (DirectionsLeg leg : direct.routes[0].legs) {
            for (DirectionsStep step : leg.steps) {
                points.addAll(step.polyline.decodePath());
            }
        }
        return points;
    }

    static public TravelMode jsonCheckTravelMode(int i){
        if(i == 0){
            return TravelMode.DRIVING;
        }if(i == 1){
            return TravelMode.BICYCLING;
        }if(i ==2){
            return TravelMode.TRANSIT;
        }if(i==3){
            return TravelMode.WALKING;
        }else{
            return TravelMode.DRIVING;
        }
    }
}
