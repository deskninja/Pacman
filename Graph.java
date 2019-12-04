package assignment10;

import components.map.Map;
import components.map.MapOnHashTable;
import components.queue.PriorityQueue;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

import java.util.ArrayList;
import java.util.Comparator;

public class Graph {
    private class Node{
        Node prev;
        int dist = INFINITY;
        int timesVisited;
        int indexInMatrix;
        String position;

        public Node(int indexInMatrix, String position) {
            this.indexInMatrix = indexInMatrix;
            this.position = position;
        }
    }

    private static final int INFINITY = Integer.MAX_VALUE;
    private char[][] maze; //stores the maze
    private char[][] newMaze; //maze with the path
    private int[][] matrix; //stores connections to other Nodes
    private Map<String, Integer> nodeMap; //position, nodeId counting up
    private Map<Integer, String> intNodeMap; //nodeId counting up, position
    private ArrayList<Node> shortestPathMap;
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
        for (int i = 0; i < (this.rows) * (this.cols); i++) {
            if (maze[position[0]][position[1]] == 'G' ||
                    maze[position[0]][position[1]] == 'S' ||
                    maze[position[0]][position[1]] == ' ')
                addEdges(position);
            nextPosition(position);
        }
        shortestPath();
    }

    public char[][] getMaze() {
        return maze;
    }

    public int getSize(){
        return this.size;
    }

    public int averageTimesVisited() {
        int times = 0;
        for (Node n :
                shortestPathMap) {
            times += n.timesVisited;
        }
        return times / shortestPathMap.size();
    }

    public int mazeSize() {
        return rows * cols;
    }

    public int notXNodes() {
        int notX = 0;
        for (Node n :
                shortestPathMap) {
            if (n.dist < INFINITY)
                notX++;
        }
        return notX;
    }

    private int[] stringToPosition(String s) {
        int[] a = newPosition();
        a[0] = Integer.parseInt(s.split(" ")[0]);
        a[1] = Integer.parseInt(s.split(" ")[1]);
        return a;
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
        shortestPathMap = new ArrayList<>();
        nodeId = 0;
    }

    private void addNode(int[] position) {
        nodeMap.add(arrayToString(position), nodeId);
        intNodeMap.add(nodeId, arrayToString(position));
        Node n = new Node(nodeId, arrayToString(position));
        shortestPathMap.add(n);
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
        if (position[0] < this.maze.length && position[0] >= 0 && position[1] < this.maze[0].length && position[1] >= 0)
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

        Node start = shortestPathMap.get(positionToInt(stringToPosition(this.start)));
        start.dist = 0;
        for (Node n :
                shortestPathMap) {
            pq.enqueue(n);
        }

        while (pq.size() > 0) {
            pq.changeOrder(compareDistOfNodes);
            Node u = pq.dequeue();

            for (int n = 0; n < matrix.length; n++) {
                if (matrix[u.indexInMatrix][n] < INFINITY && u.dist < INFINITY) {
                    int alt = u.dist + 1;
                    Node nodeN = shortestPathMap.get(n);
                    nodeN.timesVisited++;
                    if (alt < nodeN.dist) {
                        nodeN.dist = alt;
                        nodeN.prev = u;
                    }
                }
            }
        }
    }

    private void shortestPath () {
        dijkstra();
        int[] position;
        Node sspNode = shortestPathMap.get(positionToInt(stringToPosition(this.end)));
        if (sspNode.dist < INFINITY) {
            while (sspNode.prev != null) {
                this.size++;
                position = newPosition(sspNode.position);
                maze[position[0]][position[1]] = '.';
                sspNode = sspNode.prev;
            }
            position = stringToPosition(this.end);
            maze[position[0]][position[1]] = 'G';
        }
        else
            size = -1;

        char[][] newMaze = new char[rows + 1][cols + 1];
        newMaze[0] = (this.rows + " " + this.cols).toCharArray();
        position = newPosition();
        position[0]++;
        for (char[] ca :
                maze) {
            for (char c:
                 ca){
                newMaze[position[0]][position[1]] = c;
                nextPosition(position);
            }
        }
        this.newMaze = newMaze;
    }
}