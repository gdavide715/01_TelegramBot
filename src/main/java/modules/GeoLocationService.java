package modules;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import okhttp3.Request;


public class GeoLocationService {

    private static final String GOOGLE_MAPS_API_KEY = "YOUR_GOOGLE_MAPS_API_KEY";

    public static void main(String[] args) {
        try {
            double[] coordinates = getCurrentLocationCoordinates();
            System.out.println("Latitude: " + coordinates[0]);
            System.out.println("Longitude: " + coordinates[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double[] getCurrentLocationCoordinates() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?address=your_address&key=" + GOOGLE_MAPS_API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        String responseData = response.body().string();

        JSONObject jsonObject = new JSONObject(responseData);
        JSONObject location = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("lng");

        return new double[]{latitude, longitude};
    }
}
