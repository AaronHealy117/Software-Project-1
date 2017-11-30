package x14532757.softwareproject.Password;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import x14532757.softwareproject.R;

/**
 * Created by x14532757 on 24/10/2017.
 */

public abstract class ArrayAdapter extends FirebaseListAdapter<Passwords> {

    private Context context;
    private FirebaseListAdapter<Passwords> itemsArrayList;


    public ArrayAdapter(Activity activity, Class<Passwords> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, R.layout.password_text_list_layout, ref);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = layoutInflater.inflate(R.layout.password_text_list_layout, viewGroup, false);

        TextView name = (TextView) rowView.findViewById(R.id.nameText);

        name.setText(itemsArrayList.getItem(position).getPasswordName());

        return rowView;

    }



    //http://hmkcode.com/android-custom-listview-items-row/
}
