package assignment10;


import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

public class PathFinder {
    //TODO: read file using simpleReader from components into 2D Array
    //TODO: create Graph object from data
    //TODO: add shortest path to 2D Array
    //TODO: return the 2D Array in the correct format
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
        //TODO: output file = g.shortestPath();
        return g.size();
    }
}
