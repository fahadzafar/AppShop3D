package shop3d.extras;

import android.content.Context;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shop3d.activity.CheckoutActivity;
import shop3d.util.Helper;

/**
 * Created by Fahad on 11/22/2015.
 */
public class StripeOperation {
    // Just checking to see if the customer id is present or not.
    // Use for testing.
    public static Boolean IsCustomerRegistered(String cusId) {
        boolean ans = false;
        try {
            Customer retrievedCustomer = Customer.retrieve(cusId);
            ans = true;
        } catch (Exception er) {

        }
        return ans;
    }

    // Create the Stripe customer so he can check out faster.
    public static String CreateCustomer(String cusDescription) {
        Map<String, Object> defaultCustomerParams = new HashMap<String, Object>();
        defaultCustomerParams.put("description", cusDescription);
        Customer createdCustomer = null;
        try {
            createdCustomer = Customer.create(defaultCustomerParams);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        if (createdCustomer == null) {
            return null;
        } else {
            return createdCustomer.getId();
        }
    }

    public static Customer RetrieveCustomer(String id) {
        Customer retrievedCustomer = null;
        try {
            retrievedCustomer = Customer.retrieve(id);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();

        }
        return retrievedCustomer;
    }

    public static ArrayList<String> GetLast4StringArr(List<Card> allCards) {

        ArrayList<String> displayCC = new ArrayList<String>();

        if (allCards.size() != 0) {
            for (int i = 0; i < allCards.size(); i++) {
                displayCC.add("X-X-X-" + allCards.get(i).getLast4());
            }
        }
        return displayCC;
    }

    public static List<Card> GetCCCard(Customer stripeCus) {


        ArrayList<Card> allCards = new ArrayList<Card>();
        try {
            List<ExternalAccount> cCards = stripeCus.getSources().getData();
            for (int i = 0; i < cCards.size(); i++) {
                allCards.add((Card)(cCards.get(i)));
            }

            return allCards;
        } catch (Exception er) {
            return new ArrayList<Card>();
        }
    }

    public static boolean CreateCreditCard(PaymentForm form, Context con) {
        try {
            final HashMap<String, Object> defaultCardParams = new HashMap<String, Object>();
/*
            // Zip fail
            defaultCardParams.put("number", "4000000000000036");
            defaultCardParams.put("address_zip", "94024");
            defaultCardParams.put("exp_month", 12);
            defaultCardParams.put("exp_year", 2015);
  */

            /*
            defaultCardParams.put("number", "4242424242424242");
            defaultCardParams.put("exp_month", 12);
            defaultCardParams.put("exp_year", 2016);
            defaultCardParams.put("cvc", "123");
            defaultCardParams.put("name", "J Bindings Cardholder");
            defaultCardParams.put("address_zip", "04105");*/

            defaultCardParams.put("number", form.getCardNumber());
            defaultCardParams.put("exp_month", form.getExpMonth());
            defaultCardParams.put("exp_year", form.getExpYear());
            defaultCardParams.put("cvc", form.getCvc());
            defaultCardParams.put("name", form.getCardHolderName());
            defaultCardParams.put("address_zip",form.getZipcode());

            Map<String, Object> creationParams = new HashMap<String, Object>();
            creationParams.put("card", defaultCardParams);
            Customer retrievedCustomer = CheckoutActivity.currStripeUser;
            Card newCard = retrievedCustomer.createCard(creationParams);

            String check = newCard.getAddressZipCheck();
            if (check.equals("fail")) {
                Helper.ShowDialogue("Error:", "Incorrect Zip code", con);
                newCard.delete();
                return false;
            }

          // Token tok =  Token.create(creationParams);

            return true;
        } catch (StripeException e) {

        }

        return false;
    }
}
