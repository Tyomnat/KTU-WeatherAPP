package com.example.ktuweather;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import com.example.ktuweather.Retrofit.ApiClient;
import com.example.ktuweather.Retrofit.ApiInterface;
import com.example.ktuweather.Retrofit.Example;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ArrayList<WeatherDataClass> cities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cities = PrefConfig.readListFromPref(this);
        if (true){
            try {
                if (cities.isEmpty())
                    cities = new ArrayList<>();
            } catch (Exception ex){
                cities = new ArrayList<>();
            }
        }
        else if ((cities.get(0).getUpdateTime().getTime())/60000 < (new Date().getTime())/60000){
            if (updateCityData(this, 0)) {
                cities = PrefConfig.readListFromPref(this);
                finish();
                startActivity(getIntent());
            }
        }
        setContentView(R.layout.activity_main);
        Bundle objectCity = getIntent().getExtras();
        if (objectCity != null) {
            WeatherDataClass c = objectCity.getParcelable("cityData");
            if (!containsCity(c, cities)) {
                cities.add(c);
                PrefConfig.writeListInPref(this, cities);
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "This city is already added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } // if new city is being added

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_statistics)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public ArrayList<WeatherDataClass> getCities(){
        cities = PrefConfig.readListFromPref(this);
        return cities;
    }

    private boolean containsCity (WeatherDataClass c, ArrayList<WeatherDataClass> cities){
        for (WeatherDataClass cit : cities){
            if (cit.getCity().equals(c.getCity()))
                return true;
        }
        return false;
    }

    public boolean updateCityData (final Context context, final int i){
        final boolean[] success = {false};
        if (i<cities.size()) {
            final String cityName = cities.get(i).getCity();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            final Call<Example> call = apiInterface.getWeatherData(cityName);
            Thread update = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<Example> response = call.execute();
                        WeatherDataClass cityData = new WeatherDataClass();
                        cityData.setCity(cityName);
                        cityData.setTemperature(response.body().getMain().getTemp());
                        cityData.setFeels_like(response.body().getMain().getFeels_like());
                        cityData.setHumidity(response.body().getMain().getHumidity());
                        cityData.setUpdateTime();
                        cities.set(i, cityData);
                        if (updateCityData(context, i + 1)) {
                            success[0] = true;
                        }
                        if (i == 0) {
                            PrefConfig.writeListInPref(context, cities);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            update.start();
            try {
                update.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (success[0])
                return true;
        } else {
            return true;
        }
        return false;
    }
}