/*
 *  AppSecurity.java
 *
 *  Version: AppSecurity.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */

package com.dream.the.code.droidswatch;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class gathers facts for the App Security aspect of the evaluation
 * (unknown sources, blacklisted apps, app permission goodness)
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class AppSecurity {


    // List containing all the AppSecurity facts
    private List<String> appSecurityList = new ArrayList<String>();

    /**
     * appSecurity method gathers the following facts about device -
     * unknownsources, blacklisted apps, applcation permission goodness
     *
     * param        packageManager	    PackageManager
     *
     * @return		appSecurityList     List containing all app security aspect facts
     */

    public List<String> appSecurity( PackageManager packageManager ){

        //Get all the apps details
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        int goodAppCount = 0;
        int totalNumberOfApps = 0;
        int dangerousPermCount = 0;
        int unknownsourcesCount = 0;
        int blackListedAppCount = 0;
        double goodnessPercentage = 0.0;
        String packageInstaller = null;
        String appName = null;
        PackageInfo packageInfo = null;

        List<String> blackListedApps = Arrays.asList( Constants.BLACKLISTED_APPS.split(",") );

        // Loop over all the apps in the device and count the number of good apps
        for( int i=0; i< packageInfos.size(); i++ ){
            dangerousPermCount = 0;
            packageInfo = packageInfos.get(i);

            //Check if the app is user installed
            if( isUserInstalledApp( packageInfo ) ){

                // Check if the app is installed from Unknown Source
                packageInstaller = packageManager.getInstallerPackageName( packageInfo.packageName );
                if( packageInstaller != null && !( packageInstaller.equals ( BuildConfig.APPLICATION_ID ) || packageInstaller.equals( Constants.PLAYSTORE_PACKAGE_NAME ) ) )
                    unknownsourcesCount++;


                // Check if app is blacklisted
                appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
                if ( blackListedApps.contains(appName) )
                    blackListedAppCount++;


                totalNumberOfApps++;
                PermissionInfo[] permissionInfos = packageInfo.permissions;
                if( permissionInfos == null ) continue;

                /** Loop over all the permissions of the app and check
                    if any dangerous permissions are granted for the app
                */
                for( PermissionInfo permissionInfo : permissionInfos ){
                    int protectionLevel = permissionInfo.protectionLevel;
                    if ( protectionLevel == PermissionInfo.PROTECTION_DANGEROUS )
                        dangerousPermCount++;
                }

                /** If the number of dangerous permissions granted for an app is <= 5
                 *  then increment the good apps count
                */
                if( dangerousPermCount <= 5 )
                    goodAppCount++;
            }
        }

        // Add the facts to the appSecurityList

        if( unknownsourcesCount == 0 )
            appSecurityList.add( Constants.UNKNOWN_SOURCES_ZERO );
        else
            appSecurityList.add( Constants.UNKNOWN_SOURCES_NOT_ZERO );

        if ( blackListedAppCount == 0 )
            appSecurityList.add( Constants.BLACKLISTED_APP_ZERO );
        else
            appSecurityList.add( Constants.BLACKLISTED_APP_NOT_ZERO );

        // Compute overall application permission goodness and add the fact to the list
        if( totalNumberOfApps != 0 ){
            goodnessPercentage = (double) ( ( goodAppCount * 100 ) /totalNumberOfApps );
            if( goodnessPercentage >= 50 )
                appSecurityList.add( Constants.APPLICATION_PERM_GOODNESS_OVER50 );
            else
                appSecurityList.add( Constants.APPLICATION_PERM_GOODNESS_UNDER50 );
        }
        else
            appSecurityList.add( Constants.APPLICATION_PERM_GOODNESS_OVER50 );

        return appSecurityList;

    }



    /**
     * isUserInstalledApp method checks if the app is user installed app or system installed
     *
     *
     * param        packageInfo	        PackageInfo
     *
     * @return		true/false          Returns true if app is user installed else false
     */

    public boolean isUserInstalledApp(PackageInfo packageInfo){
        return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ) ? false : true;
    }


}
