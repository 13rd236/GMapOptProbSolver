package GoogleMap;

import java.util.ArrayList;
import java.util.List;

public class SpaningTreeDriver {
    List<Edge> edges;
    List<String> vertex;

    public SpaningTreeDriver(List<Edge> edges, List<String> vertex) {
        this.edges = edges;
        this.vertex = new ArrayList<>(vertex);
    }

    public List<Edge> run() {
        List<String> visited = new ArrayList<String>();
        List<Edge> A = new ArrayList<Edge>();
        String u, v = null;

        u = edges.get(0).origin;
        visited.add(u);
        vertex.remove(u);

        while (vertex.size() > 0) {
            for (int i = 0; i < edges.size(); i++) {
                Edge e = edges.get(i);
                u = e.getOrigin();
                v = e.getDestination();
                if (InclusionString(u, visited)) {
                    if (!InclusionString(v, visited)) {
                        A.add(e);
                        edges.remove(e);
                        visited.add(v);
                        vertex.remove(v);
                        break;
                    }
                } else if (InclusionString(v, visited)) {
                    if (!InclusionString(u, visited)) {
                        A.add(e);
                        edges.remove(e);
                        visited.add(u);
                        vertex.remove(u);
                        break;
                    }
                }
            }
        }
        printEdges(A);
        return A;
    }


    public void printEdges(List<Edge> A){
        double total=0;

        System.out.println("answer");
        for(int i=0;i<A.size();i++){
            System.out.println(A.get(i).origin+ ":"+A.get(i).destination+" = "+A.get(i).distance);
            total += A.get(i).distance;
        }
        System.out.println("Total:" + total);
    }

    public boolean InclusionString(String u,List<String> s){
        for(int i=0;i<s.size();i++){
            if(u.equals(s.get(i))){
                return true;
            }
        }
        return false;
    }

}
