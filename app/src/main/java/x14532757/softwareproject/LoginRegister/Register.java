package x14532757.softwareproject.LoginRegister;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 *
 * Code Copied and Modified from:
 * Title: Authenticate with Firebase using Password-Based Accounts on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/auth/android/password-auth?authuser=0
 *
 * Code Modified from:
 * Title: Password validate 8 digits, contains upper,lowerCase,and a special Character
 * Author: Ankur
 * Availability: https://stackoverflow.com/questions/36097097/password-validate-8-digits-contains-upper-lowercase-and-a-special-character
 *
 */

public final class Register extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputName;
    private EditText inputPhone;
    private EditText inputPassword;
    private EditText inputConfirm;

    private FirebaseAuth auth;

    private RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get buttons
        final Button btnSignIn = (Button) findViewById(R.id.loginBtn);
        final Button btnSignUp = (Button) findViewById(R.id.sign_up_button);

        //get register inputs
        inputEmail = (EditText) findViewById(R.id.emailTxt);
        inputPassword = (EditText) findViewById(R.id.showText);
        inputConfirm = (EditText) findViewById(R.id.ConfirmPassText);
        inputName = (EditText) findViewById(R.id.FullNameText);
        inputPhone = (EditText) findViewById(R.id.PhoneNumText);

        //get layout
        layout = (RelativeLayout) findViewById(R.id.layout);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get inputs to Strings
                final String email = inputEmail.getText().toString();
                final String name = inputName.getText().toString();
                final String phone = inputPhone.getText().toString();
                final String password = inputPassword.getText().toString();
                String confirm = inputConfirm.getText().toString();

                //input validation to make sure important inputs arent empty
                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(layout, "Please Enter an Email Address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    Snackbar.make(layout, "Please Enter Your Full Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Snackbar.make(layout, "Please Enter your Phone Number", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //input validation to make sure the password is a strong password
                List<String> errorList = new ArrayList<>();
                if(!isValid(password, confirm, errorList)){
                    Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_LONG).show();
                    return;
                }
                //create user account
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                    //get current firebase auth user
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    assert user != null;
                                    String userID = user.getUid();
                                    //get reference database
                                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                    //push the user data
                                    DatabaseReference newPost = db.push();
                                    //upload the users data to firebase database
                                    newPost.child("UserEmail").setValue(email);
                                    newPost.child("UserName").setValue(name);
                                    newPost.child("UserPhoneNumber").setValue(phone);

                                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);

                        //user register has failed
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(layout, "Registration Error, Please Try Again", Snackbar.LENGTH_SHORT).show();
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

    //input validation method to make a strong password
    public boolean isValid(String password, String confirm, List<String> errorList) {

        //create patterns
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        errorList.clear();

        boolean flag=true;

        //check if password is empty
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "Enter password!", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        //check if password matches confirm input
        if (!password.equals(confirm)) {
            errorList.add("Password and confirm password does not match");
            flag=false;
        }
        //check if password is 12 characters
        if (password.length() < 12) {
            errorList.add("Password length must have at least 8 character !!");
            flag=false;
        }
        //check if password has special character
        if (!specailCharPatten.matcher(password).find()) {
            errorList.add("Password must have at least one specail character !!");
            flag=false;
        }
        //check if password has an uppercase letter
        if (!UpperCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one uppercase character !!");
            flag=false;
        }
        //check if password has lowercase letters
        if (!lowerCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one lowercase character !!");
            flag=false;
        }
        //check if password has a number
        if (!digitCasePatten.matcher(password).find()) {
            errorList.add("Password must have at least one digit character !!");
            flag=false;
        }
        return flag;
    }






}
