package com.example.group4planninggame.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.group4planninggame.R;
import com.example.group4planninggame.adapters.MapInfoPanelAdapter;
import com.example.group4planninggame.models.Product;
import com.example.group4planninggame.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private DatabaseReference productsRef;
    private LatLng pos;
    private double radius;
    private List<Product> productList;
    private String category;
    private String keyword;

    public static MapsFragment newInstance(Bundle info) {
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(info);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productsRef = FirebaseDatabase.getInstance().getReference("PRODUCT");
        Context context = getContext();

        if (getArguments() != null) {
            double latitude = getArguments().getDouble("SEARCH_LAT");
            double longitude = getArguments().getDouble("SEARCH_LON");
            pos = new LatLng(latitude, longitude);
            category = getArguments().getString("CATEGORY_FILTER");
            keyword = getArguments().getString("PRODUCT_NAME");
            radius = getArguments().getInt("ITEM_RADIUS");
        } else {
            pos = new LatLng(Constants.USER_LOCATION.getLatitude(), Constants.USER_LOCATION.getLongitude());
            radius = 10000;
            category = getResources().getStringArray(R.array.ItemCategory)[0];
            keyword = null;
        }

        getProductList(context);
    }

    private void getProductList(Context context) {
        productList = new ArrayList<>();
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear(); // Clear to avoid duplication on data change
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    Location loc = new Location("");
                    loc.setLatitude(pos.latitude);
                    loc.setLongitude(pos.longitude);
                    if (product != null
                            && (category.equals(context.getResources().getStringArray(R.array.ItemCategory)[0]) || product.getCategory().equalsIgnoreCase(category))
                            && (keyword == null || product.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                            && radius >= product.getDistance(loc)
                    ) {
                        productList.add(product);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MapSearchListError", error.toString());
            }
        });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setInfoWindowAdapter(new MapInfoPanelAdapter(getActivity()));
            CircleOptions options = new CircleOptions();
            options.radius(radius);
            options.center(pos);
            googleMap.addCircle(options);

            for (Product prod : productList) {
                MarkerOptions marker = new MarkerOptions();
                marker.title(prod.getTitle());
                String descString;
                if(prod.getDescription().trim().isEmpty()) {
                    descString = "";
                } else {
                    descString = prod.getDescription() + "\n";
                }
                marker.snippet(prod.getUserEmail() + "\n" + descString + "Tap for details.");
                marker.position(new LatLng(prod.getLat(), prod.getLon()));

                googleMap.addMarker(marker);
            }

            googleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                return true;
            });

            googleMap.setOnInfoWindowClickListener(marker -> {
                String email = marker.getSnippet().split("\n")[0];
                String title = marker.getTitle();
                String id = email + " " + title;
                Product selectedProduct = null;
                for (Product p : productList) {
                    if ((p.getUserEmail() + " " + p.getTitle()).equals(id)) {
                        selectedProduct = p;
                    }
                }
                if (selectedProduct != null) {
                    Intent detailsIntent = new Intent(getActivity(), ProductDetailsActivity.class);
                    detailsIntent.putExtra("PRODUCT_TITLE", selectedProduct.getTitle());
                    detailsIntent.putExtra("PRODUCT_CATEGORY", selectedProduct.getCategory());
                    detailsIntent.putExtra("PRODUCT_CONDITION", selectedProduct.getCondition());
                    detailsIntent.putExtra("PRODUCT_USER_EMAIL", selectedProduct.getUserEmail());
                    detailsIntent.putExtra("PRODUCT_DESCRIPTION", selectedProduct.getDescription());
                    detailsIntent.putExtra("PRODUCT_LAT", selectedProduct.getLat());
                    detailsIntent.putExtra("PRODUCT_LON", selectedProduct.getLon());
                    startActivity(detailsIntent);
                }
            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}