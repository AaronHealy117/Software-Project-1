package x14532757.softwareproject.Password;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 13/10/2017.
 */

public class ViewPasswords extends AppCompatActivity {



    public ViewPasswords() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpasswords);

        final ListView listview = (ListView) findViewById(R.id.PasswordList);
        Button home = (Button) findViewById(R.id.homeBtn);
        Button add  = (Button) findViewById(R.id.newBtn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPasswords.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPasswords.this, AddPassword.class);
                startActivity(intent);
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Passwords").child(userID);

        final FirebaseListAdapter<Passwords> fAdapter = new FirebaseListAdapter<Passwords>(
               ViewPasswords.this,
                Passwords.class,
                R.layout.password_text_list_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Passwords model, int position) {
                TextView name = (TextView) v.findViewById(R.id.nameText);
                name.setText(model.getPasswordName());
            }
        };

        listview.setAdapter(fAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewPasswords.this, DecryptPassword.class);

                Passwords itemRef = fAdapter.getItem(position);

                intent.putExtra("data", itemRef.getPasswordName());
                intent.putExtra("pass", itemRef.getPasswordText());
                intent.putExtra("pin", itemRef.getPinCode());


                startActivity(intent);
            }
        });


        //https://stackoverflow.com/questions/6277662/how-to-pass-the-selectedlistitems-object-to-another-activity
        //http://hmkcode.com/android-custom-listview-items-row/







    }


}
