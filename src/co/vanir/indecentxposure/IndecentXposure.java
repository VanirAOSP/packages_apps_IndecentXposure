/**
 * Copyright (C) 2015 VanirAOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package co.vanir.indecentxposure;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import java.util.concurrent.RunnableFuture;


public class IndecentXposure extends BroadcastReceiver {

    private static IndecentXposure _instance;
    public IndecentXposure()
    {
        super();
        if (_instance != null) {
            Log.e(TAG, "THERE ARE MORE THAN ONE INSTANCES OF IndecentXposure!");
        }
    }
    static synchronized IndecentXposure getInstance() {
        if (_instance == null) {
            _instance = new IndecentXposure();
        }
        return _instance;
    }

    //teh loggingz
    private final static String TAG = "IndecentXposure";

    //notification identifier
    private static final String NOTIFICATION_TAG = "IndecentXposureNotification";

    private boolean _state;
    private Object _padlock = new Object();

    void start(Context context) {
        synchronized(_padlock) {
            if (_state) {
                Log.e(TAG, "IndecentXposure ALREADY receiving package changes");
            } else {
                _state = true;
                IntentFilter packageAddOrRemovedFilter = new IntentFilter();
                packageAddOrRemovedFilter.addAction("android.intent.action.PACKAGE_ADDED");
                packageAddOrRemovedFilter.addAction("android.intent.action.PACKAGE_REMOVED");
                packageAddOrRemovedFilter.addDataScheme("package");
                context.registerReceiver(this, packageAddOrRemovedFilter);

                //if we're here, and the user hasn't yet explicitly acknowledged the risk of their choice
                //  GET ALL UP IN THEIR GRILL.
                boolean userDoesntWantTheirPhoneToWork = SerialOffender.hasXposedInstaller(context);
                if (!SerialOffender.getIgnoredState(context) && userDoesntWantTheirPhoneToWork) {

                    //if the user hasn't acknowledged they're probably causing their own bugs by
                    //  having xposed installed, and the installer is present, then pop up a fresh
                    //  reminder
                    IndecentXposure.notify(context, "XposedInstaller detected");
                }
            }
        }
    }

    void end(Context context) {
        synchronized(_padlock) {
            //this is only called by the tester activity, if the tester is enabled in the overlay
            if (!_state) {
                Log.e(TAG, "ALREADY DISABLED");
            } else {
                context.unregisterReceiver(this);
            }
        }
    }

    // handle BOOT_COMPLETE, and ignore button
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive("+intent.getAction()+")");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            start(context);

        } else if (intent.getAction().equals(intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(intent.ACTION_PACKAGE_REMOVED)) {
            //handle package install/uninstall all smart-like

            if (intent.getData() == null ||
                    intent.getData().getScheme() == null ||
                    !intent.getData().getScheme().equals("package") ||
                    intent.getData().getEncodedSchemeSpecificPart() == null) {
                Log.e(TAG, "Received garbage package data in intent");
                return;
            }

            //are you my mommy?
            if (!intent.getData().getEncodedSchemeSpecificPart().equals(SerialOffender.getPackageName()))
            {
                Log.v(TAG, "Ignoring irrelevant package addition/removal");
                return;
            }

            if (intent.getAction().equals(intent.ACTION_PACKAGE_ADDED)) {

                //sound the alarm
                IndecentXposure.notify(context, "package installed -- and something about the consequences of that");
            } else {

                //all is well that ends well
                IndecentXposure.cancel(context);

                if (SerialOffender.getIgnoredState(context)) {
                    //reload, in case xposed gets reinstalled
                    SerialOffender.setIgnoredState(context, false);
                }
            }
        } else {
            //if it's not boot, and not pack add/remove, then the user must've chosen the "ignore risks" option on the notification
            Log.i(TAG, "Received ignore request");
            SerialOffender.setIgnoredState(context, true);
            IndecentXposure.cancel(context);
        }
    }

    //build a notification with specified ticker text
    public static void notify(final Context context, final String exampleString) {
        final Resources res = context.getResources();

        final String ticker = exampleString;
        final String title = res.getString(
                R.string.solve_problems_notification_title); //_template, exampleString);
        final String text = res.getString(
                R.string.solve_problems_notification_placeholder_text_template);//, exampleString);

        //one button opens the "uninstall app" settings page
        final Intent removeIntent = new Intent();
        removeIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", SerialOffender.getPackageName(), null);
        removeIntent.setData(uri);

        //the other option will hide the notification until the user uninstalls and reinstalls
        //  the installer
        final Intent ignoreIntent = new Intent();
        ignoreIntent.setAction("co.vanir.indecentxposure.IGNORE_LIKELY_FUNK");

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_solve_problems)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTicker(ticker)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText(res.getString(R.string.summary_thanks)))
                .addAction(
                        0,
                        res.getString(R.string.action_remove),
                        PendingIntent.getActivity(
                                context,
                                0,
                                removeIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        0,
                        res.getString(R.string.action_accept_consequences),
                        PendingIntent.getBroadcast(
                                context,
                                0,
                                ignoreIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT));

        notify(context, builder.build(), true);
    }

    //show a built notification with specified clearability
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification, boolean sticky) {
        if (sticky)
            notification.flags |= Notification.FLAG_NO_CLEAR;
        else
            notification.flags &= ~Notification.FLAG_NO_CLEAR;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_TAG, 0, notification);
        } else {
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    //hide the evidence
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        Log.i(TAG,"Canceling notification");
        // to cancel the notification, we need to make it less sticky first
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(0)
                .setContentTitle("go")
                .setContentText("away");

        notify(context, builder.build(), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_TAG, 0);
        } else {
            ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
