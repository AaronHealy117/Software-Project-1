package x14532757.softwareproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import x14532757.softwareproject.Files.ViewFiles;
import x14532757.softwareproject.Images.ViewImage;
import x14532757.softwareproject.LoginRegister.LoginChoice;
import x14532757.softwareproject.Password.ViewPasswords;
import x14532757.softwareproject.Text.ViewText;
import x14532757.softwareproject.User.MyAccount;

public class HomeScreen extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Error homepage";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 123;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        ImageButton viewpassword = (ImageButton) findViewById(R.id.viewpasswordBtn);
        ImageButton viewtext = (ImageButton) findViewById(R.id.addtextButton);
        ImageButton images = (ImageButton) findViewById(R.id.imageBtn);
        Button logout = (Button) findViewById(R.id.logoutBtn);
        Button account = (Button) findViewById(R.id.accountBtn);
        ImageButton viewfiles = (ImageButton) findViewById(R.id.filesBtn);
        linearLayout = (LinearLayout) findViewById(R.id.MainLayout);


        ActivityCompat.requestPermissions(HomeScreen.this, new String[]{
                Manifest.permission.USE_FINGERPRINT}, MY_PERMISSIONS_REQUEST_READ_STORAGE
        );


        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, MyAccount.class);
                startActivity(intent);
            }
        });

        viewpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ViewPasswords.class);
                startActivity(intent);
            }
        });

        viewtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ViewText.class);
                startActivity(intent);
            }
        });

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ViewImage.class);
                startActivity(intent);
            }
        });

        viewfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ViewFiles.class);
                startActivity(intent);
            }
        });

        //get currently signed in users UID
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
        } else {
            // No user is signed in so go back to login screen
            Toast.makeText(this, "You shouldn't be here", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomeScreen.this, LoginChoice.class);
            startActivity(intent);
        }

        //google api sign out.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                // Google sign out
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(HomeScreen.this, "Logout successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeScreen.this, LoginChoice.class);
                                startActivity(intent);
                            }
                        });
            }
        });
    }

    //reguest method to verify user has given the needed application permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(linearLayout, "Permission Granted", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(HomeScreen.this, ViewImage.class);
                    Snackbar.make(linearLayout, "Please Enable Application Permissions", Snackbar.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screen, menu);
        return true;
    }
}
