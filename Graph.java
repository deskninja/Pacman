package assignment10;

public class Graph {
    private class Node{
        //TODO: position in graph
        /**
         * check to see if Node has been visited before
         */
        Boolean visited;
        /**
         * counts how many times this node was looked at. created for analysis document
         */
        int lookedAt;
        
        /**
         * valid directional Nodes
         */
        Node north;
        Node south;
        Node east;
        Node west;
        /**
         * keep track of shortest distance from start
         */
        int distance;

        private final int INFINITY = Integer.MAX_VALUE;

        public Node(){
            this.distance = INFINITY;
        }
    }


    public Graph() {
    }
    //TODO: add russian method
}
