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

import shop3d.beans.SaleItem;
import shop3d.util.ATAGS;
import shop3d.util.Printable;
import shop3d.util.SPManager;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.CartActivity;

public class CartListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;

    static class ViewHolder {
        TextView title;
        ImageView icon;
        TextView quantity;
        TextView price;
        TextView qTPTotal;
        TextView dims;
        TextView itemPrice;

        Button decQuantity;
        Button incQuantity;
    }

    public CartListViewAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return SPManager.CartObjects.size();
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
            convertView = inflater.inflate(R.layout.shopping_cart_item, null);

            holder = new ViewHolder();

            // Set the question statement after the processing.
            holder.title = (TextView) convertView.findViewById(R.id.cartItem_ItemTitle);
            holder.quantity = (TextView) convertView.findViewById(R.id.cartItem_ItemQuantity);
            holder.price = (TextView) convertView.findViewById(R.id.cartItem_ItemPrice);
            holder.itemPrice = (TextView) convertView.findViewById(R.id.cartItem_Price_value);
            holder.qTPTotal = (TextView) convertView.findViewById(R.id.cartItem_ItemQTPTotal);
            holder.dims = (TextView) convertView.findViewById(R.id.cartItem_ItemDimensions);

            //ImageView modelIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.cartItem_ItemIcon);
            holder.icon.setAdjustViewBounds(true);

            holder.decQuantity = (Button) convertView.findViewById(R.id.cartitem_ItemDecQuantity);
            holder.incQuantity = (Button) convertView.findViewById(R.id.cartItem_ItemIncQuantity);
            holder.incQuantity.setTag(position);
            holder.decQuantity.setTag(position);

            holder.decQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(holder.quantity.getText().toString());
                    if ((count - 1) <= 0) {
                        DisplayDeleteItemDialogue(position);
                        //UpdateQTP(si, position, holder);
                        CartActivity.UpdateTotalCost();
                    } else {
                        count -= 1;

                        SaleItem si = SPManager.CartObjects.get(position);
                        holder.quantity.setText(String.valueOf(count));
                        SPManager.CartObjects.get(position).Quantity = count;
                        UpdateQTP(si, position, holder);

                    }

                }
            });

            holder.incQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int likeCount = position;

                    int count = Integer.parseInt(holder.quantity.getText().toString()) + 1;
                    holder.quantity.setText(String.valueOf(count));
                    SPManager.CartObjects.get(position).Quantity = count;
                    UpdateQTP(SPManager.CartObjects.get(position), position, holder);

                }
            });


            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // Now fill in the placeHolder with correct data.
        ParseObject fillModel = SPManager.CartObjects.get(position).item;

        // Only ATAGS.TAG_ACTIVITY_MYMODEL has this field.
        if (fillModel.has("original_model_id")) {
            // Now you have to check if its not a my model object
            // Since the if code should run only for a favorite object.
            if (fillModel.has("mtl_color") == false)
                holder.title.setText(Printable.GetTitle(fillModel.getParseObject("original_model_id")));
            else
                holder.title.setText(Printable.GetTitle(fillModel));

            ParseObject insideObj = fillModel.getParseObject("original_model_id");
            holder.price.setText(Printable.MakePrintablePrice((float) SPManager.CartObjects.get(position).SalePrice));
            holder.itemPrice.setText(Printable.MakePrintablePrice((float) SPManager.CartObjects.get(position).SalePrice));
            holder.quantity.setText(SPManager.CartObjects.get(position).Quantity + "");
            UpdateQTP(SPManager.CartObjects.get(position), position, holder);
            holder.dims.setText(Printable.GetSize(insideObj, SPManager.CartObjects.get(position).SizeIndex));
        } else {
            holder.title.setText(Printable.GetTitle(fillModel));
            holder.price.setText(Printable.MakePrintablePrice((float) SPManager.CartObjects.get(position).SalePrice));
            holder.itemPrice.setText(Printable.MakePrintablePrice((float) SPManager.CartObjects.get(position).SalePrice));

            holder.quantity.setText(SPManager.CartObjects.get(position).Quantity + "");
            UpdateQTP(SPManager.CartObjects.get(position), position, holder);
            holder.dims.setText(Printable.GetSize(fillModel, SPManager.CartObjects.get(position).SizeIndex));
        }
        // Setting image icon.
        holder.icon.setImageBitmap(null);

        // This if has to run for only the favorite item.
        // So here we need to differentiate between Favorite and mymodel and model.
        if (fillModel.getClassName().equals(ATAGS.TABLE_PARSE_FAVORITE))
            //    ImageDownloader.SetImageHere("icon_0", holder.icon, fillModel.getParseObject("original_model_id"));
            UrlImageViewHelper.setUrlDrawable(holder.icon, fillModel.getParseObject("original_model_id").getParseFile("icon_0").getUrl());

            // Both the other objects, Model_UserMade and Model have "icon_0"
        else
            //  ImageDownloader.SetImageHere("icon_0", holder.icon, fillModel);
            UrlImageViewHelper.setUrlDrawable(holder.icon, fillModel.getParseFile("icon_0").getUrl());


        return convertView;
    }

    public static void UpdateQTP(SaleItem si, int position, ViewHolder holder) {
        float qTPTotal = 0;
        // if (si.item.has("original_model_id")) {
        //     qTPTotal = si.Quantity * Printable.GetPriceInInt(si.item.getParseObject("original_model_id"), si.SizeIndex);
        // } else {
        qTPTotal = si.Quantity * (float) si.SalePrice;// Printable.GetPriceInInt(si.item, si.SizeIndex);
        // }
        holder.qTPTotal.setText(Printable.MakePrintablePrice(qTPTotal));
        CartActivity.UpdateTotalCost();
    }

    public void DisplayDeleteItemDialogue(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SPManager.CartObjects.remove(position);
                        CartActivity.RefreshCart();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Dont do anything.
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}