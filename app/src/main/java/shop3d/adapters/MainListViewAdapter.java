package shop3d.adapters;

/**
 * Created by Fahad on 11/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.ParseObject;
import com.stripe.model.Charge;

import java.util.HashMap;

import shop3d.util.ColorHelper;
import shop3d.util.Helper;
import shop3d.util.ImageDownloader;
import shop3d.util.PriceGetter;
import shop3d.util.Printable;

import shop3d.activity.MainActivity;
import activity.shop3d.org.shop3d.R;
import shop3d.util.SPManager;

public class MainListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;

    static class ViewHolder {
        // Title of the model
        TextView title;

        // The number of changeable materials on the model.
        TextView modifiers;

        // For extra messages like sale, featured, new etc.
        TextView extraMsg;

        // The category the model belongs to.
        TextView category;

        // Price.
        TextView price;

        // The rating calculated art runtime.
        TextView rating;

        // The height of the model.
        TextView tallSize;

        // The first image icon.
        ImageView icon;
    }

    public MainListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return MainActivity.listOfModels.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    double dPrintPriceHolder = 0;
    double dSalePriceHolder = 0;
    String sSalePriceHolder = "";
    TextView tvPriceA;
    PriceGetter pg = new PriceGetter();

    public View getView(int position, View convertView, ViewGroup parent) {
        // Create the holder
       final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);

            holder = new ViewHolder();

            // Set the String values for display.
            holder.title = (TextView) convertView.findViewById(R.id.main_ItemTitle);
            holder.modifiers = (TextView) convertView.findViewById(R.id.main_ItemModifiers);
            holder.extraMsg = (TextView) convertView.findViewById(R.id.main_ItemExtraMsg);
            holder.category = (TextView) convertView.findViewById(R.id.main_ItemCategory);
            holder.rating = (TextView) convertView.findViewById(R.id.main_ItemRatings);
            holder.tallSize = (TextView) convertView.findViewById(R.id.main_ItemTall);
            holder.price = (TextView) convertView.findViewById(R.id.main_ItemPrice);


            //ImageView modelIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.main_ItemIcon);
            holder.icon.setAdjustViewBounds(true);

            holder.icon.setImageBitmap(null);
            holder.price.setText("");
            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject fillModel = MainActivity.listOfModels.get(position);
        holder.icon.setImageBitmap(null);


        // Now fill in the placeHolder with correct data.
        holder.title.setText(Printable.GetTitle(fillModel));
        holder.modifiers.setText(Printable.GetModifers(fillModel));
        holder.category.setText("Category: " + Printable.GetCategory(fillModel));
        holder.rating.setText(Printable.GetRatings(fillModel));
        holder.tallSize.setText(Printable.GetTallSize(fillModel));

        String extraMsg = fillModel.getString("extra_msg_text");
        if ( extraMsg != null) {
            if (extraMsg.equals("") == false) {
                holder.extraMsg.setText(extraMsg);
                holder.extraMsg.setBackgroundColor(ColorHelper.StringToColorInt(fillModel.getString("extra_msg_color")));
                holder.extraMsg.setVisibility(View.VISIBLE);
            } else {
                holder.extraMsg.setVisibility(View.GONE);
            }
        } else {
            holder.extraMsg.setVisibility(View.GONE);
        }
        double printPrice =  fillModel.getDouble("smallest_size_price");
        double displayPrice = Helper.FormatDouble(printPrice + fillModel.getDouble("percentage_profit") * printPrice, 0);
        String pPrice = Printable.MakePrintablePrice((float)displayPrice);
        holder.price.setText(pPrice);
        //----------------------------

/*

        final ParseObject finalFillModel  = fillModel;
        holder.price.setText("Loading ...");
        tvPriceA = holder.price;
       // tvPriceA = holder.price;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    dPrintPriceHolder = pg.GetSinglePrice(finalFillModel, 0);

                    // Make the price including profit here.
                    dSalePriceHolder = PriceGetter.MakeSalePrice(dPrintPriceHolder, finalFillModel.getDouble("percentage_profit"));
                    sSalePriceHolder = Printable.MakePrintablePrice((float) dSalePriceHolder);

                } catch (Exception er) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                tvPriceA.setText(sSalePriceHolder);
            }
        }.execute();
*/

        //--------------------------------
        UrlImageViewHelper.setUrlDrawable(holder.icon, fillModel.getParseFile("icon_0").getUrl());

        return convertView;
    }


}