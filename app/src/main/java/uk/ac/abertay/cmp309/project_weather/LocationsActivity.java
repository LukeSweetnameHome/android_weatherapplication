package uk.ac.abertay.cmp309.project_weather;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ListView locationsListView;
    ArrayList<LocationList> locationListsArrayList;
    FirebaseFirestore db;

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


        // below line is use to initialize our variables
        locationsListView = findViewById(R.id.locationsListView);
        locationListsArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();
    }
    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection("users").document(userID).collection("Locations")
                .document().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                LocationList locationList = d.toObject(LocationList.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                locationListsArrayList.add(locationList);
                            }
                            // after that we are passing our array list to our adapter class.
                            LocationLVAdapter adapter = new LocationLVAdapter(LocationsActivity.this, locationListArrayList);

                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                            locationsListView.setAdapter(adapter);
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(LocationsActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(LocationsActivity.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
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
        CollectionReference parentCollectionRef = db.collection("users");
        DocumentReference parentDocRef = parentCollectionRef.document(userID);

        CollectionReference subCollectionRef = parentDocRef.collection("Locations");


        // Starting new firebase map for storage
        Map<String, Object> location = new HashMap<>();
        location.put("Location", Location);
            // adding location data to user collection
            //DocumentReference  =  db.collection("users").document(mAuth.getCurrentUser().getUid()).collection("Location").document()
                    subCollectionRef.add(location)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        // Successful database write message
                        public void onSuccess(DocumentReference messageRef) {
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