package myApp;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import myApp.TemperatureData.WeatherData;

import java.io.IOException;

public class CityInfo extends AnchorPane {
    private WeatherPageController parentController;
    private WeatherData weather;

    @FXML
    private AnchorPane cityInfoPane;

    @FXML
    private ImageView cityInfoBackground;

    @FXML
    private Label cityLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private ImageView closeIcon;

    @FXML
    private ImageView favoriteIcon;

    @FXML
    private ImageView unfavoriteIcon;


    @FXML
    void releaseFavoriteButton() {
        parentController.addFavorite(weather);
        favoriteIcon.toBack();
        favoriteIcon.setOpacity(0);
        unfavoriteIcon.toFront();
        unfavoriteIcon.setOpacity(0.8);
    }

    @FXML
    void releaseUnfavoriteButton() {
        parentController.removeFavorite(weather);
        unfavoriteIcon.toBack();
        unfavoriteIcon.setOpacity(0);
        favoriteIcon.toFront();
        favoriteIcon.setOpacity(0.8);
    }

    @FXML
    void iconPressed() {
        if (closeIcon.isPressed()){
            closeIcon.setOpacity(0.5);
        }
        if (favoriteIcon.isPressed()){
            favoriteIcon.setOpacity(0.5);
        }
        if (unfavoriteIcon.isPressed()){
            unfavoriteIcon.setOpacity(0.5);
        }
    }
    @FXML
    void releaseCloseButton() {
        final CityInfo requester = this;
        closeIcon.setOpacity(1);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), requester);
        fade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                parentController.removeCityInfo(requester);
            }
        });
        fade.setToValue(0);
        fade.play();
    }

    @FXML
    void onHoverStart() {
        cityInfoBackground.setOpacity(0.7);
    }

    @FXML
    void onHoverStop() {
        cityInfoBackground.setOpacity(1);
        //parentController.hideDetails();
    }

    @FXML
    void openDetails() {
        parentController.showDetails(weather);
    }

    public CityInfo(WeatherData weather, WeatherPageController weatherPageController, boolean favorite) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CityInfo.fxml")); //recipe_listitem... från början
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.weather = weather;
        this.parentController = weatherPageController;

        String city = weather.getCityName();
        String country = weather.getCountry();
        String temperature = weather.getTemperature();

        cityLabel.setText(city);
        countryLabel.setText(country);
        temperatureLabel.setText(temperature + "°C");
        //System.out.println(weather.getWeatherDescription());
        switch(weather.getWeatherDescription()){ //add more here
            case "light rain":
                cityInfoBackground.setImage(ImageHandler.getImage("rainy.jpg"));
                break;
            case "overcast clouds":
                cityInfoBackground.setImage(ImageHandler.getImage("cloudy.jpg"));
                break;
            case "sunny":
                cityInfoBackground.setImage(ImageHandler.getImage("sunny.jpg"));
                break;
            case "broken clouds":
                cityInfoBackground.setImage(ImageHandler.getImage("cloudy.jpg"));
                break;
            default:
                cityInfoBackground.setImage(ImageHandler.getImage("sunny.jpg"));
        }

        cityInfoBackground.setFitWidth(200);
        cityInfoBackground.setFitHeight(300);


        closeIcon.setOpacity(0.8);
        favoriteIcon.setOpacity(0.8);
        unfavoriteIcon.setOpacity(0);

        if(favorite){
            favoriteIcon.setOpacity(0);
            unfavoriteIcon.toFront();
            unfavoriteIcon.setOpacity(0.8);
        }

    }
    public WeatherData getWeather() {
        return weather;
    }
}
