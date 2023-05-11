package uk.ac.abertay.cmp309.project_weather;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LocationLVAdapter extends ArrayAdapter<LocationList> {

    // constructor for our list view adapter.
    public LocationLVAdapter(@NonNull Context context, ArrayList<LocationList> locationListsArrayList) {
        super(context, 0, locationListsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;


        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        LocationList locationList = getItem(position);

        // initializing our UI components of list view item.
        TextView nameTV = listitemView.findViewById(R.id.idTVtext);


        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        nameTV.setText(locationList.getLocation());

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Toast.makeText(getContext(), "Item clicked is : " + locationList.getLocation(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }
}