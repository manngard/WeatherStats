package myApp;

import com.google.gson.JsonSyntaxException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import myApp.TemperatureData.WeatherData;

import java.net.URL;
import java.util.*;

public class WeatherPageController implements Initializable {
    RequestHandler handler = new RequestHandler();
    List<String> errorMessages = new ArrayList<>();
    String searchBarHelpMessage;
    String cityNotFoundErrorMessage;
    String cityAlreadyAddedErrorMessage;

    @FXML
    private LineChart<String, Number> weatherDataGraph;

    @FXML
    private CategoryAxis dateAxis;

    @FXML
    private NumberAxis temperatureAxis;

    @FXML
    private FlowPane cityInfoPane;

    @FXML
    private AnchorPane detailPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchBar;

    @FXML
    private ImageView closeDetails;

    @FXML
    private Label detailCityLabel;

    @FXML
    private Button searchButton;

    @FXML
    private ImageView logoImg;

    @FXML
    public void hideDetails() {
        detailPane.toBack();
    }

    @FXML
    void enterSearchBar(MouseEvent event) {
        if (errorMessages.contains(searchBar.getText())){
            searchBar.setText("");
            searchBar.setStyle("-fx-text-fill: black;");
        }
    }

    @FXML
    void searchByButton() {
        searchQuery();
    }

    @FXML
    void search(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){
            searchQuery();
        }
    }

    void searchQuery(){
        try{
            handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", searchBar.getText());
            populateCityInfoPane();
        }
        catch(JsonSyntaxException e){
            searchBar.setText(cityNotFoundErrorMessage);
            searchBar.setStyle("-fx-text-fill: red;");
            cityInfoPane.requestFocus();
        }
        catch(Exception e){
            searchBar.setText(cityAlreadyAddedErrorMessage);
            searchBar.setStyle("-fx-text-fill: red;");
            cityInfoPane.requestFocus();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Stockholm,se");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "London,gb");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Manchester,gb");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Göteborg,se");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Västerås,se");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Roma,it");
        handler.createWeatherDataObject("https://api.openweathermap.org/data/2.5/forecast", "Bangkok,th");*/

        temperatureAxis.setLabel("Temperature(C)");
        dateAxis.setLabel("date");

        handler.loadDataObjects();
        populateCityInfoPane();

        searchBarHelpMessage = "Start by searching for a city";
        cityNotFoundErrorMessage = "Your city was not found";
        cityAlreadyAddedErrorMessage = "Your city is already added";

        errorMessages.add(searchBarHelpMessage);
        errorMessages.add(cityNotFoundErrorMessage);
        errorMessages.add(cityAlreadyAddedErrorMessage);


        searchBar.setText(searchBarHelpMessage);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }

    private void populateCityInfoPane(){
        cityInfoPane.getChildren().clear();
        for (CityInfo c : getCityInfo(this)){
            cityInfoPane.getChildren().add(c);
        }
    }

    void removeCityInfo(CityInfo info){
        handler.removeWeatherDataObject(info.getWeather());
        cityInfoPane.getChildren().remove(info);
    }

    public Set<CityInfo> getCityInfo(WeatherPageController controller){
        Set<CityInfo> cityInfo = new TreeSet<>(new Comparator<CityInfo>() {
            @Override
            public int compare(CityInfo o1, CityInfo o2) {
                return o1.getWeather().getCityName().compareTo(o2.getWeather().getCityName());
            }
        });
        for (WeatherData d : handler.getFavorites()){
            cityInfo.add(new CityInfo(d,controller,true));
        }
        for (WeatherData d : handler.getWeatherLocations()){
            cityInfo.add(new CityInfo(d,controller,false));
        }
        return cityInfo;
    }

    public void addFavorite(WeatherData data){
        handler.addFavorite(data.getCityName());
    }

    public void showDetails(WeatherData data){
        weatherDataGraph.getData().clear();
        detailCityLabel.setText(data.getCityName());
        XYChart.Series series = new XYChart.Series();
        series.setName(data.getCityName());

        //fortsätt med pair här

        series.getData().add(new XYChart.Data("2020-04-11 14:00:00",32));
        series.getData().add(new XYChart.Data(data.getDate(), Integer.valueOf(data.getTemperature() )));



        /*series.getData().add(new XYChart.Data("Jan", 23));
        series.getData().add(new XYChart.Data("Feb", 14));
        series.getData().add(new XYChart.Data("Mar", 15));
        series.getData().add(new XYChart.Data("Apr", 24));
        series.getData().add(new XYChart.Data("May", 34));
        series.getData().add(new XYChart.Data("Jun", 36));
        series.getData().add(new XYChart.Data("Jul", 22));
        series.getData().add(new XYChart.Data("Aug", 45));
        series.getData().add(new XYChart.Data("Sep", 43));
        series.getData().add(new XYChart.Data("Oct", 17));
        series.getData().add(new XYChart.Data("Nov", 29));
        series.getData().add(new XYChart.Data("Dec", 25));*/
        weatherDataGraph.getData().add(series);
        detailPane.toFront();
    }


    public void removeFavorite (WeatherData data){
        handler.removeFavorite(data.getCityName());
    }
}
