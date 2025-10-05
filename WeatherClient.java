import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherClient {

    // OpenWeatherMap API (requires an API key)
    private static final String BASE = "https://api.openweathermap.org/data/2.5/weather";

    public static JsonObject fetchWeather(String city, String apiKey) throws IOException, InterruptedException {
        String uri = String.format(BASE + "?q=%s&appid=%s&units=metric", city, apiKey);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new IOException("HTTP error: " + resp.statusCode() + " body: " + resp.body());
        }

        JsonElement je = JsonParser.parseString(resp.body());
        return je.getAsJsonObject();
    }

    public static void printWeatherSummary(JsonObject json) {
        String city = json.get("name").getAsString();
        JsonObject main = json.getAsJsonObject("main");
        double temp = main.get("temp").getAsDouble();
        int humidity = main.get("humidity").getAsInt();
        String weatherDesc = json.getAsJsonArray("weather")
                                 .get(0).getAsJsonObject()
                                 .get("description").getAsString();

        System.out.printf(
            "City: %s%nTemp: %.1f °C%nHumidity: %d%%%nConditions: %s%n",
            city, temp, humidity, weatherDesc
        );
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java WeatherClient <city> <API_KEY>");
            return;
        }
        String city = args[0];
        String key = args[1];
        try {
            JsonObject json = fetchWeather(city, key);
            printWeatherSummary(json);
        } catch (Exception e) {
            System.err.println("Failed to fetch weather: " + e.getMessage());
        }
    }
}
