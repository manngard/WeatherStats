package myApp.temperaturedata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Integer message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<myApp.temperaturedata.List> list = null;
    @SerializedName("city")
    @Expose
    private City city;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public String getDate(){
        return list.get(0).getDtTxt();
    }

    public String getTemperature() {
        return list.get(0).getMain().getTemp();
    }

    public String getWeatherDescription() {
        return list.get(0).getWeather().get(0).getDescription();
    }

    public String getWeather() {
        return list.get(0).getWeather().get(0).getMain();
    }

    public void setList(java.util.List<myApp.temperaturedata.List> list) {
        this.list = list;
    }

    public String getCityName() {
        return city.getName();
    }

    public String getCountry() {
        return city.getCountry();
    }

    public String getCityCoords() {
        return city.getCoord().toString();
    }

    public String getCityPopulation() {
        return city.getPopulation().toString();
    }
}
