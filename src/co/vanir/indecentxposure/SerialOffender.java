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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

class SerialOffender {
    static final String PREFS_NAME = "NagState";
    static final String PACKAGE_NAME = "de.robv.android.xposed.installer";

    static boolean getIgnoredState(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean("ignored", false);
    }

    static void setIgnoredState(Context context, boolean ignored) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putBoolean("ignored", ignored).commit();
    }

    static boolean hasXposedInstaller(final Context context){
        try {
            context.getPackageManager().getPackageInfo(PACKAGE_NAME,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    static String getPackageName() {
        return PACKAGE_NAME;
    }
}
