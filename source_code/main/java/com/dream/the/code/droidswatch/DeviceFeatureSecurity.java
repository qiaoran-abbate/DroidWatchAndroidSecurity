/*
 *  DeviceFeatureSecurity.java
 *
 *  Version: DeviceFeatureSecurity.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */


package com.dream.the.code.droidswatch;

import android.os.Build;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class gathers facts for the Device Feature Security aspect of the evaluation
 * (Android OS version, security patch, device model)
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class DeviceFeatureSecurity {

    // List containing all the Device Feature Security facts
    private List<String> deviceSecurityList = new ArrayList<String>();


    /**
     * deviceFeatureSecurity method gathers the following facts about device -
     * Android OS version, security patch, device model
     *
     * param        none
     *
     * @return		deviceSecurityList     List containing all device feature security aspect facts
     */

    public List<String>  deviceFeatureSecurity(){

        // Check if the installed Android OS version is the latest and the fact
        String androidOSVersion =  Build.VERSION.SDK_INT >= Constants.ANDROID_OS_VERSION_LATEST ? Constants.ANDROID_OS_VERSION_APPROVED : Constants.ANDROID_OS_VERSION_NOT_APPROVED ;
        deviceSecurityList.add( androidOSVersion );

        // Security patch approval computation
        Date latestSecurityPatch = null;
        Date deviceSecurityPatchDate = null;
        String securityPatchDate = Constants.SECURITY_PATCH_DATE_NOT_APPROVED;
        try{
            latestSecurityPatch = new SimpleDateFormat("yyyy-mm-dd").parse( Constants.SECURITY_PATCH_DATE_LATEST );
            deviceSecurityPatchDate = new SimpleDateFormat("yyyy-mm-dd").parse( Build.VERSION.SECURITY_PATCH );

            // Check if the installed security patch is the latest and the fact
            securityPatchDate = ( deviceSecurityPatchDate.after( latestSecurityPatch ) || deviceSecurityPatchDate.equals(latestSecurityPatch) )
                    ? Constants.SECURITY_PATCH_DATE_APPROVED : Constants.SECURITY_PATCH_DATE_NOT_APPROVED ;

        }catch ( ParseException ex ){
            Log.e("ParseDateError",ex.getMessage() );
        }

        deviceSecurityList.add( securityPatchDate );

        // Device model check
        List<String> deviceModelsVulnerable = Arrays.asList( Constants.DEVICE_MODEL_VULNERABLE.split(",") );
        String modelName = Build.MODEL;
        modelName = modelName.replaceAll("\\s","");

        // Check if the device model is approved and add the fact
        modelName = deviceModelsVulnerable.contains( modelName ) ? Constants.DEVICE_MODEL_NOT_APPROVED : Constants.DEVICE_MODEL_APPROVED;
        deviceSecurityList.add( modelName );

        return deviceSecurityList;

    }

}
