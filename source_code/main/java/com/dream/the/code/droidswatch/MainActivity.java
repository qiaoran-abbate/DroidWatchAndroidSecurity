/*
 *  MainActivity.java
 *
 *  Version: MainActivity.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */


package com.dream.the.code.droidswatch;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Build;
import android.content.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.*;
import java.io.*;


/**
 * This class is the Welcome page and handles externals application calls (SafteyNetAPI and Rule
 * Engine), gathers all device facts and receives the final security evaluation.
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class MainActivity extends AppCompatActivity {

    private String mResult = null;
    byte[] nonce = new byte[32];
    private boolean isSafteyNetAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        // Send request to SafteyNetAPI if Internet and Google Play Services are available
        if ( Utils.isInternetAvailable( context ) && Utils.isGooglePlayServicesAvailable( context) ) {
            isSafteyNetAvailable = true;
            safteyNetRequest();
        }
    }

    /**
     * onEvaluateClick method is called on Evaluate button click.It gathers all device facts and
     * sends the facts to Rule Engine for final evaluation
     * It sends the result to MainActivity2 for displaying the results to the user
     *
     * param        v	    View
     *
     * @return		None
     */

    public void onEvaluateClick( View v ){

        // Check if we are on correct running on Android version (API 24 and above )
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            Context context = v.getContext();

            // Gather device facts
            StringBuilder allPhoneFacts = gatherPhoneFacts( context );

            // Get final evaluation result from rule engine
            String finalEvaluation = getFinalSecurityEvaluation( allPhoneFacts.toString() );

            // Merging final security evaluation with the facts
            String allFactsAndFinalRes = allPhoneFacts.toString() + "@"+ finalEvaluation;

            Intent summary = new Intent(MainActivity.this, MainActivity2.class);
            summary.putExtra("message",allFactsAndFinalRes);

            // Send the result to MainActivity2 for displaying the result to user
            startActivity(summary);
            finish();

        }

    }

    /**
     * getFinalSecurityEvaluation method calls the Rule Engine and gets the final Security
     * Evaluation result (Low/Medium/High)
     *
     * param        allDeviceFacts	                Device facts
     *
     * @return		finalSecurityEvaluationRes      Low/Medium/High
     */

    private String getFinalSecurityEvaluation( String allDeviceFacts ){
        String finalSecurityEvaluationRes = Constants.FINAL_MESSAGE;
        try{

            // Read the rules from ESRules.txt
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open( Constants.RULES_FILE_NAME )));

            String rule = "";
            String allRules = "";

            //all rules added to the string
            while (( rule = reader.readLine()) != null ) {
                rule = rule.trim();
                if(!rule.equals("")) {
                    allRules += rule + "@";
                }

            }

            // All rule and device facts from ESRules.txt
            allRules =  allRules + allDeviceFacts;
            //allRules = allRules.substring(0, allRules.length()-1 );

            List<String> goals = Arrays.asList( Constants.FINAL_SECURITY_EVAL_GOALS.split(",") );
            Entail ruleEngine = new Entail();

            for ( String goal : goals ) {

                // Call the Rule Engine
                if( ruleEngine.ruleEngineProcess( allRules, goal ) ){
                    finalSecurityEvaluationRes += goal + "!";
                    break;
                }
            }

        }catch ( IOException ex ){
            ex.printStackTrace();
        }

        // Return the final security evaluation result
        return finalSecurityEvaluationRes;

    }

    /**
     * gatherPhoneFacts method gathers all device facts
     *
     * param        context	                Context
     *
     * @return		factsStringBuilder      All device facts
     */

    private StringBuilder gatherPhoneFacts( Context context){

        StringBuilder factsStringBuilder = new StringBuilder();
        List<String> phoneFacts = new ArrayList<String>();

        // Fetch App Security aspect facts
        AppSecurity appSecurityObj = new AppSecurity();
        phoneFacts.addAll( appSecurityObj.appSecurity( context.getPackageManager() ) );

        // Fetch Device Feature Security aspect facts
        DeviceFeatureSecurity deviceFeatureSecurityObj = new DeviceFeatureSecurity();
        phoneFacts.addAll( deviceFeatureSecurityObj.deviceFeatureSecurity() );

        // Fetch Sensor Security aspect facts
        SensorSecurity sensorSecurityObj = new SensorSecurity();
        phoneFacts.addAll( sensorSecurityObj.sensorSecurity( context, mResult, isSafteyNetAvailable, nonce ) );


        int numberOfFacts = phoneFacts.size();
        int counter = 0;

        // For each fact append @
        for ( String fact : phoneFacts ) {
            counter++;
            factsStringBuilder.append( fact );
            if( counter != numberOfFacts )
                factsStringBuilder.append("@");
        }

        // Return device facts
        return factsStringBuilder;

    }

    /**
     * safteyNetRequest method sends a request to SafteyNetAPI
     *
     * param        None
     *
     * @return		None
     */

    private void safteyNetRequest(){
        nonce = Utils.generateOneTimeRequestNonce();
        SafetyNetClient client = SafetyNet.getClient(MainActivity.this);
        Task<SafetyNetApi.AttestationResponse> task = client.attest(nonce, Constants.API_KEY);
        task.addOnSuccessListener(MainActivity.this, mSuccessListener)
                .addOnFailureListener(MainActivity.this, mFailureListener);
    }


    /**
     * OnSuccessListener method is called when the SafteyNetAPI returns with response.
     * mResult will have the response
     *
     * param        None
     *
     * @return		None
     */

    private OnSuccessListener<SafetyNetApi.AttestationResponse> mSuccessListener =
            new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                @Override
                public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
                    /*
                     Successfully communicated with SafetyNet API.
                     Use result.getJwsResult() to get the signed result data. See the server
                     component of this sample for details on how to verify and parse this result.
                     */
                    mResult = attestationResponse.getJwsResult();
                    //System.out.print( mResult );
                    Log.d("tag", "Success! SafetyNet result:\n" + mResult + "\n");

                }
            };

    /**
     * OnFailureListener method is called when there is a SafteyNetAPI request error.
     * mResult will be null
     *
     * param        None
     *
     * @return		None
     */

    private OnFailureListener mFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // An error occurred while communicating with the service.
            mResult = null;
            if (e instanceof ApiException) {
                // An error with the Google Play Services API contains some additional details.
                ApiException apiException = (ApiException) e;
                Log.d("TAG", "Error: " +
                        CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " +
                        apiException.getStatusMessage());
            } else {
                // A different, unknown type of error occurred.
                Log.d("TAG", "ERROR! " + e.getMessage());
            }

        }
    };

}
