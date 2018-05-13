package x14532757.softwareproject.Files;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;
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
 * Code Copied from:
 * Title: Request App Permissions
 * Author: Android Developer
 * Availability: https://developer.android.com/training/permissions/requesting.html
 *
 * Code Copied from:
 * Title: Open Files using Storage Access Framework
 * Author: Android Developer
 * Availability: https://developer.android.com/guide/topics/providers/document-provider.html
 *
 * Code Copied from:
 * Title: Image Uri to bytesarray
 * Author: user370305
 * Date: 24/04/12
 * Availability: https://stackoverflow.com/questions/10296734/image-uri-to-bytesarray
 *
 * Code Modified from:
 * Title: Android Spinner – drop down list
 * Author:  ANUPAM CHUGH
 * Date: APRIL 2, 2018
 * Availability: https://www.journaldev.com/9231/android-spinner-drop-down-list
 *
 */

public class AddFile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int PICK_FILE = 100;
    private Uri filePath;
    ProgressDialog pd;

    private EditText FileName;
    private EditText FileDesc;
    private EditText FilePin;

    private LinearLayout linearLayout;

    private String TAG = "...............................................";
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfile);

        //get current logged in user information
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();

        //get reference to the logged in users filed
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Files").child(userID);
        //get reference to logged in users database table
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Files").child(userID);

        Button pickPDFFileButton = (Button) findViewById(R.id.pdffileBtn);
        Button uploadButton = (Button) findViewById(R.id.submitbutton);
        linearLayout = (LinearLayout) findViewById(R.id.layout);
        FileName = (EditText)findViewById(R.id.FileName);
        FileDesc = (EditText)findViewById(R.id.FileDesc);
        FilePin = (EditText) findViewById(R.id.pinCode);

        //check the permission to access the phones storage is given
        ActivityCompat.requestPermissions(AddFile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        //create listview for different file extensions
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Please Select a File Extension");
        categories.add(".pdf");
        categories.add(".docx");
        categories.add(".pptx");
        categories.add(".xlsx");
        // Creating adapter for spinner
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        uploadButton.setOnClickListener(new View.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onClick(View v) {
                                                //checking to make sure that a file is selected and the Uri variable isnt empty
                                                if (filePath != null) {

                                                    pd.show();

                                                    //get data from inputs which will be uploaded to database
                                                    String uri = filePath.toString();
                                                    String fileExtension = spinner.getSelectedItem().toString();//getMimeType(uri);
                                                    String titleData = FileName.getText().toString().trim();
                                                    final String fullName = titleData+fileExtension;
                                                    final String descData = FileDesc.getText().toString().trim();
                                                    final String key = FilePin.getText().toString();


                                                    //input validation to make sure user supplied inputs meet standards
                                                    if(Objects.equals(titleData, "")){
                                                        Snackbar.make(linearLayout, "Please Enter a File Name", Snackbar.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                        return;
                                                    }
                                                    if(Objects.equals(key, "")){
                                                        Snackbar.make(linearLayout, "Please Enter a Pin", Snackbar.LENGTH_SHORT).show();
                                                        pd.dismiss();
                                                        return;
                                                    }
                                                    if(key.length() < 6){
                                                        Snackbar.make(linearLayout, "Please Enter a 6 Digit Pin", Snackbar.LENGTH_SHORT).show();
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

                                                    //Create instance of the Hash class
                                                    Hash hash = new Hash();
                                                    //Hash the user entered pincode
                                                    String hashedKey = null;
                                                    try {
                                                        hashedKey = hash.PBK(key);
                                                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                                        e.printStackTrace();
                                                    }

                                                    //Convert the user selected file data to InputStream
                                                    InputStream iStream = null;
                                                    try {
                                                        iStream = getContentResolver().openInputStream(filePath);
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                    //create byte array of file data from InputStream
                                                    byte[] file = new byte[0];
                                                    try {
                                                        file = getBytes(iStream);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }


                                                    //Create instance of the Twofish class
                                                    TwoFish twofish = new TwoFish();
                                                    //create new byte array and encrypt the file byte array
                                                    byte[] encryptedfile = new byte[0];
                                                    try {
                                                        encryptedfile = twofish.encrypt(file, hashedKey);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    //get database references
                                                    final DatabaseReference newPost = db.push();
                                                    final StorageReference childRef = storageReference.child(titleData);

                                                    //upload the file as a byte array to Firebase Storage
                                                    UploadTask uploadTask = childRef.putBytes(encryptedfile);

                                                    //local final hashed pin code
                                                    final String finalHashedKey = hashedKey;
                                                    //if the encrypted file is uploaded to storage successfully
                                                    //then upload the remaining data to the database
                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            //get download url of the file in storage, so we can download the file again
                                                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                            assert downloadUrl != null;
                                                            String url = downloadUrl.toString();

                                                            //upload extra data to firbase database
                                                            newPost.child("FileName").setValue(fullName);
                                                            newPost.child("FileDesc").setValue(descData);
                                                            newPost.child("FileURL").setValue(url);
                                                            newPost.child("PinCode").setValue(finalHashedKey);

                                                            pd.dismiss();
                                                            Toast.makeText(getApplicationContext(), "File Upload Successful", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(AddFile.this, HomeScreen.class);
                                                            startActivity(intent);

                                                    //if error upload failed so try again
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pd.dismiss();
                                                            Snackbar.make(linearLayout, "File Upload Failed, Please Try Again", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }else{
                                                    Snackbar.make(linearLayout, "Please Select a File!", Snackbar.LENGTH_SHORT).show();
                                                }

                                            }

                                        });

        pickPDFFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPDFGallery();
            }
        });


    }


    //method to check that user has given the needed permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(linearLayout, "Permission Granted", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddFile.this, ViewFiles.class);
                    Snackbar.make(linearLayout, "Please Enable Application Permissions", Snackbar.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

        }
    }

    //method used to convert file selected by user to a byte array
    public byte[] getBytes(InputStream inputStream) throws IOException {
        //create ByteArrayOutputStream
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        //write file to a byte array
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        //return file as a byte array
        return byteBuffer.toByteArray();
    }

    //method used to get the file the user selects to encrypt
    //variable Uri filepath = currently selected file data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                filePath = data.getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //method used to open the devices storage on the document folder
    private void openPDFGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
