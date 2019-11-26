package assignment10;

import components.list.List;
import components.list.ListOnJavaArrayList;
import components.map.Map;
import components.map.MapOnHashTable;
import components.queue.PriorityQueue;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

import java.util.Comparator;

public class Graph {
    private class Node{
        /**
         * check to see if Node has been visited before
         */
        Boolean visited;

        /**
         * states if this is an X in the maze
         */
        Boolean x;
        /**
         * counts how many times this node was looked at. created for analysis document
         */
        int lookedAt;
        /**
         * size of the path so far
         */
        int dist = INFINITY;
        /**
         * previous Node in the path
         */
        int[] prev;
        int[] indexInMatrix;

        public Node(int x, int y) {
            this.indexInMatrix = new int[2];
            this.indexInMatrix[0] = x;
            this.indexInMatrix[1] = y;
        }
    }


    private int[][] matrix;
    private Map<int[], Integer> nodeMap; //int[] is the position in the maze, Integer is the cost of the path
    private Map<Integer, int[]> intNodeMap;
    private Map<int[], Node> shorestPathMap;
    private int[] sspSource;
    private int nodeId;
    private int size;
    private int x;
    private int y;

    private final int INFINITY = Integer.MAX_VALUE;
    private static final int UNDEFINED = Integer.MIN_VALUE;

