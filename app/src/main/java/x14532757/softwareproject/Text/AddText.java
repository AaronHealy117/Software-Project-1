package x14532757.softwareproject.Text;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;
import x14532757.softwareproject.Text.StegClasses.BitmapHelper;
import x14532757.softwareproject.Text.StegClasses.Steg;
import x14532757.softwareproject.Utils.Hash;
import x14532757.softwareproject.Utils.InputValidation;

/**
 * Created by x14532757 on 15/10/2017.
 *
 * Code Modified from:
 * Title: Read and Write Data on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/database/android/read-and-write?authuser=0
 *
 * Code Modified from:
 * Title: Upload Files on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/storage/android/upload-files?authuser=0
 *
 * Code Modified from:
 * Title: steganography
 * Author: stealthcopter
 * Date: 09/12/15
 * Availability: https://github.com/stealthcopter/steganography
 *
 */

public class AddText extends AppCompatActivity {

    private EditText name;
    private EditText text;
    private EditText pin;

    private LinearLayout layout;

    private ProgressDialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtext);

        //get edit texts
        name = (EditText) findViewById(R.id.textName);
        text = (EditText) findViewById(R.id.textBlock);
        pin = (EditText) findViewById(R.id.choosePinBtn);
        layout = (LinearLayout) findViewById(R.id.layout);
        //get button
        Button upload = (Button) findViewById(R.id.addButton);

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        //get the current logged in users information
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();

        //create reference to the firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Bitmaps").child(userID);

        //create reference to the firebase database
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("TextBlocks").child(userID);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.show();

                //get user inputs to string
                final String nameValue = name.getText().toString();
                final String textValue = text.getText().toString();
                final String pinValue = pin.getText().toString();

                //input validation to make sure everything is filled out and correct
                if(nameValue.isEmpty()){
                    Snackbar.make(layout, "Please Enter Text Name", Snackbar.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                if(textValue.isEmpty()){
                    Snackbar.make(layout, "Please Enter A Text Block", Snackbar.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                if(pinValue.isEmpty()){
                    Snackbar.make(layout, "Please Enter A Pin Code", Snackbar.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                if(pinValue.length() < 6){
                    Snackbar.make(layout, "Pin Code Must Be 6 Digits", Snackbar.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }

                //input validation to make sure no special characters in the inputs
                InputValidation validation = new InputValidation();
                List<String> errorList = new ArrayList<>();
                boolean isValid = validation.NoSpecialChars(nameValue, errorList);
                if(isValid){
                    Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }
                boolean isValid1 = validation.NoSpecialChars(textValue, errorList);
                if(isValid1){
                    Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return;
                }

                //create instance of hash class
                Hash hash = new Hash();
                //hash the user inputted pin code
                String hashedPin = null;
                try {
                    hashedPin = hash.PBK(pinValue);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                // create push to database
                final DatabaseReference dbref = db.push();
                //create upload to
                StorageReference childRef = storageReference.child(nameValue);

                //combine bitmap and text
                byte[] map = testBitmapEncoder(textValue);

                //upload the new bitmap as bytes to firebase storage
                UploadTask uploadTask = childRef.putBytes(map);

                final String finalHashedPin = hashedPin;
                //upload check
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //get download url so we can download the bitmap again
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        assert downloadUrl != null;
                        String url = downloadUrl.toString();

                        //upload the user data to the firebase database
                        dbref.child("TextName").setValue(nameValue);
                        dbref.child("PinCode").setValue(finalHashedPin);
                        dbref.child("URL").setValue(url);

                        Toast.makeText(AddText.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Intent intent = new Intent(AddText.this, HomeScreen.class);
                        startActivity(intent);

                //if bitmap upload fails show message and return
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(layout, "An Error Occurred", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

            }
        });

    }

    //Stenography method
    private byte[] testBitmapEncoder(String textValue){

        //create 200*200 pixel bitmap of random colour
        Bitmap bitmap = BitmapHelper.createTestBitmap(200, 200, null);

        //use the class Steg to create a bitmap with the user inputted text embedded
        Bitmap newbit = null;
        try {
            //Steg class uses the withInput method which uses a bitmap and then
            // the encode method which encodes the text value into it
            newbit = Steg.withInput(bitmap).encode(textValue).intoBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //convert the bitmap to a byte array and return it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assert newbit != null;
        newbit.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();

    }






}
