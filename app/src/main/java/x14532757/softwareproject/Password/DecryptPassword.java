package x14532757.softwareproject.Password;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 23/10/2017.
 */

public class DecryptPassword extends Activity{

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    private static String TAG = "decryptPassword";
    private TextView name;
    private DatabaseReference dbRef;
    private EditText pin;
    private TextView test;
    private TextView password;
    private TextView successMes;

    private LinearLayout clayout;
    private LinearLayout ddlayout;

    //fingerprint success message
    private String success = "Fingerprint Scan Successful";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decryptpassword);

        clayout = (LinearLayout) findViewById(R.id.chooselayout);
        ddlayout = (LinearLayout) findViewById(R.id.pinCodeLayout);
        //text views
        name = (TextView) findViewById(R.id.imageNameText);
        password = (TextView) findViewById(R.id.showText);
        test = (TextView) findViewById(R.id.pincodeText);
        successMes = (TextView) findViewById(R.id.confirmation_message);
        //edit text
        pin = (EditText) findViewById(R.id.PinInput);
        //buttons
        final Button choosepin = (Button) findViewById(R.id.choosePinBtn);
        final Button decrypt = (Button) findViewById(R.id.DecryptButton);
        final Button delete = (Button) findViewById(R.id.DeleteButton);
        Button exit = (Button) findViewById(R.id.ExitButton);

        //get data passed from view passwords and put them into strings and text views
        name.setText(getIntent().getExtras().getString("data"));
        password.setText(getIntent().getExtras().getString("pass"));
        test.setText(getIntent().getExtras().getString("pin"));

        //get current user id and reference to database
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Passwords").child(userID);

        //button to show pincode GUI
        choosepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clayout.setVisibility(View.GONE);
                ddlayout.setVisibility(View.VISIBLE);
                pin.setVisibility(View.VISIBLE);
            }
        });

        //button to delete the password
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String pincode = pin.getText().toString();
                final String pinc = test.getText().toString();
                String successMessage = successMes.getText().toString();

                if(success.equals(successMessage)){
                    DeleteData();
                }
                if(pincode.equals(pinc)){
                    DeleteData();
                }
                if(!Objects.equals(pincode, pinc)){
                    Toast.makeText(DecryptPassword.this, "Pin Incorrect", Toast.LENGTH_SHORT).show();
                }
                if(pincode.isEmpty()){
                    Toast.makeText(DecryptPassword.this, "Pin Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String pincode = pin.getText().toString();
                final String pinc = test.getText().toString();
                String passwordEncrypted = password.getText().toString();
                String successMessage = successMes.getText().toString();

                //String pass = pin.getText().toString() ;
                String key = pinc;
                String salt = "LDQjdlZ%NiJhTQB%dVuc)%TyGiG9ZBqS";
                byte[] iv = {-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};
                final Encryption decryption = Encryption.getDefault(key, salt, iv);

                if(success.equals(successMessage)){
                    assert decryption != null;
                    String passwordDecypted = decryption.decryptOrNull(passwordEncrypted);
                    password.setText(passwordDecypted);

                    if(password!=null){
                        Toast.makeText(DecryptPassword.this, "success decrypt", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DecryptPassword.this, "decrypt failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(pincode.equals(pinc)){
                    assert decryption != null;
                    String passwordDecypted = decryption.decryptOrNull(passwordEncrypted);
                    password.setText(passwordDecypted);

                    if(password!=null){
                        Toast.makeText(DecryptPassword.this, "success decrypt", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(DecryptPassword.this, "decrypt failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!Objects.equals(pincode, pinc)){
                    Toast.makeText(DecryptPassword.this, "Pin Incorrect", Toast.LENGTH_SHORT).show();
                }
                if(pincode.isEmpty()){
                    Toast.makeText(DecryptPassword.this, "Pin Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DecryptPassword.this, ViewPasswords.class);
                startActivity(intent);
            }
        });

        //fingerprint
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
        Button purchaseButton = (Button) findViewById(R.id.choosePrintBtn);


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
        purchaseButton.setOnClickListener(new DecryptPassword.PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));

    }



    public void DeleteData() {
        final String passName = name.getText().toString();

        Query query = dbRef.orderByChild("PasswordName").equalTo(passName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() > 0){
                        for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()){
                            itemSnapshot.getRef().removeValue();
                            Toast.makeText(DecryptPassword.this, "Password Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DecryptPassword.this, ViewPasswords.class);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(DecryptPassword.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DecryptPassword.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //finger print
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




    //https://stackoverflow.com/questions/39657449/remove-specific-value-from-firebase-database
    //https://stackoverflow.com/questions/39642635/delete-item-from-the-firebase-database
    //https://stackoverflow.com/questions/45734001/comparing-edittext-string-with-firebase-databases-child-value
    //https://stackoverflow.com/questions/37390864/how-to-delete-from-firebase-realtime-database

    //https://github.com/puf/firebase-stackoverflow-android/blob/master/app/src/main/java/com/firebasedemo/stackoverflow/Activity32469846.java


