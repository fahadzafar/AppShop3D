/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop3d.request;

import com.goebl.david.Webb;

/**
 *
 * @author Fahad
 */
public class Req_Base {

    RunMode CurrentRunMode;
    String URLBaseSculpteo;
    String URLPost;
    String WebAPIDIR;
    private final String USER_AGENT = "Shop3D";
    private final String SANDBOX_ID = "I1Pjyb6Rtkc%3D";
    private final String UserLogin = "";
    private final String UserPassword = "";

    Webb webb;

    public Req_Base(RunMode iRunMode, String iWebApiDir) {
        
        webb = Webb.create();  
        WebAPIDIR = iWebApiDir;
        // Set the correct URL for TEST or Production
        if (iRunMode == RunMode.PRODUCTION) {
            URLBaseSculpteo = "http://www.sculpteo.com/en/";
            URLPost = URLBaseSculpteo + WebAPIDIR;
        } else {
            URLBaseSculpteo = "http://sandbox.sculpteo.com/en/";
            URLPost = URLBaseSculpteo + WebAPIDIR + "?sandbox="+SANDBOX_ID;
        }
    }

    /*
    public JSONArray SendPostRequest() {
        JSONArray result = webb
                .get(URLPost)
                .retry(1, false) // at most one retry, don't do exponential backoff
                .asJsonArray()
                .getBody();

        String data = result.toString();
        return result;
    }
*/
    // Used to figure out if running in the sandbox environment or production
    public enum RunMode {

        TEST,
        PRODUCTION
    }

}
