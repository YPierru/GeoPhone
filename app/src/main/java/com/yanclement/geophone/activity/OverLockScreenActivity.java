package com.yanclement.geophone.activity;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yanclement.geophone.Constants;
import com.yanclement.geophone.R;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OverLockScreenActivity extends AppCompatActivity {

    private TextView tvLabel;
    private boolean flashStatus;
    private boolean vibrateStatus;
    private boolean soundStatus;

    private Camera camera;
    private Camera.Parameters parameters;
    private int delay = 100; // in ms
    private boolean flahslightOn;
    private int flashRepeat=1000000;

    private int vibrateRepeat=0;

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_lock_screen);
        tvLabel = (TextView)findViewById(R.id.fullscreen_content);
        tvLabel.setText(getIntent().getExtras().getString(Constants.SEARCHED_PHONE_TEXT_ALERT));

        frameLayout = (FrameLayout) findViewById(R.id.fl_main);

        flashStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_FLASH_STATUS);
        vibrateStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_VIBRATE_STATUS);
        soundStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_SOUND_STATUS);

        if(vibrateStatus) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0,50, 500};
            v.vibrate(pattern, vibrateRepeat);
        }

        if(flashStatus){
            Thread t = new Thread() {
                public void run() {
                    try {
                        // Switch on the cam for app's life
                        if (camera == null) {
                            // Turn on Cam
                            camera = Camera.open();
                            try {
                                camera.setPreviewDisplay(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            camera.startPreview();
                        }

                        for (int i=0; i < flashRepeat*2; i++) {
                            toggleFlashLight();
                            sleep(delay);
                        }

                        if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            t.start();


            Animation animation = new AlphaAnimation(1, 0); // Change alpha
            animation.setDuration(100); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at
            frameLayout.startAnimation(animation);

        }

        if(soundStatus){
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.sound);
            mp.start();
        }
    }


    /** Turn the devices FlashLight on */
    public void turnOn() {
        if (camera != null) {
            // Turn on LED
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);

            flahslightOn = true;
        }
    }

    /** Turn the devices FlashLight off */
    public void turnOff() {
        // Turn off flashlight
        if (camera != null) {
            parameters = camera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            }
        }
        flahslightOn = false;
    }

    /** Toggle the flashlight on/off status */
    public void toggleFlashLight() {
        if (!flahslightOn) { // Off, turn it on
            turnOn();
        } else { // On, turn it off
            turnOff();
        }
    }
}
