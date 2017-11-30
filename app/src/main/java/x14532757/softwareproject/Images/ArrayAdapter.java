package x14532757.softwareproject.Images;

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
 * Created by x14532757 on 12/11/2017.
 */

public abstract class ArrayAdapter extends FirebaseListAdapter<Image> {

    private Context context;
    private FirebaseListAdapter<Image> itemsArrayList;


    public ArrayAdapter(Activity activity, Class<Image> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, R.layout.list_layout, ref);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert layoutInflater != null;
        View rowView = layoutInflater.inflate(R.layout.list_layout, viewGroup, false);

        TextView name = (TextView) rowView.findViewById(R.id.nameText);
        TextView pass = (TextView) rowView.findViewById(R.id.passwordText);

        name.setText(itemsArrayList.getItem(position).getImageName());
        pass.setText(itemsArrayList.getItem(position).getImageDesc());



        return rowView;

    }
}
