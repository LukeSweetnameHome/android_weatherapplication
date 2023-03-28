package uk.ac.abertay.cmp309.project_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        String jsonResponseString = getIntent().getStringExtra("json_response");
        try {
            JSONObject jsonResponse = new JSONObject(jsonResponseString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}