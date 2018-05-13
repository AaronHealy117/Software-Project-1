package x14532757.softwareproject.Images;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;
import x14532757.softwareproject.Utils.InputValidation;

/**
 * Created by x14532757 on 15/10/2017.
 *
 *  Code Modified from:
 * Title: Read and Write Data on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/database/android/read-and-write?authuser=0
 *
 * Code Modified from:
 * Title: Upload Files on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/storage/android/upload-files?authuser=0
 *
 * Code Copied from:
 * Title: Open Files using Storage Access Framework
 * Author: Android Developer
 * Availability: https://developer.android.com/guide/topics/providers/document-provider.html
 *
 * Code Copied from:
 * Title: Request App Permissions
 * Author: Android Developer
 * Availability: https://developer.android.com/training/permissions/requesting.html
 *
 * Code Copied and Modified from:
 * Title: How to get Bitmap from an Uri?
 * Author: Mark Ingram
 * Date: 17/01/11
 * Availability:https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
 *
 * Code Modified from:
 * Title: Convert selected image into byte array and into string
 * Author: Chintan Rathod
 * Date: 13/06/13
 * Availability:https://stackoverflow.com/questions/16980775/convert-selected-image-into-byte-array-and-into-string
 *
 */

public class AddImage extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private Uri filePath;
    ProgressDialog pd;

    private ImageButton pickImageButton;
    private EditText ImageName;
    private EditText ImageDesc;
    private EditText pincode;
    private TextView title;

    private LinearLayout linearLayout;

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addimage);

        //get current logged in user information
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String userID = user.getUid();
        //get reference to the logged in users image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Images").child(userID);
        //get reference to logged in users database table
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Images").child(userID);


        pickImageButton = (ImageButton) findViewById(R.id.pdffileBtn);
        Button uploadButton = (Button) findViewById(R.id.submitbutton);
        ImageName = (EditText)findViewById(R.id.FileName);
        ImageDesc = (EditText)findViewById(R.id.FileDesc);
        pincode  = (EditText) findViewById(R.id.choosePinBtn);
        title = (TextView) findViewById(R.id.title);
        linearLayout = (LinearLayout) findViewById(R.id.layout);

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        //check the permission to access the phones storage is given
        ActivityCompat.requestPermissions(AddImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                title.setVisibility(View.INVISIBLE);
                pickImageButton.setBackgroundColor(Color.TRANSPARENT);
            }
        });



        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking to make sure that a file is selected and the Uri variable isnt empty
                if(filePath != null) {
                    pd.show();

                    //get data from inputs which will be uploaded to database
                    final String titleData = ImageName.getText().toString().trim();
                    final String descData = ImageDesc.getText().toString().trim();
                    final String pin = pincode.getText().toString().trim();

                    //input validation to make sure user supplied inputs meet standards
                    if(titleData.isEmpty()){
                        Snackbar.make(linearLayout, "Please Enter a Image Name", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    if(pin.isEmpty()){
                        Snackbar.make(linearLayout, "Please Enter a Pin Code", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    if(pin.length() < 6){
                        Snackbar.make(linearLayout, "Please Enter 6 Digit Pin Code", Snackbar.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }

                    //Input validation on the image name and description to make sure no special characters are
                    //are used, stops noSQL Injections
                    InputValidation validation = new InputValidation();
                    List<String> errorList = new ArrayList<>();
                    boolean isValid = validation.NoSpecialChars(titleData, errorList);
                    if(isValid){
                        Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }
                    boolean isValid1 = validation.NoSpecialChars(descData, errorList);
                    if(isValid1){
                        Toast.makeText(getApplicationContext(), ""+errorList, Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        return;
                    }

                    //create reference to where the data will be going in the database
                    final DatabaseReference newPost = db.push();
                    StorageReference childRef = storageReference.child(titleData);

                    //create a new bitmap using the image the user selected
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //get bitmap to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] byteArrayImage = baos.toByteArray();

                    //get instance of the TripleDES class
                    TripleDES des = new TripleDES();

                    //hash the user inputted pin code
                    String hashedpin = null;
                    try {
                        hashedpin = des.PBK(pin);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                    //encrypt bitmap bytes
                    byte[] encrypted = null;
                    try {
                        encrypted = des.encrypt(byteArrayImage, hashedpin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    assert encrypted != null;

                    //upload the encrypted bytes to firebase storage
                    UploadTask uploadTask = childRef.putBytes(encrypted);

                    final String finalHashedpin = hashedpin;
                    //if the image bytes is uploaded successfully
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //get the images download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            assert downloadUrl != null;
                            String url = downloadUrl.toString();

                            //upload the user inputted data to the firebase database
                            newPost.child("ImageName").setValue(titleData);
                            newPost.child("ImageDesc").setValue(descData);
                            newPost.child("ImageURL").setValue(url);
                            newPost.child("PinCode").setValue(finalHashedpin);
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddImage.this, HomeScreen.class);
                            startActivity(intent);

                    //file bytes upload failed
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Snackbar.make(linearLayout, "Upload Failed, Please Try Again", Snackbar.LENGTH_SHORT).show();
                        }
                    });

                }
                //no image has been selected
                else {
                    Snackbar.make(linearLayout, "Please Select an Image", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    //method used to make sure the user has given the needed permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(linearLayout, "Permission Granted", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddImage.this, ViewImage.class);
                    Snackbar.make(linearLayout, "Please Enable Application Permissions", Snackbar.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

        }
    }

    //method used to get the image data from external storage and putting the image in an imageview on the screen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                pickImageButton.setImageURI(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //method used to open the devices external storage in images folder
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}
