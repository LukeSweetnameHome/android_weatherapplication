package uk.ac.abertay.cmp309.project_weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {

    // initialising variables
    private int conditionCode = 500;
    TextView temperatureTextView, locationTextView, mainTextView, feels_likeTextView;
    // declaring df as desired format for temperature and feels like fields
    DecimalFormat df = new DecimalFormat("#" + "°C");
    private ImageView icon_Image_View;
    private CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // adding textview results to variables
        cardView = findViewById(R.id.cardView);
        icon_Image_View = findViewById(R.id.icon_Image_View);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        locationTextView = findViewById(R.id.locationTextView);
        mainTextView = findViewById(R.id.mainTextView);
        feels_likeTextView = findViewById(R.id.feels_likeTextView);

        // Obtaining JSON response from MainActivity
        String jsonResponseString = getIntent().getStringExtra("json_response");

        try {
            JSONObject jsonResponse = new JSONObject(jsonResponseString);

            // Get the location name
            String locationName = jsonResponse.getString("name");

            // Get the temperature
            JSONObject main = jsonResponse.getJSONObject("main");
            double temp = main.getDouble("temp") - 273.15;
            // Formatting temp result to be whole number and have degress celcius symbol
            String formatted = df.format(temp);

            // Get the weather description
            JSONArray weather = jsonResponse.getJSONArray("weather");
            JSONObject weatherObject = weather.getJSONObject(0);
            String description = weatherObject.getString("description");

            // getting condition code
            int condition = getIntent().getIntExtra("condition_code", -1);

            // Get feels_like temp
            double feelsLike = main.getDouble("feels_like") - 273.15;
            // Formatting feels_like temp to same as temp
            String feelslikev2 = df.format(feelsLike);

            // getting iconcode from JSON response
            weather = jsonResponse.getJSONArray("weather");
            weatherObject = weather.getJSONObject(0);
            // Defining icon as iconcode
            String icon = weatherObject.getString("icon");
            // defining iconUrl as the openweather image library + icon variable + png extension
            String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
            Context context = this;
            // using glide to add iconUrl variable to image view within cardview
            Glide.with(context).load(iconUrl).into(icon_Image_View);

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
        // Home button
        Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
