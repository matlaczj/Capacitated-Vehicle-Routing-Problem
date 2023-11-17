package aisd.proj2.proj.AlgorithmicMechanics;

import aisd.proj2.proj.MapElements.Road;

import java.util.ArrayList;
import java.util.Collections;

public class ShortestPathAlgorithm {

    private ArrayList<double[]> shortestPathsList;
    private ArrayList<Integer[]> previousNodesList;
    private int nNodes;

    public ShortestPathAlgorithm(double[][] adjacencyMatrix, int firstIdxOfShadowHosp) {
        nNodes = adjacencyMatrix[0].length;
        shortestPathsList = new ArrayList<>();
        previousNodesList = new ArrayList<>();
        for (int i = 0; i < firstIdxOfShadowHosp; i++) {
            shortestPathAlgorithm(adjacencyMatrix, i);
        }
    }

    private void shortestPathAlgorithm(double[][] adjacencyMatrix, int sourceNode) {
        double[] shortestPaths = new double[nNodes];
        Integer[] previousNodes = new Integer[nNodes];
        Boolean[] wasVisited = new Boolean[nNodes];

        for (int i = 0; i < nNodes; i++) {
            shortestPaths[i] = Integer.MAX_VALUE;
            wasVisited[i] = false;
        }
        shortestPaths[sourceNode] = 0;

        for (int i = 0; i < nNodes - 1; i++) {

            int nearestNode = nearestNode(shortestPaths, wasVisited);
            wasVisited[nearestNode] = true;

            for (int node = 0; node < nNodes; node++)

                if (!wasVisited[node] && adjacencyMatrix[nearestNode][node] != 0 &&
                        shortestPaths[nearestNode] != Integer.MAX_VALUE &&
                        shortestPaths[nearestNode] + adjacencyMatrix[nearestNode][node] < shortestPaths[node]) {
                    shortestPaths[node] = adjacencyMatrix[nearestNode][node] + shortestPaths[nearestNode];
                    previousNodes[node] = nearestNode;
                }
        }
        shortestPathsList.add(shortestPaths);
        previousNodesList.add(previousNodes);
        showTable(shortestPaths, previousNodes);
    }

    private static int nearestNode(double[] shortestPaths, Boolean[] wasVisited) {
        int nNodes = shortestPaths.length;
        double minPath = Double.MAX_VALUE;
        int minNode = -1;

        for (int node = 0; node < nNodes; node++)
            if (!wasVisited[node] && shortestPaths[node] <= minPath) {
                minPath = shortestPaths[node];
                minNode = node;
            }

        return minNode;
    }

    private static void showTable(double[] shortestPaths, Integer[] previousNodes) {
        int nNodes = shortestPaths.length;
        System.out.println("Node:\tShortest path from source:\tPrevious node:");
        for (int i = 0; i < nNodes; i++)
            System.out.println(i + "\t\t" + shortestPaths[i] + "\t".repeat(7) + previousNodes[i]);
    }

    public static int findShortestPath(int[] toOmit, double[] shortestPaths, int firstIdxOfShadowHosp) {
        double min = Double.MAX_VALUE;
        int minIdx = -1;
        for (int i = 0; i < shortestPaths.length && i < firstIdxOfShadowHosp; i++) {
            if (shortestPaths[i] <= min && shortestPaths[i] != 0 && !isInArray(i, toOmit)) {
                min = shortestPaths[i];
                minIdx = i;
            }
        }

        return minIdx;
    }

    private static boolean isInArray(int value, int[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    public static int[] getTraces(Integer[] previousNodes, int node) {
        ArrayList<Integer> traces = new ArrayList<>();

        if (node == -1) {
            return null;
        }

        Integer prevNode = previousNodes[node];
        while (prevNode != null) {
            traces.add(prevNode);
            prevNode = previousNodes[prevNode];
        }

        Collections.reverse(traces);

        return traces.stream().mapToInt(i -> i).toArray();
    }

    public static double[][] createAdjacencyMatrix(ArrayList<Road> roads, int nNodes) {
        double[][] adjacencyMatrix = new double[nNodes][nNodes];
        for (int i = 0; i < roads.size(); i++) {
            Road currRoad = roads.get(i);
            int firstHospIdx = currRoad.getFirstHospIdx();
            int secondHospIdx = currRoad.getSecondHospIdx();
            adjacencyMatrix[firstHospIdx][secondHospIdx] = currRoad.getDist();
            adjacencyMatrix[secondHospIdx][firstHospIdx] = currRoad.getDist();
        }
        return adjacencyMatrix;
    }

    public ArrayList<double[]> getShortestPathsList() {
        return shortestPathsList;
    }

    public ArrayList<Integer[]> getPreviousNodesList() {
        return previousNodesList;
    }
}



