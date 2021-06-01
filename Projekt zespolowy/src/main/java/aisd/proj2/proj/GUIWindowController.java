package aisd.proj2.proj;

import aisd.proj2.proj.AlgorithmicMechanics.CitizenshipCheck;
import aisd.proj2.proj.AlgorithmicMechanics.MapContourCreation;
import aisd.proj2.proj.AlgorithmicMechanics.Point;
import aisd.proj2.proj.InputDataLoading.LoadDataFromInput;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.graphstream.ui.graphicGraph.GraphPosLengthUtils.nodePosition;

public class GUIWindowController {

    private Graph graph;

    @FXML
    private SplitPane mainPane;

    private FxViewPanel graphPanel;

    @FXML
    private TextArea mapPath;

    @FXML
    private TextArea patientsPath;

    @FXML
    private TextArea X;

    @FXML
    private TextArea Y;

    @FXML
    private ListView<String> patientsList;

    private String mapFile;
    private GraphCreator graphCreator;
    private final List<Integer[]> patientsHospitals = new ArrayList<>();
    private int actualNumOfPatients = 0;

    public void actionChooseMap(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("data" + File.separator + "input"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            mapPath.clear();
            mapPath.appendText(file.getName());
        } else
            System.out.println("Not a file");
    }

    public void actionLoadMap(ActionEvent event) {
        mapFile = mapPath.getText();
        try {
            patientsList.getItems().clear();
            graphCreator = new GraphCreator(mapPath.getText().trim());
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong file");
            alert.setHeaderText("Wrong map file!");
            alert.setContentText("Please try enter another file");

            alert.showAndWait();
            return;
        }

        graphCreator.createMapGraph();
        graph = graphCreator.getGraph();
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.disableAutoLayout();
        graphPanel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
        graphPanel.setDisable(true);
        mainPane.getItems().remove(0);
        mainPane.getItems().add(0, graphPanel);
        mainPane.setDividerPositions(0.7);
    }

    public void actionChoosePatients(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("data" + File.separator + "input"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            patientsPath.clear();
            patientsPath.appendText(file.getName());
        } else
            System.out.println("Not a file");
    }

    public void actionLoadPatients(ActionEvent event) {
        try {
            graphCreator.loadPatiensFromFile(patientsPath.getText().trim());
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Wrong file");
            alert.setHeaderText("Wrong patient file!");
            alert.setContentText("Please try enter another file");

            alert.showAndWait();
            return;
        }

        patientsHospitals.clear();
        patientsList.getItems().clear();
        actualNumOfPatients = (graphCreator.getPatientCounter() + 1);
        for (String s : graphCreator.getPatientsList())
            patientsList.getItems().add(s);
        for (int i = actualNumOfPatients; i > 0; i--) {
            patientsHospitals.add(graphCreator.createPatientPath(actualNumOfPatients - i));
        }
        graph = graphCreator.getGraph();
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.disableAutoLayout();
        graphPanel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
        graphPanel.setDisable(true);
        mainPane.getItems().remove(0);
        mainPane.getItems().add(0, graphPanel);
        mainPane.setDividerPositions(0.7);
    }

    public void actionEnterPatient(ActionEvent event) {
        try {
            graphCreator.addPatient(X.getText().trim(), Y.getText().trim());
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entering patient error");
            alert.setHeaderText("There is no map to enter patient on!");
            alert.setContentText("Please load map first!");
            alert.showAndWait();
            return;
        }
        patientsList.getItems().clear();
        for (String s : graphCreator.getPatientsList())
            patientsList.getItems().add(s);
        patientsHospitals.add(graphCreator.createPatientPath(actualNumOfPatients, Double.parseDouble(X.getText()), Double.parseDouble(Y.getText())));
        actualNumOfPatients++;
        graph = graphCreator.getGraph();
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.disableAutoLayout();
        graphPanel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
        graphPanel.setDisable(true);
        mainPane.getItems().remove(0);
        mainPane.getItems().add(0, graphPanel);
        mainPane.setDividerPositions(0.7);
    }

    public void actionOnClickedMouse(MouseEvent event) {
        for (int i = 0; i < patientsHospitals.size(); i++) {
            graphCreator.hidePatientsEdges(i);
        }
        for (String patientNodeID : patientsList.getItems())
            graph.getNode(patientNodeID).setAttribute("ui.style", "fill-color: green;");
        String selected = patientsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        Node node = graph.getNode(selected);
        node.setAttribute("ui.style", "fill-color: red;");
        double[] pos = nodePosition(node);
        LoadDataFromInput ldfi = new LoadDataFromInput(mapFile, "", LoadDataFromInput.MAP_MODE);
        MapContourCreation map = new MapContourCreation(ldfi.getHospitals(), ldfi.getThings());
        if (!CitizenshipCheck.isPointInside(map.getBorders(), new Point(pos[0], pos[1]))) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Patient");
            alert.setHeaderText("This patient is not from our country");
            alert.setContentText(selected);
            alert.showAndWait();
        } else {
            String[] splitSelected = selected.split("\\)");
            int id = Integer.parseInt(splitSelected[0]);
            try {
                graphCreator.showPatientPath(id, patientsHospitals.get(id));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("This patient is without Hospital!");
            }
            graph = graphCreator.getGraph();
            FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            v.disableAutoLayout();
            graphPanel = (FxViewPanel) v.addDefaultView(false, new FxGraphRenderer());
            graphPanel.setDisable(true);
            mainPane.getItems().remove(0);
            mainPane.getItems().add(0, graphPanel);
            mainPane.setDividerPositions(0.7);
        }
    }

    public void actionHidePatientDistr(ActionEvent event) {
        for (int i = 0; i < patientsHospitals.size(); i++) {
            graphCreator.hidePatientsEdges(i);
        }
        for (String patientNodeID : patientsList.getItems())
            graph.getNode(patientNodeID).setAttribute("ui.style", "fill-color: green;");
    }
}
