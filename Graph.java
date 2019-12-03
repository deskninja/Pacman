package assignment10;

import components.map.Map;
import components.map.MapOnHashTable;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

import java.util.Comparator;

public class Graph {
    private class Node{
        String prev = "";
        int dist = INFINITY;
        int timesVisited;
        boolean visited; //maybe unnecessary
        int indexInMatrix;
        String position;

        public Node(int indexInMatrix, String position) {
            this.indexInMatrix = indexInMatrix;
            this.position = position;
        }
    }

    private static final int INFINITY = Integer.MAX_VALUE;
    private char[][] maze; //stores the maze
    private int[][] matrix; //stores connections to other Nodes
    private Map<String, Integer> nodeMap; //position, nodeId counting up
    private Map<Integer, String> intNodeMap; //nodeId counting up, position
    private Map<String, Node> shortestPathMap; //indexInMatrix, Node where prev leads back to start
    int cols; //x value for size
    int rows; //y value for size
    String start; //position of start Node
    String end; //position of end Node
    int nodeId;

    public Graph(String file) {
        SimpleReader in = new SimpleReader1L(file);
        int[] position = newPosition();
        String line = in.nextLine();
        this.cols = Integer.parseInt(line.split(" ")[0]);
        this.rows = Integer.parseInt(line.split(" ")[1]);
        resize();
        while (!in.atEOS()) {
            line = in.nextLine();
            char[] nodes = line.toCharArray();
            for (char node : nodes) {
                if (node == ' ' || node == 'X') {
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    position = nextPosition(position);
                }
                if (node == 'S') {
                    this.start = arrayToString(position);
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    position = nextPosition(position);
                }
                if (node == 'G') {
                    this.end = arrayToString(position);
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    position = nextPosition(position);
                }
            }
        }
    }

    private int[] newPosition() {
        int[] position = new int[2];
        position[0] = 0;
        position[1] = 0;
        return position;
    }

    private int[] newPosition(String posit) {
        int[] position = new int[2];
        int temp = Integer.parseInt(posit.split(" ")[0]);
        position[0] = temp;
        temp = Integer.parseInt(posit.split(" ")[1]);
        position[1] = temp;
        return position;
    }

    private int[] nextPosition(int[] position) {
        assert cols > 0 : "Violation of: graph is bigger than 0";
        if (position[0] + 1 >= cols) {
            position[1]++;
            position[0] = 0;
        }
        else
            position[0]++;
        return position;
    }

    private String arrayToString(int[] position) {
        return position[0] + " " + position[1];
    }

    private void resize(){
        int numVertices = rows * cols;
        this.maze = new char[rows][cols];
        this.matrix = new int[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; i++) {
                matrix[i][j] = INFINITY;
            }
        }
        nodeMap = new MapOnHashTable<>();
        intNodeMap = new MapOnHashTable<>();
        nodeId = 0;
    }

    private void addNode(int[] position) {
        nodeMap.add(arrayToString(position), nodeId);
        intNodeMap.add(nodeId, arrayToString(position));
        nodeId++;
    }

    private void addEdges(int[] postion) {
        int[] temp = newPosition();
        temp[0] = postion[0];
        temp[1] = postion[1] - 1;
        checkPosition(postion, temp);
        temp[1] += 2;
        checkPosition(postion, temp);
        temp[1] += 1;
        temp[0] -= 1;
        checkPosition(postion, temp);
        temp[0] += 2;
        checkPosition(postion, temp);
    }

    private void checkPosition(int[] position, int[] temp) {
        if (validPosition(temp) && maze[temp[0]][temp[1]] != 'X') {
            addEdge(arrayToString(position), arrayToString(temp));
        }
    }

    private boolean validPosition(int[] position) {
        if (position[0] >= this.rows || position[0] < 0 || position[1] >= this.cols || position[1] < 0)
            return false;
        return true;
    }

    private void addEdge(String src, String dst) {
        assert nodeMap.hasKey(src) : "Violation of : src is a node in the graph";
        assert nodeMap.hasKey(dst) : "Violation of : dst is a node in the graph";
        int srcIndex = nodeMap.value(src);
        int dstIndex = nodeMap.value(dst);
        matrix[srcIndex][dstIndex] = 1;
    }

    private void dijkstra() {
        Comparator<Node> compareDistOfNodes = (v1, v2) -> Integer.compare(v1.dist, v2.dist);
        MyQueue<Node> pq = new MyQueue<Node>(compareDistOfNodes);
        shortestPathMap.clear();
        int numNodes = matrix.length;
        for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++) {
            Node n = new Node(nodeIndex, intNodeMap.value(nodeIndex));
            shortestPathMap.add(n.position, n);
            if (n.position.equals(this.start))
                n.dist = 0;
            pq.enqueue(n);
        }

        while (pq.size() > 0) {
            Node u = pq.dequeue();

            for (int n = 0; n < numNodes; n++) {
                if (matrix[u.indexInMatrix][n] < INFINITY && u.dist < INFINITY) {
                    int alt = u.dist + matrix[u.indexInMatrix][n];
                    Node nodeN = shortestPathMap.value(u.position);
                    if (alt < nodeN.dist) {
                        nodeN.dist = alt;
                        nodeN.prev = u.position;
                    }
                }
            }
        }
    }

    private int shortestPathCost() {
        dijkstra();
        int dstIdx = nodeMap.value(this.end);
        Node sspNode = shortestPathMap.value(this.end);
        return sspNode.dist;
    }

    private String shortestPath () {
        int dstIdx = nodeMap.value(this.end);
        Node sspNode = shortestPathMap.value(this.end);
        String result = "";
        if (sspNode.dist < INFINITY) {
            while (sspNode.prev != "") {
                result += sspNode.position + " ";
                sspNode = shortestPathMap.value(sspNode.prev);
            }
            result += sspNode.position;
        }
        return result;
    }
}