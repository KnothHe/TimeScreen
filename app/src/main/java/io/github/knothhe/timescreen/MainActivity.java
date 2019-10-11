package io.github.knothhe.timescreen;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String USE_DARK_THEME = "USE_DARK_THEME";
    private TextView mContentView;
    private FontAwesome switchThemeBtn;
    private FontAwesome fontSizeBtn;
    private SeekBar fontSizeBar;
    private List<FontAwesome> configBtns;
    private boolean configBtnsVisibility = false;
    private int maxFontSize = 90;
    private int minFontSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set activity theme
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setTheme(preferences.getBoolean(USE_DARK_THEME, true) ?
                R.style.FullScreenTheme_DarkTheme : R.style.FullScreenTheme_LightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        configBtns = new ArrayList<>();

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setOnClickListener(this);
        mContentView.setTextSize(maxFontSize);

        switchThemeBtn = findViewById(R.id.switch_theme);
        switchThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTheme();
                hideConfigBtns();
            }
        });
        configBtns.add(switchThemeBtn);

        fontSizeBtn = findViewById(R.id.font_size);
        fontSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontSizeBar.setVisibility(View.VISIBLE);
            }
        });
        configBtns.add(fontSizeBtn);

        fontSizeBar = findViewById(R.id.font_size_bar);
        fontSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int fontSize = (int) ((progress + 10) / 100.0 * maxFontSize);
                mContentView.setTextSize(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hideConfigBtns();

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
        if (configBtnsVisibility) {
            hideConfigBtns();
        } else {
            showConfigBtns();
        }
        configBtnsVisibility = !configBtnsVisibility;
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

    private void setConfigBtnsVisibility(int visibility) {
        for (FontAwesome btn : configBtns) {
            btn.setVisibility(visibility);
        }
        fontSizeBar.setVisibility(View.INVISIBLE);
    }

    private void showConfigBtns() {
        setConfigBtnsVisibility(View.VISIBLE);
    }

    private void hideConfigBtns() {
        setConfigBtnsVisibility(View.INVISIBLE);
    }

}
