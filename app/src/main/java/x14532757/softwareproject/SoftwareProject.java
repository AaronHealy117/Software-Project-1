package x14532757.softwareproject;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Security;

/**
 * Created by x14532757 on 13/10/2017.
 */

public class SoftwareProject extends Application {

    //This class is used to include Firebase, BouncyCastle and SpongyCastle
    //across the application

    @Override
    public void onCreate() {
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        String providerName = "BC";

        if (Security.getProvider(providerName) == null) {
            System.out.println(providerName + " provider not installed");
        } else {
            System.out.println(providerName + " is installed.");
        }

        String providerName1 = "SC";

        if (Security.getProvider(providerName1) == null) {
            System.out.println(providerName1 + " provider not installed");
        } else {
            System.out.println(providerName1 + " is installed.");
        }
    }

}

