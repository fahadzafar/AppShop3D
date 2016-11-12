package shop3d.adapters;

/**
 * Created by Fahad on 11/6/2015.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Typeface;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.List;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.CartActivity;
import shop3d.activity.MyModelActivity;
import shop3d.activity.MyOrderActivity;
import shop3d.util.ATAGS;
import shop3d.util.ImageDownloader;
import shop3d.util.OrderParser;
import shop3d.util.Printable;
import shop3d.parse.ParseOperation;

public class MyOrderListViewAdapter extends BaseAdapter {

    private Activity activity;
    private static LayoutInflater inflater = null;
    List<ParseObject> itemDAta;
    Typeface custom_font;

    static class ViewHolder {
        // Title of the model
        TextView title;
        TextView status;
        TextView price;
        TextView deliverDate;
        TextView shipDate;
        TextView stageCompleted;
        TextView stageInProgress;
        TextView shipCost;
        TextView taxCost;
        TextView itemCount;
        LinearLayout itemInfo;
        LinearLayout orderUPS;

    }

    public MyOrderListViewAdapter(Activity a) {
        activity = a;
        custom_font = Typeface.createFromAsset(activity.getAssets(), "RalewayRegular.ttf");
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return MyOrderActivity.listOfModels.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    boolean UPSDataAvailable = true;

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Create the holder
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_myorder_list_item, null);

            holder = new ViewHolder();

            // Set the String values for display.
            holder.title = (TextView) convertView.findViewById(R.id.myorder_orderTitle);
            holder.status = (TextView) convertView.findViewById(R.id.myorder_orderStatus);

            holder.price = (TextView) convertView.findViewById(R.id.myorder_orderPrice);
            holder.itemCount = (TextView) convertView.findViewById(R.id.myorder_orderItemCount);
            holder.shipDate = (TextView) convertView.findViewById(R.id.myorder_eShipDate);
            holder.shipCost = (TextView) convertView.findViewById(R.id.myorder_shippingCost);
            holder.deliverDate = (TextView) convertView.findViewById(R.id.myorder_eDelDate);
            holder.itemInfo = (LinearLayout) convertView.findViewById(R.id.myorder_itemInfo);
            holder.orderUPS = (LinearLayout) convertView.findViewById(R.id.myorder_orderItem_UPS_SETME);
            holder.taxCost = (TextView) convertView.findViewById(R.id.myorder_taxCost);
            holder.stageCompleted = (TextView) convertView.findViewById(R.id.myorder_stageCompleted);
            holder.stageInProgress = (TextView) convertView.findViewById(R.id.myorder_stageInProgress);
            // holder.myorder_item_title = (TextView) convertView.findViewById(R.id.myorder_item_title);
            // holder.myorder_item_dim = (TextView) convertView.findViewById(R.id.myorder_item_dim);
            // holder.myorder_item_price = (TextView) convertView.findViewById(R.id.myorder_item_price);
            // holder.myorder_item_quantity = (TextView) convertView.findViewById(R.id.myorder_item_quantity);
            // Save for reuse.
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject fillOrder = MyOrderActivity.listOfModels.get(position);

        holder.orderUPS.removeAllViews();
        final String orderId = fillOrder.getString("s_order_id");
        // Extract dates from the order information page.
        final OrderParser op = new OrderParser();


        if (fillOrder.getInt("status") == 1) {
            holder.status.setText("Order Processing (Paid)");
            final TextView iv = new TextView(convertView.getContext());
            iv.setText("Shipping UPS info not available yet ...");
            holder.orderUPS.addView(iv);
            UPSDataAvailable = true;
        } else if (fillOrder.getInt("status") == 2) {
            holder.status.setText("Order Placed Succesfully");
            UPSDataAvailable = true;
        } else if (fillOrder.getInt("status") == 10) {
            holder.status.setText("Order CANCELLED !!!");
            UPSDataAvailable = false;
        }

        holder.shipDate.setText("");
        holder.deliverDate.setText("");
        holder.stageCompleted.setText("");
        holder.stageInProgress.setText("");

        final Context con = convertView.getContext();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                holder.orderUPS.removeAllViews();
                holder.orderUPS.requestLayout();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                op.LoadHtmlPage(orderId);//"E74VTVTF");//// "6BNJGE2D");//
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Just put the estimated dates of the last item.
                // for test orders it is 0.
                if (op.EShipDate.size() != 0) {
                    holder.shipDate.setText(op.EShipDate.get(op.EShipDate.size() - 1));
                } else {
                    holder.shipDate.setText(Html.fromHtml("<b><font color='#000000'> Unavailable </font> </b>"));
                }
                if (op.EDeliveryDate.size() != 0) {
                    holder.deliverDate.setText("" + op.EDeliveryDate.get(op.EDeliveryDate.size() - 1));
                }

                holder.stageCompleted.setText(op.StepCompleted);
                holder.stageInProgress.setText(op.StepInProgress);

                //-----------------
                if (UPSDataAvailable) {
                    holder.orderUPS.removeAllViews();
                    for (int i = 0; i < op.UpsLinks.size(); i++) {
                        final int finalI = i;
                        Button btUps = new Button(con);
                        btUps.setText("Shipment " + i + 1);
                        btUps.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(op.UpsLinks.get(finalI)));
                                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                con.getApplicationContext().startActivity(browserIntent);


                            }
                        });

                        holder.orderUPS.addView(btUps);
                    }
                    holder.orderUPS.requestLayout();
                }
            }
        }.execute();


        // Now fill in the placeHolder with correct data.
        holder.title.setText(fillOrder.getObjectId());
        holder.price.setText("" + fillOrder.getDouble("charge_amount"));//  fillOrder.getDouble("sale_price"));
        holder.itemCount.setText("" + fillOrder.getInt("total_item_type_count"));
        holder.shipCost.setText("" + (fillOrder.getDouble("shipping_cost") + fillOrder.getDouble("shipping_cc_cost")));
        holder.taxCost.setText("" + fillOrder.getDouble("tax"));

        holder.itemInfo.removeAllViews();
        // Get all sub items in order

        itemDAta = ParseOperation.GetItemsPerOrder(fillOrder);


        for (int i = 0; i < itemDAta.size(); i++) {
            final ImageView iv = new ImageView(con);
            final TextView tvDims = new TextView(con);
            final TextView tvPrice = new TextView(con);
            final TextView tvQuantity = new TextView(con);
            final TextView tvTitle = new TextView(con);
            final LinearLayout textOnlyInfo = new LinearLayout(con);
            final LinearLayout rowMaker= new LinearLayout(con);
            rowMaker.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams paramRowMaker = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            paramRowMaker.gravity = Gravity.LEFT;
            rowMaker.setLayoutParams(paramRowMaker);

            textOnlyInfo.setOrientation(LinearLayout.VERTICAL);

            DisplayMetrics metrics = activity.getApplicationContext().getResources().getDisplayMetrics();

            int hw = getDPI(100, metrics);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(hw, hw);
            iv.setLayoutParams(params);

            textOnlyInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            tvTitle.setTypeface(null, Typeface.BOLD);

            if (itemDAta.get(i).getString("model_class_name").equals(ATAGS.TABLE_PARSE_MODEL)) {
                ParseObject refObj = itemDAta.get(i).getParseObject("model_id");
                //ImageDownloader.SetImageInView(refObj, "icon_0", iv);
                if (refObj != null) {
                    UrlImageViewHelper.setUrlDrawable(iv, refObj.getParseFile("icon_0").getUrl());
                    tvTitle.setText((i+1) + ". Title: " + refObj.getString("title"));
                } else {
                    tvDims.setText("-----------------------");
                    tvTitle.setText((i+1) + ". Title: " + "CANT FIND ITEM IMAGE/INFORMATION");
                    tvPrice.setText("------------------------");
                    holder.itemInfo.addView(tvDims);
                    holder.itemInfo.addView(tvTitle);
                    holder.itemInfo.addView(tvPrice);
                    continue;
                }


            } else {
                ParseObject refObj = itemDAta.get(i).getParseObject("usermade_model_id");
                if (refObj != null) {
                    UrlImageViewHelper.setUrlDrawable(iv, refObj.getParseFile("icon_0").getUrl());
                    tvTitle.setText((i+1) + ". Title: " + refObj.getString("title"));
                } else {
                    tvDims.setText("-----------------------");
                    tvTitle.setText((i+1) + ". Title: " + "CANT FIND THIS ITEM IMAGE/INFORMATION");
                    tvPrice.setText("------------------------");
                    holder.itemInfo.addView(tvDims);
                    holder.itemInfo.addView(tvTitle);
                    holder.itemInfo.addView(tvPrice);
                    continue;
                }
            }

            tvDims.setText("Dimensions: " + itemDAta.get(i).getString("dim"));
            tvQuantity.setText("Quantity: " + itemDAta.get(i).getDouble("quantity"));
            tvPrice.setText("Price (all " + itemDAta.get(i).getDouble("quantity") + "): " + Printable.MakePrintablePrice((float) itemDAta.get(i).getDouble("total_sale_price")));


            textOnlyInfo.addView(tvTitle);
            textOnlyInfo.addView(tvDims);
            textOnlyInfo.addView(tvPrice);
            textOnlyInfo.addView(tvQuantity);
            rowMaker.addView(iv);
            rowMaker.addView(textOnlyInfo);

            holder.itemInfo.addView(rowMaker);
            //   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            //   startActivity(browserIntent);


            // holder.myorder_item_dim.setText());
            // holder.myorder_item_price.setText();
            // holder.myorder_item_quantity.setText();


//            TextView newLine = new TextView(con);
//
//            // Add a new line at the end of the order information.
//            newLine.setText(" ------------------------------------- ");
//            holder.itemInfo.addView(newLine);
        }
        return convertView;
    }

    public static int getDPI(int size, DisplayMetrics metrics) {
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }
}