package shop3d.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseObject;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.shipping.FragmentShippingDialogue;
import shop3d.beans.SaleItem;
import shop3d.extras.StripeOperation;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.Helper_Shipping;
import shop3d.util.Pricer;
import shop3d.util.Printable;
import shop3d.util.SPManager;
import shop3d.parse.ParseOperation;

public class CheckoutActivity extends AppCompatActivity {

    String shipTypeCode = "simple";
    TextView tvTotalOrderCost, tvTotalTaxCost, tvShipName, tvShipCost, tvOrderItemTotal, tvShipTitle, tvShipLine1, tvShipLine2, tvShipCity,
            tvShipState, tvShipZipcode, tvShipCountry;

    TextView tvCCTitle, tvCCName, tvCCNumber, tvCCExpiry;
    public final int ACTIVITY_CODE_PAYMENT = 1;

    float taxPercentage = 0;
    float taxOnOrder = 0;
    float cartFinalCost = 0;

    Button btnAddAddress, btnAddCC, btnCompleteCheckout;
    static Spinner spnrShip;
    int selectedShipping = -1;

    Spinner spnrShipOptionsPerCountry, spnrCC;
    static ArrayAdapter<String> shipSpnrArrayAdapter;
    ArrayAdapter<String> shipSpnrOptionPerCountryArrayAdapter;

    static ArrayList<ParseObject> ListShippingAddresses;
    static ArrayList<String> ListShippingAddressesString;

    // these arrays store the string
    static ArrayList<ParseObject> ListShipOptionsPerCountryParseArr;

    float totalOrderItemCost = 0;


    List<Card> allCards;
    ArrayList<String> SpnrCCData;
    ArrayAdapter<String> SpnrCCArrayAdapter;
    int selectedCC = -1;

    LinearLayout ll_cc;


    public static Customer currStripeUser = null;

    static void SetShippingTitles(ArrayList<ParseObject> ParsShippingObject) {
        ListShippingAddresses.clear();
        ListShippingAddressesString.clear();
        ListShippingAddresses = ParsShippingObject;
        for (int i = 0; i < ParsShippingObject.size(); i++) {
            ListShippingAddressesString.add(ParsShippingObject.get(i).getString("address_title"));
        }
    }

