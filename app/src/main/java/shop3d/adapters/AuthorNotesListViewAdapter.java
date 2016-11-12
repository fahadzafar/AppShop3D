package shop3d.adapters;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.AccountActivity;
import shop3d.activity.AuthorNoteActivity;
import shop3d.parse.ParseOperation;
import shop3d.util.Printable;

public class AuthorNotesListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;

    static class ViewHolder {
        // Title of the model
        TextView messsage;
        TextView date;

    }

    public AuthorNotesListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return AuthorNoteActivity.listOfModels.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Create the holder
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_authornotes_list_item, null);

            holder = new ViewHolder();


            // Set the String values for display.
            holder.date = (TextView) convertView.findViewById(R.id.authornotes_date);
            holder.messsage = (TextView) convertView.findViewById(R.id.authornotes_message);

            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject message = AuthorNoteActivity.listOfModels.get(position);

        String data[] = message.getCreatedAt().toString().split(" ");
        // Now fill in the placeHolder with correct data.
        holder.date.setText(data[1] + " " + data[2] + " " + data[data.length - 1] +
                " (" + data[0] + "):");
        holder.messsage.setText(message.getString("message"));


        return convertView;
    }


}