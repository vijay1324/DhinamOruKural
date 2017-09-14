package com.atsoft.dhinamorukural;

import com.google.android.gms.ads.AdRequest;

/**
 * Created by ATSoft on 9/3/2017.
 */

public class Defs {
    public static String[] allwords;
    public static String currentVersionName = "2.1";
    public static String currentVersionCode = "5";
    public static AdRequest adRequest = new AdRequest.Builder().addTestDevice("4c2da3293cd5f88b").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//    public static AdRequest adRequest = new AdRequest.Builder().build();
}
