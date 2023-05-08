package uk.ac.abertay.cmp309.project_weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LocationsActivity extends AppCompatActivity {

    // Initialise variables
    Button goHomeButton, newLocationButton, viewWeatherButton;
    EditText editTextLocation;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userID;

    TextView userTV;

    // Assigning base API to url variable
    private final String url = "http://api.openweathermap.org/data/2.5/weather";

    // Assigning api key to appid variable
    private final String appid = "4e2322456db9e681dcd39712eb48af6b";


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        // assigning results from textview to variables
        viewWeatherButton = findViewById(R.id.viewWeatherButton);
        editTextLocation= findViewById(R.id.editTextLocation);
        db = FirebaseFirestore.getInstance();

        // Initialising firebase user auth
        mAuth = FirebaseAuth.getInstance();
        // getting current firebase user
        firebaseUser = mAuth.getCurrentUser();
    }

    
    public void getWeatherDetails(View view) {

        // initialising variables
        String tempUrl;
        String location = editTextLocation.getText().toString().trim();

        // conditional logic in case the location edit text is left blank
        if (location.equals("")) {
            Toast.makeText(this, "Please fill in the Location field, or check the current location switch.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            // putting together variables to make api url
            Log.d("LocationsActivity", "location: " + location);
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

                    // Sending user to weatheractivity with API JSON response added as intent extra
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
                // Error reporting
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        // Network request using volley
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Add button would probably be an intent carrying the new desired locations weather details to the weatheractivity
    // but also to write, read data from the firebase

    public void handleHomeButton(View v) {
        // Home button
        Intent intent = new Intent(LocationsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void handleDatabaseWrite(View v) {
        // function to take data entered into location edit text and put it into firebase
        // assigning text entered to Location variable
        String Location = editTextLocation.getText().toString();
        // assigning firebase current user to variable userID
        userID = mAuth.getCurrentUser().getUid();
        userTV = findViewById(R.id.tvUserID);

        // Starting new firebase map for storage
        Map<String, Object> location = new HashMap<>();
        location.put("Location", Location);
            // adding data to location collection
            db.collection("users").document(userID)
                    .update("Location", Location)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        // Successful database write message
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LocationsActivity.this, "Location Added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        // Failed database write message
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LocationsActivity.this, "Location Not Added", Toast.LENGTH_SHORT).show();
                            Log.e("LocationsActivity", "Error adding location", e);
                        }
                    });
            userTV.setText(firebaseUser.getUid());
    }
}