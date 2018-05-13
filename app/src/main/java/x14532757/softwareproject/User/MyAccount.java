package x14532757.softwareproject.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.LoginRegister.ForgotPassword;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 29/11/2017.
 */

public class MyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_myaccount);

        //get current firebase auth user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        //get database reference to user table
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        Button home = (Button) findViewById(R.id.homeBtn);
        Button reset = (Button) findViewById(R.id.resetBtn);

        final TextView name = (TextView) findViewById(R.id.nameTxt);
        final TextView email = (TextView) findViewById(R.id.emailTxt);
        final TextView phone = (TextView) findViewById(R.id.phoneTxt);

        //when screen is opened download the users information stored in the database and
        //display it on screen in text views
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);

                    assert user != null;
                    String names = user.getUserName();
                    name.setText(names);

                    String emails = user.getUserEmail();
                    email.setText(emails);

                    String phones = user.getUserPhoneNumber();
                    phone.setText(phones);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, ForgotPassword.class);
                startActivity(intent);
            }
        });




    }
}
