package com.vibramedia.hexclock;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnTouchListener {

    private TextView hexText;
    private RelativeLayout rl;

    private int _xDelta;
    private int _yDelta;

    private SpringSystem springSystem;
    private Spring spring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hexText = (TextView)findViewById(R.id.hexText);
        rl = (RelativeLayout)findViewById(R.id.main_layout);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/helveticaneueultralight.ttf");
        hexText.setTypeface(face);

        hexText.setOnTouchListener(this);

        ViewTreeObserver viewTreeObserver = hexText.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    hexText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) hexText.getLayoutParams();
                    lParams.leftMargin = getCenterLeftMargin();
                    lParams.topMargin = getCenterTopMargin();
                    hexText.setLayoutParams(lParams);
                    hexText.setMinWidth(hexText.getWidth());
                }
            });
        }

        springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                hexText.setScaleX(scale);
                hexText.setScaleY(scale);
            }
        });

        Timer timer = new Timer();
        TimerTask tick = new TimerTick();
        timer.scheduleAtFixedRate(tick, 0, 1000);
    }

    class TimerTick extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CharSequence hex = android.text.format.DateFormat.format("#HHmmss", new Date());
                    hexText.setText(hex);
                    rl.setBackgroundColor(Color.parseColor(hex.toString()));
                }
            });
        }
    }

    private int getCenterLeftMargin() {
        return (getApplicationContext().getResources().getDisplayMetrics().widthPixels/2)-(hexText.getWidth()/2);
    }

    private int getCenterTopMargin() {
        return (getApplicationContext().getResources().getDisplayMetrics().heightPixels/2)-(hexText.getHeight()/2);
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                spring.setEndValue(1);
                break;
            case MotionEvent.ACTION_UP:
                spring.setEndValue(0);
                TranslateAnimation translation;
                translation = new TranslateAnimation(0, getCenterLeftMargin()-lParams.leftMargin,
                        0, getCenterTopMargin()-lParams.topMargin);
                translation.setStartOffset(0);
                translation.setDuration(300);
                translation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        hexText.clearAnimation();
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) hexText.getLayoutParams();
                        lParams.leftMargin = getCenterLeftMargin();
                        lParams.topMargin = getCenterTopMargin();
                        lParams.rightMargin = 0;
                        lParams.bottomMargin = 0;
                        hexText.setLayoutParams(lParams);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                translation.setInterpolator(new OvershootInterpolator());
                hexText.startAnimation(translation);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                lParams.leftMargin = X - _xDelta;
                lParams.topMargin = Y - _yDelta;
                lParams.rightMargin = -250;
                lParams.bottomMargin = -250;
                view.setLayoutParams(lParams);
                break;
        }
        return true;
    }
}
