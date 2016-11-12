package shop3d.request;

import org.json.JSONObject;

import shop3d.util.SPManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fahad
 */
public class Req_GetPrice extends Req_Base {

    public Req_GetPrice(Req_Base.RunMode iRunMode) {
        super(iRunMode, "api/design/3D/price_by_uuid/");

    }

    public JSONObject SendPostRequest(String uuid, String scale) {
        JSONObject result = null;
        try {
            boolean continuPricing = true;
            while (continuPricing) {
                result = webb
                        .post(URLPost)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .param("uuid", uuid)
                        .param("quantitiy", "1")
                        .param("scale", scale)
                        .param("unit", SPManager.S_Units)
                        .param("currency", SPManager.S_Currency)
                        .param("productname", SPManager.S_PrintMaterial)
                        .asJsonObject()
                        .getBody();

                boolean gotPrice = false;
                try {
                    gotPrice = result.getJSONObject("body").getBoolean("success");
                } catch (Exception er) {

                }
                if (gotPrice == false) {
                    System.out.println("Waiting 15 seconds for price:");
                    Thread.sleep(15000);

                } else {
                    // The price has been received.
                    continuPricing = false;
                }

            }
            return result;

        } catch (Exception ex) {
            System.err.println(ex);
        }

        return null;
    }
}
