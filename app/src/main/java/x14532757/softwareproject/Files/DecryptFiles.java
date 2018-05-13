package x14532757.softwareproject.Files;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import x14532757.softwareproject.R;
import x14532757.softwareproject.Utils.Hash;

/**
 * Created by x14532757 on 29/10/2017.
 *
 * Code Copied from:
 * Title: Request App Permissions
 * Author: Android Developer
 * Availability: https://developer.android.com/training/permissions/requesting.html
 *
 * Code Copied and Modified from:
 * Title: firebase-stackoverflow-android Delete Data
 * Author: puf
 * Availability: https://github.com/puf/firebase-stackoverflow-android/blob/master/app/src/main/java/com/firebasedemo/stackoverflow/Activity32469846.java
 *
 * Code Copied from:
 * Title: android-FingerprintDialog
 * Author: googleSamples
 * Date: 12/02/17
 * Availability: https://github.com/googlesamples/android-FingerprintDialog
 *
 * Code Modified from:
 * Title: Download Files on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/storage/android/download-files?authuser=0
 *
 * Code Modified from:
 * Title: How to write to file in Java – FileOutputStream
 * Author: Mykong
 * Date: 30/03/10
 * Availability: https://www.mkyong.com/java/how-to-write-to-file-in-java-fileoutputstream-example/
 *
 */

public class DecryptFiles extends Activity {
    private TextView name;
    private DatabaseReference dbRef;
    private StorageReference storageReference1;
    private EditText pin;
    private TextView successMes;

    ProgressDialog pd;

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    private LinearLayout clayout;
    private LinearLayout ddlayout;
    private LinearLayout linearLayout;

    private static final String success = "Fingerprint Scan Successful";

