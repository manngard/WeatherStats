package myApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger( App.class.getName() );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("myApp/WeatherPage.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("WeatherStats");
            primaryStage.getIcons().add(new Image("myApp/logo.png"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            primaryStage.show();
            LOGGER.info("Application successfully launched");
        }
        catch(IOException | NullPointerException e){
            LOGGER.log(Level.SEVERE, "WeatherPage.fxml could not be found",e);
        }


    }
}
