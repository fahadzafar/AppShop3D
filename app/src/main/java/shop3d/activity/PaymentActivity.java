package shop3d.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import activity.shop3d.org.shop3d.R;
import shop3d.extras.ErrorDialogFragment;
import shop3d.extras.PaymentForm;
import shop3d.extras.ProgressDialogFragment;
import shop3d.extras.StripeOperation;
import shop3d.util.Helper;

public class PaymentActivity extends FragmentActivity {

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */

    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }

    public static boolean createCardReturn = false;

    // This is called everytime the user enters a new creadit card number.
    public void saveCreditCard(final PaymentForm form) {
        // Create the card from teh form data.


        startProgress();


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                createCardReturn = false;
                try {
                    // Since the card is valid, add it to the user account at stripe.
                    createCardReturn = StripeOperation.CreateCreditCard(form, getApplicationContext());

                } catch (Exception er) {
                    Helper.ShowDialogue("Error: ", er.getMessage(), getApplicationContext());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                finishProgress();
                if (createCardReturn) {
                    Helper.ShowDialogue("Success: ", " New card added", getApplicationContext());
                    finish();
                } else {
                    Helper.ShowDialogue("Error: ", "incorrect card information", getApplicationContext());
                }
            }
        }.execute();


    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }
}

