package x14532757.softwareproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.shimmer.ShimmerFrameLayout;

import x14532757.softwareproject.LoginRegister.LoginChoice;

/**
 * Created by x14532757 on 17/10/2017.
 */

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        container = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container1);
        container.startShimmerAnimation();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginChoice.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

}