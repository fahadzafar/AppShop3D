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
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.ParseObject;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.MyModelActivity;
import shop3d.util.ImageDownloader;
import shop3d.util.PriceGetter;
import shop3d.util.Printable;
import shop3d.parse.ParseOperation;

public class MyModelListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;

    static class ViewHolder {
        // Title of the model
        TextView title;

        // The number of changeable materials on the model.
        TextView modifiers;

        // The sale price of the model.
        TextView price;

        // The category the model belongs to.
        TextView category;

        // The rating calculated art runtime.
        TextView rating;

        // The height of the model.
        TextView tallSize;

        // The first image icon.
        ImageView icon;

        Button remove;
    }

    public MyModelListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return MyModelActivity.listOfModels.size();
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
        if (convertView == null || convertView.isAttachedToWindow() == false) {
            convertView = inflater.inflate(R.layout.activity_mymodel_list_item, null);

            holder = new ViewHolder();

            // Set the String values for display.
            holder.title = (TextView) convertView.findViewById(R.id.favorite_ItemTitle);
            holder.modifiers = (TextView) convertView.findViewById(R.id.favorite_ItemModifiers);
            holder.price = (TextView) convertView.findViewById(R.id.favorite_ItemPrice);
            holder.category = (TextView) convertView.findViewById(R.id.favorite_ItemCategory);
            holder.rating = (TextView) convertView.findViewById(R.id.favorite_ItemRatings);
            holder.tallSize = (TextView) convertView.findViewById(R.id.favorite_ItemTall);

            holder.remove = (Button) convertView.findViewById((R.id.favorite_BtnRemove));


            //ImageView modelIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.favorite_ItemIcon);

            holder.icon.setImageBitmap(null);

            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

            holder.icon.setImageBitmap(null);
        }

        holder.icon.setImageBitmap(null);
        ParseObject fillModel = MyModelActivity.listOfModels.get(position).getParseObject("original_model_id");

        // Now fill in the placeHolder with correct data.
        holder.title.setText(Printable.GetTitle(MyModelActivity.listOfModels.get(position)));
        holder.price.setText(PriceGetter.GetDisplayItemEstimatePrice(fillModel));//Printable.GetPrice(fillModel, 0));
        holder.modifiers.setText(Printable.GetModifersWithoutTitle(fillModel));
        holder.category.setText(Printable.GetCategory(fillModel));
        holder.rating.setText(Printable.GetRatings(fillModel));
        holder.tallSize.setText(Printable.GetTallSize(fillModel));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the are you sure dialogue
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ParseObject delModelId = MyModelActivity.listOfModels.get(position);
                                ParseOperation.VizFalseParseObject(delModelId);
                                MyModelActivity.listOfModels.remove(position);
                                MyModelActivity.RefreshFavoriteList();
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
        // Setting image icon.
        // ImageDownloader.SetImageHere("icon_0", holder.icon, MyModelActivity.listOfModels.get(position));
        UrlImageViewHelper.setUrlDrawable(holder.icon,  MyModelActivity.listOfModels.get(position).getParseFile("icon_0").getUrl());

        return convertView;
    }


}