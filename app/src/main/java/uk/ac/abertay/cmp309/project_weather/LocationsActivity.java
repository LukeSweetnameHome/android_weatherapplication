package uk.ac.abertay.cmp309.project_weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationsActivity extends AppCompatActivity implements View.OnClickListener {
    Button goHomeButton;
    EditText editTextLocation;
    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "4e2322456db9e681dcd39712eb48af6b";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
    }
    
    public void getWeatherDetails(View view) {
        String tempUrl;
        String location = editTextLocation.getText().toString().trim();

        if (location.equals("")) {
            Toast.makeText(this, "Please fill in the Location field, or check the current location switch.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Log.d("LocationsActivity", "location: " + location);
            tempUrl = url + "?q=" + location + "," + "&appid=" + appid;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    //int conditionCode = response.getJSONArray("weather").getJSONObject(0).getInt("id");
                    int conditionCode = jsonObjectWeather.getInt("id");
                    String iconCode = jsonObjectWeather.getString("icon");
                    //String iconCode = response.getJSONArray("weather").getJSONObject(0).getString("icon");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String locationName = jsonResponse.getString("name");

                    Intent intent = new Intent(LocationsActivity.this, WeatherActivity.class);
                    intent.putExtra("json_response", jsonResponse.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Add button would probably be an intent carrying the new desired locations weather details to the weatheractivity
    // but also to write, read data from the firebase
    @Override
    public void onClick(View v) {
        Button goHomeButton = findViewById(R.id.goHomeButton);
        Intent intent = new Intent(LocationsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}