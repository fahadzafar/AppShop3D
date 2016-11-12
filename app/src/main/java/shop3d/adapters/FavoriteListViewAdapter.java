package shop3d.adapters;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.stripe.model.Charge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import shop3d.activity.MyModelActivity;
import shop3d.parse.ParseOperation;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.ImageDownloader;
import shop3d.util.PriceGetter;
import shop3d.util.Printable;

import shop3d.activity.FavoritesActivity;
import activity.shop3d.org.shop3d.R;
import shop3d.util.SPManager;

public class FavoriteListViewAdapter extends BaseAdapter {

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

    public FavoriteListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return FavoritesActivity.listOfModels.size();
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
            convertView = inflater.inflate(R.layout.activity_favorite_list_item, null);

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


        boolean hasParent = false;
        ParseObject parentModel = null;

        ParseObject fillModel = null;
        if (FavoritesActivity.listOfModels.get(position).getString("model_class_name").equals(ATAGS.TABLE_PARSE_MODEL))
            fillModel = FavoritesActivity.listOfModels.get(position).getParseObject("model_id");
        else if (FavoritesActivity.listOfModels.get(position).getString("model_class_name").equals(ATAGS.TABLE_PARSE_MODEL_USERMADE)) {
            fillModel = FavoritesActivity.listOfModels.get(position).getParseObject("usermade_model_id");
            parentModel = fillModel.getParseObject("original_model_id");
            hasParent = true;
        }

        // Now fill in the placeHolder with correct data.
        holder.title.setText(Printable.GetTitle(fillModel));

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Show the are you sure dialogue
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ParseObject delModelId = FavoritesActivity.listOfModels.get(position);
                                ParseOperation.RemoveParseObject(delModelId);
                                FavoritesActivity.listOfModels.remove(position);
                                FavoritesActivity.RefreshFavoriteList();

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


        if (hasParent) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Model");
                query.whereEqualTo("objectId", parentModel.getObjectId());

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        holder.modifiers.setText(Printable.GetModifersWithoutTitle(parseObject));
                        holder.category.setText("Category: " + Printable.GetCategory(parseObject));
                        holder.rating.setText(Printable.GetRatings(parseObject));
                        holder.tallSize.setText(Printable.GetTallSize(parseObject));
                        holder.price.setText(PriceGetter.GetDisplayItemEstimatePrice(parseObject));
                    }
                });
            } catch (Exception er) {
                int y = 0;
            }


            UrlImageViewHelper.setUrlDrawable(holder.icon, fillModel.getParseFile("icon_0").getUrl());
        } else {
            holder.price.setText(PriceGetter.GetDisplayItemEstimatePrice(fillModel));
            holder.modifiers.setText(Printable.GetModifersWithoutTitle(fillModel));
            holder.category.setText("Category: " +Printable.GetCategory(fillModel));
            holder.rating.setText(Printable.GetRatings(fillModel));
            holder.tallSize.setText(Printable.GetTallSize(fillModel));
            UrlImageViewHelper.setUrlDrawable(holder.icon, fillModel.getParseFile("icon_0").getUrl());
        }

        // Setting image icon.


        return convertView;
    }


}