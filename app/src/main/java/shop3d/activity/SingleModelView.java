package shop3d.activity;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
//import com.socialize.ActionBarUtils;
//import com.socialize.Socialize;
//import com.socialize.entity.Entity;


import java.util.ArrayList;
import java.util.List;

import activity.shop3d.org.shop3d.R;

import shop3d.activity.ImageDisplay.FragmentDisplayDialogue;
import shop3d.activity.login.FragmentLoginDialogue;
import shop3d.beans.SaleItem;
import shop3d.util.Helper;
import shop3d.util.PriceGetter;
import shop3d.util.Printable;
import shop3d.util.SPManager;

public class SingleModelView extends AppCompatActivity {
    PriceGetter pg;
    ArrayAdapter<String> spnrScaleIndexAdapter;

    //int SelectedIndex = -1;
    //public static ParseObject actualObject;
    String iconNumberOnDisplay = "icon_0";
    Spinner spnrScaleIndex;
    RatingBar rateBar;

    // Holds the returned price for a certain size selected using the spinner.
    String sSalePriceHolder = "";
    double dPrintPriceHolder = 0;
    double dSalePriceHolder = 0;

    Button btnAddFavorite, btnCustomize, btnBuy, btnColorChart;
    TextView tvTitle, tvPrice, tvModifiers, tvDescription, tvCategory, tvMaterial;

    //ParseObject realModel = null;

    // This string holds the name of the caller activity.
    // String RequesterActivity = "";

