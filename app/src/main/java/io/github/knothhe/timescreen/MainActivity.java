package io.github.knothhe.timescreen;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String USE_DARK_THEME = "USE_DARK_THEME";
    private TextView mContentView;
    private FontAwesome switchThemeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set activity theme
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(preferences.getBoolean(USE_DARK_THEME, true) ?
                R.style.FullScreenTheme_DarkTheme : R.style.FullScreenTheme_LightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(this);

        switchThemeBtn = findViewById(R.id.switch_theme);
        switchThemeBtn.setVisibility(View.INVISIBLE);
        switchThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTheme();
                switchThemeBtn.setVisibility(View.INVISIBLE);
            }
        });

        // Start timer
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date currentTime = Calendar.getInstance().getTime();
                        mContentView.setText(formatTime(currentTime));
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        int switchThemeBtnVisibility = switchThemeBtn.getVisibility();
        if (switchThemeBtnVisibility == View.INVISIBLE) {
            switchThemeBtn.setVisibility(View.VISIBLE);
        } else {
            switchThemeBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void switchTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useDarkTheme = preferences.getBoolean(USE_DARK_THEME, true);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(USE_DARK_THEME, !useDarkTheme);
        editor.apply();
        recreate();
    }

    private String formatTime(Date time) {
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("HH : mm : ss");
        format.setTimeZone(TimeZone.getDefault());
        return format.format(time.getTime());
    }

}
