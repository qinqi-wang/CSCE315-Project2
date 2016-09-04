package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.net.URL;

public class Main extends Application {

    Stage window;
    Scene scene;
    Button button;
    Stage ResultWindow;
    // ComboBox<String> comboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("Class Search");
        BorderPane layout = FXMLLoader.load(
                new URL(Main.class.getResource("sample.fxml").toExternalForm())
        );

        window.setScene(new Scene(layout));
        window.show();
    }
}