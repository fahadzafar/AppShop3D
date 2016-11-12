package shop3d.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import activity.shop3d.org.shop3d.R;
import shop3d.adapters.CartListViewAdapter;
import shop3d.beans.SaleItem;
import shop3d.util.ATAGS;
import shop3d.util.Helper;
import shop3d.util.Printable;
import shop3d.util.SPManager;

public class CartActivity extends AppCompatActivity {
    public static ListView listView;
    CartListViewAdapter adapter;
    Button BtnContinueShopping;
    Button BtnCheckout;
    static TextView IvTotalBillCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        IvTotalBillCost = (TextView) findViewById(R.id.shoppingCart_TotalCost);


        listView = (ListView) findViewById(R.id.cart_listView);
        adapter = new CartListViewAdapter(this);
        listView.setAdapter(adapter);
        listView.requestLayout();

        BtnContinueShopping = (Button) findViewById(R.id.shopcart_BtnKeepShopping);
        BtnContinueShopping.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        BtnCheckout = (Button) findViewById(R.id.shopcart_BtnCheckout);
        BtnCheckout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {
                    // Place the total order tiem cost in the bundle.
                    Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                    String[] arr = IvTotalBillCost.getText().toString().split(" ");
                    float totalCost = Float.parseFloat(arr[0]);
                    if (totalCost != 0) {
                        intent.putExtra("total_order_item_cost", totalCost);
                        startActivityForResult(intent, ATAGS.USER_PLACED_ORDER);

                    } else {
                        Helper.ShowDialogue("Cant proceed: ", "Cart is empty", getApplicationContext());
                    }
                    // Call the checkout-shipping activity here
                } catch (Exception er) {
                    Helper.ShowDialogue("Cant proceed ", "Cost error", getApplicationContext());
                }
                //finish();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ATAGS.USER_PLACED_ORDER) {
            // If the activity exiting chain is coming through because hte user succesfully
            // placed an order, send him to the main menu.
            if (SPManager.CartObjects.size() == 0)
                finish();
        }

    }

    public static void UpdateTotalCost() {
        int totalItems = 0;
        float totalPrice = 0;
        for (int i = 0; i < SPManager.CartObjects.size(); i++) {
            SaleItem si = SPManager.CartObjects.get(i);
            totalItems = si.Quantity;

           // if (si.item.has("original_model_id"))
            totalPrice += si.Quantity * si.SalePrice; // Printable.GetPriceInInt(si.item.getParseObject("original_model_id"), si.SizeIndex);
         //   else
              //  totalPrice += si.Quantity * // Printable.GetPriceInInt(si.item, si.SizeIndex);
        }
        IvTotalBillCost.setText(Printable.MakePrintablePrice(totalPrice));

    }

    public static void RefreshCart() {
        listView.invalidateViews();
        listView.requestLayout();
        UpdateTotalCost();
    }
}
