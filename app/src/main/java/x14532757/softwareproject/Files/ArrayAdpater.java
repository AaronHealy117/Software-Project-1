package x14532757.softwareproject.Files;

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
 * Created by x14532757 on 26/10/2017.
 */

public abstract class ArrayAdpater extends FirebaseListAdapter<Files>{

    private Context context;
    private FirebaseListAdapter<Files> itemsArrayList;


    public ArrayAdpater(Activity activity, Class<Files> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, R.layout.list_layout, ref);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = layoutInflater.inflate(R.layout.list_layout, viewGroup, false);

        TextView name = (TextView) rowView.findViewById(R.id.nameText);
        TextView pass = (TextView) rowView.findViewById(R.id.passwordText);

        name.setText(itemsArrayList.getItem(position).getFileName());
        pass.setText(itemsArrayList.getItem(position).getFileDesc());



        return rowView;

    }
}