    /**
     * Resets the matrix to the given number of nodes and resets the other fields
     * to their initial values.
     *
     * @param numNodes number of nodes for the new graph
     * @modifies this
     */
    private void resize(int numNodes) {
        matrix = new int[numNodes][4]; //one for each possible edge
        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < 4; j++)
                matrix[i][j] = INFINITY;
        nodeMap = new MapOnHashTable<>();
        intNodeMap = new MapOnHashTable<>();
        shorestPathMap = new MapOnHashTable<>();
        sspSource = null;
        nodeId = 0;
    }

    private List<String> intListToStringList(List<Integer> idxResult) {
        //TODO: make this return the formatted output file
        List<String> result = new ListOnJavaArrayList<>();
        return result;
    }

    /**
     * No argument constructor.
     *
     * @param numNodes number of nodes
     */
    public Graph(int numNodes) {
        resize(numNodes);
    }

    /**
     * Constructor from a file.
     *
     * @param file path of a file in the specified format
     * @requires the file is in this format: first line is an integer, indicating
     *           number of nodes (n), followed by n lines, each containing an
     *           edge in this comma-separated format: (src,dst,cost)
     */
    public Graph(String file) {
        // precondition not checked
        SimpleReader in = new SimpleReader1L(file);
        String line = in.nextLine();
        this.x = Integer.parseInt(line.split(" ")[0]);
        this.y = Integer.parseInt(line.split(" ")[1]);
        int[] position = newPositon();
        resize(x * y);
        while (!in.atEOS()) {
            line = in.nextLine();
            char[] edgeParts = line.toCharArray();
            for (char c : edgeParts) {
                if (c != 'X') {
                    addNode(position);
                }
                nextPosition(x, position);
            }
        }
        in.close();
    }

    private void nextPosition(int x, int[] position){
        if (position[0] + 1 == x) {
            position[1]++;
            position[0] = 0;
        }
    }

    private int[] newPositon() {
        int[] x = new int[2];
        x[0] = 0;
        x[1] = 0;
    }

    /**
     * Adds a new node with the given label to this graph.
     *
     * @param position label for the new node and the position in the maze
     *
     * @requires label is not a node of this graph
     * @modifies this
     */
    public void addNode(int[] position) {
        assert !nodeMap
                .hasKey(position) : "Violation of: label is not a node of this graph";

        nodeMap.add(position, nodeId);
        intNodeMap.add(nodeId, position);
        sspSource = null;// adding a node invalidates previous ssp run
        nodeId++;
    }

    /**
     * Adds the edge (src, dst, cost) to this graph.
     *
     * @param src  source node label
     * @param dst  destination node label
     * @requires [edge cost is non-negative] and [src and dst are nodes in this
     *           graph]
     * @modifies this
     */
    public void addEdge(int[] src, int[] dst) {
        assert nodeMap
                .hasKey(src) : "Violation of: src is a node already in the graph";
        assert nodeMap
                .hasKey(dst) : "Violation of: dst is a node already in the graph";

        int srcIndex = nodeMap.value(src);
        int dstIndex = nodeMap.value(dst);
        matrix[srcIndex][dstIndex] = 1;
        sspSource = null; // adding an edge invalidates previous ssp run
    }

    public int getSize() {
        return size;
    }

    /**
     * Computes the single source shortest paths from the given source node.
     *
     * @param src label of the start node
     * @requires [src is a node in this graph]
     * @modifies this
     */
    public void dijkstra(int[] src) {

        int srcIdx = nodeMap.value(src);

        Comparator<Node> compareDistOfVerts = (v1, v2) -> Integer
                .compare(v1.dist, v2.dist);
        Queue<Node> pq = new PriorityQueue<Node>(compareDistOfVerts);

        shorestPathMap.clear();

        int numVerts = matrix.length;
        int[] position = newPositon();
        for (int currNodeIdx = 0; currNodeIdx < numVerts; currNodeIdx++) {
            Node v = new Node(position[0], position[1]);
            shorestPathMap.add(position, v);
            nextPosition(x, position);
            if (currNodeIdx == srcIdx)
                v.dist = 0;
            pq.enqueue(v);
        }
        position = newPositon();
        while (pq.size() > 0) {
            Node u = pq.dequeue();
            for (int v = 0; v < numVerts; v++) {
                if (matrix[position[0] + (x * position[1])][v] < INFINITY && u.dist < INFINITY) {
                    // if an edge exists and the node itself is reachable from the src
                    int alt = u.dist + matrix[position[0] + (x * position[1])][v];
                    Node nodeV = shorestPathMap.value(position);
                    if (alt < nodeV.dist) {
                        nodeV.dist = alt;
                        nodeV.prev = u.indexInMatrix;
                    }
                }
            }
            nextPosition(x, position);
        }
        sspSource = src;
    }


    /**
     * Returns the shortest path cost from {@code src} to {@code dst}. Invokes
     * dijkstra if its last run was not with {@code src}.
     *
     * @param src source node label
     * @param dst destination node label
     * @return shortest path cost, {@code INFINITY} if {@code dst} is not reachable
     *         from {@code src}
     * @requires [src is a node in this graph] and [dst is a node in this graph]
     * @modifies this
     */
    public int shortestPathCost(int[] src, int[] dst) {
        if (!src.equals(sspSource))
            dijkstra(src);
        int dstIdx = nodeMap.value(dst);
        Node sspNode = shorestPathMap.value(toIntArr(dstIdx));
        return sspNode.dist;
    }

    private int[] toIntArr(int x){
        int[] arr = newPositon();
        if(x > this.x - 1){
            arr[1] = x/(this.x - 1);
        }
        arr[0] = x % (this.x - 1);
    }

    /**
     * Returns the description of the shortest path from {@code src} to {@code dst}.
     * Invokes dijkstra if its last run was not with {@code src}.
     *
     * @param src source node label
     * @param dst destination node label
     * @return shortest path cost and the actual path
     * @requires [src is a node in this graph] and [dst is a node in this graph]
     * @modifies this
     */
    public String shortestPath(int[] src, int[] dst) {
        assert nodeMap
                .hasKey(src) : "Violation of: src is a node in this graph";
        assert nodeMap
                .hasKey(dst) : "Violation of: dst is a node in this graph";

        List<int[]> path = new ListOnJavaArrayList<>();
        if (!src.equals(sspSource))
            dijkstra(src);
        int dstIdx = nodeMap.value(dst);
        Node sspNode = shorestPathMap.value(toIntArr(dstIdx));

        if (sspNode.dist < INFINITY) {
            String result = "Shortest path cost = " + sspNode.dist + ", Path = ";
            while (sspNode.prev != null) {
                //add plus one to the size for every node in the path
                this.size++;
                path.add(0, sspNode.indexInMatrix);
                sspNode = shorestPathMap.value(sspNode.prev);
            }
            path.add(0, nodeMap.value(toIntArr(src)));
            return result + intListToStringList(path).toString();
        } else {
            return dst + " is not reachable from " + src;
        }
    }

    public String shortestPath(){
        return shortestPath("S", "G");
    }
    
    @Override
    public String toString() {
        int numNodes = intNodeMap.size();
        StringBuilder sb = new StringBuilder();
        sb.append("digraph g {\n");
        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < numNodes; j++)
                if (matrix[i][j] != INFINITY) {
                    sb.append(String.format("%s->%s [label=%d];\n",
                            intNodeMap.value(i), intNodeMap.value(j), matrix[i][j]));
                }
        sb.append("}\n");
        return sb.toString();
    }
}