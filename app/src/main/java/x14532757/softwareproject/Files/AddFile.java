package x14532757.softwareproject.Files;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 15/10/2017.
 */

public class AddFile extends AppCompatActivity{
    private static final int PICK_FILE = 100;
    private Uri filePath;
    ProgressDialog pd;

    private EditText FileName;
    private EditText FileDesc;
    private EditText FilePin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfile);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Files").child(userID);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Files").child(userID);

        Button pickPDFFileButton = (Button) findViewById(R.id.pdffileBtn);
        Button uploadButton = (Button) findViewById(R.id.submitbutton);
        FileName = (EditText)findViewById(R.id.FileName);
        FileDesc = (EditText)findViewById(R.id.FileDesc);
        FilePin = (EditText) findViewById(R.id.pinCode);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(filePath != null) {
                    pd.show();

                    final String titleData = FileName.getText().toString().trim();
                    final String descData = FileDesc.getText().toString().trim();
                    final String pin = FilePin.getText().toString().trim();

                    final DatabaseReference newPost = db.push();
                    final StorageReference childRef = storageReference.child(titleData);

                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            assert downloadUrl != null;
                            String url = downloadUrl.toString();

                            newPost.child("FileName").setValue(titleData);
                            newPost.child("FileDesc").setValue(descData);
                            newPost.child("FileURL").setValue(url);
                            newPost.child("PinCode").setValue(pin);

                            pd.dismiss();
                            Toast.makeText(AddFile.this, "File Upload successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddFile.this, HomeScreen.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddFile.this, "File Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            pd.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    });
                }
                else {
                    Toast.makeText(AddFile.this, "Select a file", Toast.LENGTH_SHORT).show();
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

    private void openPDFGallery() {
        //https://developer.android.com/guide/topics/providers/document-provider.html
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_FILE);
    }

    public static String getMimeType(String url) {
        //https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }


}
