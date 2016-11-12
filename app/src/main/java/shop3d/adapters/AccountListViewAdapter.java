package shop3d.adapters;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;

import shop3d.activity.FavoritesActivity;
import shop3d.util.SPManager;
import shop3d.parse.ParseOperation;
import shop3d.util.Printable;

import shop3d.activity.AccountActivity;
import activity.shop3d.org.shop3d.R;

public class AccountListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;

    static class ViewHolder {
        // Title of the model
        TextView title;
        TextView name;
        TextView addressLine1;
        TextView addressLine2;
        TextView city;
        TextView state;
        TextView zipcode;
        TextView country;

        Button remove;
    }

    public AccountListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return AccountActivity.listOfModels.size();
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
            convertView = inflater.inflate(R.layout.activity_account_list_item, null);

            holder = new ViewHolder();

            TextView addressLine1;
            TextView addressLine2;
            TextView city;
            TextView state;
            TextView zipcode;
            TextView country;

            // Set the String values for display.
            holder.title = (TextView) convertView.findViewById(R.id.account_ShippingTitle);
            holder.name = (TextView) convertView.findViewById(R.id.account_ShippingName);
            holder.addressLine1 = (TextView) convertView.findViewById(R.id.account_ShippingLine1);
            holder.addressLine2 = (TextView) convertView.findViewById(R.id.account_ShippingLine2);
            holder.city = (TextView) convertView.findViewById(R.id.account_ShippingCity);
            holder.state = (TextView) convertView.findViewById(R.id.account_ShippingState);
            holder.zipcode = (TextView) convertView.findViewById(R.id.account_ShippingZipcode);
            holder.country = (TextView) convertView.findViewById(R.id.account_ShippingCountry);

            holder.remove = (Button) convertView.findViewById((R.id.favorite_BtnRemove));

            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject address = AccountActivity.listOfModels.get(position);

        // Now fill in the placeHolder with correct data.
        holder.title.setText(Printable.GetShippingTitle(address));
        holder.name.setText(address.getString("name"));
        holder.addressLine1.setText(Printable.GetShippingAddressLine(address, 1));
        holder.addressLine2.setText(Printable.GetShippingAddressLine(address, 2));
        holder.city.setText(address.getString("city"));
        holder.state.setText(address.getString("state"));
        holder.zipcode.setText(address.getInt("zipcode") + "");


            holder.country.setText(address.getString("country"));


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Show the are you sure dialogue
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ParseObject delModelId = AccountActivity.listOfModels.get(position);
                                ParseOperation.RemoveParseObject(delModelId);
                                AccountActivity.listOfModels.remove(position);
                                AccountActivity.RefreshListView();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                // ------------------------------------------


            }
        });

        return convertView;
    }


}