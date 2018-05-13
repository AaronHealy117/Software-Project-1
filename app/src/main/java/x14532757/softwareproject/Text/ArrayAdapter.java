package x14532757.softwareproject.Text;

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
 * Created by x14532757 on 29/10/2017.
 *
 * Title: android-custom-listview
 * Author: hmkcode
 * Date: 07/09/2013
 * Availability: https://github.com/hmkcode/Android/tree/master/android-custom-listview
 */

public abstract class ArrayAdapter extends FirebaseListAdapter<Text> {

    private Context context;
    //Create new Firabase list adapter
    private FirebaseListAdapter<Text> itemsArrayList;

    //constructor
    public ArrayAdapter(Activity activity, Class<Text> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, R.layout.password_text_list_layout, ref);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    //create new listview of the users data
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Create reference to the layout file = list_layout
        View rowView = layoutInflater.inflate(R.layout.password_text_list_layout, viewGroup, false);

        //get the UI elements used to show the users data
        TextView name = rowView.findViewById(R.id.nameText);

        //set the password name of the user data in the firebase listview
        name.setText(itemsArrayList.getItem(position).getTextName());

        return rowView;

    }


}
