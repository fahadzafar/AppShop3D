package shop3d.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import activity.shop3d.org.shop3d.R;
import shop3d.activity.shipping.FragmentShippingDialogue;
import shop3d.adapters.AccountListViewAdapter;
import shop3d.parse.ParseOperation;
import shop3d.util.Printable;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    static ListView listView;
    private AccountListViewAdapter adapter;

    public static ArrayList<ParseObject> listOfModels;
    TextView username;
    Button btnAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Set the username.
        username = (TextView) findViewById(R.id.account_Username);
        username.setText(Printable.GetUsername());

        btnAddAddress = (Button) findViewById(R.id.account_btn_add_address);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentShippingDialogue.CallerActivity = "AccountActivity";
                FragmentShippingDialogue overlay = new FragmentShippingDialogue();
                overlay.show(fm, "Shipping");
            }
        });

        listView = (ListView) findViewById(R.id.account_listViewShipping);

        adapter = new AccountListViewAdapter(this);
        listOfModels = new ArrayList<ParseObject>();
        listView.setAdapter(adapter);
        RefreshModelListFirstTime();

    }

    public static void SetUserShippingAddresses(){
        ArrayList<ParseObject> allModels =
                ParseOperation.GetAllShippingAddress();
        listOfModels.clear();
        listOfModels.addAll(allModels);
    }
    public static void RefreshModelListFirstTime() {
       SetUserShippingAddresses();
        RefreshListView();
    }

    public static void RefreshListView() {
        listView.invalidateViews();
        listView.requestLayout();
    }

}
