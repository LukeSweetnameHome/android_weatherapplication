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
    private void updateWeather(int conditionCode, String iconCode) {
        // Get the weather condition text and icon drawable
        String conditionText = getConditionText(conditionCode);
        Drawable iconDrawable = getIconDrawable(iconCode);

        // Update the UI with the weather condition text and icon
        TextView conditionTextView = findViewById(R.id.condition_text_view);
        conditionTextView.setText(conditionText);
        ImageView iconImageView = findViewById(R.id.icon_image_view);
        iconImageView.setImageDrawable(iconDrawable);
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
