package x14532757.softwareproject.Password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 */

public final class AddPassword extends AppCompatActivity {

    private EditText name;
    private EditText password;
    private EditText pin;
    private ProgressDialog pd;

    private static final int keySize = 256;
    private static final int saltlength = keySize / 8;


    /*call encryption class -- instance of builder using
       key size 128
       Key Algorithm AES
       CharsetName("UTF8")
       IterationCount(1000)
       DigestAlgorithm("SHA1")
       Base64Mode(Base64.DEFAULT)
       Algorithm("AES/CBC/PKCS5Padding")
       SecureRandomAlgorithm("SHA1PRNG")
       setSecretKeyType("PBKDF2WithHmacSHA1");
    */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpassword);


        //get current firebase auth user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        //get reference database bucket
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Passwords").child(userID);

        //get GUI
        name = (EditText) findViewById(R.id.textName);
        password = (EditText) findViewById(R.id.textBlock);
        pin = (EditText) findViewById(R.id.choosePinBtn);
        final Button addBtn = (Button) findViewById(R.id.addButton);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");

        addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();

                    String pass = pin.getText().toString();
                    String key = pass;
                    String salt = "LDQjdlZ%NiJhTQB%dVuc)%TyGiG9ZBqS";
                    byte[] iv = {-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};
                    final Encryption encryption = Encryption.getDefault(key, salt, iv);

                    //get user inputs to string
                    String nameValue = name.getText().toString();
                    String pinValue = pin.getText().toString();
                    String passwordToEncrypt = password.getText().toString();
                    //use encryption class to encrypt password
                    assert encryption != null;
                    String passwordEncrypted = encryption.encryptOrNull(passwordToEncrypt);

                    //test.setText(passwordEncrypted);


                    // Title is required
                    if (TextUtils.isEmpty(nameValue)) {
                        Toast.makeText(AddPassword.this, "A Password Name is Required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Body is required
                    if (TextUtils.isEmpty(passwordEncrypted)) {
                        Toast.makeText(AddPassword.this, "A Password is Required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //pin is required
                    if (TextUtils.isEmpty(pinValue)) {
                        Toast.makeText(AddPassword.this, "A Password Pin Code is Required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //push to database if ^ met
                    DatabaseReference newPost = db.push();

                    //database data
                    newPost.child("PasswordName").setValue(nameValue);
                    newPost.child("PasswordText").setValue(passwordEncrypted);
                    newPost.child("PinCode").setValue(pinValue);

                    Toast.makeText(AddPassword.this, "Password Encryption Successful", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    Intent intent = new Intent(AddPassword.this, HomeScreen.class);
                    startActivity(intent);

                }
            });

    }

    private byte[] generateIV(){
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        return iv;
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[saltlength];
        random.nextBytes(bytes);
        return new String(bytes);
    }


}
