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
    private int[][] matrix;
    private Map<String, Integer> nodeMap; //position, nodeId counting up
    private Map<Integer, String> intNodeMap; //nodeId counting up, position
    private Map<Integer, Node> shortestPathMap; //indexInMatrix, Node where prev leads back to start
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
                    position = nextPosition(position);
                }
                if (node == 'S') {
                    this.start = arrayToString(position);
                    addNode(position);
                    position = nextPosition(position);
                }
                if (node == 'G') {
                    this.end = arrayToString(position);
                    addNode(position);
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
}