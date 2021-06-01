module Projekt.zespolowy {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires gs.core;
    requires gs.ui.javafx;

    exports aisd.proj2.proj to javafx.graphics, javafx.fxml;
    opens aisd.proj2.proj;
}