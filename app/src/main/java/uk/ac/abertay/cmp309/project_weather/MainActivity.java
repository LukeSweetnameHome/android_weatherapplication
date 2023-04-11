package uk.ac.abertay.cmp309.project_weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

// API Key
// http://api.openweathermap.org/
// http://api.openweathermap.org/data/2.5/weather?q=London,UK&APPID=4e2322456db9e681dcd39712eb48af6b
// http://api.openweathermap.org/?=London,&api=4e2322456db9e681dcd39712eb48af6b

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    EditText editTextLocationName;
    private Switch currentLocationSwitch;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private Intent myIntent;
    TextView textView2, titleTextView;
    private TextView addressText;
    private static final String TAG = "MainActivity";

    private double latitude;
    private double longitude;

    private final String url = "http://api.openweathermap.org/data/2.5/weather";
    private final String appid = "4e2322456db9e681dcd39712eb48af6b";
    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLocationName = findViewById(R.id.editTextLocationName);

        myIntent = new Intent(this, WeatherActivity.class);

        Switch currentLocationSwitch;
        addressText = findViewById(R.id.addressText);

        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        currentLocationSwitch = (Switch) findViewById(R.id.currentLocationSwitch);
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
        // https://api.openweathermap.org/data/2.5/weather?lat={latitude}&lon={longitude}&appid={API key}
        if (location.equals("")) {
            Toast.makeText(this, "Please fill in the Location field, or check the current location switch.", Toast.LENGTH_SHORT).show();
        } else {
            if (!location.isEmpty()) {
                tempUrl = url + "?q=" + location + "," + "&appid=" + appid;
            }
            else {
                if (currentLocationSwitch.isChecked()) {
                    tempUrl = url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + appid;
                }
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
                        //output += "Current weather of " + locationName
                        //       + "\n Temp: " + df.format(temp) + "C";

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
        Toast.makeText(this, "The switch is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
        if (isChecked) {
            // carry out current location activity
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();

                if (isGPSEnabled()) {
                    Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    Log.d(TAG, "onLocationResult: location result received");
                                    if (locationResult == null) {
                                        Log.d(TAG, "onLocationResult: location result is null");
                                        return;
                                    }

                                    // Log the latitude and longitude values
                                    for (Location location : locationResult.getLocations()) {
                                        Log.d(TAG, "onLocationResult: latitude=" + location.getLatitude() + ", longitude=" + location.getLongitude());
                                    }

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        addressText.setText("Latitude: " + latitude + "/n" + "Longitude: " + longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    Toast.makeText(this, "GPS is not on", Toast.LENGTH_SHORT).show();
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }




    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }


    @Override
    public void onClick(View v) {

    }
}