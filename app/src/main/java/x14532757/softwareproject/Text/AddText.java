package x14532757.softwareproject.Text;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 15/10/2017.
 */

public class AddText extends AppCompatActivity {

    private EditText name;
    private EditText text;
    private EditText pin;
    private Button upload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtext);

        name = (EditText) findViewById(R.id.textName);
        text = (EditText) findViewById(R.id.textBlock);
        pin = (EditText) findViewById(R.id.choosePinBtn);
        upload = (Button) findViewById(R.id.addButton);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("TextBlocks").child(userID);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameValue = name.getText().toString();
                String textValue = text.getText().toString();
                String pinValue = pin.getText().toString();
                String userId = user.getUid();

                DatabaseReference dbref = db.push();
                dbref.child("TextName").setValue(nameValue);
                dbref.child("TextBlock").setValue(textValue);
                dbref.child("PinCode").setValue(pinValue);
                dbref.child("UserID").setValue(userId);

                Toast.makeText(AddText.this, "Upload Successful", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(AddText.this, HomeScreen.class);
                startActivity(intent);

            }
        });



    }
}
