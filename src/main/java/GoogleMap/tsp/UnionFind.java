package GoogleMap.tsp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syun on 2017/12/20.
 */
public class UnionFind {
    int[] parent;
    int size;

    public UnionFind(int size){
        this.parent = new int[size+1];
        for (int i = 1; i < parent.length; i++) {
            parent[i] = i;
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int[] getParent() {
        return parent;
    }

    public int getParentLength(){
        return parent.length;
    }

    public void makeSet(int x){
        parent[x] = 0;
    }

//    public void union(int x,int y){
//        int ufX = x + 1;
//        int ufY = y + 1;
//        int xFind = find(x);
//        int yFind = find(y);
//
//        if(size > 1) {
//            if(xFind ==0 && yFind ==0) {
//                parent[ufY] = ufX;
//                parent[ufX] -= 1;
//                size -= 1;
//            }else if(xFind < 0 && yFind < 0){
//                parent[ufY] = ufX;
//                parent[ufX] -= 1;
//                size -= 1;
//            }else if (xFind < 0) {
//                parent[ufY] = ufX;
//                parent[ufX] -= 1;
//                size -= 1;
//            }else if(yFind < 0) {
//                parent[ufX] = ufY;
//                parent[ufY] -= 1;
//                size -= 1;
//            }
//
////            }else if(xFind == yFind){
////                parent[ufY] = ufX;
////                //parent[x] = yFind;
////                parent[ufX] -= 1;
////                size -= 1;
////            }
//            //xfind == yFindの時はsize -= 1しない
//        }
//    }

//    public int find(int x){
//        int element = parent[x+1];
//        int result = 0;
//        while(element > 0){
//            result = element;
//            element = parent[element];
//        }
//        //parent[x] = result;
//        return result;
//    }

    public int find(int x){
        if(parent[x] == x) return x;
        parent[x] = find(parent[x]);
        return parent[x];
    }

    public void union(int x,int y) {
        if(find(x) == find(y)) return;
        parent[find(x)] = find(y);
    }

    public boolean same(int x,int y){
        return find(x) == find(y);
    }

    public List<Integer> countGroupElement(){
        //int count = 0;
        List<Integer> groupElement = new ArrayList<>();
        for(int i = 1;i<parent.length;i++){
            if(parent[i] == i){
                //count++;
                groupElement.add(i);
            }
        }
        return groupElement;
    }

    public List<List<Integer>> groupCut(List<Integer> groupElement){
        List<List<Integer>> result = new ArrayList<>();
        for(int i = 0;i<groupElement.size();i++) {
            List<Integer> group = new ArrayList<>();
            //group.add(groupElement.get(i));
            for(int j=0;j<parent.length;j++){
                if(groupElement.get(i) == find(j)){
                    group.add(j);
                }
            }
            result.add(group);
        }
        return result;
    }

    public void ufCount(){
        int count=0;
        for(int i=0;i<parent.length;i++){
            if(parent[i]<0){
                System.out.println("Index"+i+"Parent"+parent[i]);
                count++;
            }
        }
    }

    public boolean isConnect() {
        for (int i = 1; i < parent.length - 1; i++) {
            for (int j = i + 1; j < parent.length; j++) {
                if (!same(i, j)) return false;
            }
        }
        return true;
    }
}
