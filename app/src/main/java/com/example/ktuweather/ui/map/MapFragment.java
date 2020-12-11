package com.example.ktuweather.ui.map;

import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ktuweather.CustomInfoWindow;
import com.example.ktuweather.MainActivity;
import com.example.ktuweather.Retrofit.ApiClient;
import com.example.ktuweather.Retrofit.ApiInterface;
import com.example.ktuweather.WeatherDataClass;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.ktuweather.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapViewModel mapViewModel;
    GoogleMap gMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapFragment.onCreate(mapViewBundle);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        final MainActivity activity = (MainActivity) getActivity();
        ArrayList<WeatherDataClass> cities = activity.getCities();
        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
        CustomInfoWindow window = new CustomInfoWindow(activity);
        googleMap.setInfoWindowAdapter(window);
        for(WeatherDataClass city : cities){
            Geocoder geocoder = new Geocoder(activity);
            int maxResults = 1;
            try {
                List<Address> address = geocoder.getFromLocationName(city.getCity(), maxResults);
                double lat = address.get(0).getLatitude();
                double lon = address.get(0).getLongitude();
                LatLng latLng = new LatLng(lat, lon);
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                marker.title(city.getCity());
                marker.snippet("Temperature: " + city.getTemperature() + "°C\nFeels like: " +
                        city.getFeels_like() + "°C\nHumidity: " + city.getHumidity() + "%");
                googleMap.addMarker(marker);
                markers.add(marker);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
    }
}