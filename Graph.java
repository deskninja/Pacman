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
         * counts how many times this node was looked at. created for analysis document
         */
        int lookedAt;
        int dist = INFINITY;
        int prev = UNDEFINED;
        int indexInMatrix;

        public Node(int indexInMatrix) {
            this.indexInMatrix = indexInMatrix;
        }
    }

    /*
     * Private members
     */
    private int[][] matrix;
    private Map<String, Integer> nodeMap;
    private Map<Integer, String> intNodeMap;
    private Map<Integer, Node> shorestPathMap;
    private String sspSource;
    private int nodeId;
    private int size;

    private final int INFINITY = Integer.MAX_VALUE;
    private static final int UNDEFINED = Integer.MIN_VALUE;

    public Graph() {

    }

    /**
     * Resets the matrix to the given number of nodes and resets the other fields
     * to their initial values.
     *
     * @param numNodes number of nodes for the new graph
     * @modifies this
     */
    private void resize(int numNodes) {
        matrix = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < numNodes; j++)
                matrix[i][j] = INFINITY;
        nodeMap = new MapOnHashTable<>();
        intNodeMap = new MapOnHashTable<>();
        shorestPathMap = new MapOnHashTable<>();
        sspSource = "";
        nodeId = 0;
    }

    private List<String> intListToStringList(List<Integer> idxResult) {
        List<String> result = new ListOnJavaArrayList<>();
        for (Integer idx : idxResult)
            result.add(intNodeMap.value(idx));
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
        int numNodes = Integer.parseInt(in.nextLine());
        resize(numNodes);
        while (!in.atEOS()) {
            String line = in.nextLine();
            String[] edgeParts = line.split(",");
            String src = edgeParts[0];
            if (!nodeMap.hasKey(src))
                addNode(src);
            String dst = edgeParts[1];
            if (!nodeMap.hasKey(dst))
                addNode(dst);
            int cost = Integer.parseInt(edgeParts[2]);
            addEdge(src, dst, cost);
        }
        in.close();
    }

    /**
     * Adds a new node with the given label to this graph.
     *
     * @param label label for the new node
     *
     * @requires label is not a node of this graph
     * @modifies this
     */
    public void addNode(int position) {
        assert !nodeMap
                .hasKey(label) : "Violation of: label is not a node of this graph";

        nodeMap.add(label, nodeId);
        intNodeMap.add(nodeId, label);
        sspSource = "";// adding a node invalidates previous ssp run
        nodeId++;
    }

    /**
     * Adds the edge (src, dst, cost) to this graph.
     *
     * @param src  source node label
     * @param dst  destination node label
     * @param cost edge weight
     * @requires [edge cost is non-negative] and [src and dst are nodes in this
     *           graph]
     * @modifies this
     */
    public void addEdge(String src, String dst, int cost) {
        assert cost >= 0 : "Violation of: edge cost is non negative";
        assert nodeMap
                .hasKey(src) : "Violation of: src is a node already in the graph";
        assert nodeMap
                .hasKey(dst) : "Violation of: dst is a node already in the graph";

        int srcIndex = nodeMap.value(src);
        int dstIndex = nodeMap.value(dst);
        matrix[srcIndex][dstIndex] = cost;
        sspSource = "";// adding an edge invalidates previous ssp run
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
    public void dijkstra(String src) {

        int srcIdx = nodeMap.value(src);

        Comparator<Node> compareDistOfVerts = (v1, v2) -> Integer
                .compare(v1.dist, v2.dist);
        Queue<Node> pq = new PriorityQueue<Node>(compareDistOfVerts);

        shorestPathMap.clear();

        int numVerts = matrix.length;
        for (int currNodeIdx = 0; currNodeIdx < numVerts; currNodeIdx++) {
            Node v = new Node(currNodeIdx);
            shorestPathMap.add(currNodeIdx, v);
            if (currNodeIdx == srcIdx)
                v.dist = 0;
            pq.enqueue(v);
        }

        while (pq.size() > 0) {
            Node u = pq.dequeue();
            System.out.println("node: " + u.indexInMatrix + " is " + intNodeMap.value(u.indexInMatrix));
            for (int v = 0; v < numVerts; v++) {
                if (matrix[u.indexInMatrix][v] < INFINITY && u.dist < INFINITY) {
                    // if an edge exists and the node itself is reachable from the src
                    int alt = u.dist + matrix[u.indexInMatrix][v];
                    Node nodeV = shorestPathMap.value(v);
                    if (alt < nodeV.dist) {
                        nodeV.dist = alt;
                        nodeV.prev = u.indexInMatrix;
                    }
                }
            }
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
    public int shortestPathCost(String src, String dst) {
        assert nodeMap
                .hasKey(src) : "Violation of: src is a node in this graph";
        assert nodeMap
                .hasKey(dst) : "Violation of: dst is a node in this graph";

        if (!src.equals(sspSource))
            dijkstra(src);
        int dstIdx = nodeMap.value(dst);
        Node sspNode = shorestPathMap.value(dstIdx);
        return sspNode.dist;
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
    public String shortestPath(String src, String dst) {
        assert nodeMap
                .hasKey(src) : "Violation of: src is a node in this graph";
        assert nodeMap
                .hasKey(dst) : "Violation of: dst is a node in this graph";

        List<Integer> path = new ListOnJavaArrayList<>();
        if (!src.equals(sspSource))
            dijkstra(src);
        int dstIdx = nodeMap.value(dst);
        Node sspNode = shorestPathMap.value(dstIdx);

        if (sspNode.dist < INFINITY) {
            String result = "Shortest path cost = " + sspNode.dist + ", Path = ";
            while (sspNode.prev != UNDEFINED) {
                //add plus one to the size for every node in the path
                this.size++;
                path.add(0, sspNode.indexInMatrix);
                sspNode = shorestPathMap.value(sspNode.prev);
            }
            path.add(0, nodeMap.value(src));
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