package GoogleMap.tsp;

//import cplextest.Edge;
//import cplextest.Graph;
//import cplextest.GraphByList;
//import edu.ucsb.cs.jicos.applications.utilities.graph.WeightedMatch;
import GoogleMap.Edge;
import GoogleMap.Place;
import Spaning2.*;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.LazyConstraintCallback;

import java.io.IOException;
import java.util.*;


/**
 * Created by cohalz on 2015/12/07.
 */
public class TspOneTree {
    List<Place> places;
    List<Edge> edges;
    List<String> vertex;
    Map<String,Integer> map;
    Map<Integer,String> reverseMap;

//    public TspOneTree(List<Place> places) {
//        this.places = places;
//        this.map = makeHashMap(places);
//        this.reverseMap = makeReverseHashMap(places);
//    }

    public TspOneTree(List<String> vertex,List<Edge> edges) {
        this.vertex = vertex;
        this.edges = edges;
        this.places = new Place().placeConnect(edges,vertex);
        this.map = makeHashMap(places);
        this.reverseMap = makeReverseHashMap(places);
    }

    public List<Place> run(){
        //Graph originalGraph = Graph.getInstance(args);

        IloNumVar[] x;
        int nodeSize;
        IloCplex cplex;
        long start = System.currentTimeMillis();
        nodeSize = this.places.size();
        List<Place> result = null;

        try {
            cplex = new IloCplex();
            x = cplex.boolVarArray(nodeSize * nodeSize);


            //名前を決める
            for (int i = 0; i < nodeSize; i++)
                for (int j = 0; j < nodeSize; j++)
                    x[i * nodeSize + j].setName("i" + i + "_j" + j);


            for (int i = 0; i < nodeSize; i++) {
                //各頂点で選ぶ辺は2本
                IloLinearNumExpr v1 = cplex.linearNumExpr();
                for (int j = 0; j < nodeSize; j++) {
                    if (i == j) continue;
                    if (i < j)
                        v1.addTerm(1.0, x[nodeSize * i + j]);
                    else
                        v1.addTerm(1.0, x[nodeSize * j + i]);
                }

                cplex.addEq(v1, 2.0);

                //対角線上は0
            }

            //自分と自分の距離を０にする
            for (int i = 0; i < nodeSize; i++) {
                IloLinearNumExpr id = cplex.linearNumExpr();
                id.addTerm(1.0, x[nodeSize * i + i]);
                cplex.addEq(id, 0.0);
            }

            IloLinearNumExpr loop = cplex.linearNumExpr();

            //n本の辺でできている
            for (int i = 0; i < nodeSize -1; i++)
                for (int j = i + 1; j < nodeSize; j++)
                    loop.addTerm(1.0, x[nodeSize * i + j]);

            //ノードの数だけ辺を選ぶ
            cplex.addEq(loop, nodeSize);

            //番号が昇順になってないものは0にして使われないようにする
            IloLinearNumExpr dummy = cplex.linearNumExpr();
            for (int i = 1; i < nodeSize; i++) {
                for (int j = 0; j < i; j++){
                    dummy.addTerm(1.0, x[nodeSize * i + j]);
                }
            }
            cplex.addEq(dummy, 0);

            IloLinearNumExpr minimizeFunc = cplex.linearNumExpr();
            for(int i = 0;i<places.size();i++){
                for(int j =0;j<places.get(i).getEdges().size();j++){
                   Edge e = places.get(i).getEdges().get(j);
                    int eOriginIdx = map.get(e.getOrigin());
                    int eDestIdx = map.get(e.getDestination());
                    if (eOriginIdx < eDestIdx) {
                        System.out.println("i="+eOriginIdx+",j="+eDestIdx+",distance="+e.getDistance());
                           minimizeFunc.addTerm(e.getDistance(), x[eOriginIdx * nodeSize + eDestIdx]);
                    }
                }
            }

            //  System.out.println(prim(graph));

            cplex.addMinimize(minimizeFunc);

            cplex.exportModel("testmodel1.lp");

            //サイクルの削除
            cplex.use(new IntegerCutCallback(x, map, reverseMap, cplex));

             cplex.exportModel("lazymodel.lp");

            if (cplex.solve()) {

                //Graph tmpGraph = new Graph(cplex.getValues(x), nodeSize);
                result = new Place().makeGraph(cplex.getValues(x),places,map);
                double[] val = cplex.getValues(x);
                int ncols = cplex.getNcols();
                for (int j = 0; j < ncols; ++j) {
                    if (val[j] > 0.5) {
                        //cplex.output().println("Column: " + j + " Value = " + val[j]);
                        cplex.output().println("Column: " + x[j].getName() + " Value = " + val[j]);
                    }
                }
                cplex.exportModel("testmodel2.lp");
                cplex.end();
                return result;
//                cplex.output().println("Solution status = " + cplex.getStatus());
//                cplex.output().println("Solution value = " + cplex.getObjValue());
//                    if(val[j]>0.5) {
//                        //System.out.println("Column: " + x[j].getName());
//                    }
//                }
//                cplex.end();
            }
            // create model and solve it

        } catch (IloCplex.UnknownObjectException e) {
            e.printStackTrace();
        } catch (IloException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public Map<String,Integer> makeHashMap(List<Place> places){
        Map<String,Integer> map = new HashMap<>();
        for(int i=0;i<places.size();i++){
            map.put(places.get(i).getPlace(),i);
        }
        return map;
    }
    public Map<Integer,String> makeReverseHashMap(List<Place> places) {
        Map<Integer,String> map = new HashMap<>();
        for(int i=0;i<places.size();i++){
            map.put(i,places.get(i).getPlace());
        }
        return map;
    }

    //サイクルを除く処理
    class IntegerCutCallback extends LazyConstraintCallback {
        private IloNumVar[] x;
        private Map<String,Integer> map;
        private Map<Integer,String> reverseMap;
        private IloCplex cplex;
        public IntegerCutCallback(IloNumVar[] x,Map<String,Integer> map,Map<Integer,String> reverseMap, IloCplex cplex) {
            this.x = x;
            this.map = map;
            this.reverseMap = reverseMap;
            this.cplex = cplex;
        }

        @Override
        protected void main() {
            //System.out.println("CAAAAAAAAAAAAAAALBAAAAAAAAAAAAACK!!!!!!!!!!");
            try {
//            Graph tmpGraph = new Graph(this.getValues(x), nodeSize);
            List<Place> result = null;
            List<Edge> resultEdges = new ArrayList<>();
            String origin,dest;
            int nodeSize = places.size();
            result = new Place().makeGraph(this.getValues(x), places,map);

            UnionFind uf = new UnionFind(places.size());

            for(int i=0;i<result.size();i++){
                for(int j =0;j<result.get(i).getEdges().size();j++){
                    List<Edge> edges = result.get(i).getEdges();
                    int originInt = map.get(edges.get(j).getOrigin());
                    int destInt = map.get(edges.get(j).getDestination());
                    if(originInt < destInt){
                        resultEdges.add(edges.get(j));
                    }
                }
            }

            for(int i=0;i<result.size();i++){
                Place p = result.get(i);
                List<Edge> es = p.getEdges();
                for(int j=0;j<es.size();j++){
                    Edge e = es.get(j);
                    origin = e.getOrigin();
                    dest = e.getDestination();
                    int x = map.get(origin);
                    int y = map.get(dest);
                    uf.union(x+1, y+1);
                }
            }

            List<Integer> groupElement = uf.countGroupElement();
             //int count = uf.ufCount();
            List<Edge> edges = new Place().makeEdge(this.getValues(x), places,map);


            if(!uf.isConnect()){
                System.out.println("Callback Start");
                    for(int i=0;i<this.getValues(x).length;i++){
                        if(this.getValues(x)[i]>0) {
                            System.out.println(this.x[i]);
                        }
                    }
                System.out.println("Callback End");
                List<List<Integer>> groupList = uf.groupCut(groupElement);
                for(int i=0;i<groupList.size();i++) {
                    List<Integer> group = groupList.get(i);
                    IloLinearNumExpr missCircle = cplex.linearNumExpr();
                    for(int l=0;l<edges.size();l++) {
                        Edge edge = edges.get(l);
                        for (int j = 0; j < group.size(); j++) {
                            for (int k = j + 1; k < group.size(); k++) {
                                //System.out.println(nodeSize*(group.get(j) - 1) +"_"+(group.get(k) - 1));
//                                if (checkUFPlace(group.get(j) - 1, group.get(k) - 1, result)) {
//                                    missCircle.addTerm(1.0, x[nodeSize * (group.get(j) - 1) + (group.get(k) - 1)]);
//                                }
                                if (checkUFEdge(group.get(j) - 1, group.get(k) - 1,edge)) {
                                    missCircle.addTerm(1.0, x[nodeSize * (group.get(j) - 1) + (group.get(k) - 1)]);
                              }
//                                if(j<k){
//                                    missCircle.addTerm(1.0, x[nodeSize * (group.get(j) - 1) + (group.get(k) - 1)]);
                              //}
                            }
                        }
                    }

                    this.add(cplex.le(missCircle, group.size()-1));
                    //cplex.addEq(missCircle,places.size()-1);
                }
            }
            } catch (IloException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public boolean checkUFPlace(int x,int y,List<Place> places){
//        for(int i=0;i<places.size();i++){
//            for(int j=0;j<places.size();i++){
//                int iMap = map.get(places.get(i).getPlace());
//                int jMap = map.get(places.get(j).getPlace());
//                if(x == iMap && y == jMap){
//                    return true;
//                }else if(x == jMap && y == iMap){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    public boolean checkUFEdge(int x,int y,Edge edge){
            int origin = map.get(edge.getOrigin());
            int dest = map.get(edge.getDestination());
            if(x == origin && y == dest){
                return true;
            }else if(x == origin && y == dest){
                return true;
            }
        return false;
    }

}

