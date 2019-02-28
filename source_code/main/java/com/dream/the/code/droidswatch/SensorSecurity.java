/*
 *  SensorSecurity.java
 *
 *  Version: SensorSecurity.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */

package com.dream.the.code.droidswatch;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class gathers facts for the Sensor Security aspect of the evaluation
 * (Bootloader setting, Root access, developer menu, device lock)
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class SensorSecurity {

    // List containing all the Sensor Security Security facts
    private List<String> sensorSecurityList = new ArrayList<String>();

    /**
     * sensorSecurity method gathers the following facts about device -
     * Android OS version, security patch, device model
     *
     * param        context	                Context
     * param        jsonResposnse           String containing response from SaftelNetAPI
     * param        isSafteyNetavailable    boolean value true if SafteyNetAPI is available else false
     * param        nonce                   One time secure code sent to SafteyNetAPI
     *
     * @return		sensorSecurityList     List containing all device feature security aspect facts
     */

    public List<String> sensorSecurity( Context context, String jsonResposnse, boolean isSafteyNetavailable, byte[] nonce  ){

        // check if developer menu is enabled and add the fact
        int devSetting = Settings.Secure.getInt( context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED ,0 );
        if( devSetting != 1 )
            sensorSecurityList.add( Constants.DEVELOPER_MENU_DISABLED );
        else
            sensorSecurityList.add( Constants.DEVELOPER_MENU_ENABLED );

        // Check if the device has a locking mechanism and add the fact
        if( isDeviceLocked(context) )
            sensorSecurityList.add( Constants.DEVICE_LOCKED );
        else
            sensorSecurityList.add( Constants.DEVICE_NOT_LOCKED );

        // Check if SafteyNetAPI is available and has sent a response
        if( isSafteyNetavailable && jsonResposnse != null ){
            HashMap<String, String> safteyNetResponse = new HashMap<String, String>();

            // Decode , parse and get the SaftelyNetAPI response in HashMap
            safteyNetResponse = Utils.getSafteyNetResponse( jsonResposnse );

            // Fetch the Bootloader and RootAccess status of the device
            boolean isBootloaderLocked = Boolean.parseBoolean( safteyNetResponse.get( Constants.SAFTEYNET_CTSPROFILEMATCH ) );
            boolean isRootAcessDisabled = Boolean.parseBoolean( safteyNetResponse.get( Constants.SAFTEYNET_BASICINTEGRITY ) );

            // Add the Bootloader and RootAccess facts to the list
            if( isBootloaderLocked )
                sensorSecurityList.add( Constants.BOOTLOADER_LOCKED );
            else
                sensorSecurityList.add( Constants.BOOTLOADER_UNLOCKED );

            if( isRootAcessDisabled )
                sensorSecurityList.add( Constants.ROOTACCESS_DISABLED );
            else
                sensorSecurityList.add( Constants.ROOTACCESS_ENABLED );


        }
        else{
            // If SaftelyNetAPI response is not available add Sensor Data not available fact
            sensorSecurityList.add( Constants.SENSORDATA_NOT_AVAILABLE );
        }

        return sensorSecurityList;

    }

    /**
     * isDeviceLocked method checks if the device has any kind of lock
     *
     * param        context	          Context
     *
     * @return		true/false        Returns true if device has a lock else false
     */

    public boolean isDeviceLocked( Context context ){

        // Check if the device has Primary Lock Mechanism - Password, Pin or Pattern
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService( Context.KEYGUARD_SERVICE ); //api 16+
        if ( keyguardManager.isKeyguardSecure() ){
            return true;
        }

        // Check if the device has Secondary Mechanism - Biometric
        long mode = Settings.Secure.getLong( context.getContentResolver(), Constants.PASSWORD_TYPE_KEY,
                DevicePolicyManager.PASSWORD_QUALITY_SOMETHING );

        if ( mode == DevicePolicyManager.PASSWORD_QUALITY_BIOMETRIC_WEAK ){
            return true;
        }

        // No Lock Mechanism return false
        return false;

    }

}
