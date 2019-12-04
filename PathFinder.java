package assignment10;


import components.simplereader.SimpleReader;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This class reads a maze from a file using a SimpleReader object
 * and outputs the solved maze to a file
 * This class uses a graph data structure to solve the problem.
 *
 * @author Jonathan Oliveros and Joshua Wells
 */
public class PathFinder {
    SimpleReader kbd;

    /**
     * sole constructor
     * @param kbd is the SimpleReader object to read the file
     */
    public PathFinder(SimpleReader kbd) {
        this.kbd = kbd;
    }

    /**
     * This method reads a maze from a file with the given input name
     * and outputs the solved maze to a file with the given output name.
     * This method uses a graph data structure to solve the problem.
     *
     * @param inputFileName
     * @param outputFileName
     * @return the length of the path from S to G in the given file
     */
    public static int solveMaze(String inputFileName, String outputFileName) {
        Graph g = new Graph(inputFileName);
        SimpleWriter output = new SimpleWriter1L(outputFileName);
        char[][] maze = g.getNewMaze();
        for (char[] line:
        maze){
            for (char c : line) {
                output.print(c);
            }
            output.print('\n');
        }
        //for analysis of the algorithm
        System.out.println("maze size: " + g.mazeSize() + " with: " + g.visitableNodes() + " number of open spaces and being a ratio of " +
                ((double)g.visitableNodes()) / ((double)g.mazeSize()) + " with " +  g.averageTimesVisited() + " Visits to each node");
        return g.getSize();
    }
}
