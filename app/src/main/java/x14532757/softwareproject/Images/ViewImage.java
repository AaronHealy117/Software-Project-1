package x14532757.softwareproject.Images;

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
 * Created by x14532757 on 12/11/2017.
 */

public class ViewImage extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimages);

        list = (ListView) findViewById(R.id.ImageList);
        Button home = (Button) findViewById(R.id.gohomeBtn);
        Button add = (Button) findViewById(R.id.gonewBtn);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Images").child(userID);

        final FirebaseListAdapter<Image> fAdapter = new FirebaseListAdapter<Image>(
                ViewImage.this,
                Image.class,
                R.layout.list_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Image model, int position) {

                TextView name = (TextView) v.findViewById(R.id.nameText);
                name.setText(model.getImageName());
                TextView pass = (TextView) v.findViewById(R.id.passwordText);
                pass.setText(model.getImageDesc());

            }
        };

        list.setAdapter(fAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewImage.this, DecryptImage.class);

                Image itemRef = fAdapter.getItem(position);
                intent.putExtra("data", itemRef.getImageName());
                intent.putExtra("desc", itemRef.getImageDesc());
                intent.putExtra("pin", itemRef.getPinCode());
                intent.putExtra("Durl", itemRef.getImageURL());

                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewImage.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewImage.this, AddImage.class);
                startActivity(intent);
            }
        });

    }
}
