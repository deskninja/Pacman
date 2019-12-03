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
    int size;

    public Graph(String file) {
        SimpleReader in = new SimpleReader1L(file);
        int[] position = newPosition();
        String line = in.nextLine();
        this.rows = Integer.parseInt(line.split(" ")[0]);
        this.cols = Integer.parseInt(line.split(" ")[1]);
        resize();
        while (!in.atEOS()) {
            line = in.nextLine();
            char[] nodes = line.toCharArray();
            for (char node : nodes) {
                if (node == ' ' || node == 'X') {
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    nextPosition(position);
                }
                if (node == 'S') {
                    this.start = arrayToString(position);
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    nextPosition(position);
                }
                if (node == 'G') {
                    this.end = arrayToString(position);
                    addNode(position);
                    maze[position[0]][position[1]] = node;
                    nextPosition(position);
                }
            }
        }
        position = newPosition();
        for (int i = 0; i < (this.rows - 1) * (this.cols - 1); i++) {
            if (maze[position[0]][position[1]] == 'G' ||
                    maze[position[0]][position[1]] == 'S' ||
                    maze[position[0]][position[1]] == ' ')
                addEdges(position);
            nextPosition(position);
        }
        size = shortestPathCost();
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
        if (position[1] + 1 >= cols) {
            position[0]++;
            position[1] = 0;
        }
        else
            position[1]++;
        return position;
    }

    private String arrayToString(int[] position) {
        return position[0] + " " + position[1];
    }

    private void resize(){
        int numVertices = (rows) * (cols);
        this.maze = new char[rows][cols];
        this.matrix = new int[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                matrix[i][j] = INFINITY;
            }
        }
        nodeMap = new MapOnHashTable<>();
        intNodeMap = new MapOnHashTable<>();
        shortestPathMap = new MapOnHashTable<>();
        nodeId = 0;
    }

    private void addNode(int[] position) {
        nodeMap.add(arrayToString(position), nodeId);
        intNodeMap.add(nodeId, arrayToString(position));
        nodeId++;
    }

    private void addEdges(int[] position) {
        int[] temp = newPosition();
        //check left
        temp[0] = position[0];
        temp[1] = position[1] - 1;
        checkPosition(position, temp);
        //check right
        temp[1] = position[1] + 1;
        checkPosition(position, temp);
        //check up
        temp[0] = position[0] - 1;
        temp[1] = position[1];
        checkPosition(position, temp);
        //check down
        temp[0] = position[0] + 1;
        checkPosition(position, temp);
    }

    private void checkPosition(int[] position, int[] temp) {
        if (validPosition(temp) && maze[temp[0]][temp[1]] != 'X') {
            addEdge(arrayToString(position), arrayToString(temp));
        }
    }

    private boolean validPosition(int[] position) {
        if (position[0] <= this.maze.length - 1 && position[0] >= 0 && position[1] <= this.maze[0].length - 1 && position[1] >= 0)
            return true;
        return false;
    }

    private void addEdge(String src, String dst) {
        assert nodeMap.hasKey(src) : "Violation of : src is a node in the graph";
        assert nodeMap.hasKey(dst) : "Violation of : dst is a node in the graph";
        int sIndex = nodeMap.value(src);
        int dIndex = nodeMap.value(dst);

        matrix[sIndex][dIndex] = 1;
    }

    private int positionToInt(int[] position) {
        int num = position[0] * cols + position[1];
        return num;
    }

    private void dijkstra() {
        Comparator<Node> compareDistOfNodes = (v1, v2) -> Integer.compare(v1.dist, v2.dist);
        MyQueue<Node> pq = new MyQueue<>(compareDistOfNodes);
        int[] position = newPosition();
        shortestPathMap.clear();

        while (validPosition(position)) {
            Node n = new Node(positionToInt(position), arrayToString(position));
            shortestPathMap.add(n.position, n);
            if (n.position.equals(this.start))
                n.dist = 0;
            pq.enqueue(n);
            nextPosition(position);
        }

        while (pq.size() > 0) {
            Node u = pq.dequeue();

            for (int n = 0; n < matrix.length; n++) {
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
        Node sspNode = shortestPathMap.value(this.end);
        return sspNode.dist;
    }

    public char[][] shortestPath () {
        Node sspNode = shortestPathMap.value(this.end);
        if (sspNode.dist < INFINITY) {
            while (sspNode.prev != "") {
                int[] position = newPosition(sspNode.position);
                maze[position[0]][position[1]] = '.';
                sspNode = shortestPathMap.value(sspNode.prev);
            }
        }
        return maze;
    }

    public int getSize(){
        return this.size;
    }
}