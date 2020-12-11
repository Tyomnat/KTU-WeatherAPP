package com.example.ktuweather.ui.statistics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ktuweather.lab3.CompassActivity;
import com.example.ktuweather.lab3.Lab3;
import com.example.ktuweather.R;
import com.example.ktuweather.lab3.Task1and2;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private Button lab3Button;
    private Button task1and2Button;
    private Button compassButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        lab3Button = (Button) root.findViewById(R.id.lab3Button);
        lab3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        task1and2Button = (Button) root.findViewById(R.id.task1and2button);
        task1and2Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Task1and2.class);
                startActivity(intent);
            }
        });

        compassButton = (Button) root.findViewById(R.id.compassButton);
        compassButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CompassActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    public void openNewActivity(){
        Context context = getActivity().getApplicationContext();

        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = Settings.System.canWrite(context);

        if(!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }
        Intent intent = new Intent(getActivity(), Lab3.class);
        startActivity(intent);
    }
}