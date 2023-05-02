package uk.ac.abertay.cmp309.project_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationsActivity extends AppCompatActivity implements View.OnClickListener {
    Button goHomeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
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