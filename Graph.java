package assignment10;

public class Graph {
    private class Node{
        //TODO: position in graph
        Boolean visited;
        int lookedAt;
        Node north;
        Node south;
        Node east;
        Node west;
        int distance;

        private final int INFIFITY = Integer.MAX_VALUE;

        public Node(){
            this.distance = INFIFITY;
        }
    }


    public Graph() {
    }
    //TODO: add russian method
}
