package assignment10;

import components.map.Map;
import components.map.MapOnHashTable;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class solves a maze from a text file.
 * Format must be:
 * top line is number of rows space number of columns
 * lines below that are the maze
 *  edges are X
 *  S is the start
 *  G is the end
 *  space char is an open space
 *  X char is a wall
 *
 * @author Jonathan Oliveros and Joshua Wells
 */
public class Graph {

    /**
     * made for each char in the maze
     * tracks and stores information for the Graph class to use
     *
     * @author Jonathan Oliveros and Joshua Wells
     */
    private class Node{
        /**
         * Node with an edge to this Node and the smallest {@code dist}
         */
        Node prev;
        /**
         * distance from this Node to the start Node
         */
        int dist = INFINITY;
        /**
         * number of times this Node is checked to see if it is closer than the current path
         */
        int timesVisited;
        /**
         * index of the record for this Node's edges in {@code matrix}
         */
        int indexInMatrix;
        /**
         * position of this Node in {@code maze}
         * format is row space column
         * example: "1 2"
         */
        String position;

        /**
         * sole constructor for Node
         *
         * @param indexInMatrix int index in {@code matrix}
         * @param position String position in {@code maze}
         */
        public Node(int indexInMatrix, String position) {
            this.indexInMatrix = indexInMatrix;
            this.position = position;
        }
    }

    /**
     * constant for setting {@code dist} in Node objects
     */
    private static final int INFINITY = Integer.MAX_VALUE;
    /**
     * stores chars from the text file maze
     */
    private char[][] maze;
    /**
     * maze from the text file with "." in place of " " to represent a shortest path solution to the maze
     */
    private char[][] newMaze;
    /**
     * stores connections between nodes as 1 iff connected or {@code INFINITY} iff not connected
     */
    private int[][] matrix;
    /**
     * map of position in maze and position in matrix
     */
    private Map<String, Integer> nodeMap;
    /**
     * map of position in matrix and position in maze
     */
    private Map<Integer, String> intNodeMap;
    /**
     * map of Nodes where {@code Node.prev} leads to start iff a path is possible
     */
    private ArrayList<Node> shortestPathMap;
    /**
     * x value of maze size from text file
     */
    int cols;
    /**
     * y value of maze size from text file
     */
    int rows;
    /**
     * position instance variable of the start Node
     */
    String start;
    /**
     * position instance variable of the end Node
     */
    String end;
    /**
     * int used to track indexInMatrix of each node
     */
    int nodeId;
    /**
     * dist instance variable of the end Node after the path has been found
     * -1 if there was no path found
     */
    int size;

    /**
     * sole constructor
     *
     * @param file text file to be read
     * @requires first line is the number of rows then columns separated by a space
     * @requires second line to end is the text representation of the maze
     * @requires X represents a wall, a space represents an open space, S represents the start, and G
     *      represents the end.
     * @requires the edges of the maze are all X and the maze contains one S and one G
     */
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
        in.close();
        
