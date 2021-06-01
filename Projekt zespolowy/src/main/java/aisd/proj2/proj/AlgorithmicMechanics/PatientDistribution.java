package aisd.proj2.proj.AlgorithmicMechanics;

import aisd.proj2.proj.InputDataLoading.LoadDataFromInput;
import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Patient;
import aisd.proj2.proj.MapElements.Road;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PatientDistribution {
    private ArrayList<double[]> shortestPathsList;
    private ArrayList<Integer[]> previousNodesList;
    private double[][] adjacencyMatrix;
    private Point[] borders;
    private ArrayList<Hospital> hospitals;
    private ArrayList<Road> roads;
    private int nHosp;
    private int nRoads;
    private int firstIdxOfShadowHosp;

    public PatientDistribution(LoadDataFromInput ldfiMap, MapContourCreation mapContour, int firstIdxOfShadowHosp) {
        hospitals = ldfiMap.getHospitals();
        roads = ldfiMap.getRoads();
        borders = mapContour.getBorders();

        this.firstIdxOfShadowHosp = firstIdxOfShadowHosp;
        nRoads = roads.size();
        nHosp = hospitals.size();

        adjacencyMatrix = ShortestPathAlgorithm.createAdjacencyMatrix(roads, nHosp);
        ShortestPathAlgorithm spa = new ShortestPathAlgorithm(adjacencyMatrix, firstIdxOfShadowHosp);

        shortestPathsList = spa.getShortestPathsList();
        previousNodesList = spa.getPreviousNodesList();
    }

    public Integer[] patientsDistribution(Patient patient) {

        System.out.println("\nDistributing patient with idx " + patient.getIdx() + "...");
        Patient currPat = patient;
        if (!CitizenshipCheck.isPointInside(borders, currPat.getPoint())) {
            System.out.println("Patient with idx " + patient.getIdx() + " is not from our country.");
            return null;
        }
        System.out.println("Patient with idx " + patient.getIdx() + " is from our country...");

        Hospital primeHosp = currPat.findNearestHospital(hospitals, firstIdxOfShadowHosp);
        System.out.println("first idx of shadow " + firstIdxOfShadowHosp);
        if (primeHosp.getFreeBeds() != 0) {
            primeHosp.decrementFreeBeds();
            System.out.println("Found place for patient with idx " + patient.getIdx() + " in hospital with idx " + primeHosp.getIdx() +
                    " that has " + primeHosp.getFreeBeds() + " free beds now.");
            return new Integer[]{primeHosp.getIdx()};
        }
        System.out.println("There was no place for patient with idx " + patient.getIdx() + " in its initial hospital with idx " + primeHosp.getIdx() + ".");

        ArrayList<Integer> fullJourneyOfPatient = new ArrayList<>();

        ArrayList<Integer> visitedHosp = new ArrayList<>();
        Hospital currHosp = primeHosp;
        visitedHosp.add(currHosp.getIdx());
        double[] currHospShortestPaths = shortestPathsList.get(currHosp.getIdx());
        Integer[] currHospPreviousNodes = previousNodesList.get(currHosp.getIdx());
        int nearestHospIdx = ShortestPathAlgorithm.findShortestPath(visitedHosp.stream().mapToInt(Integer::intValue).toArray(), currHospShortestPaths, firstIdxOfShadowHosp);
        int[] roadToNearestHosp = ShortestPathAlgorithm.getTraces(currHospPreviousNodes, nearestHospIdx);
        Hospital nearestHosp = hospitals.get(nearestHospIdx);
        fullJourneyOfPatient.addAll(Arrays.stream(roadToNearestHosp).boxed().collect(Collectors.toList()));

        while (roadToNearestHosp != null) {
            if (nearestHosp.getFreeBeds() != 0) {
                nearestHosp.decrementFreeBeds();
                visitedHosp = new ArrayList<>();
                System.out.println("Found place for patient with idx " + patient.getIdx() + " in hospital with idx " + nearestHosp.getIdx() +
                        " that has " + nearestHosp.getFreeBeds() + " free beds now.");
                System.out.println("Moved on the road: " + Arrays.toString(roadToNearestHosp));
                fullJourneyOfPatient.add(nearestHospIdx);
                break;
            }
            System.out.println("There was no place for patient with idx " + patient.getIdx() + " in  hospital with idx " + nearestHosp.getIdx() + ".");
            System.out.println("Moved on the road: " + Arrays.toString(roadToNearestHosp));

            visitedHosp.add(nearestHospIdx);
            currHosp = nearestHosp;
            currHospShortestPaths = shortestPathsList.get(currHosp.getIdx());
            currHospPreviousNodes = previousNodesList.get(currHosp.getIdx());
            nearestHospIdx = ShortestPathAlgorithm.findShortestPath(visitedHosp.stream().mapToInt(Integer::intValue).toArray(), currHospShortestPaths, firstIdxOfShadowHosp);
            if (nearestHospIdx == -1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Patient distribution");
                alert.setHeaderText("There is no place for this patient");
                alert.setContentText("No place for him :(");
                alert.showAndWait();
                throw new IndexOutOfBoundsException("No place for the patient!");
            }
            roadToNearestHosp = ShortestPathAlgorithm.getTraces(currHospPreviousNodes, nearestHospIdx);
            nearestHosp = hospitals.get(nearestHospIdx);
            fullJourneyOfPatient.addAll(Arrays.stream(roadToNearestHosp).boxed().collect(Collectors.toList()));
        }
        return fullJourneyOfPatient.toArray(new Integer[0]);
    }
}
