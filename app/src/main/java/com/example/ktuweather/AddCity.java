package com.example.ktuweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ktuweather.Retrofit.ApiClient;
import com.example.ktuweather.Retrofit.ApiInterface;
import com.example.ktuweather.Retrofit.Example;
import com.example.ktuweather.Retrofit.Main;
import com.example.ktuweather.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCity extends AppCompatActivity {

    ImageView search;
    TextView tempText, descText, humidText;
    EditText searchText;

    AppCompatAutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        final Context context = this;

        //Read city list
        ArrayList cityList;
        cityList = JSONtoCityList();

        if (cityList != null) {
            autoCompleteTextView = (AppCompatAutoCompleteTextView) findViewById(R.id.searchText);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, cityList);
            autoCompleteTextView.setThreshold(3); //will start working from the third character
            autoCompleteTextView.setAdapter(adapter);
        }

        search = findViewById(R.id.search);
        tempText = findViewById(R.id.tempText);
        descText = findViewById(R.id.descText);
        humidText = findViewById(R.id.humidText);
        searchText = findViewById(R.id.searchText);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWeatherData(searchText.getText().toString().trim(), context);

            }
        });
    }

    private String loadJSONFromAsset(final Activity act, final String fileName) {
        String text = "";
        try {
            InputStream is = act.getAssets().open(fileName);

            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private ArrayList<String> JSONtoCityList(){
        Gson gson = new Gson();

        ArrayList<String> cityList = new ArrayList<>();

        String json = loadJSONFromAsset(this, "city.list.json");
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        try{
            cityList = new Gson().fromJson(json, listType);
            return cityList;
        }
        catch (Exception e) {
            // we never know :)
        }

        return cityList;
    }

    private void getWeatherData(final String name, final Context context){
        final WeatherDataClass cityData = new WeatherDataClass();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Example> call = apiInterface.getWeatherData(name);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if (response.body() != null) {

                    cityData.setCity(name);
                    cityData.setTemperature(response.body().getMain().getTemp());
                    cityData.setFeels_like(response.body().getMain().getFeels_like());
                    cityData.setHumidity(response.body().getMain().getHumidity());
                    cityData.setUpdateTime();

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("cityData", cityData);
                    startActivity(i);
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Please enter a valid city", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please check your internet connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}