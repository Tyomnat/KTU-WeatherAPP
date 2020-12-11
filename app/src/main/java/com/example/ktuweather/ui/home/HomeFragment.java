package com.example.ktuweather.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ktuweather.AddCity;
import com.example.ktuweather.IndicatingView;
import com.example.ktuweather.LogoActivity;
import com.example.ktuweather.MainActivity;
import com.example.ktuweather.PrefConfig;
import com.example.ktuweather.R;
import com.example.ktuweather.WeatherDataClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    LinearLayout city_list;
    CustomProgressBarView progressBar;
    Button sendRequestButton;
    private IndicatingView indicator;

    int progress;
    boolean requestInProgress;
    Handler handler;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCity.class));
            }
        });
        /*handler = new Handler();
        indicator = (IndicatingView) root.findViewById(R.id.generated_graphic);
        progressBar = (CustomProgressBarView) root.findViewById(R.id.cstm_progressbar);
        setProgressBarStatus(CustomProgressBarView.NOTSTARTED, 0);*/
        Button sendRequestButton = (Button) root.findViewById(R.id.send_request);
        sendRequestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });

        city_list = root.findViewById(R.id.city_list);
        addCitiesToView();

        return root;
    }

    private void addCitiesToView() {
        MainActivity activity = (MainActivity) getActivity();
        ArrayList<WeatherDataClass> cities = activity.getCities();

        if (true){
            try {
                if (cities.isEmpty())
                    cities = new ArrayList<>();
            } catch (Exception ex){
                cities = new ArrayList<>();
            }
        }

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

    public void setProgressBarStatus(final int status, final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setState(status);
                progressBar.setProgress(progress);
                progressBar.invalidate();
            }
        });
    }

    private void sendRequest() {
        final MainActivity activity = (MainActivity) getActivity();
        ArrayList<WeatherDataClass> cities = activity.getCities();
        /*requestInProgress = true;
        setIndicatorStatus(IndicatingView.INPROGRESS);
        setProgressBarStatus(CustomProgressBarView.INPROGRESS, 0);
        startProgressBar();*/
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity.updateCityData(activity, 0)) { // SUCCESS
                    try {
                        getActivity().runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                city_list.removeAllViews();
                                addCitiesToView();
                                startActivity(new Intent(activity, LogoActivity.class));
                                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                        //Thread.sleep(3000);
                        //setIndicatorStatus(IndicatingView.SUCCESS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // FAILED
                    //setIndicatorStatus(IndicatingView.FAILED);
                }
            }
        });
        a.start();
        /*indicator.setStrokes(0);
        LoadAnimation loadA = new LoadAnimation(indicator);
        loadA.setDuration(3750);
        indicator.startAnimation(loadA);*/
    }

    public void setIndicatorStatus(final int status){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }

    public void startProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progress = 0;
                while (progress < 100 && requestInProgress) {
                    progress++;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setProgressBarStatus(CustomProgressBarView.INPROGRESS, progress);
                        }
                    });
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setProgressBarStatus(CustomProgressBarView.FINISHED, 100);
            }
        }).start();
    }
}