    private final String TAG = "......................";

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decryptfiles);
        //layouts
        clayout =  findViewById(R.id.chooselayout);
        ddlayout =  findViewById(R.id.pinCodeLayout);
        linearLayout = findViewById(R.id.layout);
        //text views
        name =  findViewById(R.id.imageNameText);
        successMes = findViewById(R.id.confirmation_message);
        //pincode input
        pin =  findViewById(R.id.PinInput);
        //buttons
        final Button choosepin = findViewById(R.id.choosePinBtn);
        final Button decrypt =  findViewById(R.id.DecryptButton);
        final Button delete = findViewById(R.id.DeleteButton);
        Button exit = findViewById(R.id.ExitButton);

        //create progress bar
        pd = new ProgressDialog(this);
        pd.setMessage("Downloading....");

        //get the data passed from the viewFiles class and show them on the screen or store in variables
        name.setText(getIntent().getExtras().getString("data"));
        String durl = getIntent().getExtras().getString("Durl");
        final String storedPin = getIntent().getExtras().getString("pin");
        final String filename = name.getText().toString();

        //get current user id and reference to database
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        //create reference to where the users files are stored
        FirebaseStorage storage = FirebaseStorage.getInstance();
        assert durl != null;
        storageReference1 = storage.getReferenceFromUrl(durl);

        //check permissions have been granted to access the devices external storage
        ActivityCompat.requestPermissions(DecryptFiles.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);

        //create reference to where the users data is stored in the database
        dbRef = FirebaseDatabase.getInstance().getReference().child("Files").child(userID);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String enteredPin = pin.getText().toString();
                String successMessage = successMes.getText().toString();

                //create instance of the Hash class
                Hash hash = new Hash();
                //validate the user inputted pin code
                boolean match = false;
                try {
                    match = hash.validatePassword(enteredPin, storedPin);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "matched: " + match);

                //if no pincode entered then user must have used fingerprint
                if(enteredPin.equals("")){
                    //if fingerprint correct then data can be deleted
                    if(success.equals(successMessage)){
                        DeleteData();
                    }
                }

                //if pincode entered check that the hash matches the once stored in the database
                if(!enteredPin.isEmpty()){
                    //if the hashes match then data can be deleted
                    if(match){
                        DeleteData();
                    }else{
                        Snackbar.make(linearLayout, "Please Enter Correct Pin Code", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();

                //get the file name
                final String filename = name.getText().toString();
                //create a random number from 0-1000
                Random random = new Random();
                int n = random.nextInt(1000);
                //combine filename and random number, this allows the file to be downloaded again without the same file name
                final String newFileName = n + filename;
                final String key = pin.getText().toString();

                String successMessage = successMes.getText().toString();

                final File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                final File localFile = new File(rootPath, newFileName);

                //if the pincode has been entered take this route to decrypting the file
                if(!Objects.equals(key, "")){

                    //create instance of hash class
                    Hash hash = new Hash();
                    //validate the hash by comparing entered pincode and stored hash
                    boolean match = false;
                    try {
                        match = hash.validatePassword(key, storedPin);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                    //if the hashes match
                    if(match){
                        //download the file from firebase storage as a byte array
                        storageReference1.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            //if file downloaded successfully get the file bytes
                            @Override
                            public void onSuccess(byte[] bytes) {

                                //get instance of twofish class
                                TwoFish twofish = new TwoFish();

                                //decrypt the file bytes
                                byte[] decrypted = new byte[0];
                                //try decrypt the file
                                try {
                                    decrypted = twofish.decrypt(bytes, storedPin);
                                //if file decryption error occurs print error and return
                                } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
                                    Snackbar.make(linearLayout, "An Error Occurred While Decrypting", Snackbar.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                //convert file byte array back to an actual file
                                FileOutputStream fos;
                                try {
                                    fos = new FileOutputStream(localFile);
                                    fos.write(decrypted);
                                    Toast.makeText(getApplicationContext(), "Download Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DecryptFiles.this, ViewFiles.class);
                                    startActivity(intent);
                                    pd.dismiss();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        //file download error occurred
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(linearLayout, "An Error Occurred While Downloading", Snackbar.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    //pincode does not match one stored in database
                    }else{
                        Snackbar.make(linearLayout, "Pin Code Does Not Match", Snackbar.LENGTH_LONG).show();
                        pd.dismiss();
                    }


                }

                //fingerprint authentication is used
                if(Objects.equals(key, "")){
                    //if the fingerprint matches the one stored then we can start to decrypt the file
                    if(success.equals(successMessage)){
                        //download the file from firebase storage as a byte array
                        storageReference1.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                //get instance of twofish class
                                TwoFish twofish = new TwoFish();
                                //decrypt the file bytes
                                byte[] decrypted = new byte[0];
                                //try decrypt the file
                                try {
                                    decrypted = twofish.decrypt(bytes, storedPin);

                                    //if file decryption error occurs print error and return
                                } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
                                    Snackbar.make(linearLayout, "An Error Occurred While Decrypting", Snackbar.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                //convert file byte array back to an actual file
                                FileOutputStream fos;
                                try {
                                    fos = new FileOutputStream(localFile);
                                    fos.write(decrypted);
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Download Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(DecryptFiles.this, ViewFiles.class);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //file download error occurred
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(linearLayout, "An Error Occurred While Downloading", Snackbar.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    }
                }


            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DecryptFiles.this, ViewFiles.class);
                startActivity(intent);
            }
        });

        choosepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clayout.setVisibility(View.GONE);
                ddlayout.setVisibility(View.VISIBLE);
                pin.setVisibility(View.VISIBLE);
            }
        });
















        //the following code is used to access the android fingerprint manager
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        Cipher defaultCipher;
        Cipher cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
        Button purchaseButton = findViewById(R.id.choosePrintBtn);

        if (keyguardManager != null && !keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            purchaseButton.setEnabled(false);
            return;
        }

        if (fingerprintManager != null && !fingerprintManager.hasEnrolledFingerprints()) {
            purchaseButton.setEnabled(false);
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);
        purchaseButton.setEnabled(true);
        purchaseButton.setOnClickListener(new DecryptFiles.PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
    }


    //method used to verify the permissions needed have been granted by the user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(linearLayout, "Permission Granted", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(DecryptFiles.this, ViewFiles.class);
                    Snackbar.make(linearLayout, "Please Enable Application Permissions", Snackbar.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        }
    }


    private void DeleteData() {
        final String textName = name.getText().toString();

        //query the data by the filename in the database
        Query query = dbRef.orderByChild("FileName").equalTo(textName);

        //if data in the database exists then delete it
        storageReference1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DecryptFiles.this, "File Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DecryptFiles.this, "File Deletion Failure", Toast.LENGTH_SHORT).show();
            }
        });

        //then remove the file from firebase storage
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0){
                        for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                            itemSnapshot.getRef().removeValue();
                            Toast.makeText(DecryptFiles.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DecryptFiles.this, ViewFiles.class);
                            startActivity(intent);
                        }
                    }else{
                        Snackbar.make(linearLayout, "Error Occurred", Snackbar.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(linearLayout, "Error Occurred", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject.getCipher());
            clayout.setVisibility(View.GONE);
            ddlayout.setVisibility(View.VISIBLE);
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation(null);
        }
    }

    // Show confirmation, if fingerprint was used show crypto information.
    @SuppressLint("SetTextI18n")
    private void showConfirmation(byte[] encrypted) {
        findViewById(R.id.confirmation_message).setVisibility(View.VISIBLE);
        successMes.setText("Fingerprint Scan Successful");
        //if (encrypted != null) {
        //TextView v = (TextView) findViewById(R.id.encrypted_message);
        //v.setVisibility(View.VISIBLE);
        //v.setText(Base64.encodeToString(encrypted, 0 /* flags */));
        //}
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(Cipher cipher) {
        try {
            byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
            showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {

        try {
            mKeyStore.load(null);
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class PurchaseButtonClickListener implements View.OnClickListener {

        Cipher mCipher;
        String mKeyName;

        PurchaseButtonClickListener(Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }

        @Override
        public void onClick(View view) {
            findViewById(R.id.confirmation_message).setVisibility(View.GONE);
            findViewById(R.id.encrypted_message).setVisibility(View.GONE);

            // Set up the crypto object for later. The object will be authenticated by use
            // of the fingerprint.
            if (initCipher(mCipher, mKeyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or you can fall back to using a server-side verified password.
                FingerprintAuthDialog fragment
                        = new FingerprintAuthDialog();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(FingerprintAuthDialog.Stage.FINGERPRINT);

                } else {
                    fragment.setStage(FingerprintAuthDialog.Stage.PASSWORD);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                FingerprintAuthDialog fragment
                        = new FingerprintAuthDialog();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        FingerprintAuthDialog.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }


}
