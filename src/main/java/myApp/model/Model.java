package myApp.model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import kotlin.Triple;
import myApp.Pair;
import myApp.model.data.WeatherData;

import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The specification for a Model class, this class is responsible for encapsulating the application's data
 */

public class Model {
    private static final Logger LOGGER = Logger.getLogger( Model.class.getName() );

    private final APIHandler handler = new APIHandler();
    private final Gson gson = new Gson();
    private final Map<String, WeatherData> weatherLocations = new HashMap<>();
    private final Map<String, WeatherData> graphItems = new HashMap<>();
    private final Map<String, WeatherData> favorites = new HashMap<>();
    private final Map<String, String> aliasNames = new HashMap<>();
    private final Set<Triple<String, String, Number>> history = new HashSet<>();

    /**
     *
     * @param location - The city of created WeatherData object
     * @param inGraph - If the city is to be added to graph or main view
     * @throws IllegalArgumentException if the city can't be found
     * @throws IllegalStateException if the city is already added to chosen view
     */

    public void createWeatherDataObject(String location, boolean inGraph) throws IllegalArgumentException, IllegalStateException {
        String city = aliasNames.getOrDefault(location, location);
        WeatherData data;
        try{
           data = gson.fromJson(handler.fetchNewData(city), WeatherData.class);
        }
        catch(JsonSyntaxException j){
            throw new IllegalArgumentException();
        }

        if (!data.getCityName().equals(city)) {
            updateHistory(city, data.getCityName());
        } else {
            updateHistory(data.getCityName());
        }
        if (!inGraph) {
            weatherLocations.put(data.getCityName(), data);
        }

        cacheDataObjects();
    }

    /**
     *
     * @param data - WeatherData object to be removed from view
     */

    public void removeWeatherDataObject(WeatherData data) {
        weatherLocations.remove(data.getCityName());
        removeFavorite(data.getCityName());
    }

    /**
     * Attempts to load all favorites and historic data saved in last session of application use
     */

    public void loadDataObjects() {
        for (String s : DataStorer.loadFromTextfile("cache.txt")) {
            try{
                WeatherData data = gson.fromJson(s, WeatherData.class);
                WeatherData updatedData = gson.fromJson(handler.fetchNewData(data.getCityName()),WeatherData.class);
                weatherLocations.put(updatedData.getCityName(), updatedData);
                favorites.put(updatedData.getCityName(), updatedData);
            }
            catch(JsonSyntaxException e){
                LOGGER.log(Level.WARNING, "API call could not find saved city: " + s);
            }

        }
        for (String s2 : DataStorer.loadFromTextfile("history.txt")) {
            if (s2.isEmpty()) break;
            try{
                Triple<String, String, Number> data2 = gson.fromJson(s2, Triple.class);
                history.add(new Triple<>(data2.getFirst(), data2.getSecond(), data2.getThird()));
            }
            catch(JsonSyntaxException e){
                LOGGER.log(Level.WARNING, "API call could not find saved city: " + s2);
            }

        }
    }

    /**
     *
     * @param city - The city to be added to favorites
     */

    public void addFavorite(String city) {
        favorites.put(city, weatherLocations.get(city));
        cacheDataObjects();
    }

    /**
     *
     * @param city - The city to be removed from favorites
     */

    public void removeFavorite(String city) {
        favorites.remove(city);
        cacheDataObjects();
    }

    /**
     *
     * @param city - the city to be added to graph
     */
    public void addGraphItem(String city) {
        graphItems.put(city, weatherLocations.get(city));
    }

    /**
     * Removes all items from the graph
     */
    public void cleargraphItems() {
        graphItems.clear();
    }

    /**
     *
     * @param city - The city to be added to history
     * @throws IllegalStateException if city is already shown in the current graph
     */

    private void updateHistory(String city) throws IllegalStateException {
        if (graphItems.containsKey(city)) {
            throw new IllegalStateException();
        }

        WeatherData data = gson.fromJson(handler.fetchNewData(city),WeatherData.class);
        history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));

    }

    /**
     *
     * @param city - the city to be added to history
     * @param alias - the city's alias name
     * @throws IllegalStateException if city is already shown in the current graph
     */

    private void updateHistory(String city, String alias) throws IllegalStateException {
        if (graphItems.containsKey(city) || graphItems.containsKey(alias)) {
            throw new IllegalStateException();
        }
        try{
            WeatherData data = gson.fromJson(handler.fetchNewData(city), WeatherData.class);
            history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));
            aliasNames.put(alias, city);
        }
        catch(JsonSyntaxException j) {
            WeatherData aliasdata = gson.fromJson(handler.fetchNewData(alias), WeatherData.class);
            history.add(new Triple<>(city, aliasdata.getDate(), Integer.valueOf(aliasdata.getTemperature())));
            aliasNames.put(alias, city);
        }
    }

    /**
     *
     * @param data - The objects wanted as a JSON-array
     * @return - The data as a JSON-array
     */

    private List<String> objectsToJson(List<Object> data) {
        List<String> jsonArray = new ArrayList<>();
        for (Object object : data) {
            String json = gson.toJson(object);
            jsonArray.add(json);
        }
        return jsonArray;
    }

    /**
     * Saves the current application state using the DataStorer class
     */

    private void cacheDataObjects() {
        DataStorer.saveAsTextfile(objectsToJson(Arrays.asList(favorites.values().toArray())), "cache.txt");
        DataStorer.saveAsTextfile(objectsToJson(new ArrayList<>(history)), "history.txt");
    }

    public Collection<WeatherData> getWeatherLocations() {
        return weatherLocations.values();
    }

    public Collection<WeatherData> getFavorites() {
        return favorites.values();
    }

    public Collection<Pair<String, Number>> getHistory(String city) {
        List<Pair<String, Number>> cityHistory = new ArrayList<>();
        List<String> aliases = new ArrayList<>();
        aliases.add(city);
        aliases.add(aliasNames.get(city));

        for (Triple<String, String, Number> t : history) {
            if (aliases.contains(t.getFirst())) {
                cityHistory.add(new Pair<>(t.getSecond(), t.getThird()));
            }
        }
        return cityHistory;
    }
}
