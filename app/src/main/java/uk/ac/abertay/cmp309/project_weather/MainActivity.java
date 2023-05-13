package uk.ac.abertay.cmp309.project_weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    // Initialising variables
    EditText editTextLocationName;
    private Switch currentLocationSwitch;

    // specifying locationrequest library as google, not android
    private com.google.android.gms.location.LocationRequest locationRequest;
    private Intent myIntent;
    TextView textView2, titleTextView;
    TextView userEmail;
    private static final String TAG = "MainActivity";

    Button goToLocations;
    Button userLogout;

    // initialising latitude and longitude variables
    private double latitude, longitude;

    // Initialising firebaseAuth variable
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    // assign base api url to url variable
    private final String url = "http://api.openweathermap.org/data/2.5/weather";

    // assigning api key to appid variable
    private final String appid = "4e2322456db9e681dcd39712eb48af6b";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // directing variable names to correct textviews or buttons
        editTextLocationName = findViewById(R.id.editTextLocationName);
        userEmail = findViewById(R.id.tvUserEmail);
        userLogout = findViewById(R.id.btnLogout);

        // Initialising firebase user auth
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Inserting user email into text box
        userEmail.setText(firebaseUser.getEmail());

        // Attaching logout method to Sign out button on main activity
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // signing out firebase user
                FirebaseAuth.getInstance().signOut();
                // directing user to login again if logged out
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // flags to ensure once user is signed out, using back button does not show user details
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // declaring new intent
        myIntent = new Intent(this, WeatherActivity.class);
        // location request from google libraries for current location switch
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        // Try to implement wifi checking
        //<uses-permission android:name="android.permission.INTERNET" />

        // creating new location update listener
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                // latitude and longitude request
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };
        // check if the user has given location request permission to the app

        // initialise current location switch
        currentLocationSwitch = findViewById(R.id.currentLocationSwitch);
        if (currentLocationSwitch != null) {
            // checking to see if permission has been granted, if it hasn't, then a request to the user is made
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            currentLocationSwitch.setOnCheckedChangeListener(this);
        }
        // initialising text view variables
        textView2 = findViewById(R.id.textView2);
        titleTextView = findViewById(R.id.titleTextView);
    }

    public void getWeatherDetails(View view) {
        // initialising url variable and location textview
        String tempUrl;
        String location = editTextLocationName.getText().toString().trim();
        // conditional logic if neither the location textview is populated and current location switch is checked
        if (location.equals("") && !currentLocationSwitch.isChecked()) {
            Toast.makeText(this, "Please fill in the Location field, or check the current location switch.", Toast.LENGTH_SHORT).show();
            return;
        }
        // conditional logic if the current location switch is checked
        if (currentLocationSwitch != null && currentLocationSwitch.isChecked()) {
            Log.d("MainActivity", "location: " + location);
            // accessing location manager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // checking if location permission has been granted

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // get last known location from location manager
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // if last known location is null, assign getLatitude & getLongitude methods to latitude & longitude variables
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
            }
            // assign latitude and longitude to api url to be used when using currentlocationswitch
            tempUrl = url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + appid;
        } else {
            // if none of the conditions are met, it is assumed the location textview is populated by a valid location
            Log.d("MainActivity", "location: " + location);
            tempUrl = url + "?q=" + location + "," + "&appid=" + appid;
        }
        // Retrieving API in JSON format
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                    int condition = jsonObjectWeather.getInt("id");
                    String icon = jsonObjectWeather.getString("icon");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String locationName = jsonResponse.getString("name");
                    // sending user to weatheractivity with API in JSON format as extra
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("json_response", jsonResponse.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }, new Response.ErrorListener() {
                @Override
                // error report
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }
            );
        // Network request using volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // toast message showing if the switch checked or not
        Toast.makeText(this, "The switch is " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
        if (isChecked) {
            // carry out current location activity
            // check if location permissions are granted
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // toast message saying if permissions have been granted
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();

                if (isGPSEnabled()) {
                    // check if gps is enabled for current location switch
                    // toast message to say whether gps is enabled
                    Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();
                    // initialise location provider client in this activity
                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            // request location updates from above location provider
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
                                    // remove location updates till new request is made
                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    // Retrieve latitude and longitude values from last location within locationresult object
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        // assigning longitude and latitude values to variables
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    // toast message alert if gps is not on
                    Toast.makeText(this, "GPS is not on", Toast.LENGTH_SHORT).show();
                    turnOnGPS();
                }

            } else {
                // if location permissions were not activated at first check, activate them now.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }




    // method to turn gps on
    private void turnOnGPS() {
        // creating location setting request object with required location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        // show location settings
        builder.setAlwaysShow(true);

        // check to see if location settings meet requirements specified in locations setting request object
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        // create on complete listener
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    // deal with result of location settings check
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {
                    // get status code of above exception
                    switch (e.getStatusCode()) {
                        // in the below case we prompt the user to check their location settings
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
    // method to handle if gps is enabled
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        // check to see if location manager is null , if it is, it initialises it
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } else {
            Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show();
        }
        // if check comes back that location manager is enabled return boolean isEnabled true
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    //@Override
    public void handleLocationsButton(View v) {
        // button for directing user to locations page
        Intent intent = new Intent(MainActivity.this, LocationsActivity.class);
        startActivity(intent);
    }
}