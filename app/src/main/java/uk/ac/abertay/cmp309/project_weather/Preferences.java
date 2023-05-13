package uk.ac.abertay.cmp309.project_weather;

import androidx.annotation.NonNull;
// Class for preferences array
public class Preferences {
    // declaring Location name as string
    private String Location;
    // generate preferences constructor
    public Preferences() {
    }
    // generating preferences constructor for string location
    public Preferences(String location) {
        Location = location;
    }
    // getter
    public String getLocation() {
        return Location;
    }
    // setter
    public void setLocation(String location) {
        Location = location;
    }
    // setting toString method
    @NonNull
    @Override
    public String toString() {
        return this.Location;
    }
}
