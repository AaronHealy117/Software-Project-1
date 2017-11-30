package x14532757.softwareproject.LoginRegister;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 */

public final class Register extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputName;
    private EditText inputPhone;
    private EditText inputPassword;
    private EditText inputConfirm;

    private FirebaseAuth auth;

    private static final String SHA = "SHA1PRNG";
    private static final int ITERATIONS = 1000;
    private static final String PBK = "PBKDF2WithHmacSHA1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");

        final Button btnSignIn = (Button) findViewById(R.id.loginBtn);
        final Button btnSignUp = (Button) findViewById(R.id.sign_up_button);

        inputEmail = (EditText) findViewById(R.id.emailText);
        inputPassword = (EditText) findViewById(R.id.showText);
        inputConfirm = (EditText) findViewById(R.id.ConfirmPassText);
        inputName = (EditText) findViewById(R.id.FullNameText);
        inputPhone = (EditText) findViewById(R.id.PhoneNumText);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = inputEmail.getText().toString();
                final String name = inputName.getText().toString();
                final String phone = inputPhone.getText().toString();
                final String password = inputPassword.getText().toString();
                String confirm = inputConfirm.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Objects.equals(password, confirm)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //db.child("PasswordName").setValue(email);
                //db.child("PasswordText").setValue(password);

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Register.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                } else {
                                    //get current firebase auth user
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    assert user != null;
                                    String userID = user.getUid();
                                    //get reference database bucket
                                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                    DatabaseReference newPost = db.push();

                                    newPost.child("UserEmail").setValue(email);
                                    newPost.child("UserName").setValue(name);
                                    newPost.child("UserPhoneNumber").setValue(phone);

                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                }
                            }
                        });

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

    }






}
