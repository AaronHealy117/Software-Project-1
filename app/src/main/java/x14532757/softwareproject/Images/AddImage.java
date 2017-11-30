package x14532757.softwareproject.Images;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class AddImage extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private Uri filePath;
    ProgressDialog pd;


    private ImageButton pickImageButton;
    private EditText ImageName;
    private EditText ImageDesc;
    private EditText pincode;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addimage);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String userID = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Images").child(userID);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Images").child(userID);


        pickImageButton = (ImageButton) findViewById(R.id.pdffileBtn);
        Button uploadButton = (Button) findViewById(R.id.submitbutton);
        ImageName = (EditText)findViewById(R.id.FileName);
        ImageDesc = (EditText)findViewById(R.id.FileDesc);
        pincode  = (EditText) findViewById(R.id.choosePinBtn);
        title = (TextView) findViewById(R.id.title);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

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
                if(filePath != null) {
                    pd.show();

                    final String titleData = ImageName.getText().toString().trim();
                    final String descData = ImageDesc.getText().toString().trim();
                    final String pin = pincode.getText().toString().trim();

                    final DatabaseReference newPost = db.push();
                    StorageReference childRef = storageReference.child(titleData);

                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            assert downloadUrl != null;
                            String url = downloadUrl.toString();

                            newPost.child("ImageName").setValue(titleData);
                            newPost.child("ImageDesc").setValue(descData);
                            newPost.child("ImageURL").setValue(url);
                            newPost.child("PinCode").setValue(pin);
                            pd.dismiss();
                            Toast.makeText(AddImage.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddImage.this, HomeScreen.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddImage.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AddImage.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



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


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}