        position = newPosition();
        for (int i = 0; i < (this.rows) * (this.cols); i++) {
            if (maze[position[0]][position[1]] == 'G' ||
                    maze[position[0]][position[1]] == 'S' ||
                    maze[position[0]][position[1]] == ' ')
                addEdges(position);
            nextPosition(position);
        }
        //updates {@code shortestPathMap} to be accurate to this maze
        shortestPath();
    }

    /**
     * @return newMaze with the path from start to end represented by "."
     */
    public char[][] getNewMaze() {
        return newMaze;
    }

    /**
     * the length of the path from start to end
     * @return size
     */
    public int getSize(){
        return this.size;
    }

    /**
     * for efficiency analysis
     * returns the time each Node path was compared to the current path for each Node with a path back
     * to the start
     * @return times / the number of nodes visitable from start
     */
    public int averageTimesVisited() {
        int times = 0;
        for (Node n :
                shortestPathMap) {
            times += n.timesVisited;
        }
        return times / visitableNodes();
    }

    /**
     * @return rows in this maze times columns in this maze
     */
    public int mazeSize() {
        return rows * cols;
    }

    /**
     * @return the number of visitable Nodes from start
     */
    public int visitableNodes() {
        int visitable = 0;
        for (Node n :
                shortestPathMap) {
            if (n.dist < INFINITY)
                visitable++;
        }
        return visitable;
    }

    /**
     * used to convert {@code Node.position} to an int array
     * @param s the position of a Node
     * @return int array representation of s
     */
    private int[] stringToPosition(String s) {
        int[] a = newPosition();
        a[0] = Integer.parseInt(s.split(" ")[0]);
        a[1] = Integer.parseInt(s.split(" ")[1]);
        return a;
    }

    /**
     * used to create an int array of size two with 0 for both values
     * @return int array
     */
    private int[] newPosition() {
        int[] position = new int[2];
        position[0] = 0;
        position[1] = 0;
        return position;
    }

    /**
     * creates an int array representation of posit
     * @param posit representation of an int array with spaces separating elements
     * @return int array of two items
     */
    private int[] newPosition(String posit) {
        int[] position = new int[2];
        int temp = Integer.parseInt(posit.split(" ")[0]);
        position[0] = temp;
        temp = Integer.parseInt(posit.split(" ")[1]);
        position[1] = temp;
        return position;
    }

    /**
     * increments position to be one more element across in the maze
     * @param position int array position in the maze
     * @return the next position in the maze as an int array
     */
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

    /**
     * converts an int array to a string representation of the int array
     * @param position int array of size 2
     * @return String representation of the int array separated by spaces
     */
    private String arrayToString(int[] position) {
        return position[0] + " " + position[1];
    }

    /**
     * instantiates instance variable and sets all connections in matrix to {@code INFINITY}
     */
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

    /**
     * adds the node to {@code nodeMap, intNodeMap, shortestPathMap}
     * @param position int array of the position of the node in the maze
     */
    private void addNode(int[] position) {
        nodeMap.add(arrayToString(position), nodeId);
        intNodeMap.add(nodeId, arrayToString(position));
        Node n = new Node(nodeId, arrayToString(position));
        shortestPathMap.add(n);
        nodeId++;
    }

    /**
     * checks all possible connections (up, down, left, right) to other nodes from position
     * and adds them if a connection is found
     * @param position int array position of the node in the maze
     */
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

    /**
     * determines if there should be an edge between two nodes
     * addEdge iff the space is not an X and is a valid position
     * @param position int array position of the node in the maze
     * @param temp int array position of the possible node connection
     */
    private void checkPosition(int[] position, int[] temp) {
        if (validPosition(temp) && maze[temp[0]][temp[1]] != 'X') {
            addEdge(arrayToString(position), arrayToString(temp));
        }
    }

    /**
     * determines if position is an open space in the maze (start and end are considered open spaces)
     * and if position is in the maze
     * @param position int array position of the node in the maze
     * @return true iff the position of this node is a possible path location
     */
    private boolean validPosition(int[] position) {
        if (position[0] < this.maze.length && position[0] >= 0 && position[1] < this.maze[0].length && position[1] >= 0)
            return true;
        return false;
    }

    /**
     * adds the edge to {@code matrix}
     * @param src position of the fist node
     * @param dst position of the node that is linked to this node
     */
    private void addEdge(String src, String dst) {
        assert nodeMap.hasKey(src) : "Violation of : src is a node in the graph";
        assert nodeMap.hasKey(dst) : "Violation of : dst is a node in the graph";
        int sIndex = nodeMap.value(src);
        int dIndex = nodeMap.value(dst);

        matrix[sIndex][dIndex] = 1;
    }

    /**
     * @param position int array of the position in the maze
     * @return int of the position in {@code matrix}
     */
    private int positionToInt(int[] position) {
        int num = position[0] * cols + position[1];
        return num;
    }

    /**
     * updates the shortestPathMap such that all Node.prev calls eventually lead to start in the
     * shortest path possible iff that Node can lead back to start
     */
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

    /**
     * computes the shortest path from start to end and updates maze and newMaze to include the path
     * represented by "."
     * newMaze includes the size of the maze in the first line where maze is just the updated maze
     * from the text file with the path
     */
    private void shortestPath() {
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
        for (char[] ca : maze) {
            for (char c: ca){
                newMaze[position[0]][position[1]] = c;
                nextPosition(position);
            }
        }
        this.newMaze = newMaze;
    }
}