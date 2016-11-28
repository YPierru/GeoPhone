package com.yanclement.geophone.activity;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
    private Button btnStop;
    private FrameLayout frameLayout;

    private boolean flashStatus;
    private boolean vibrateStatus;
    private boolean soundStatus;

    private Camera camera;
    private Camera.Parameters parameters;
    private int delay = 100; // in ms
    private boolean flahslightOn;
    private int flashRepeat=1000000;

    private MediaPlayer mediaPlayer;

    private Vibrator vibrator;
    private long[] vibrationPattern = {0,500, 500};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_lock_screen);


        //Waking up the screen
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();


        //Release the screenlock. ONLY IF THERE IS NO PIN CODE/PATTERN CODE
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

        //Hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        tvLabel = (TextView)findViewById(R.id.fullscreen_content);
        tvLabel.setText(getIntent().getExtras().getString(Constants.SEARCHED_PHONE_TEXT_ALERT));

        frameLayout = (FrameLayout) findViewById(R.id.fl_main);

        flashStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_FLASH_STATUS);
        vibrateStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_VIBRATE_STATUS);
        soundStatus = getIntent().getExtras().getBoolean(Constants.SEARCHED_PHONE_SOUND_STATUS);

        btnStop = (Button)findViewById(R.id.btn_stop);

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashRepeat=0;
                vibrator.cancel();
                mediaPlayer.stop();
                finish();
            }
        });


        if(vibrateStatus) {
            vibrator.vibrate(vibrationPattern, 0);
        }

        if(flashStatus){
            startFlash();
        }

        if(soundStatus){
            mediaPlayer.start();
        }
    }


    private void startFlash(){
        //Flashlight
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


        //Screen
        Animation animation = new AlphaAnimation(1, 0); // Change alpha
        animation.setDuration(100); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at
        frameLayout.startAnimation(animation);
    }

    /** Turn the devices FlashLight on */
    private void turnOn() {
        if (camera != null) {
            // Turn on LED
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);

            flahslightOn = true;
        }
    }

    /** Turn the devices FlashLight off */
    private void turnOff() {
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
    private void toggleFlashLight() {
        if (!flahslightOn) { // Off, turn it on
            turnOn();
        } else { // On, turn it off
            turnOff();
        }
    }
}
