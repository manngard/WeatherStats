package myApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        WeatherPageController controller = new WeatherPageController();

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("myApp/WeatherPage.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("WeatherStats");
        primaryStage.getIcons().add(new Image("myApp/logo.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.show();
    }
}