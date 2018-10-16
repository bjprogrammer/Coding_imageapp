package com.bobby.coding.splash;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.bobby.coding.BaseApp;
import com.bobby.coding.R;
import com.bobby.coding.main.MainActivity;
import com.bobby.coding.model.SettingDatabaseHelper;
import com.bobby.coding.utils.DatabaseScope;
import com.bobby.coding.utils.ParticleView;
import com.bobby.coding.utils.TextSpeechService;


import javax.inject.Inject;
import javax.inject.Named;
import io.codetail.animation.ViewAnimationUtils;

public class SplashActivity extends AppCompatActivity implements SplashView {

    @Inject
    @Named("splash")
    public SharedPreferences pref;
    @Inject
    @Named("splash")
    public SharedPreferences.Editor editor;

    @Inject
    @DatabaseScope
    public SettingDatabaseHelper dh2;

    private ParticleView particleView;
    private View splashView;

    private Intent service;
    private ImageView imageView;
    private int cx, cy, dx, dy;
    private View myView;
    private float finalRadius;
    private SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ((BaseApp) getApplicationContext()).getDeps().splashinject(this);

        presenter = new SplashPresenter(pref, editor, dh2);
        if (!presenter.isFirstTimeLaunch()) {
            directMainScreen();
            finish();
        } else {
            renderView();
            init();
        }
    }

    private void renderView() {
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        particleView = findViewById(R.id.particleview);
        splashView = findViewById(R.id.awesome_card1);
        myView = findViewById(R.id.awesome_card);

        imageView = findViewById(R.id.imageView);
    }

    private void init() {
        presenter.setDefault();
        service = new Intent(getApplicationContext(), TextSpeechService.class);

        splashView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        splashView.post(new Runnable() {
            @Override
            public void run() {
                circularanimation().start();
            }});

        particleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        splashView.setVisibility(View.GONE);
                        myView.setVisibility(View.VISIBLE);
                        imageView.setAlpha(1.0f);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopService(service);
                                launchNextScreen();

                            }
                        }, 3500);
                    }
                },2000);
            }
        });

        imageView.setImageResource(R.drawable.marsplay);
        startService(service);

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(service);
    }

    //Transition from splash screen to main screen with shared element animation
    public void launchNextScreen() {

        presenter.setFirstTimeLaunch(false);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    public void directMainScreen() {
        presenter.setFirstTimeLaunch(false);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
        this.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Circular animation from center of screen
    private Animator circularanimation() {
        cx = (myView.getLeft() + myView.getRight()) / 2;
        cy = (myView.getTop() + myView.getBottom()) / 2;
        dx = Math.max(cx, myView.getWidth() - cx);
        dy = Math.max(cy, myView.getHeight() - cy);
        finalRadius = (float) Math.hypot(dx, dy);

        Animator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                particleView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        particleView.startAnim();
                    }},200);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        return animator;
    }
}
