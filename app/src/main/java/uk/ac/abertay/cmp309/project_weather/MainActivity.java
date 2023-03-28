package uk.ac.abertay.cmp309.project_weather;

import static uk.ac.abertay.cmp309.project_weather.R.id.getWeatherButton;
import static uk.ac.abertay.cmp309.project_weather.R.id.titleTextView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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

import java.text.DecimalFormat;

// API Key
// http://api.openweathermap.org/
// http://api.openweathermap.org/data/2.5/weather?q=London,UK&APPID=4e2322456db9e681dcd39712eb48af6b
// http://api.openweathermap.org/?=London,&api=4e2322456db9e681dcd39712eb48af6b
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    EditText editTextLocationName;
    Switch currentLocationSwitch;
    private Intent myIntent;
    TextView textView2, titleTextView;
    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "4e2322456db9e681dcd39712eb48af6b";
    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLocationName = findViewById(R.id.editTextLocationName);

        myIntent = new Intent(this, WeatherActivity.class);

        Switch currentLocationSwitch = (Switch) findViewById(R.id.currentLocationSwitch);
        if (currentLocationSwitch != null) {
            currentLocationSwitch.setOnCheckedChangeListener(this);
        }
        textView2 = findViewById(R.id.textView2);
        titleTextView = findViewById(R.id.titleTextView);

        //getResources().getDrawable(R.drawable.fog_1);
        //getResources().getDrawable(R.drawable)
    }

    public void getWeatherDetails(View view) {

        String tempUrl = "";
        String location = editTextLocationName.getText().toString().trim();
        StringRequest stringRequest = null;
        if (location.isEmpty()) {
            Toast.makeText(this, "Please fill in the Location field, or check the current location switch.", Toast.LENGTH_SHORT).show();
        } else {
            if (!location.isEmpty()) {
                tempUrl = url + "?q=" + location + "," + "&appid=" + appid;
            }
            stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response", response);
                    String output = "";
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
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String locationName = jsonResponse.getString("name");
                        output += "Current weather of " + locationName;

                        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                        intent.putExtra("json_response", jsonResponse.toString());
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Button getWeatherButton = findViewById(R.id.getWeatherButton);
                    getWeatherButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(myIntent);
                        }
                    });
                }

            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }
            );
    }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "The switch is " + (isChecked ? "on" :"off"), Toast.LENGTH_SHORT).show();
        if(isChecked) {
            // carry out current location activity
        } else {
            Toast.makeText(this, "Please fill in location field or check the current location switch.", Toast.LENGTH_SHORT).show();
        }

    }
}