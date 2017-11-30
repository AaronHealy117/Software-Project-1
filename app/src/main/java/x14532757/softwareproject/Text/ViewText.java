package x14532757.softwareproject.Text;

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

import x14532757.softwareproject.Files.AddFile;
import x14532757.softwareproject.Files.ViewFiles;
import x14532757.softwareproject.HomeScreen;
import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 19/10/2017.
 */

public class ViewText extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtext);

        final ListView list = (ListView) findViewById(R.id.TextListView);

        Button home = (Button) findViewById(R.id.homeBtn);
        Button add  = (Button) findViewById(R.id.newBtn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewText.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewText.this, AddText.class);
                startActivity(intent);
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("TextBlocks").child(userID);

        final FirebaseListAdapter<Text> fAdapter = new FirebaseListAdapter<Text>(
                ViewText.this,
                Text.class,
                R.layout.password_text_list_layout,
                dbRef
        ) {
            @Override
            protected void populateView(View v, Text model, int position) {
                TextView name = (TextView) v.findViewById(R.id.nameText);
                name.setText(model.getTextName());
            }
        };

        list.setAdapter(fAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewText.this, DecryptText.class);

                Text itemRef = fAdapter.getItem(position);

                intent.putExtra("data", itemRef.getTextName());
                intent.putExtra("stuff", itemRef.getTextBlock());
                intent.putExtra("pin", itemRef.getPinCode());


                startActivity(intent);
            }
        });


    }
}
