/*
 *  Utils.java
 *
 *  Version: Utils.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */

package com.dream.the.code.droidswatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * This class provides helper functions
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class Utils {

    /**
     * isGooglePlayServicesAvailable method checks if Google Play Services is available or not
     *
     * param        context          context
     *
     * @return		boolean          Returns true if Google Play Services is available else false
     */

    public static boolean isGooglePlayServicesAvailable( Context context ){
        if ( GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
                == ConnectionResult.SUCCESS) {
            // The SafetyNet Attestation API is available.
            return true;
        }
        return false;
    }

    /**
     * isInternetAvailable method checks if Internet is available or not
     *
     * param        context          context
     *
     * @return		boolean          Returns true if Internet is available else false
     */

    public static boolean isInternetAvailable( Context context ) {
        boolean connected = false;
        ConnectivityManager connectivityManager;
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            Log.e("connectivity", e.toString());
        }
        return connected;
    }

    /**
     * generateOneTimeRequestNonce method generates a one time secure code
     *
     * param        None
     *
     * @return		nonce          Returns one time secure code
     */

    public static byte[] generateOneTimeRequestNonce() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] nonce = new byte[32];
        secureRandom.nextBytes(nonce);
        return nonce;
    }

    /**
     * getSafteyNetResponse parses SafteyNetAPI response and returns in a HashMap
     *
     * param        jwsResult               SafteyNet response
     *
     * @return		safteyNetResponse       Returns SaftelyNetAPI response in HashMap
     */

    public static HashMap<String, String> getSafteyNetResponse( String jwsResult ) {

        HashMap<String, String> safteyNetResponse = new HashMap<String, String>();

        // The jwsResult (JSON WEB TOKEN) is a 3 base64 encoded parts concatenated by a . character
        String[] jwtParts = jwsResult.split("\\.");
        String decodedPayload = null;

        if (jwtParts.length == 3) {
            decodedPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));

            try{
                // Parse jason response
                JSONObject root = new JSONObject( decodedPayload );

                // Fetch Bootloader setting
                if (root.has( Constants.SAFTEYNET_BASICINTEGRITY ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_BASICINTEGRITY ,
                            root.getString(Constants.SAFTEYNET_BASICINTEGRITY) );
                }

                //Fetch Root Access setting
                if (root.has( Constants.SAFTEYNET_CTSPROFILEMATCH ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_CTSPROFILEMATCH ,
                            root.getString(Constants.SAFTEYNET_CTSPROFILEMATCH) );
                }

                /* Commented as it is not used in this release
                if (root.has( Constants.SAFTEYNET_NONCE ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_NONCE ,
                            root.getString(Constants.SAFTEYNET_NONCE) );
                }
                if (root.has( Constants.SAFTEYNET_TIMESTAMPMS ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_TIMESTAMPMS ,
                            root.getString(Constants.SAFTEYNET_TIMESTAMPMS) );
                }
                if (root.has( Constants.SAFTEYNET_APKPACKAGENAME ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_APKPACKAGENAME ,
                            root.getString(Constants.SAFTEYNET_APKPACKAGENAME) );
                }

                if (root.has( Constants.SAFTEYNET_ADVICE ) ) {
                    safteyNetResponse.put( Constants.SAFTEYNET_ADVICE ,
                            root.getString(Constants.SAFTEYNET_ADVICE) );
                }
                */

            }catch ( JSONException e ){
                Log.e("JsonResponse", "problem parsing decodedPayload:" + e.getMessage(), e);
            }

        } else {
            return null;
        }

        // Return response HashMap
        return safteyNetResponse;
    }

}
