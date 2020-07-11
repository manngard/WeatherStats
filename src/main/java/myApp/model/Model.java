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
    private JSONParser <WeatherData> parser = new JSONParser<>();
    Gson gson = new Gson();
    private final Map<String, WeatherData> weatherLocations = new HashMap<>();
    private final Map<String, WeatherData> graphItems = new HashMap<>();
    private final Map<String, WeatherData> favorites = new HashMap<>();
    private final Map<String, String> aliasNames = new HashMap<>();
    private final Set<Triple<String, String, Number>> history = new HashSet<>();

    public void createWeatherDataObject(String location, boolean inGraph) throws IllegalArgumentException, IllegalStateException {
        String city = aliasNames.getOrDefault(location, location);
        WeatherData data;
        try{
           data = parser.objectFromJson(handler.fetchNewData(city), WeatherData.class);
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



    private void cacheDataObjects() {
        DataStorer.saveAsTextfile(parser.objectsToJson(Arrays.asList(favorites.values().toArray())), "cache.txt");
        DataStorer.saveAsTextfile(parser.objectsToJson(new ArrayList<>(history)), "history.txt");
    }

    public void loadDataObjects() {
        for (String s : DataStorer.loadFromTextfile("cache.txt")) {
            try{
                WeatherData data = parser.objectFromJson(s, WeatherData.class);
                WeatherData updatedData = parser.objectFromJson(handler.fetchNewData(data.getCityName()),WeatherData.class);
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

    public void removeWeatherDataObject(WeatherData data) {
        weatherLocations.remove(data.getCityName());
        removeFavorite(data.getCityName());
    }

    public void removeFavorite(String city) {
        favorites.remove(city);
        cacheDataObjects();
    }

    public void addFavorite(String city) {
        favorites.put(city, weatherLocations.get(city));
        cacheDataObjects();
    }

    public void addGraphItem(String city) {
        graphItems.put(city, weatherLocations.get(city));
    }

    public void cleargraphItems() {
        graphItems.clear();
    }


    private void updateHistory(String city) throws IllegalStateException {
        if (graphItems.containsKey(city)) {
            throw new IllegalStateException();
        }

        WeatherData data = parser.objectFromJson(handler.fetchNewData(city),WeatherData.class);
        history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));

    }

    private void updateHistory(String city, String alias) throws IllegalArgumentException, IllegalStateException {
        if (graphItems.containsKey(city) || graphItems.containsKey(alias)) {
            throw new IllegalStateException();
        }
        try{
            WeatherData data = parser.objectFromJson(handler.fetchNewData(city), WeatherData.class);
            history.add(new Triple<>(city, data.getDate(), Integer.valueOf(data.getTemperature())));
            aliasNames.put(alias, city);
        }
        catch(JsonSyntaxException j) {
            WeatherData aliasdata = parser.objectFromJson(handler.fetchNewData(alias), WeatherData.class);
            history.add(new Triple<>(city, aliasdata.getDate(), Integer.valueOf(aliasdata.getTemperature())));
            aliasNames.put(alias, city);
        }
    }
}
