package x14532757.softwareproject.LoginRegister;
//https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#bcrypt-scrypt

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 */

public class Login extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    ProgressDialog pd;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Button login = (Button) findViewById(R.id.loginBtn);
        Button register = (Button) findViewById(R.id.registerBtn);
        Button forgot = (Button) findViewById(R.id.resetBtn);
        emailInput = (EditText) findViewById(R.id.emailText);
        passwordInput = (EditText) findViewById(R.id.passwordText);

        auth = FirebaseAuth.getInstance();

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
                startSignIn();
            }
        });

    }

    private void startSignIn(){

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(Login.this, "Failed to Login.", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Intent intent = new Intent(Login.this, HomeScreen.class);
                            startActivity(intent);
                            Toast.makeText(Login.this, "Welcome to Kryptium", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        }

}




