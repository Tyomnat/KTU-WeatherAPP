package com.example.ktuweather.ui.home;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ktuweather.AddCity;
import com.example.ktuweather.MainActivity;
import com.example.ktuweather.PrefConfig;
import com.example.ktuweather.R;
import com.example.ktuweather.Retrofit.ApiClient;
import com.example.ktuweather.Retrofit.ApiInterface;
import com.example.ktuweather.Retrofit.Example;
import com.example.ktuweather.WeatherDataClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    LinearLayout city_list;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCity.class));
            }
        });

        city_list = root.findViewById(R.id.city_list);

        addCity();

        return root;
    }

    private void addCity() {
        MainActivity activity = (MainActivity) getActivity();
        ArrayList<WeatherDataClass> cities = activity.getCities();

        for(WeatherDataClass cit : cities) {
            final View cityView = getLayoutInflater().inflate(R.layout.row_city_data, null, false);

            ((TextView)cityView.findViewById(R.id.cityName)).setText(cit.getCity());
            ((TextView)cityView.findViewById(R.id.cityTemp)).setText(String.valueOf(cit.getTemperature()) + "°C");

            ImageView imageClose = (ImageView)cityView.findViewById(R.id.removeCity);
            imageClose.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v) {
                    removeCity(cityView);
                }
            });

            city_list.addView(cityView);
        }
    }

    private void removeCity(View view) {
        city_list.removeView(view);
        String cityName = ((TextView)view.findViewById(R.id.cityName)).getText().toString();
        ArrayList<WeatherDataClass> cities = PrefConfig.readListFromPref(getContext());
        for (WeatherDataClass cit : cities){
            if (cit.getCity().equals(cityName)) {
                cities.remove(cit);
                break;
            }
        }
        PrefConfig.writeListInPref(getContext(), cities);
    }
}