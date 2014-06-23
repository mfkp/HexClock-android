package com.vibramedia.hexclock;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private TextView hexText;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hexText = (TextView)findViewById(R.id.hexText);
        rl = (RelativeLayout)findViewById(R.id.main_layout);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/helveticaneueultralight.ttf");
        hexText.setTypeface(face);

        Timer timer = new Timer();
        TimerTask tick = new TimerTick();
        timer.scheduleAtFixedRate(tick, 0, 1000);
    }

    class TimerTick extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String hex = "#" + android.text.format.DateFormat.format("HHmmss", new Date());
                    hexText.setText(hex);
                    rl.setBackgroundColor(Color.parseColor(hex));
                }
            });
        }
    }

}
