package shop3d.dialogue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import activity.shop3d.org.shop3d.R;

/**
 * Created by Fahad on 11/16/2015.
 */
public class AddAddressDialogue extends Dialog implements
        android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes, no;

    EditText evTitle, evLine1, evLine2, evCity, evZipcode, evState, evCountry;

    public AddAddressDialogue(Context con) {
        super(con);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_shipping_dialogue_1);



    }

    void AddThisAddress() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dlg_add_address_btn_ok:
                AddThisAddress();
                break;
            case R.id.dlg_add_address_btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}
