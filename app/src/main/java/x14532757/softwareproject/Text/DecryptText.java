package x14532757.softwareproject.Text;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.io.IOException;
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import x14532757.softwareproject.R;
import x14532757.softwareproject.Text.StegClasses.Steg;
import x14532757.softwareproject.Utils.Hash;

/**
 * Created by x14532757 on 29/10/2017.
 *
 * Code Copied and Modified from:
 * Title: Read and Write Data on Android
 * Author: Google
 * Availability: https://firebase.google.com/docs/database/android/read-and-write?authuser=0
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
 */

public class DecryptText extends Activity {

    private TextView name;
    private DatabaseReference dbRef;
    private EditText pin;
    private TextView text;
    private TextView successMes;

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    private LinearLayout clayout;
    private LinearLayout ddlayout;
    private RelativeLayout layout;

    private String success = "Fingerprint Scan Successful";

    private final String TAG = "decryptText";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypttext);

        //get layouts
        clayout = findViewById(R.id.chooselayout);
        ddlayout = findViewById(R.id.pinCodeLayout);
        layout = findViewById(R.id.layout);
        //get text views
        name = findViewById(R.id.imageNameText);
        text = findViewById(R.id.showText);
        successMes = findViewById(R.id.confirmation_message);
        // get edit text
        pin = findViewById(R.id.PinInput);

        // getbuttons
        final Button choosepin = findViewById(R.id.choosePinBtn);
        Button decrypt = findViewById(R.id.DecryptButton);
        final Button delete = findViewById(R.id.DeleteButton);
        Button exit = findViewById(R.id.ExitButton);

        //get data passed from viewpasswords and put them into textviews or strings
        name.setText(getIntent().getExtras().getString("data"));
        final String storedPin = getIntent().getExtras().getString("pin");

        //get current user id and reference to database
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();

        //get storage reference of where the bitmap image is stored
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        String bitmap = name.getText().toString();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://softwareproject-b1a79.appspot.com/Bitmaps").child(userID).child(bitmap);

        //get database reference of where the rest of the bitmap data is
        dbRef = FirebaseDatabase.getInstance().getReference().child("TextBlocks").child(userID);


        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fingerprint success message
                String successMessage = successMes.getText().toString();

                //get entered pin code
                String enteredPin = pin.getText().toString();

                //create instance of hash class
                Hash matchHash = new Hash();
                //validate the hash to make sure the inputted pin code matches the hash in the database
                boolean match = false;
                try {
                    match = matchHash.validatePassword(enteredPin, storedPin);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                //pin code entered
                if(!Objects.equals(enteredPin, "")){
                    //if pin code matches the one stored in the database
                    if(match){

                        //download the bitmap in firebase storage as byte array
                        final long ONE_MEGABYTE = 1024 * 1024;
                        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                //create new bitmap with the text in it
                                Bitmap factory = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                //Use class Steg to decode the bitmap and get the text back out
                                String decodedMessage = null;
                                try {
                                    decodedMessage = Steg.withInput(factory).decode().intoString();
                                    assert decodedMessage != null;
                                    //set the text in a textview
                                    text.setText(decodedMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //if text is not null the text has been removed from the bitmap
                                if(text != null){
                                    Snackbar.make(layout, "Steg Successful", Snackbar.LENGTH_SHORT).show();
                                }else{
                                    Snackbar.make(layout, "Steg Error Occurred", Snackbar.LENGTH_SHORT).show();
                                }

                        //if download of bitmap fails error message is displayed
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Snackbar.make(layout, "An Error Occurred", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    //pin code does not match the one stored in the database
                    }else{
                        Snackbar.make(layout, "Please Enter Correct Pin Code", Snackbar.LENGTH_SHORT).show();
                    }
                }

                //fingerprint scanner was used
                if(Objects.equals(enteredPin, "")){

                    //if fingerprint matches the one stored in the android fingerprint manager
                    if(success.equals(successMessage)){

                        //download the bitmap in firebase storage as byte array
                        final long ONE_MEGABYTE = 1024 * 1024;
                        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                //create new bitmap with the text in it
                                Bitmap factory = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                //Use class Steg to decode the bitmap and get the text back out
                                String decodedMessage = null;
                                try {
                                    decodedMessage = Steg.withInput(factory).decode().intoString();
                                    assert decodedMessage != null;
                                    //set the text in a textview
                                    text.setText(decodedMessage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //if text is not null the text has been removed from the bitmap
                                if(text != null){
                                    Snackbar.make(layout, "Steg Successful", Snackbar.LENGTH_SHORT).show();
                                }else{
                                    Snackbar.make(layout, "Steg Error Occurred", Snackbar.LENGTH_SHORT).show();
                                }

                                //if download of bitmap fails error message is displayed
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Snackbar.make(layout, "An Error Occurred", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                        //fingerprint does not match the one stored
                    }else{
                        Snackbar.make(layout, "Fingerprint Scan Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }


            }
        });

        //button onclick for delete methods
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get pin code and success message to string
                final String pincode = pin.getText().toString();
                String successMessage = successMes.getText().toString();

                //get instance of hash class
                Hash hash = new Hash();
                //valdate the hash
                boolean match = false;
                try {
                    match = hash.validatePassword(pincode, storedPin);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                //if no pin code entered
                if(pincode.equals("")){
                    //and if fingerprint scan successful
                    if(success.equals(successMessage)){
                        //delete data
                        DeleteData();
                    }
                }

                //pin code entered
                if(!pincode.isEmpty()){
                    //and if the pin code matches the one in the database
                    if(match){
                        //delete the data
                        DeleteData();
                    }else{
                        Snackbar.make(layout, "Please Enter Correct Pin Code", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });



        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DecryptText.this, ViewText.class);
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

















        //code needed for fingerprint scanner
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
        purchaseButton.setOnClickListener(new DecryptText.PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));

    }


    private void DeleteData() {
        //get bitmap name to string
        final String textName = name.getText().toString();

        //query the database for the bitmap name
        Query query = dbRef.orderByChild("TextName").equalTo(textName);

        //if the bitmap name exists then delete it
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0){
                        for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                            itemSnapshot.getRef().removeValue();
                            Toast.makeText(DecryptText.this, "Text Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DecryptText.this, ViewText.class);
                            startActivity(intent);
                        }
                    }else{
                        Snackbar.make(layout, "Deletion Error", Snackbar.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(layout, "Error Occurred", Snackbar.LENGTH_SHORT).show();
            }
        });
    }



    //finger print stuff

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
                x14532757.softwareproject.Text.FingerprintAuthDialog fragment
                        = new x14532757.softwareproject.Text.FingerprintAuthDialog();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(x14532757.softwareproject.Text.FingerprintAuthDialog.Stage.FINGERPRINT);

                } else {
                    fragment.setStage(x14532757.softwareproject.Text.FingerprintAuthDialog.Stage.PASSWORD);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                x14532757.softwareproject.Text.FingerprintAuthDialog fragment
                        = new x14532757.softwareproject.Text.FingerprintAuthDialog();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        x14532757.softwareproject.Text.FingerprintAuthDialog.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }
}
