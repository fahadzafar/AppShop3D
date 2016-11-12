package shop3d.activity.shipping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.parse.ParseObject;

import java.util.ArrayList;

import shop3d.activity.AccountActivity;
import shop3d.activity.CheckoutActivity;
import shop3d.parse.ParseOperation;
import shop3d.util.Helper;
import shop3d.util.SPManager;

import activity.shop3d.org.shop3d.R;


/**
 * A simple counterpart for tab1 layout...
 */
public class FragmentShippingDialogue_1 extends Fragment implements View.OnClickListener {
    public Button yes, no;
    EditText evTitle, evName, evLine1, evLine2, evCity, evZipcode, evState;
    Spinner spnrShipCountry;
    ArrayAdapter<String> adapterSpnrShipCountry;
    Context con;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (SPManager.ShippingRates.size() == 0) {
            ParseOperation.FillShippingRates();
        }
        View view = inflater.inflate(R.layout.fragment_shipping_dialogue_1, container, false);

        // Setup the shipping country spinner
        spnrShipCountry = (Spinner) view.findViewById(R.id.dlg_address_spnr_country);


        String[] intlCountries = getResources().getStringArray(R.array.iso_countries);
        adapterSpnrShipCountry = new ArrayAdapter<String>(
                view.getContext(), R.layout.item_spinner, intlCountries);
        adapterSpnrShipCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrShipCountry.setAdapter(adapterSpnrShipCountry);

        spnrShipCountry.setSelection(235);

        evTitle = (EditText) view.findViewById(R.id.dlg_add_address_title);
        evName = (EditText) view.findViewById(R.id.dlg_add_address_name);
        evLine1 = (EditText) view.findViewById(R.id.dlg_add_address_line1);
        evLine2 = (EditText) view.findViewById(R.id.dlg_add_address_line2);
        evCity = (EditText) view.findViewById(R.id.dlg_add_address_city);
        evZipcode = (EditText) view.findViewById(R.id.dlg_add_address_zipcode);
        evState = (EditText) view.findViewById(R.id.dlg_add_address_state);

        con = view.getContext();
        yes = (Button) view.findViewById(R.id.dlg_add_address_btn_ok);
        no = (Button) view.findViewById(R.id.dlg_add_address_btn_cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        return view;
    }

    public boolean EmptyCheck(EditText ev, String errorMsg) {
        if (Helper.isEmpty(ev.getText().toString())) {
            FragmentShippingDialogue.DisplayMessage("Incomplete: ", "Please enter a " + errorMsg + ", enter a \"-\" if zipcode is not applicable.",
                    con);
            return false;
        }
        return true;
    }

    // Checks sanity for everything and then tries to login.
    public boolean CheckNewAddress() {

        if (spnrShipCountry.getSelectedItemPosition() == -1) {
            return false;
        }

        return (EmptyCheck(evTitle, "Title") &&
                EmptyCheck(evName, "Name") &&
                EmptyCheck(evLine1, "Address line") &&
                EmptyCheck(evCity, "City") &&
                EmptyCheck(evState, "State") &&
                EmptyCheck(evZipcode, "Zipcode")
        );
    }

    void AddThisAddress() {

      //  String[] intlCountries = getResources().getStringArray(R.array.iso_test_countries);
     //   for (int i = 0; i < intlCountries.length; i++) {
            ParseObject NewAdd = new ParseObject("Shipping_Address");
          //  NewAdd.put("address_title", evTitle.getText().toString() + "- " + i);
            NewAdd.put("address_title", evTitle.getText().toString());
            NewAdd.put("address_name", evName.getText().toString());
            NewAdd.put("address_line_1", evLine1.getText().toString());
            NewAdd.put("address_line_2", evLine2.getText().toString());
            NewAdd.put("zipcode", evZipcode.getText().toString());
            NewAdd.put("city", evCity.getText().toString());
            NewAdd.put("state", evState.getText().toString());

            // DONT SPLIT COUNTRY CODE.
        //    NewAdd.put("country", intlCountries[i]);
            NewAdd.put("country", spnrShipCountry.getSelectedItem().toString());

            NewAdd.put("user_id", SPManager.current_user);
            try {
                NewAdd.save();
            } catch (Exception er) {
                FragmentShippingDialogue.DisplayMessage("Error: ", er.getMessage(),
                        con);
            }
      //  }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dlg_add_address_btn_ok:
                if (CheckNewAddress()) {
                    AddThisAddress();
                    if (FragmentShippingDialogue.CallerActivity.equals("AccountActivity"))
                        AccountActivity.RefreshModelListFirstTime();
                    else if (FragmentShippingDialogue.CallerActivity.equals("CheckoutActivity"))
                        CheckoutActivity.UpdateShippingSpinner();
                    FragmentShippingDialogue.dialog.dismiss();


                }
                break;
            case R.id.dlg_add_address_btn_cancel:
                FragmentShippingDialogue.dialog.dismiss();

                break;
            default:
                break;
        }

    }
}