package x14532757.softwareproject.Password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;
import x14532757.softwareproject.Utils.Hash;
import x14532757.softwareproject.Utils.InputValidation;

/**
 * Created by x14532757 on 13/10/2017.
 *
 * Code Copied and Modified from:
 * Title: Read and Write Data on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/database/android/read-and-write?authuser=0
 *
 */

public final class AddPassword extends AppCompatActivity {

    private EditText name;
    private EditText password;
    private EditText pin;
    private ProgressDialog pd;

    private LinearLayout layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpassword);

        //get current firebase auth user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        //get reference to database bucket
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Passwords").child(userID);

        //get button and inputs
        name = (EditText) findViewById(R.id.textName);
        password = (EditText) findViewById(R.id.textBlock);
        pin = (EditText) findViewById(R.id.choosePinBtn);
        final Button addBtn = (Button) findViewById(R.id.addButton);
        layout = (LinearLayout) findViewById(R.id.layout);

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pd.show();

                    //get inputs to strings
                    String passName = name.getText().toString();
                    String pass = password.getText().toString();
                    String pincode = pin.getText().toString();

                    //input validation
                    if (TextUtils.isEmpty(passName)) {
                        Snackbar.make(layout, "A Password Name is Required", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    if (TextUtils.isEmpty(pass)) {
                        Snackbar.make(layout, "A Password is Required", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    if (TextUtils.isEmpty(pincode)) {
                        Snackbar.make(layout, "A Pin Code is Required", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    if(pincode.length() < 6){
                        Snackbar.make(layout, "Pin Code Must Have 6 Digits", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }

                    //input validation to make sure no special characters in some inputs
                    InputValidation validation = new InputValidation();
                    List<String> errorList = new ArrayList<>();
                    boolean isValid = validation.NoSpecialChars(passName, errorList);
                    if(isValid){
                        Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }

                    //create instance of hash class
                    Hash hash = new Hash();
                    //hash the user inputted pin code
                    String key = null;
                    try {
                        key = hash.PBK(pincode);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                    //Create instance of AEScipher class
                    AESCipher ciph = null;
                    try {
                        ciph = new AESCipher();
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                        e.printStackTrace();
                    }
                    //encrypt the password
                    String passwordEncrypted = null;
                    try {
                        assert ciph != null;
                        passwordEncrypted = ciph.encrypt(pass, key);
                    } catch (InvalidKeyException | UnsupportedEncodingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                        e.printStackTrace();
                    }

                    //push to database
                    DatabaseReference newPost = db.push();

                    //upload the input data and the encrypted password to the database
                    newPost.child("PasswordName").setValue(passName);
                    newPost.child("PasswordText").setValue(passwordEncrypted);
                    newPost.child("PinCode").setValue(key);

                    Toast.makeText(AddPassword.this, "Password Encryption Successful", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    Intent intent = new Intent(AddPassword.this, HomeScreen.class);
                    startActivity(intent);

                }
            });



    }



}
