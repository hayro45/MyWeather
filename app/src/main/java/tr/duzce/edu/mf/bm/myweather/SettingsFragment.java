package tr.duzce.edu.mf.bm.myweather;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Objects;

public class SettingsFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;

    SwitchCompat switchCompat;
    RadioGroup radioGroup;
    RadioButton radioCelsius;
    RadioButton radioFahrenheit;
    private static final String PREFERENCES_NAME = "my_preferences";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        initComponent();
        listenRadioGroup();

        listenSwitchCompact();


    }

    private void listenSwitchCompact() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkTheme", true);
                    }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkTheme", false);
                }
                editor.apply();
            }
        });
    }


    public void listenRadioGroup(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (checkedId == R.id.radioCelcius) {
                    editor.putBoolean("isCelsius", true);
                } else if (checkedId == R.id.radioFahrenheit) {
                    editor.putBoolean("isCelsius", false);
                }

                editor.apply();
            }
        });
    }

    public void initComponent(){
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        
        switchCompat = requireView().findViewById(R.id.switchCompat);
        boolean isCelsius = sharedPreferences.getBoolean("isCelsius", true);
        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);
        
        switchCompat.setChecked(isDarkTheme);

        radioCelsius = requireView().findViewById(R.id.radioCelcius);
        radioFahrenheit = requireView().findViewById(R.id.radioFahrenheit);

        if (isCelsius) {
            radioCelsius.setChecked(true);
        } else {
            radioFahrenheit.setChecked(true);
        }

        radioGroup = requireView().findViewById(R.id.radioGroup);
    }
}