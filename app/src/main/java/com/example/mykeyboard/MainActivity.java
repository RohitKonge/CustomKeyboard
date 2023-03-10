package com.example.mykeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;

import java.time.LocalDate;


public class MainActivity extends AppCompatActivity {

    Button activate_keyboard_btn;
    Button change_keyboard_btn;
    Switch vibration_switch;

    public static boolean is_vibration_on = true;
    Switch dark_theme_switch;

    public static boolean is_dark_theme_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activate_keyboard_btn = this.findViewById(R.id.activate_keyboard_btn);

        activate_keyboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    Intent showInputPicker = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                    showInputPicker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(showInputPicker);
                }
            }
        });

        change_keyboard_btn = this.findViewById(R.id.change_keyboard_btn);

        change_keyboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                    imeManager.showInputMethodPicker();
                }
            }
        });

        vibration_switch = this.findViewById(R.id.vibration_switch_btn);

        vibration_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vibration_switch.isChecked()){
                    Log.d("Checked value", String.valueOf(vibration_switch.isChecked() ));
                }else {
                    Log.d("Checked value", String.valueOf(vibration_switch.isChecked() ));
                }
            }
        });

        dark_theme_switch = this.findViewById(R.id.dark_theme_switch_btn);

        is_dark_theme_on = dark_theme_switch.isChecked();

        dark_theme_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dark_theme_switch.isChecked()){
                    is_dark_theme_on = true;
                }else {
                    is_dark_theme_on = false;
                }
            }
        });
    }
}