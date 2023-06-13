/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.placesdemo;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.placesdemo.databinding.AutocompleteAddressActivityBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity for using Place Autocomplete to assist filling out an address form.
 */
@SuppressWarnings("FieldCanBeLocal")
public class TestActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private static final String TAG = "ADDRESS_AUTOCOMPLETE";
    private static final String MAP_FRAGMENT_TAG = "MAP";
    private LatLng coordinates;
    private boolean checkProximity = false;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker marker;
    private View mapPanel;

    private AutocompleteAddressActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AutocompleteAddressActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // LEGACY - long click on marker works
        //MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LEGACY, this);

        // LATEST - long click on marker doesn't work
        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

        showMap();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.setOnMarkerClickListener(marker -> {
            Toast.makeText(TestActivity.this, "OnMarkerClickListener", Toast.LENGTH_SHORT).show();
            return false;
        });
        map.setOnMapLongClickListener(latLng -> {
            Toast.makeText(TestActivity.this, "OnMapLongClickListener", Toast.LENGTH_SHORT).show();
        });
        map.setOnMapClickListener(latLng -> {
            Toast.makeText(TestActivity.this, "OnMapClickListener", Toast.LENGTH_SHORT).show();
        });

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a string resource.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f));
        marker = map.addMarker(new MarkerOptions().position(coordinates));
    }

    private void showMap() {
        coordinates = new LatLng(37.0930750107, -81.2461160744);

        // It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

        // We only create a fragment if it doesn't already exist.
        if (mapFragment == null) {
            mapPanel = ((ViewStub) findViewById(R.id.stub_map)).inflate();
            GoogleMapOptions mapOptions = new GoogleMapOptions();
            mapOptions.mapToolbarEnabled(false);

            // To programmatically add the map, we first create a SupportMapFragment.
            mapFragment = SupportMapFragment.newInstance(mapOptions);

            // Then we add it using a FragmentTransaction.
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.confirmation_map, mapFragment, MAP_FRAGMENT_TAG)
                    .commit();
            mapFragment.getMapAsync(this);
        } else {
            updateMap(coordinates);
        }
    }

    private void updateMap(LatLng latLng) {
        marker.setPosition(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        if (mapPanel.getVisibility() == View.GONE) {
            mapPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                 Toast.makeText(TestActivity.this, "The latest version of the renderer is used.", Toast.LENGTH_SHORT).show();
                break;
            case LEGACY:
                Toast.makeText(TestActivity.this, "The legacy version of the renderer is used.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}