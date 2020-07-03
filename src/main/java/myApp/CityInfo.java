package myApp;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import myApp.temperaturedata.WeatherData;

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
        parentController.addFavorite(weather.getCityName());
        favoriteIcon.toBack();
        favoriteIcon.setOpacity(0);
        unfavoriteIcon.toFront();
        unfavoriteIcon.setOpacity(0.8);
    }

    @FXML
    void releaseUnfavoriteButton() {
        parentController.removeFavorite(weather.getCityName());
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
    }

    @FXML
    void openDetails() {
        parentController.showDetails(weather.getCityName());
    }

    public CityInfo(WeatherData weather, WeatherPageController weatherPageController, boolean favorite) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CityInfo.fxml"));
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

        switch(weather.getWeatherDescription()){ //add more here
            case "light rain":
            case "moderate rain":
            case "heavy intensity rain":
            case "very heavy rain":
            case "extreme rain":
            case "freezing rain":
            case "light intensity shower rain":
            case "shower rain":
            case "high intensity shower rain":
            case "ragged shower rain":
            case "light intensity drizzle":
            case "drizzle":
            case "heavy intensity drizzle":
            case "light intensity drizzle rain":
            case "drizzle rain":
            case "heavy intensity drizzle rain":
            case "shower rain and drizzle":
            case "heavy shower rain and drizzle":
            case "shower drizzle":
                cityInfoBackground.setImage(ImageHandler.getImage("rainy.jpg"));
                break;

            case "few clouds":
            case "scattered clouds":
                cityInfoBackground.setImage(ImageHandler.getImage("partlycloudy.jpg"));
                break;


            case "overcast clouds":
            case "broken clouds":
                cityInfoBackground.setImage(ImageHandler.getImage("cloudy.jpg"));
                break;

            case "clear sky":
                cityInfoBackground.setImage(ImageHandler.getImage("sunny.jpg"));
                break;

            case "thunderstorm with light rain":
            case "thunderstorm with rain":
            case "thunderstorm with heavy rain":
            case "light thunderstorm":
            case "thunderstorm":
            case "heavy thunderstorm":
            case "ragged thunderstorm":
            case "thunderstorm with light drizzle":
            case "thunderstorm with drizzle":
            case "thunderstorm with heavy drizzle":
                cityInfoBackground.setImage(ImageHandler.getImage("stormy.jpg"));
                break;

            case "light snow":
            case "Snow":
            case "Heavy snow":
            case "Light rain and snow":
            case "Rain and snow":
            case "Light shower snow":
            case "Shower snow":
            case "Heavy shower snow":
                cityInfoBackground.setImage(ImageHandler.getImage("snowy.jpg"));
                break;

            case "fog":
            case "mist":
            case "Smoke":
                cityInfoBackground.setImage(ImageHandler.getImage("foggy.jpg"));
                break;

            default:
                cityInfoBackground.setImage(ImageHandler.getImage("cloudy.jpg"));
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
