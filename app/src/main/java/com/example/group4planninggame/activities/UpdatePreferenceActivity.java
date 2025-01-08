package com.example.group4planninggame.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group4planninggame.R;
import com.example.group4planninggame.interfaces.PreferenceViewInterface;
import com.example.group4planninggame.managers.Crud;
import com.example.group4planninggame.managers.CrudSingleton;
import com.example.group4planninggame.adapters.CategoriesAdapter;
import com.example.group4planninggame.models.Preferences;
import com.example.group4planninggame.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class UpdatePreferenceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PreferenceViewInterface {
    private RecyclerView recyclerView;
    private CategoriesAdapter categoriesAdapter;
    private List<String> preferenceList;
    private Crud crud;

    private Button updateBackButton;
    private TextView noOffersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        crud = CrudSingleton.getInstance().getCrud();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_preferences);

        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        String[] categoryOptions = getResources().getStringArray(R.array.ItemCategory);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, categoryOptions);
        categorySpinner.setAdapter(aa);

        recyclerView = findViewById(R.id.categoriesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        preferenceList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(preferenceList, this);
        recyclerView.setAdapter(categoriesAdapter);
        updateBackButton = findViewById(R.id.updateBackButton);

        categorySpinner.setOnItemSelectedListener(this);

        loadUserPreferences();
        setupBackButton();

        Button setLocation = findViewById(R.id.setLocationButton);
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView distanceInput = findViewById(R.id.DistanceInput);
                TextView locationInput = findViewById(R.id.LocationInput);

                // so that empty distance is not registered as a location. (if set, notifications will fail to work as intended)
                if (distanceInput.getText().length() > 0) {
                    Double distance;
                    distance = Double.parseDouble(distanceInput.getText().toString().trim());
                    updateLocationAndDistance(distance,locationInput.getText().toString());
                    String distanceString = "Distance: " + distance;
                    distanceInput.setHint(distanceString);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Can't set location, no distance specified",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    protected void updateLocationTextView(Preferences preferences) {
        TextView locationName = findViewById(R.id.LocationName);
        if (preferences == null || preferences.getLat() == null) {
            locationName.setText("Location not set");
        } else {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(preferences.getLat(),
                        preferences.getLon(), 1);
                if (addressList == null || addressList.isEmpty()) {
                    locationName.setText("Lat: " + preferences.getLat() +
                            " Lon: " + preferences.getLon() +
                            "Distance: " + preferences.getDistance() + "m");
                } else {
                    locationName.setText(addressList.get(0).getLocality() +
                            " Distance: " + preferences.getDistance() + "m");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void updateLocationAndDistance(Double distance,String locationAddrStr) {
        final AtomicReference<Preferences> preferencesRef = new AtomicReference<>(crud.readUserPreferences());
        if (preferencesRef.get() == null) {
            preferencesRef.set(new Preferences());
        }

        final Handler handler = new Handler();
        final boolean[] toasted = {false};

        Runnable checkLocationRunnable = new Runnable() {
            @Override
            public void run() {
                if (Constants.USER_LOCATION == null) {
                    if (!toasted[0]) {
                        Toast.makeText(getApplicationContext(), "Waiting for location services.",
                                Toast.LENGTH_SHORT).show();
                        toasted[0] = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Still waiting for location services.",
                                Toast.LENGTH_SHORT).show();
                    }
                    handler.postDelayed(this, 1000); // Check again in 1 second
                } else {
                    // Proceed once location is available
                    Preferences preferences = preferencesRef.get();

                    String locationServiceSetMessage;
                    locationServiceSetMessage = preferences.setLocation(getApplicationContext(), locationAddrStr);
                    preferences.setDistance(distance);

                    crud.writeUserPreferences(preferences);
                    categoriesAdapter.notifyDataSetChanged();
                    updateLocationTextView(preferences);
                    Toast.makeText(getApplicationContext(), locationServiceSetMessage,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        handler.post(checkLocationRunnable);
    }


    private void loadUserPreferences() {
        Preferences preferences = crud.readUserPreferences();
        if (preferences == null) {
            preferences = new Preferences(new ArrayList<>());
        }
        preferenceList.clear();
        preferenceList.addAll(preferences.getCategories());
        categoriesAdapter.notifyDataSetChanged();
        updateLocationTextView(preferences);
    }

    protected void addCategory(String category) {
        Preferences preferences = crud.readUserPreferences();
        if (preferences == null) {
            preferences = new Preferences(new ArrayList<>());
        }
        if (preferences.addCategory(category) && !category.isEmpty()) {
            crud.writeUserPreferences(preferences);
            preferenceList.clear();
            preferenceList.addAll(preferences.getCategories());
            categoriesAdapter.notifyDataSetChanged();
            String addCategorySetMessage = category + " added!";
            Toast.makeText(getApplicationContext(), addCategorySetMessage,
                    Toast.LENGTH_SHORT).show();
        }else{
            preferenceList.clear();
            preferenceList.addAll(preferences.getCategories());
            categoriesAdapter.notifyDataSetChanged();
            String addCategorySetMessage = category + " already added! :(";
            Toast.makeText(getApplicationContext(), addCategorySetMessage,
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String category = adapterView.getItemAtPosition(i).toString();
        if (!category.equals("Select Category")) {
            addCategory(category);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(int position) {
        Preferences preferences = crud.readUserPreferences();
        preferenceList.remove(position);
        preferences.setCategories(preferenceList);
        crud.writeUserPreferences(preferences);
        categoriesAdapter.notifyDataSetChanged();
    }

    public void setupBackButton(){
        updateBackButton.setOnClickListener(view -> {
            finish();
        });
    }
}
