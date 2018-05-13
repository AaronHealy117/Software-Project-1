package x14532757.softwareproject.Files;

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
 * Created by x14532757 on 19/10/2017.
 *
 * Code Modified from:
 * Title: Android Custom ListView Items “Row”
 * Author: hmkcode
 * Date: 07/09/13
 * Availability: https://github.com/hmkcode/Android/tree/master/android-custom-listview
 *
 * Code Modified from:
 * Title: How to pass the selectedListItem's object to another activity?
 * Author: KillerFish
 * Date: 11/06/11
 * Availability: https://stackoverflow.com/questions/6277662/how-to-pass-the-selectedlistitems-object-to-another-activity
 */

public class ViewFiles extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfiles);
        ListView list = (ListView) findViewById(R.id.FileList);
        list.setFilterTouchesWhenObscured(true);

        Button home = (Button) findViewById(R.id.homeBtn);
        Button add  = (Button) findViewById(R.id.newBtn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewFiles.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewFiles.this, AddFile.class);
                startActivity(intent);
            }
        });

        //get the currently logged in user information
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Files").child(userID);

        //create a firebase list adapter and get the frontend list_layout
        // and populate the listview with the data stored in the database
        final FirebaseListAdapter<Files> fAdapter = new FirebaseListAdapter<Files>(
                ViewFiles.this,
                Files.class,
                R.layout.list_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Files model, int position) {
                TextView name = v.findViewById(R.id.nameText);
                name.setText(model.getFileName());
                TextView pass = v.findViewById(R.id.passwordText);
                pass.setText(model.getFileDesc());

            }
        };

        list.setAdapter(fAdapter);

        //when a user clicks on an item in the listview, data from the lisview is passed to the decryptFile page
        //to be used to decrypt the files
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewFiles.this, DecryptFiles.class);

                Files itemRef = fAdapter.getItem(position);

                intent.putExtra("data", itemRef.getFileName());
                intent.putExtra("desc", itemRef.getFileDesc());
                intent.putExtra("pin", itemRef.getPinCode());
                intent.putExtra("Durl", itemRef.getFileURL());


                startActivity(intent);
            }
        });


    }
}
