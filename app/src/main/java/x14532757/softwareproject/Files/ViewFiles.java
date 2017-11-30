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
 */

public class ViewFiles extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfiles);
        list = (ListView) findViewById(R.id.FileList);

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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Files").child(userID);

        final FirebaseListAdapter<Files> fAdapter = new FirebaseListAdapter<Files>(
                ViewFiles.this,
                Files.class,
                R.layout.list_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Files model, int position) {
                TextView name = (TextView) v.findViewById(R.id.nameText);
                name.setText(model.getFileName());
                TextView pass = (TextView) v.findViewById(R.id.passwordText);
                pass.setText(model.getFileDesc());

            }
        };

        list.setAdapter(fAdapter);

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
