package uk.ac.abertay.cmp309.project_weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private int conditionCode = 500;

    TextView temperatureTextView, locationTextView, mainTextView, feels_likeTextView;

    DecimalFormat df = new DecimalFormat("#" + "°C");

    /*private Drawable getIconDrawable(Context context, String iconCode) {
        String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";
        String filename = "icon_" + iconCode + ".png";
        File file = new File(context.getCacheDir(), filename);

        if (file.exists()) {
            // Load the icon from cache if it exists
            return Drawable.createFromPath(file.getAbsolutePath());
        } else {
            // Download and cache the icon if it doesn't exist in cache
            try {
                InputStream inputStream = (InputStream) new URL(iconUrl).getContent();
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return Drawable.createFromStream(new FileInputStream(file), "src name");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/

    private void updateWeather(int conditionCode, String iconCode) {
        // Get the weather condition text and icon drawable
        String conditionText = getConditionText(conditionCode);
//        Drawable iconDrawable = getIconDrawable(iconCode);
    }

    private String getConditionText(int conditionCode) {
        String conditionText = "";
        switch (conditionCode) {
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                conditionText = "Thunderstorm";
                break;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                conditionText = "Drizzle";
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:
                conditionText = "Rain";
                break;
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 613:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                conditionText = "Snow";
                break;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:
                conditionText = "Atmosphere";
                break;
            case 800:
                conditionText = "Clear";
                break;
            case 801:
            case 802:
            case 803:
            case 804:
                conditionText = "Clouds";
                break;
        }
        return conditionText;
    }

    private ImageView weatherIcon;
    ImageView icon_Image_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        icon_Image_View = findViewById(R.id.icon_Image_View);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        locationTextView = findViewById(R.id.locationTextView);
        mainTextView = findViewById(R.id.mainTextView);
        feels_likeTextView = findViewById(R.id.feels_likeTextView);


        String jsonResponseString = getIntent().getStringExtra("json_response");

        try {
            JSONObject jsonResponse = new JSONObject(jsonResponseString);

            // Get the location name
            String locationName = jsonResponse.getString("name");

            // Get the temperature
            JSONObject main = jsonResponse.getJSONObject("main");
            double temp = main.getDouble("temp") - 273.15;
            // new
            String formatted = df.format(temp);

            // Get the weather description
            JSONArray weather = jsonResponse.getJSONArray("weather");
            JSONObject weatherObject = weather.getJSONObject(0);
            String description = weatherObject.getString("description");

            int conditionCode = getIntent().getIntExtra("condition_code", -1);
            String iconCode = getIntent().getStringExtra("icon_code");
            updateWeather(conditionCode, iconCode);
            //int conditionCode = weatherObject.getInt("condition_code");
            //String iconCode = weatherObject.getString("icon_code");

            // Get feels_like
            double feelsLike = main.getDouble("feels_like") - 273.15;
            String feelslikev2 = df.format(feelsLike);

            Glide.with(getApplicationContext())
                    .load("https://openweathermap.org/img/w/" + iconCode + ".png")
                    .into(icon_Image_View);

//            updateWeather(conditionCode, iconCode);

            // Use the retrieved data

            String formattedTemp = df.format(temp); // format temperature with one decimal place
            temperatureTextView.setText(formattedTemp);
            locationTextView.setText(locationName);
            mainTextView.setText(description); // need to format to upper case!!!
            String formattedFeelsLike = df.format(feelsLike);
            feels_likeTextView.setText(formattedFeelsLike);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        }
    @Override
    public void onClick(View v) {
        Button goHomeButton = findViewById(R.id.goHomeButton);
        Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
        startActivity(intent);
    }

//    private void getIconDrawable(String iconCode) {
//        int resourceId = 0;
//        switch (iconCode) {
//
//        // Clear
//        case "01d":
//        resourceId = R.drawable.clear_sky_day;
//        break;
//        case "01n":
//        resourceId = R.drawable.clear_sky_night;
//        break;
//
//        // Clouds
//        case "02d":
//        resourceId = R.drawable.few_clouds_day;
//        break;
//        case "02n":
//        resourceId = R.drawable.few_clouds_night;
//        break;
//        case "03d":
//        resourceId = R.drawable.scattered_clouds_day;
//        break;
//        case "03n":
//        resourceId = R.drawable.scattered_clouds_night;
//        break;
//        case "04d":
//        resourceId = R.drawable.broken_clouds_day;
//        break;
//        case "04n":
//        resourceId = R.drawable.broken_clouds_night;
//        break;
//        case "04d":
//        resourceId = R.drawable.overcast_clouds_day;
//        break;
//        case "04n":
//        resourceId = R.drawable.overcast_clouds_night;
//        break;
//
//        // Atmosphere
//        case "50d":
//        resourceId = R.drawable.mist;
//        break;
//        case "50d":
//        resourceId = R.drawable.smoke;
//        break;
//        case "50d":
//        resourceId = R.drawable.haze;
//        break;
//        case "50d":
//        resourceId = R.drawable.sand/dust_whirls;
//        break;
//        case "50d":
//        resourceId = R.drawable.fog;
//        break;
//        case "50d":
//        resourceId = R.drawable.sand;
//        break;
//        case "50d":
//        resourceId = R.drawable.dust;
//        break;
//        case "50d":
//        resourceId = R.drawable.volcanic_ash;
//        break;
//        case "50d":
//        resourceId = R.drawable.squalls;
//        break;
//        case "50d":
//        resourceId = R.drawable.tornado;
//        break;
//
//        // Snow
//        case "13d":
//        resourceId = R.drawable.light_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.heavy_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.sleet;
//        break;
//        case "13d":
//        resourceId = R.drawable.light_shower_sleet;
//        break;
//        case "13d":
//        resourceId = R.drawable.shower_sleet;
//        break;
//        case "13d":
//        resourceId = R.drawable.light_rain_and_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.rain_and_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.light_shower_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.shower_snow;
//        break;
//        case "13d":
//        resourceId = R.drawable.heavy_shower_snow;
//        break;
//
//        // Rain
//        case "10d":
//        resourceId = R.drawable.light_rain;
//        break;
//        case "10d":
//        resourceId = R.drawable.moderate_rain;
//        break;
//        case "10d":
//        resourceId = R.drawable.heavy_intensity_rain;
//        break;
//        case "10d":
//        resourceId = R.drawable.very_heavy_rain;
//        break;
//        case "10d":
//        resourceId = R.drawable.extreme_rain;
//        break;
//        case "13d":
//        resourceId = R.drawable.freezing_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.light_intensity_shower_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.shower_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.heavy_intensity_shower_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.ragged_shower_rain;
//        break;
//
//        // Drizzle
//        case "09d":
//        resourceId = R.drawable.light_intensity_drizzle;
//        break;
//        case "09d":
//        resourceId = R.drawable.drizzle;
//        break;
//        case "09d":
//        resourceId = R.drawable.heavy_intensity_drizzle;
//        break;
//        case "09d":
//        resourceId = R.drawable.light_intensity_drizzle_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.drizzle_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.heavy_intensity_drizzle_rain;
//        break;
//        case "09d":
//        resourceId = R.drawable.shower_rain_and_drizzle;
//        break;
//        case "09d":
//        resourceId = R.drawable.heavy_shower_rain_and_drizzle;
//        break;
//        case "09d":
//        resourceId = R.drawable.shower_drizzle;
//        break;
//
//        // Thunderstorm
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_light_rain;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_rain;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_heavy_rain;
//        break;
//        case "11d":
//        resourceId = R.drawable.light_thunderstorm;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm;
//        break;
//        case "11d":
//        resourceId = R.drawable.heavy_thunderstorm;
//        break;
//        case "11d":
//        resourceId = R.drawable.ragged_thunderstorm;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_light_drizzle;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_drizzle;
//        break;
//        case "11d":
//        resourceId = R.drawable.thunderstorm_with_heavy_drizzle;
//        break;
//    // Add more cases for other weather conditions
//    default:
//        resourceId = R.drawable.unknown;
//        break;
//        }
//        ImageView imageView = findViewById(R.id.icon_Image_View);
//        imageView.setImageResource(resourceId);
//    }
}
