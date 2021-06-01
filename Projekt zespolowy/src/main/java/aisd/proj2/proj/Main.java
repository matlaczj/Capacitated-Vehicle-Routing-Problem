package aisd.proj2.proj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("org.graphstream.ui", "javafx");
        System.setProperty("org.graphstream.debug", "true");
        SplitPane splitPane = FXMLLoader.load(getClass().getResource("/OptionPane.fxml"));
        Scene scene = new Scene(splitPane, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("UberHeals");
        stage.show();
    }
}
