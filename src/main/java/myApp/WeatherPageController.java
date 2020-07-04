package myApp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import myApp.temperaturedata.RequestHandler;
import myApp.temperaturedata.WeatherData;

import java.net.URL;
import java.util.*;

import static org.apache.commons.text.WordUtils.capitalizeFully;

/**
 * The specification of a Controller class for WeatherPage.fxml
 */

public class WeatherPageController implements Initializable {
    private final RequestHandler handler = new RequestHandler();
    private final List<String> errorMessages = new ArrayList<>();
    private final Set <String> dates = new TreeSet<>(new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    });

    private String searchBarHelpMessage;
    private String seriesAdderHelpMessage;
    private String cityNotFoundErrorMessage;
    private String cityAlreadyAddedErrorMessage;
    private boolean hideDates = false;

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
    private AnchorPane detailContainerPane;

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
    private TextField seriesAdder;

    @FXML
    void ignoreClick(MouseEvent event){
        event.consume();
    }

    @FXML
    void enterSearchBar(MouseEvent event) {
        clearTextfield(searchBar);
    }

    @FXML
    void enterSeriesAdder(MouseEvent event) {
        clearTextfield(seriesAdder);
    }

    @FXML
    void hideDetails() {
        detailContainerPane.toBack();

        weatherDataGraph.getData().clear();
        handler.cleargraphItems();

        dates.clear();
        hideDates = false;

        clearTextfield(seriesAdder);
        seriesAdder.setText(seriesAdderHelpMessage);

    }

    @FXML
    void updateDetails(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            showDetails(capitalizeFully(seriesAdder.getText()));
        }
    }

    @FXML
    void searchByButton() {
        searchQuery();
    }

    @FXML
    void search(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchQuery();
        }
    }

    private void searchQuery() {
        try {
            handler.createWeatherDataObject(capitalizeFully(searchBar.getText()));
            populateCityInfoPane();
        } catch (IllegalArgumentException e) {
            displayError(searchBar,cityNotFoundErrorMessage);
        } catch (IllegalStateException i) {
            displayError(searchBar,cityAlreadyAddedErrorMessage);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        temperatureAxis.setLabel("Temperature(C)");
        dateAxis.setLabel("Date");

        handler.loadDataObjects();
        populateCityInfoPane();

        searchBarHelpMessage = "Start by searching for a city";
        seriesAdderHelpMessage = "Add another city";
        cityNotFoundErrorMessage = "Your city was not found";
        cityAlreadyAddedErrorMessage = "Your city is already added";

        errorMessages.add(searchBarHelpMessage);
        errorMessages.add(seriesAdderHelpMessage);
        errorMessages.add(cityNotFoundErrorMessage);
        errorMessages.add(cityAlreadyAddedErrorMessage);


        searchBar.setText(searchBarHelpMessage);
        seriesAdder.setText(seriesAdderHelpMessage);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }

    private void populateCityInfoPane() {
        cityInfoPane.getChildren().clear();
        for (CityInfo c : getCityInfo(this)) {
            cityInfoPane.getChildren().add(c);
        }
    }

    private Set<CityInfo> getCityInfo(WeatherPageController controller) {
        Set<CityInfo> cityInfo = new TreeSet<>(new Comparator<CityInfo>() {
            @Override
            public int compare(CityInfo o1, CityInfo o2) {
                return o1.getWeather().getCityName().compareTo(o2.getWeather().getCityName());
            }
        });
        for (WeatherData d : handler.getFavorites()) {
            cityInfo.add(new CityInfo(d, controller, true));
        }
        for (WeatherData d : handler.getWeatherLocations()) {
            cityInfo.add(new CityInfo(d, controller, false));
        }
        return cityInfo;
    }


    void showDetails(String city) {
        try{
            handler.createWeatherDataObject(city);

            if (weatherDataGraph.getData().size() == 0){
                detailCityLabel.setText(city);
            }
            else{
                detailCityLabel.setText("Analysis");
            }

            XYChart.Series <String,Number> series = new XYChart.Series();
            series.setName(city);
            Collection<Pair<String, Number>> weatherhistory = handler.getHistory(city);

            for (Pair p : weatherhistory) {
                XYChart.Data datapoint = new XYChart.Data(p.getFirst(), p.getSecond());
                series.getData().add(datapoint);
                dates.add(datapoint.getXValue().toString());
            }

            dateAxis.setTickLabelsVisible(true);
            if (dates.size() > 4 || hideDates){
                dateAxis.setTickLabelsVisible(false);
                hideDates = true;
            }

            dateAxis.setCategories(FXCollections.observableArrayList(dates));
            weatherDataGraph.getData().add(series);

            for (XYChart.Data<String,Number> entry : series.getData()) {
                Tooltip t = new Tooltip( city + "\n" + entry.getXValue());
                Tooltip.install(entry.getNode(), t);
            }

            handler.addGraphItem(capitalizeFully(city));
            detailContainerPane.toFront();
        }
        catch(IllegalArgumentException e){
            displayError(seriesAdder,cityNotFoundErrorMessage);
        }
        catch(IllegalStateException i){
            displayError(seriesAdder,cityAlreadyAddedErrorMessage);
        }
    }

    private void clearTextfield(TextField textfield){
        if (errorMessages.contains(textfield.getText())) {
            textfield.setText("");
            textfield.setStyle("-fx-text-fill: black;");
        }
    }

    private void displayError(TextField textfield, String error){
        textfield.setText(error);
        textfield.setStyle("-fx-text-fill: red;");
        cityInfoPane.requestFocus();
    }

    void addFavorite(String city) {
        handler.addFavorite(city);
    }

    void removeFavorite(String city) {
        handler.removeFavorite(city);
    }

    void removeCityInfo(CityInfo info) {
        handler.removeWeatherDataObject(info.getWeather());
        cityInfoPane.getChildren().remove(info);
    }

}
