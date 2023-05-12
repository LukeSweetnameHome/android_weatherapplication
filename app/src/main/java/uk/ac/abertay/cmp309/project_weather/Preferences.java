package uk.ac.abertay.cmp309.project_weather;

import androidx.annotation.NonNull;

public class Preferences {

    private String Location;

    public Preferences() {
    }

    public Preferences(String location) {
        Location = location;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    @NonNull
    @Override
    public String toString() {
        return this.Location;
    }
}
