package shop3d.extras;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import activity.shop3d.org.shop3d.R;
import shop3d.extras.PaymentForm;

import shop3d.activity.PaymentActivity;

public class PaymentFormFragment extends Fragment implements PaymentForm {

    Button saveButton;
    EditText cardNumber;
    EditText cardHolderName;
    EditText cvc;
    Spinner monthSpinner;
    Spinner yearSpinner;
    EditText zipcodeNumber;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_form_fragment, container, false);

        this.saveButton = (Button) view.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveForm(view);
            }
        });

        this.cardHolderName = (EditText) view.findViewById(R.id.cardholder_name);
        this.cardNumber = (EditText) view.findViewById(R.id.number);
        this.cvc = (EditText) view.findViewById(R.id.cvc);
        this.monthSpinner = (Spinner) view.findViewById(R.id.expMonth);
        this.yearSpinner = (Spinner) view.findViewById(R.id.expYear);
        this.zipcodeNumber = (EditText) view.findViewById(R.id.zipcode);





        return view;
    }


    @Override
    public String getCardHolderName() {
        return this.cardHolderName.getText().toString();
    }

    @Override
    public String getCardNumber() {
        return this.cardNumber.getText().toString();
    }

    @Override
    public String getCvc() {
        return this.cvc.getText().toString();
    }

    @Override
    public Integer getExpMonth() {
        return getInteger(this.monthSpinner);
    }

    @Override
    public Integer getExpYear() {
        return getInteger(this.yearSpinner);
    }

    @Override
    public String getZipcode() {
        return this.zipcodeNumber.getText().toString();
    }

    public void saveForm(View button) {
        ((PaymentActivity)getActivity()).saveCreditCard(this);
    }

    private Integer getInteger(Spinner spinner) {
    	try {
    		return Integer.parseInt(spinner.getSelectedItem().toString());
    	} catch (NumberFormatException e) {
    		return 0;
    	}
    }
}
