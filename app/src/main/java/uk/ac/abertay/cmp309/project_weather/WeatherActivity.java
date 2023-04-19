package uk.ac.abertay.cmp309.project_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class WeatherActivity extends AppCompatActivity {
    TextView textViewPractice;
    EditText editTextLocationPlaceholder, temperatureText;
    // new
    DecimalFormat df = new DecimalFormat("#");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        editTextLocationPlaceholder = findViewById(R.id.editTextLocationPlaceholder);
        temperatureText = findViewById(R.id.temperatureText);
        textViewPractice = findViewById(R.id.textViewPractice);

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

            // Use the retrieved data
            textViewPractice.setText("description: " + description + "\n" + "name: " + locationName + "\n" + "temp: " + temp);
            String formattedTemp = df.format(temp); // format temperature with one decimal place
            temperatureText.setText(formattedTemp); // set formatted temperature to TextView

            editTextLocationPlaceholder.setText("" + locationName);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
