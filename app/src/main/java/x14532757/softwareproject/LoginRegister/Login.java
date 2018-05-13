package x14532757.softwareproject.LoginRegister;
//https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#bcrypt-scrypt

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 *
 * Code Copied and Modified from:
 * Title: Authenticate with Firebase using Password-Based Accounts on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/auth/android/password-auth?authuser=0
 *
 *
 * Code Copied from:
 * Title: Authenticate Using Google Sign-In on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/auth/android/google-signin?authuser=0
 */

public class Login extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    ProgressDialog pd;
    private RelativeLayout layout;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //get buttons
        Button login = (Button) findViewById(R.id.loginBtn);
        Button register = (Button) findViewById(R.id.registerBtn);
        Button forgot = (Button) findViewById(R.id.resetBtn);
        emailInput = (EditText) findViewById(R.id.emailTxt);
        passwordInput = (EditText) findViewById(R.id.passwordText);
        //get layout
        layout = (RelativeLayout) findViewById(R.id.layout);

        //get firebase authentication instance
        auth = FirebaseAuth.getInstance();

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Logging In....");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();
                startSignIn(email, password);
            }
        });



    }

    //login method
    private void startSignIn(String email, String password){

        //check the email and password isnt empty
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(layout, "Please Enter Email Address", Snackbar.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(layout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        //firebase authentication sign in method
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //sign in error occurs then show message and return
                        if (!task.isSuccessful()) {
                            // there was an error
                            Snackbar.make(layout, "Login Error Occurred", Snackbar.LENGTH_SHORT).show();
                            pd.dismiss();
                        //if sign in success, get user and go to home screen
                        } else {
                            pd.dismiss();
                            Intent intent = new Intent(Login.this, HomeScreen.class);
                            startActivity(intent);
                            Toast.makeText(Login.this, "Welcome to Kryptium ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        }

}




