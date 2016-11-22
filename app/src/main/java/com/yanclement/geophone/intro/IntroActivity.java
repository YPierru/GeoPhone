package com.yanclement.geophone.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.yanclement.geophone.R;

/**
 * Created by YPierru on 20/11/2016.
 */

public class IntroActivity extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        //Here we are adding the four slides
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro));

        // Edit the color of the nav bar on Lollipop+ devices
        // setNavBarColor(Color.parseColor("#3F51B5"));

        // Hide Skip/Done button
        showSkipButton(true);
        showStatusBar(false);
        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest
        //setVibrate(true);
        //setVibrateIntensity(30);
        //setDepthAnimation();
        // Animations -- use only one of the below. Using both could cause errors.
        //setFadeAnimation(); // OR
/*            setZoomAnimation();
            setFlowAnimation(); // OR
            setSlideOverAnimation(); // OR
            setDepthAnimation(); // OR
            setCustomTransformer(yourCustomTransformer);*/

        // Permissions -- takes a permission and slide number
        //askForPermissions(new String[]{Manifest.permission.CAMERA}, 3);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        //processDoneOrSkip();
        processPermOK();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        //processDoneOrSkip();
        processPermOK();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    private void processPermOK(){
        /*Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.ID_BEHAVIOR_MAINACTIVITY,Constants.KEY_BEHAVIOR_MAINACTIVITY_FROM_APPINTRO);
        startActivity(intent);*/
        finish();
    }
}
