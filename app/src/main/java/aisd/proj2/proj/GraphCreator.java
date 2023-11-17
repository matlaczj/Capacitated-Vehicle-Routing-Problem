package aisd.proj2.proj;

import aisd.proj2.proj.AlgorithmicMechanics.MapContourCreation;
import aisd.proj2.proj.AlgorithmicMechanics.PatientDistribution;
import aisd.proj2.proj.AlgorithmicMechanics.Point;
import aisd.proj2.proj.InputDataLoading.LoadDataFromInput;
import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Patient;
import aisd.proj2.proj.MapElements.Road;
import aisd.proj2.proj.MapElements.Thing;
import javafx.scene.control.Alert;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphCreator {

    private final LoadDataFromInput ldfiMap;
    private LoadDataFromInput ldfiPat;
    private final MapContourCreation map;
    private Node[] nodeBorder;

    public GraphCreator(String mapFileName) {
        ldfiMap = new LoadDataFromInput(mapFileName, "", LoadDataFromInput.MAP_MODE);
        Road.createAllCrossRoads(ldfiMap.getHospitals(), ldfiMap.getRoads());
        map = new MapContourCreation(ldfiMap.getHospitals(), ldfiMap.getThings());
    }

    private final Graph graph = new MultiGraph("CityMap");

    public int getPatientCounter() {
        return patientCounter;
    }

    private int patientCounter = -1;
    private final List<String> patientsList = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#.##");
    private Edge[] roadEdges;

    public List<String> getPatientsList() {
        return patientsList;
    }

    public Graph getGraph() {
        return graph;
    }

    public void createMapGraph() {
        addHospitals();
        addThings();
        addRoads();
        addBorders();
    }

    public void loadPatiensFromFile(String patientsFileName) {
        ldfiPat = new LoadDataFromInput("", patientsFileName, LoadDataFromInput.PATIENTS_MODE);
        for (Patient patient : ldfiPat.getPatients()) {
            patientCounter++;
            Node np = graph.addNode(patient.getIdx() + ") x = " + patient.getX() + " y = " + patient.getY());
            np.setAttribute("xy", patient.getX(), patient.getY());
            np.setAttribute("ui.style", "fill-color: green;");
            patientsList.add(patient.getIdx() + ") x = " + patient.getX() + " y = " + patient.getY());
        }
    }

    public void addPatient(String x, String y) {
        double X;
        double Y;
        try {
            X = Double.parseDouble(x);
            Y = Double.parseDouble(y);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong coordinates");
            alert.setHeaderText("Wrong coordinates number!");
            alert.setContentText("Please enter correct coordinates!");

            alert.showAndWait();
            return;
        }
        patientCounter++;
        Node p = graph.addNode(patientCounter + ") x = " + X + " y = " + Y);
        p.setAttribute("xy", X, Y);
        p.setAttribute("ui.style", "fill-color: green;");
        patientsList.add(patientCounter + ") x = " + X + " y = " + Y);
    }

    public Integer[] createPatientPath(int pID, double x, double y) {
        Patient patient = new Patient(pID, x, y);
        int firstShadowHospIdx = ldfiMap.getHospitals().size();
        MapContourCreation mapContour = new MapContourCreation(ldfiMap.getHospitals(), ldfiMap.getThings());
        PatientDistribution patientDistribution = new PatientDistribution(ldfiMap, mapContour, firstShadowHospIdx);
        Integer[] hospitalsId = patientDistribution.patientsDistribution(patient);
        if (hospitalsId == null) {
            return null;
        }
        int last = hospitalsId.length - 1;
        int freeBeds = ldfiMap.getHospitals().get(hospitalsId[last]).getFreeBeds();
        Hospital lastHospital = ldfiMap.getHospitals().get(hospitalsId[last]);
        String selected = pID + ") x = " + x + " y = " + y;
        Edge first = graph.addEdge("Path" + pID, selected, Integer.toString(hospitalsId[0]));
        first.setAttribute("ui.style", "fill-color: red;");
        first.setAttribute("ui.hide");
        graph.getNode(String.valueOf(hospitalsId[last])).setAttribute("ui.label", hospitalsId[last] + ") " + freeBeds + "/" + lastHospital.getAllBeds());
        return hospitalsId;
    }

    public Integer[] createPatientPath(int pID) {
        double x = ldfiPat.getPatients().get(pID).getX();
        double y = ldfiPat.getPatients().get(pID).getY();
        Patient patient = new Patient(pID, x, y);
        int firstShadowHospIdx = ldfiMap.getHospitals().size();
        MapContourCreation mapContour = new MapContourCreation(ldfiMap.getHospitals(), ldfiMap.getThings());
        PatientDistribution patientDistribution = new PatientDistribution(ldfiMap, mapContour, firstShadowHospIdx);
        Integer[] hospitalsId = patientDistribution.patientsDistribution(patient);
        if (hospitalsId == null) {
            return null;
        }
        int last = hospitalsId.length - 1;
        int freeBeds = ldfiMap.getHospitals().get(hospitalsId[last]).getFreeBeds();
        Hospital lastHospital = ldfiMap.getHospitals().get(hospitalsId[last]);
        String selected = pID + ") x = " + x + " y = " + y;
        Edge first = graph.addEdge("Path" + pID, selected, Integer.toString(hospitalsId[0]));
        first.setAttribute("ui.style", "fill-color: red;");
        first.setAttribute("ui.hide");
        graph.getNode(String.valueOf(hospitalsId[last])).setAttribute("ui.label", hospitalsId[last] + ") " + freeBeds + "/" + lastHospital.getAllBeds());
        return hospitalsId;
    }

    public void showPatientPath(int idP, Integer[] hospitalsId) {
        graph.getEdge("Path" + idP).removeAttribute("ui.hide");
        for (int i = 0; i < hospitalsId.length - 1; i++) {
            if (graph.getEdge("Road " + hospitalsId[i] + " " + hospitalsId[i + 1]) != null) {
                graph.getEdge("Road " + hospitalsId[i] + " " + hospitalsId[i + 1]).setAttribute("ui.style", "fill-color: red;");
            } else {
                graph.getEdge("Road " + hospitalsId[i + 1] + " " + hospitalsId[i]).setAttribute("ui.style", "fill-color: red;");
            }
        }
    }

    public void hidePatientsEdges(int idP) {
        if (graph.getEdge("Path" + idP) != null) {
            graph.getEdge("Path" + idP).setAttribute("ui.hide");
            for (Edge roads : roadEdges) {
                roads.setAttribute("ui.style", "fill-color: black;");
            }
            nodeBorder = setBorderNodes();
            int n = map.getBorders().length - 1;
            for (int j = 0; j < n; j++) {
                if (graph.getEdge("Road " + nodeBorder[j].getId() + " " + nodeBorder[j + 1].getId()) != null) {
                    graph.getEdge("Road " + nodeBorder[j].getId() + " " + nodeBorder[j + 1].getId()).setAttribute("ui.style", "fill-color: blue;");
                } else if (graph.getEdge("Road " + nodeBorder[j + 1].getId() + " " + nodeBorder[j].getId()) != null) {
                    graph.getEdge("Road " + nodeBorder[j + 1].getId() + " " + nodeBorder[j].getId()).setAttribute("ui.style", "fill-color: blue;");
                }
            }
            if (graph.getEdge("Road " + nodeBorder[n].getId() + " " + nodeBorder[0].getId()) != null) {
                graph.getEdge("Road " + nodeBorder[n].getId() + " " + nodeBorder[0].getId()).setAttribute("ui.style", "fill-color: blue;");
            } else if (graph.getEdge("Road " + nodeBorder[0].getId() + " " + nodeBorder[n].getId()) != null) {
                graph.getEdge("Road " + nodeBorder[0].getId() + " " + nodeBorder[n].getId()).setAttribute("ui.style", "fill-color: blue;");
            }
        }
    }

    private void addHospitals() {
        for (Hospital hospital : ldfiMap.getHospitals()) {
            Node nodeHospital = graph.addNode(Integer.toString(hospital.getIdx()));
            nodeHospital.setAttribute("xy", hospital.getX(), hospital.getY());
            if (hospital.getIsReal()) {
                nodeHospital.setAttribute("ui.label", hospital.getIdx() + ") " + hospital.getFreeBeds() + "/" + hospital.getAllBeds());
                nodeHospital.setAttribute("ui.style", "text-alignment: under; text-background-mode: plain;");
            } else
                nodeHospital.setAttribute("ui.style", "fill-color: grey;");
        }
    }

    private void addThings() {
        for (Thing thing : ldfiMap.getThings()) {
            Node nodeThing = graph.addNode("Thing" + thing.getIdx());
            nodeThing.setAttribute("xy", thing.getX(), thing.getY());
            nodeThing.setAttribute("ui.style", "fill-color: blue;");
        }
    }


    private void addRoads() {
        int i = 0;
        int nodeOne = -1;
        int nodeTwo = -1;
        roadEdges = new Edge[ldfiMap.getRoads().size()];
        for (Road road : ldfiMap.getRoads()) {
            for (Hospital hospital : ldfiMap.getHospitals()) {
                if (hospital.getIdx() == road.getFirstHospIdx()) {
                    nodeOne = hospital.getIdx();
                }
                if (hospital.getIdx() == road.getSecondHospIdx()) {
                    nodeTwo = hospital.getIdx();
                }
            }
            roadEdges[i] = graph.addEdge("Road " + nodeOne + " " + nodeTwo, nodeOne, nodeTwo);
            roadEdges[i].setAttribute("ui.label", df.format(road.getDist()));
            i++;
        }
    }

    private Node[] setBorderNodes() {
        Node[] nodeBorder = new Node[map.getBorders().length];
        int i = 0;
        for (Point point : map.getBorders()) {
            for (Hospital hospital : ldfiMap.getHospitals()) {
                if (point.getX() == hospital.getX() && point.getY() == hospital.getY()) {
                    nodeBorder[i] = graph.getNode(hospital.getIdx());
                    i++;
                    break;
                }
            }
            for (Thing thing : ldfiMap.getThings()) {
                if (point.getX() == thing.getX() && point.getY() == thing.getY()) {
                    nodeBorder[i] = graph.getNode("Thing" + thing.getIdx());
                    i++;
                    break;
                }
            }
        }
        return nodeBorder;
    }

    private void addBorders() {
        nodeBorder = setBorderNodes();
        int n = map.getBorders().length - 1;
        for (int j = 0; j < n; j++) {
            if (graph.getEdge("Road " + nodeBorder[j].getId() + " " + nodeBorder[j + 1].getId()) != null) {
                graph.getEdge("Road " + nodeBorder[j].getId() + " " + nodeBorder[j + 1].getId()).setAttribute("ui.style", "fill-color: blue;");
            } else if (graph.getEdge("Road " + nodeBorder[j + 1].getId() + " " + nodeBorder[j].getId()) != null) {
                graph.getEdge("Road " + nodeBorder[j + 1].getId() + " " + nodeBorder[j].getId()).setAttribute("ui.style", "fill-color: blue;");
            } else {
                graph.addEdge("Border" + j, nodeBorder[j], nodeBorder[j + 1]).setAttribute("ui.style", "fill-color: blue;");
            }
        }
        if (graph.getEdge("Road " + nodeBorder[n].getId() + " " + nodeBorder[0].getId()) != null) {
            graph.getEdge("Road " + nodeBorder[n].getId() + " " + nodeBorder[0].getId()).setAttribute("ui.style", "fill-color: blue;");
        } else if (graph.getEdge("Road " + nodeBorder[0].getId() + " " + nodeBorder[n].getId()) != null) {
            graph.getEdge("Road " + nodeBorder[0].getId() + " " + nodeBorder[n].getId()).setAttribute("ui.style", "fill-color: blue;");
        } else {
            graph.addEdge("Border" + n, nodeBorder[n], nodeBorder[0]).setAttribute("ui.style", "fill-color: blue;");
        }
    }

}
