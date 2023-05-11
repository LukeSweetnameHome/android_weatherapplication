public class LocationList {
    // variables for storing our name.
    private String location;

    public LocationList() {
        // empty constructor required for firebase.
    }
    // constructor for our object class.
    public LocationList(String location) {
        this.location = location;
    }
    // getter and setter methods
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