    void GetBundleItems() {
        // Get the bundle items coming from the previous activity
        // Set the order total cost before shipping.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cartFinalCost = extras.getFloat("total_order_item_cost");
            tvOrderItemTotal.setText(Printable.MakePrintablePrice(cartFinalCost));

            // If no shipping data is loaded, it happens where there is no shippnig address
            // then -1 sets shipping to 0,  otherwise use the index of the correct spinner
            // to get the cost.
            if (ListShipOptionsPerCountryParseArr.size() == 0)
                UpdateFinalCost(-1);
            else
                UpdateFinalCost(spnrShipOptionsPerCountry.getSelectedItemPosition());
        }
        // --------------------

    }


    void UpdateFinalCost(int position) {

        // When the app is loaded, first time with -1
        if (position == -1) {
            tvShipCost.setText(Printable.MakePrintablePrice(0));
            tvTotalOrderCost.setText(Printable.MakePrintablePrice(cartFinalCost));

        } else {
            float ship_cost = (float) ListShipOptionsPerCountryParseArr.get(position).getDouble("cost");
            float ship_cost_cc = (float) ListShipOptionsPerCountryParseArr.get(position).getDouble("cc_cost");
            shipTypeCode = ListShipOptionsPerCountryParseArr.get(position).getString("code");
            tvShipCost.setText(Printable.MakePrintablePrice(ship_cost + ship_cost_cc));


            float finalCost = cartFinalCost + ship_cost + ship_cost_cc;
            float taxCost = taxPercentage * finalCost;
            taxCost = taxCost + 3f / 100f * taxCost;
            taxOnOrder = (float) Helper.FormatDouble(taxCost, 2);
            tvTotalTaxCost.setText(Printable.MakePrintablePrice(taxCost));
            tvTotalOrderCost.setText(Printable.MakePrintablePrice(finalCost + taxCost));
        }
    }


    void SetupCCSpinner() {
        // Get the last 4 digits per card to populate in the spinner
        // for selection.
        SpnrCCData = StripeOperation.GetLast4StringArr(allCards);
        SpnrCCArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner, SpnrCCData);
        SpnrCCArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrCC.setAdapter(SpnrCCArrayAdapter);

        tvCCTitle.setText("");
        tvCCName.setText("");
        tvCCExpiry.setText("");
        tvCCNumber.setText("");


        spnrCC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCC = position;
                final Card selectedCard = allCards.get(position);

                currStripeUser.setDefaultCard(selectedCard.getId());

                tvCCTitle.setText(spnrCC.getSelectedItem().toString());
                tvCCName.setText(selectedCard.getName());
                tvCCExpiry.setText(selectedCard.getExpMonth() + "/" + selectedCard.getExpYear());
                tvCCNumber.setText(selectedCard.getLast4());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        spnrShip = (Spinner) findViewById(R.id.checkout_spinner_Shipping_Addresses);

        ListShippingAddressesString = new ArrayList<String>();
        ListShippingAddresses = new ArrayList<ParseObject>();
        ListShipOptionsPerCountryParseArr = new ArrayList<ParseObject>();

        ll_cc = (LinearLayout) findViewById(R.id.checkout_cc_layout);
        tvShipCost = (TextView) findViewById(R.id.checkout_shipping_option_cost);
        tvOrderItemTotal = (TextView) findViewById(R.id.checkout_total_order_item_cost);
        tvShipTitle = (TextView) findViewById(R.id.checkout_ShippingTitle);
        tvShipName = (TextView) findViewById(R.id.checkout_ShippingName);
        tvShipLine1 = (TextView) findViewById(R.id.checkout_ShippingLine1);
        tvShipLine2 = (TextView) findViewById(R.id.checkout_ShippingLine2);
        tvShipCity = (TextView) findViewById(R.id.checkout_ShippingCity);
        tvShipState = (TextView) findViewById(R.id.checkout_ShippingState);
        tvShipZipcode = (TextView) findViewById(R.id.checkout_ShippingZipcode);
        tvShipCountry = (TextView) findViewById(R.id.checkout_ShippingCountry);
        tvTotalOrderCost = (TextView) findViewById(R.id.checkout_total_order_cost);
        tvTotalTaxCost = (TextView) findViewById(R.id.checkout_total_tax_cost);

        tvCCTitle = (TextView) findViewById(R.id.checkout_cc_title);
        tvCCName = (TextView) findViewById(R.id.checkout_cc_name);
        tvCCExpiry = (TextView) findViewById(R.id.checkout_cc_expiery);
        tvCCNumber = (TextView) findViewById(R.id.checkout_cc_number);

        SetShipData(-1);

        // --- Load the user credit cards --------------------
        spnrCC = (Spinner) findViewById(R.id.checkout_spinner_cc);
        new LoadUserCC().execute();
        // -------------------------------------

        // This is the secondary spinner. It shows the options per country
        // like regular and express.
        spnrShipOptionsPerCountry = (Spinner) findViewById(R.id.checkout_spinner_shiping_options);
        spnrShipOptionsPerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (ListShipOptionsPerCountryParseArr.size() != 0) {
                    UpdateFinalCost(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        GetBundleItems();


        SetShippingTitles(ParseOperation.GetAllShippingAddress());
        shipSpnrArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.item_spinner, ListShippingAddressesString);
        shipSpnrArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrShip.setAdapter(shipSpnrArrayAdapter);

        spnrShip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedShipping = position;
                SetShipData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        // ------ Add CC Button
        btnAddCC = (Button) findViewById(R.id.checkout_btn_add_cc);
        btnAddCC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent addPayment = new Intent(CheckoutActivity.this, PaymentActivity.class);
                startActivityForResult(addPayment, ACTIVITY_CODE_PAYMENT);
            }


        });

        // ------ Add Shipping Button
        btnAddAddress = (Button) findViewById(R.id.checkout_btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentShippingDialogue.CallerActivity = "CheckoutActivity";
                FragmentShippingDialogue overlay = new FragmentShippingDialogue();
                overlay.show(fm, "Shipping");
            }
        });
        // ------------ Complete Checkout

        btnCompleteCheckout = (Button) findViewById(R.id.checkout_btn_complete_checkout);
        btnCompleteCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Check to see that the spinnerCC has a creadit card selected.
                if (selectedCC == -1 || SPManager.HOT_TESTING) {
                    Helper.ShowDialogue("Error: ", "No card selected", getApplicationContext());
                } else if (selectedShipping == -1 || SPManager.HOT_TESTING) {
                    Helper.ShowDialogue("Error: ", "No shipping selected", getApplicationContext());
                } else {
                    // if (allCards.get(spnrCC.getSelectedItemPosition()).getAddressZipCheck().equals("pass")) {
                    PlaceOrder();
                    // } else {
                    //   Helper.ShowDialogue("Error: ", "Incorrect zipcode entry for the card.", getApplicationContext());
                    //  }
                }
  /*
                // Get Stripe Customer
                // Get his selected Card.
                Card usedCard =
                new Stripe().createToken(
                        newCard,
                        SPManager.STRIPE_PUBLISHABLE_KEY,
                        new com.stripe.android.TokenCallback() {
                            public void onError(Exception error) {
                                handleError(error.getLocalizedMessage());
                                finishProgress();
                            }

                            @Override
                            public void onSuccess(Token token) {
                                finishProgress();
                            }
                        });*/
            }
        });


    }

    ProgressDialog pDialog;
    Exception cardException = null;

    // -------------------- PLACE THE ORDER
    void PlaceOrder() {
        btnCompleteCheckout.setEnabled(false);

        // Verify the creadit card and charge first.

        boolean verifyCC = true;


        // Creates a complete shipping order item.
        if (verifyCC == true) {
            final ParseObject mainOrder = new ParseObject("Order");

            // Do this to get an order pointer.
            try {
                mainOrder.save();
            } catch (Exception er) {
                Helper.ShowDialogue("Error: ", er.getMessage(), getApplicationContext());
            }

            mainOrder.put("user_id", SPManager.current_user);
            // --------- THIS IS COMING FROM SHIPPING RATES --------------------------------------
            // The cost of shipping.
            double shipCost = ListShipOptionsPerCountryParseArr.get(spnrShipOptionsPerCountry.getSelectedItemPosition()).getDouble("cost");
            mainOrder.put("shipping_cost", shipCost);

            // The credit card charge cost on shipping.
            double shipCCCost = ListShipOptionsPerCountryParseArr.get(spnrShipOptionsPerCountry.getSelectedItemPosition()).getDouble("cc_cost");
            mainOrder.put("shipping_cc_cost", shipCCCost);

            // Shipping RATE, for the specific country
            mainOrder.put("shipping_rate_id", ListShipOptionsPerCountryParseArr.get(spnrShipOptionsPerCountry.getSelectedItemPosition()));
            //----------------------------------------

            mainOrder.put("shipping_address_id", ListShippingAddresses.get(selectedShipping));

            // Calculate full cost of items
            double total_sale_price = 0;
            double total_scu_cost = 0;
            int totalItemCount = 0;
            for (int i = 0; i < SPManager.CartObjects.size(); i++) {

                SaleItem si = SPManager.CartObjects.get(i);

                // Create the new order_item
                ParseObject itemObj = new ParseObject("Order_Item");

                // Get the reference to the original model object since we are accessing price etc.
                // Having a Usermade_Model reference is incorrect.
                ParseObject actualObject = null;
                if (si.item.getClassName().equals(ATAGS.TABLE_PARSE_MODEL)) {
                    actualObject = si.item;
                    itemObj.put("model_id", si.item);
                } else if (si.item.getClassName().equals(ATAGS.TABLE_PARSE_MODEL_USERMADE)) {
                    itemObj.put("usermade_model_id", si.item);
                    actualObject = si.item.getParseObject("original_model_id");
                }

                // -------------- STart filling out orderitem fields.
                itemObj.put("model_class_name", si.item.getClassName());


                total_scu_cost += (si.PrintPrice * si.Quantity);
                total_sale_price += (si.SalePrice * si.Quantity);
                totalItemCount += si.Quantity;

                double itemCCCost = Helper.FormatDouble(total_sale_price * 2.7 / 100.0 + .30, 2);

                // ----------- Make Order_Item per object in cart.
                itemObj.put("order_id", mainOrder);


                itemObj.put("quantity", si.Quantity);
                itemObj.put("total_sale_price", total_sale_price);
                itemObj.put("total_print_cost", total_scu_cost);
                itemObj.put("total_profit", Helper.FormatDouble(total_sale_price - total_scu_cost - itemCCCost, 2));
                itemObj.put("scale_index", si.SizeIndex);
                itemObj.put("cc_cost", itemCCCost);

                itemObj.put("dim", Printable.GetSize(actualObject, si.SizeIndex));
                itemObj.put("material", "color_plastic");
                try {


                    itemObj.save();
                } catch (Exception er) {
                    Helper.ShowDialogue("Error:", er.getMessage(), getApplicationContext());
                }
            } // End of order items... now go complete the main 1 order record.
            double ccCost = Helper.FormatDouble(total_sale_price * 2.7 / 100.0 + .30, 2);
            mainOrder.put("print_cc_cost", ccCost);
            mainOrder.put("total_cc_cost", ccCost + shipCCCost);
            mainOrder.put("print_cost", total_scu_cost);
            mainOrder.put("sale_price", total_sale_price);
            mainOrder.put("total_item_count", totalItemCount);
            mainOrder.put("total_item_type_count", SPManager.CartObjects.size());
            mainOrder.put("ship_type_code", shipTypeCode);

            mainOrder.put("status", 1);

            // Profit =           sale-price + ship-price + ship-cc-charge    - printing-cost - ccCostOnSalePrice - ccShippingCost - ccCostOnShipping
            double plus = total_sale_price + shipCost + shipCCCost + taxOnOrder;
            double minus = total_scu_cost + ccCost + shipCost + shipCCCost + taxOnOrder;
            double minProfit = plus - minus;
            final double chargeAmount = (Helper.FormatDouble(total_sale_price + shipCost + shipCCCost + taxOnOrder, 2));
            mainOrder.put("min_profit", minProfit);
            mainOrder.put("tax", taxOnOrder);
            mainOrder.put("charge_amount", chargeAmount);
            if (mainOrder != null)

                try {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {


                            // Showing progress dialog before sending http request
                            pDialog = new ProgressDialog(
                                    CheckoutActivity.this);
                            pDialog.setMessage("Placing Order, please wait..");
                            pDialog.setIndeterminate(true);
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                cardException = null;
                                // Charge the Customer instead of the card
                                HashMap<String, Object> chargeParams = new HashMap<String, Object>();
                                chargeParams.put("amount", (int) (chargeAmount * 100)); // amount in cents, again
                                chargeParams.put("currency", "usd");
                                chargeParams.put("customer", currStripeUser.getId());

                                Charge cat = Charge.create(chargeParams);

                                if (cat.getStatus().equals("succeeded")) {
                                    mainOrder.put("stripe_charge_id", cat.getId());
                                    mainOrder.save();


                                    // This will reset the app to default.
                                    SPManager.CartObjects.clear();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Helper.ShowDialogue("Congrats: ", "Order placed successfully", getApplicationContext());
                                        }
                                    });

                                    finish();

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Helper.ShowDialogue("Error: ", " Could not charge card", getApplicationContext());
                                        }
                                    });

                                }


                            } catch (Exception er) {
                                cardException = er;
                                //  Helper.ShowDialogue("Error:", er.getMessage(), getApplicationContext());
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (cardException != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.ShowDialogue("Error:", cardException.getMessage(), getApplicationContext());
                                    }
                                });

                            }
                            btnCompleteCheckout.setEnabled(true);
                            pDialog.dismiss();
                        }
                    }.execute();

                    return;
                } catch (Exception er) {
                    final Exception ero = er;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Helper.ShowDialogue("Error: ", ero.getMessage(), getApplicationContext());
                        }
                    });
                }
            btnCompleteCheckout.setEnabled(true);
        }
    }


    //--------------------------------------


    // --------------------- INNER ASYNC CLASS
    class LoadUserCC extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Get the Stripe customer information.
            currStripeUser = StripeOperation.RetrieveCustomer(SPManager.current_user.getString("stripe_id"));
            allCards = StripeOperation.GetCCCard(currStripeUser);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SetupCCSpinner();
            ll_cc.setVisibility(View.VISIBLE);
        }
    }
    // --------------------------------


    /* Called when the second activity's finished */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_CODE_PAYMENT: {
                ll_cc.setVisibility(View.INVISIBLE);
                new LoadUserCC().execute();
                break;
            }
        }
    }

    public static void UpdateShippingSpinner() {
        SetShippingTitles(ParseOperation.GetAllShippingAddress());
        shipSpnrArrayAdapter.notifyDataSetChanged();

        if (ListShippingAddressesString.size() != 0)
            spnrShip.setSelection(ListShippingAddressesString.size() - 1);

    }

    void SetShipData(int index) {
        if (index == -1) {
            String empty = "";
            tvShipTitle.setText(empty);
            tvShipName.setText(empty);
            tvShipLine1.setText(empty);
            tvShipLine2.setText(empty);
            tvShipCity.setText(empty);
            tvShipState.setText(empty);
            tvShipZipcode.setText(empty);
            tvShipCountry.setText(empty);
            tvShipCost.setText(Printable.MakePrintablePrice(0));

            // Tax si based on country.
            tvTotalTaxCost.setText(Printable.MakePrintablePrice(0));

        } else {
            ParseObject selectedShipAdrs = ListShippingAddresses.get(index);
            tvShipTitle.setText(selectedShipAdrs.getString("address_title"));
            tvShipName.setText(selectedShipAdrs.getString("address_name"));
            tvShipLine1.setText(selectedShipAdrs.getString("address_line_1"));
            tvShipLine2.setText(selectedShipAdrs.getString("address_line_2"));
            tvShipCity.setText(selectedShipAdrs.getString("city"));
            tvShipState.setText(selectedShipAdrs.getString("state"));
            tvShipZipcode.setText(selectedShipAdrs.getString("zipcode"));

            String countryText = selectedShipAdrs.getString("country");
            tvShipCountry.setText(countryText);

            // Set the tax
            taxPercentage = Helper_Shipping.GetTax(countryText, getResources());
            tvTotalTaxCost.setText(taxPercentage + "");


            // Then find only that country specific option
            ListShipOptionsPerCountryParseArr = Helper_Shipping.ExtractAllCountryShippingOptionsParseObj(countryText, getResources());
            ArrayList<String> displayOptions = Helper_Shipping.ExtractAllCountryShippingOptionsInStrings(ListShipOptionsPerCountryParseArr);

            shipSpnrOptionPerCountryArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, displayOptions);
            shipSpnrOptionPerCountryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrShipOptionsPerCountry.setAdapter(shipSpnrOptionPerCountryArrayAdapter);

        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        GetBundleItems();
    }
}