    void ShowHideFavButton() {
        btnAddFavorite.setVisibility(View.GONE);

        if (SPManager.current_user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
            query.whereEqualTo("user_id", SPManager.current_user);
            query.whereEqualTo("model_id", SPManager.PassToSMV.ModelParent);

            //---- second query
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Favorite");
            query2.whereEqualTo("user_id", SPManager.current_user);
            query2.whereEqualTo("usermade_model_id", SPManager.PassToSMV.ModelChild);

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(query);
            queries.add(query2);


            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> results, ParseException e) {
                    if (results == null) {
                        // Means that the favorite does not exist
                        btnAddFavorite.setVisibility(View.VISIBLE);
                    }
                    if (results.size() == 0) {
                        // Means that the favorite does not exist
                        btnAddFavorite.setVisibility(View.VISIBLE);
                    }
                }
            });


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_model_view);

        rateBar = (RatingBar) findViewById(R.id.smv_itemRatingBar);
        btnColorChart = (Button) findViewById(R.id.smv_BtnColorChart);
        btnAddFavorite = (Button) findViewById(R.id.smv_BtnAddFavorite);
        btnCustomize = (Button) findViewById(R.id.smv_BtnCustomize);
        btnCustomize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DetailedViewModelActivity.RESETACTIVITY = true;
                Helper.LaunchActivity(getApplicationContext(),
                        DetailedViewModelActivity.class);
                finish();
            }
        });


        btnColorChart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentDisplayDialogue.TapTozoom = true;
                FragmentDisplayDialogue.SELECTEDARRAY = 1;
                FragmentDisplayDialogue overlay = new FragmentDisplayDialogue();
                overlay.show(fm, "Color Chart");

            }
        });

        btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnAddFavorite.setEnabled(false);
                ParseObject NewFav = new ParseObject("Favorite");
                NewFav.put("user_id", SPManager.current_user);
                if (SPManager.PassToSMV.HasParent)
                    NewFav.put("usermade_model_id", SPManager.PassToSMV.ModelChild);
                else {
                    NewFav.put("model_id", SPManager.PassToSMV.ModelParent);
                }
                NewFav.put("model_class_name", SPManager.PassToSMV.GetFavoriteClassName());
                try {
                    NewFav.save();
                    Helper.ShowDialogue("Success:", "Added to favorites list", getApplicationContext());
                    btnAddFavorite.setVisibility(View.GONE);
                } catch (Exception er) {
                    Helper.ShowDialogue("Failure:", er.getMessage(), getApplicationContext());

                }

            }
        });

        spnrScaleIndex = (Spinner) findViewById(R.id.smv_sp_sizes);

        tvTitle = (TextView) findViewById(R.id.smv_itemTitle);
        tvDescription = (TextView) findViewById(R.id.smv_itemDescription);
        tvCategory = (TextView) findViewById(R.id.smv_itemCategory);
        tvMaterial = (TextView) findViewById(R.id.smv_itemMaterial);
        tvModifiers = (TextView) findViewById(R.id.smv_itemModifiers);
        tvPrice = (TextView) findViewById(R.id.smv_itemPrice);


        // actualObject =   SPManager.PassToSMV.ModelParent;//extras.getInt(ATAGS.PASSVALUE_SELECTED_INDEX);
        // realModel = SPManager.PassToSMV.ModelParent;

        UpdateImgScrollView();

        pg = new PriceGetter();
        spnrScaleIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                final int selectedPos = position;
                tvPrice.setText("Fetching price ...");
                SetAvailableWhenGettingPrice(false);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        dPrintPriceHolder = pg.GetSinglePrice(SPManager.PassToSMV.ModelParent, selectedPos);

                        // Make the price including profit here.
                        dSalePriceHolder = PriceGetter.MakeSalePrice(dPrintPriceHolder, SPManager.PassToSMV.ModelParent.getDouble("percentage_profit"));
                        sSalePriceHolder = Printable.MakePrintablePrice((float) dSalePriceHolder);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        try {
                            tvPrice.setText(sSalePriceHolder);
                        } catch (Exception er) {

                            int x = 0;
                        }
                        SetAvailableWhenGettingPrice(true);
                    }
                }.execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        //    if (RequesterActivity.equals(ATAGS.TAG_ACTIVITY_MYMODEL)) {

        // Currently, you cannot customize and already custom object.
        // You cannot facorite this object either.
        //   btnCustomize.setVisibility(View.GONE);

        btnCustomize.setVisibility(View.VISIBLE);
        if (SPManager.PassToSMV.HasParent) {
            // This is for ATAGS.TAG_MAIN_ACTIVITY
            tvTitle.setText(Printable.GetTitle(SPManager.PassToSMV.ModelChild));
        } else {
            tvTitle.setText(Printable.GetTitle(SPManager.PassToSMV.ModelParent));
        }
        tvDescription.setText(Printable.GetDescription(SPManager.PassToSMV.ModelParent));
        tvCategory.setText(Printable.GetCategory(SPManager.PassToSMV.ModelParent));
        tvModifiers.setText(Printable.GetModifersInt(SPManager.PassToSMV.ModelParent) + "");

        // tvPrice.setText(Printable.GetPrice(actualObject, 0));
        rateBar.setRating(Printable.GetRatingsFloat(SPManager.PassToSMV.ModelParent));


        ArrayAdapter<String> spnrScaleIndexAdapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner, Printable.GetSizeArray(SPManager.PassToSMV.ModelParent));
        spnrScaleIndexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrScaleIndex.setAdapter(spnrScaleIndexAdapter);
        spnrScaleIndexAdapter.setNotifyOnChange(true);
        ShowHideFavButton();


        btnBuy = (Button) findViewById(R.id.smv_BtnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // When the buy button is pressed, add to cart if the user is logged in
                if (SPManager.current_user == null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentLoginDialogue overlay = new FragmentLoginDialogue();
                    FragmentLoginDialogue.CallerActivity = "SingleModelViewActivity";
                    overlay.show(fm, "register/sign-in");
                } else {
                    AddToCart();
                    Helper.LaunchActivity(getApplication(), CartActivity.class);
                    finish();
                }

            }
        });

    }


    public void SetThisIndex() {
         /*   tvPrice.setText(allPrices.get(spnrScaleOptions.getSelectedItemPosition()));
        else {
            tvPrice.setText(ans);
        }*/
    }

    void AddToCart() {
        boolean updatedExistingRecord = false;
        String searchId = "";

        searchId = SPManager.PassToSMV.ModelParent.getObjectId();


        if (SPManager.PassToSMV.HasParent == false) {
            searchId = SPManager.PassToSMV.ModelParent.getObjectId();
        } else {
            searchId = SPManager.PassToSMV.ModelChild.getParseObject("original_model_id").getObjectId();
        }


        // -------------------- See if its already present in cart or newly added.

        for (int i = 0; i < SPManager.CartObjects.size(); i++) {
            // Check if that items exists in the cart already.
            if (SPManager.CartObjects.get(i).item.getObjectId().equals(searchId)) {
                // If they have the same scale , only add to count.
                if (SPManager.CartObjects.get(i).SizeIndex == spnrScaleIndex.getSelectedItemPosition()) {
                    SPManager.CartObjects.get(i).Quantity++;
                    updatedExistingRecord = true;
                }
            }
        }
        if (updatedExistingRecord == false) {
            // If a new entry is to be made, enter the object referenced by the favorite record
            // Otherwise its the Model or Model_Usermade object.

            // Get the price first

            if (SPManager.PassToSMV.HasParent == false)
                SPManager.CartObjects.add(new SaleItem(SPManager.PassToSMV.ModelParent,
                        spnrScaleIndex.getSelectedItemPosition(), 1, dPrintPriceHolder, dSalePriceHolder, SPManager.PassToSMV.ModelParent.getDouble("percentage_profit")));
            else
                SPManager.CartObjects.add(new SaleItem(SPManager.PassToSMV.ModelChild,
                        spnrScaleIndex.getSelectedItemPosition(), 1, dPrintPriceHolder, dSalePriceHolder, SPManager.PassToSMV.ModelParent.getDouble("percentage_profit")));
        }

    }

    void SetAvailableWhenGettingPrice(boolean viz) {
        btnCustomize.setVisibility(View.VISIBLE);
        if (viz == false) {
            btnBuy.setVisibility(View.INVISIBLE);
           // btnCustomize.setVisibility(View.INVISIBLE);
        } else {
            btnBuy.setVisibility(View.VISIBLE);
            btnCustomize.setVisibility(View.VISIBLE);
        }

    }

    ImageView iv = null;

    void UpdateImgScrollView() {
        int i = 0;
        ImageView firstIconSmall = (ImageView) findViewById(R.id.itemIcon_0);
        if (SPManager.PassToSMV.HasParent) {
            UrlImageViewHelper.setUrlDrawable(firstIconSmall, SPManager.PassToSMV.ModelChild.getParseFile("icon_0").getUrl());
            // This means its a my-model
            i = 1;
            ImageView iv = (ImageView) findViewById(R.id.itemIcon_0);
            iv.setVisibility(View.VISIBLE);
            /*iv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(),
                            FullScreenImageActivity.class);
                    Bitmap bm = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
                    SPManager.BmpForFullScreen = bm;

                    // in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_FRAGMENT_DISPLAY);
                    //iconNumberOnDisplay = "icon_0";
                    //  in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, RequesterActivity);
                    // in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, SelectedIndex);
                    // in.putExtra(ATAGS.PASSVALUE_ICON_NUMBER, iconNumberOnDisplay);
                    startActivity(in);
                }
            });*/

        } else {
            // IvBigDisplay.setImageBitmap(ImageCache.Get(actualObject.getObjectId(), "icon_0"));
            UrlImageViewHelper.setUrlDrawable(firstIconSmall, SPManager.PassToSMV.ModelParent.getParseFile("icon_0").getUrl());

            ImageView iv = (ImageView) findViewById(R.id.itemIcon_0);
            iv.setVisibility(View.VISIBLE);
            /*
            iv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bitmap bm = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();
                    SPManager.BmpForFullScreen = bm;

                    Intent in = new Intent(getApplicationContext(),
                            FullScreenImageActivity.class);
                    // in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_FRAGMENT_DISPLAY);
                    // iconNumberOnDisplay = "icon_0";
                    // in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, RequesterActivity);
                    //  in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, SelectedIndex);
                    //in.putExtra(ATAGS.PASSVALUE_ICON_NUMBER, iconNumberOnDisplay);
                    startActivity(in);
                }
            });*/


        }


        // Only if the caller object is not from a MyModel, then download the other icons.
        for (; i < SPManager.PassToSMV.TotalIcons; i++) {


            if (i == 0) {
                iv = (ImageView) findViewById(R.id.itemIcon_0);

            }
            if (i == 1) {
                iv = (ImageView) findViewById(R.id.itemIcon_1);

            }
            if (i == 2) {
                iv = (ImageView) findViewById(R.id.itemIcon_2);

            }
            if (i == 3) {
                iv = (ImageView) findViewById(R.id.itemIcon_3);

            }
            if (i == 4) {
                iv = (ImageView) findViewById(R.id.itemIcon_4);

            }

            iv.setVisibility(View.VISIBLE);
          /*  iv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.itemIcon_0:
                            //IvBigDisplay.setImageBitmap(ImageCache.Get(actualObject.getObjectId(), "icon_0"));
                            //Intent in = new Intent(getApplicationContext(),
                            //      FullScreenImageActivity.class);
                            //in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, ATAGS.TAG_ACTIVITY_FRAGMENT_DISPLAY);

                            //UrlImageViewHelper.setUrlDrawable(IvBigDisplay, realModel.getParseFile("icon_0").getUrl());

                            //iconNumberOnDisplay = "icon_0";
                            break;

                        case R.id.itemIcon_1:
                            //UrlImageViewHelper.setUrlDrawable(IvBigDisplay, realModel.getParseFile("icon_1").getUrl());
                            // iconNumberOnDisplay = "icon_1";
                            break;

                        case R.id.itemIcon_2:
                            // UrlImageViewHelper.setUrlDrawable(IvBigDisplay, realModel.getParseFile("icon_2").getUrl());
                            // iconNumberOnDisplay = "icon_2";
                            break;

                        case R.id.itemIcon_3:
                            //IvBigDisplay.setImageBitmap(ImageCache.Get(actualObject.getObjectId(), "icon_3"));
                            //UrlImageViewHelper.setUrlDrawable(IvBigDisplay, realModel.getParseFile("icon_3").getUrl());

                            // iconNumberOnDisplay = "icon_3";
                            break;

                        case R.id.itemIcon_4:
                            // iconNumberOnDisplay = "icon_4";
                            //UrlImageViewHelper.setUrlDrawable(IvBigDisplay, realModel.getParseFile("icon_4").getUrl());
                            //IvBigDisplay.setImageBitmap(ImageCache.Get(actualObject.getObjectId(), "icon_4"));
                            break;
                        default:
                            throw new RuntimeException("Unknow button ID");
                    }


                    SPManager.BmpForFullScreen = iv.getDrawingCache();

                    Intent in = new Intent(getApplicationContext(),
                            FullScreenImageActivity.class);
                    // in.putExtra(ATAGS.PASSVALUE_CALLER_ACTIVITY, RequesterActivity);
                    //  in.putExtra(ATAGS.PASSVALUE_SELECTED_INDEX, SelectedIndex);
                    //in.putExtra(ATAGS.PASSVALUE_ICON_NUMBER, iconNumberOnDisplay);
                    startActivity(in);
                }
            });
*/

            try {
                // ImageDownloader.SetImageInView(actualObject,
                //        "icon_" + i, iv);
                UrlImageViewHelper.setUrlDrawable(iv, SPManager.PassToSMV.ModelParent.getParseFile("icon_" + i).getUrl());

            } catch (Exception er) {
                Helper.ShowDialogue("Image", "Unable to display", getApplicationContext());
            }

        }

        for (int j = i; j < SPManager.PassToSMV.ModelParent.getInt("total_icons"); j++) {
            ImageView iv = null;
            if (i == 0)
                iv = (ImageView) findViewById(R.id.itemIcon_0);
            if (i == 1)
                iv = (ImageView) findViewById(R.id.itemIcon_1);
            if (i == 2)
                iv = (ImageView) findViewById(R.id.itemIcon_2);
            if (i == 3)
                iv = (ImageView) findViewById(R.id.itemIcon_3);
            if (i == 4)
                iv = (ImageView) findViewById(R.id.itemIcon_4);


            iv.setVisibility(View.GONE);
            iv.getLayoutParams().height = 0;
            iv.getLayoutParams().width = 0;
            iv.requestLayout();
        }

        HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horScrollView);
        hsv.requestLayout();
    }

}
