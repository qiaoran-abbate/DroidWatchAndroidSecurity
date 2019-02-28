/*
 *  MainActivity2.java
 *
 *  Version: MainActivity2.java, v 1.0 2018/06/16
 *
 *  Revisions:
 *      Revision 1.0 2018/06/16 08:11:09
 *      Initial Revision
 *
 */

package com.dream.the.code.droidswatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

/**
 * This class is the Final Result page which is displayed to the user. The final result is
 * displayed in User friendly language
 *
 * @author Mansha Malik
 * @author Parinitha Nagaraja
 * @author Qiaoran Li
 *
 */

public class MainActivity2 extends AppCompatActivity {

    // User friendly metrics string array
    private String[] Metrics = {"Unknown Sources Found", "Blacklisted Apps Found", "App Permission", "OS Version", "Security Patch",
            "Device Model", "Developer Menu Access", "Device Lock", "Boot Loader", "Root Access"};

    // Device metrics
    private String[] MetricValues = new String[11];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //addHeaders();

        // Populates the table with results
        addData();
    }

    /**
     * onExitClick method is called when user clicks on Done.
     *
     * param        v	    View
     *
     * @return		None
     */

    public void onExitClick(View v) {
        finish();
        //System.exit(0);
        moveTaskToBack(true);
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(20, 20, 20, 20);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }

    @NonNull
    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
    }

    /**
     * addHeaders method adds headers to the table.
     *
     * param        None	    View
     *
     * @return		None
     */
    public void addHeaders() {
        TableLayout tl = findViewById(R.id.t1);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "Metric", Color.WHITE, Typeface.NORMAL, Color.BLUE));
        tr.addView(getTextView(0, "YourDeviceValues", Color.WHITE, Typeface.NORMAL, Color.BLUE));
        tl.addView(tr, getTblLayoutParams());
    }

    /**
     * addData method populates the table with facts
     *
     * param        None	    View
     *
     * @return		None
     */
    public void addData() {

        // Get all the facts from MainActivity
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");
        boolean isSensorfailed = false;

        // Split the facts and add it to String array
        MetricValues = message.split("@");

        setContentView(R.layout.activity_main2);

        TextView mTitle = (TextView) findViewById(R.id.textView);

        if ( MetricValues[8].equals( Constants.SENSORDATA_NOT_AVAILABLE ) )
            isSensorfailed = true;

        for (int i = 0; i < MetricValues.length; i++) {
            switch (i) {
                case 0:
                    if (MetricValues[i].equals( Constants.UNKNOWN_SOURCES_ZERO )) {
                        MetricValues[i] = Constants.ZERO;
                    } else
                        MetricValues[i] = Constants.MORE_THAN_ZERO;
                    break;

                case 1:
                    if (MetricValues[i].equals( Constants.BLACKLISTED_APP_ZERO )) {
                        MetricValues[i] = Constants.ZERO;
                    } else
                        MetricValues[i] = Constants.MORE_THAN_ZERO;
                    break;

                case 2:
                    if (MetricValues[i].equals( Constants.APPLICATION_PERM_GOODNESS_OVER50 )) {
                        MetricValues[i] = Constants.SAFE;
                    } else
                        MetricValues[i] = Constants.RISKY;
                    break;


                case 3:
                    if (MetricValues[i].equals( Constants.ANDROID_OS_VERSION_APPROVED )) {
                        MetricValues[i] = Constants.LATEST;
                    } else
                        MetricValues[i] = Constants.OLD;
                    break;

                case 4:
                    if (MetricValues[i].equals( Constants.SECURITY_PATCH_DATE_APPROVED )) {
                        MetricValues[i] = Constants.LATEST;
                    } else
                        MetricValues[i] = Constants.OLD;
                    break;

                case 5:
                    if (MetricValues[i].equals( Constants.DEVICE_MODEL_APPROVED )) {
                        MetricValues[i] = Constants.INVULNERABLE;
                    } else
                        MetricValues[i] = Constants.VULNERABLE;
                    break;


                case 6:
                    if (MetricValues[i].equals( Constants.DEVELOPER_MENU_DISABLED )) {
                        MetricValues[i] = Constants.DISABLED;
                    } else
                        MetricValues[i] = Constants.ENABLED;
                    break;

                case 7:
                    if (MetricValues[i].equals( Constants.DEVICE_LOCKED )) {
                        MetricValues[i] = Constants.ENABLED;
                    } else
                        MetricValues[i] = Constants.DISABLED;
                    break;

                case 8:
                    if ( isSensorfailed ) {
                        MetricValues[i] = Constants.DATA_UNAVAILABLE;
                    }
                    else if (MetricValues[i].equals( Constants.BOOTLOADER_LOCKED )) {
                        MetricValues[i] = Constants.LOCKED;
                    } else
                        MetricValues[i] = Constants.UNLOCKED;
                    break;

                case 9:
                    if ( isSensorfailed ) {
                        mTitle.setText( MetricValues[i] );
                        MetricValues[i] = Constants.DATA_UNAVAILABLE;
                    }
                    else if (MetricValues[i].equals( Constants.ROOTACCESS_DISABLED )) {
                        MetricValues[i] = Constants.DISABLED;
                    } else
                        MetricValues[i] = Constants.ENABLED;
                    break;

                case 10:
                    mTitle.setText( MetricValues[i] );
                    break;
            }
        }


        // Display the 10 results in the table
        TableLayout Tl = findViewById(R.id.t1);
        for (int i = 0; i < 10; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(i + 1, Metrics[i], R.color.grey, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(i + 10, MetricValues[i], R.color.grey, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            Tl.addView(tr, getTblLayoutParams());
        }
    }
}
