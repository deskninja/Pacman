package assignment10;


import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

public class PathFinder {
    SimpleReader kbd = new SimpleReader1L();
    SimpleWriter console = new SimpleWriter1L();

    public PathFinder(SimpleReader kbd) {
        this.kbd = kbd;
    }

    /**
     * This method will read a maze from a file with the given input name,
     * and output the solved maze to a file with the given output name.
     * You must use the filenames exactly as is (do not change the directory or path).
     * We will provide the full path to files we want to read/write in our tests.
     * See required specifications below.
     * This method must use a graph and graph pathfinding to solve the problem.
     *
     * @param inputFileName
     * @param outputFileName
     * @return
     */
    public static int solveMaze(String inputFileName, String outputFileName) {
        Graph g = new Graph(inputFileName);
        SimpleWriter output = new SimpleWriter1L(outputFileName);
        char[][] maze = g.getMaze();
        for (char[] line:
        maze){
            for (char c :
                    line) {
                output.print(c);
            }
            output.print('\n');
        }
        //for analysis of the algorythm
        System.out.println("maze size: " + g.mazeSize() + " with: " + g.notXNodes() + " number of open spaces and being a ratio of " +
                ((double)g.notXNodes()) / ((double)g.mazeSize()) + " with " +  g.averageTimesVisited() + " Visits to each node");
        return g.getSize();
    }
}
