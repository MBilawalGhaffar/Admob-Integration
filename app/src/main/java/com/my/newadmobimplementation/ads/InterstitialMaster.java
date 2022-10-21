package com.my.newadmobimplementation.ads;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.newadmobimplementation.R;


import java.util.Objects;

public class InterstitialMaster {

    static InterstitialAd admobInterstitialAd;
    static com.facebook.ads.InterstitialAd fbInterstitialAd;

    static Context mContext;
    static Activity mActivity;
    static String logTag = "Ads_";
    static boolean isRequestSent = false;
    static boolean isAlreadyLoaded = false;
    static boolean isAdmobLoaded = false;
    static boolean isFbLoaded = false;
    static boolean isInterstitialAdVisible = false;
    static boolean admob_app_open_visible = false;

    public static SharedPreferences sharedpreferences;

    static String admob_interstitial_id = "";
    static String admob_app_open = "";
    public static String admob_native = "";
    public static String admob_banner = "";
    public static String fb_interstitial = "";
    public static String fb_native = "";
    public static String fb_banner = "";

    public static boolean fb_native_visible = false;
    public static boolean admob_native_visible = false;
    public static boolean admob_interstitial_visible = false;
    public static boolean fb_interstitial_visible = false;
    public static boolean admob_native_exit_visible = false;
    public static boolean fb_native_exit_visible = false;
    public static boolean native_adapter_visible = false;
    public static boolean fb_banner_visible = false;

    static long nowTime;

    static ActionOnAdClosedListener mActionOnAdClosedListener;

    public static int dialog_show_time = 1000; // 2 seconds

    public static void load_interstitial(Context context) {
        mContext = context;

        if (checkInternetConnected(mContext)) {
            if (!isAlreadyLoaded && !isRequestSent) {
                if (admob_interstitial_visible && !isAlreadyLoaded) { // if enabled(true) from firebase
                    load_admob_interstitial();
                } else if (fb_interstitial_visible && !isAlreadyLoaded) { // if enabled(true) from firebase
                    load_fb_interstitial();
                }
            }
        }
    }

    static void load_admob_interstitial() {
        if (admob_interstitial_visible && !isAlreadyLoaded && !isRequestSent) {
            isRequestSent = true;
            //------------ Admob Starts ------------------
            AdRequest adRequest_interstitial = new AdRequest.Builder().build();
            InterstitialAd.load(mContext, admob_interstitial_id, adRequest_interstitial,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            admobInterstitialAd = interstitialAd;
                            Log.d(logTag, "Admob Insterstitial Loaded.");

                            isAlreadyLoaded = true;
                            isAdmobLoaded = true;

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d(logTag, "Admob Interstitial Failed to Load." + loadAdError.getMessage());

                            isAlreadyLoaded = false;
                            isAdmobLoaded = false;

                            isRequestSent = false;

                            load_fb_interstitial();

                        }
                    });
            //------------ Admob Ends ------------------
        }
    }

    static void load_fb_interstitial() {
        if (fb_interstitial_visible && !isAlreadyLoaded && !isRequestSent) {

            isRequestSent = true;

            //------------ FB Starts -------------------
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(mContext, fb_interstitial);
            com.facebook.ads.InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(logTag, "FB Interstitial ad displayed.");

                }

                @Override
                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                    // Interstitial dismissed callback
                    Log.e(logTag, "FB Interstitial ad dismissed.");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("isFirstAd", false);
                    editor.putLong("previousAdTime", System.currentTimeMillis());
                    editor.apply();

                    isAlreadyLoaded = false;
                    isFbLoaded = false;
                    isRequestSent = false;

                    isInterstitialAdVisible = false;

                    load_interstitial(mContext);

                    mActionOnAdClosedListener.ActionAfterAd();
                }

                @Override
                public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                    // Ad error callback
                    Log.e(logTag, "FB Interstitial ad failed to load: " + adError.getErrorMessage());

                    isAlreadyLoaded = false;
                    isFbLoaded = false;
                    isRequestSent = false;

                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d(logTag, "FB Insterstitial Loaded.");

                    isAlreadyLoaded = true;
                    isFbLoaded = true;

                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {
                    // Ad clicked callback
                    Log.d(logTag, "FB Interstitial ad clicked!");

                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {
                    // Ad impression logged callback
                    Log.d(logTag, "FB Interstitial ad impression logged!");
                    isInterstitialAdVisible = true;
//                    InterstitialMaster.sharedpreferences.edit().putLong("lastAppOpenShowTime", System.currentTimeMillis()).apply();
                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            fbInterstitialAd.loadAd(
                    fbInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

        }
    }

    public static void show_interstitial(Activity activity, Context context, ActionOnAdClosedListener actionOnAdClosedListener) {
        mActivity = activity;
        mContext = context;
        mActionOnAdClosedListener = actionOnAdClosedListener;
        sharedpreferences = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        nowTime = System.currentTimeMillis();
       /* if ((System.currentTimeMillis() >= (InterstitialMaster.sharedpreferences.getLong("lastAppOpenShowTime", 0) + (InterstitialMaster.sharedpreferences.getInt("app_open_interstitial_delay", 10) * 1000L) ) )
        ) {*/
        if (sharedpreferences.getBoolean("isFirstAd", true)) {
            if (isAlreadyLoaded) {
                if (isAdmobLoaded) {
                    show_admob_intestitial();
                } else if (isFbLoaded) {
                    show_fb_interstitial();
                } else {
                    mActionOnAdClosedListener.ActionAfterAd();
                }
            } else {
                mActionOnAdClosedListener.ActionAfterAd();
            }
        }
//        }
        else {
            // if is not first time ad. then show after ad_delay time.
            if ((System.currentTimeMillis() >= (InterstitialMaster.sharedpreferences.getLong("lastAppOpenShowTime", 0) + (InterstitialMaster.sharedpreferences.getInt("app_open_interstitial_delay", 10) * 1000L) ) )
            ) {

                if((System.currentTimeMillis() >= (sharedpreferences.getLong("previousAdTime", 0) + (sharedpreferences.getInt("ad_delay", 8) * 1000L))))  {
                    if (isAdmobLoaded) {
                        show_admob_intestitial();
                    } else if (isFbLoaded) {
                        show_fb_interstitial();
                    } else {
                        mActionOnAdClosedListener.ActionAfterAd();
                    }
                }else {
                    mActionOnAdClosedListener.ActionAfterAd();
                    Log.d("Ads_", "App open Condition False");
                }

            } else {
                Log.d(logTag, "Ad will be shown after ad_delay time.");
                mActionOnAdClosedListener.ActionAfterAd();
            }
        }
    }

    static void show_admob_intestitial() {
        showWaitDialog();
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissWaitDialog();
                    if (admobInterstitialAd != null) {
                        admobInterstitialAd.show(mActivity);
                        isInterstitialAdVisible = true;
//                        InterstitialMaster.sharedpreferences.edit().putLong("lastAppOpenShowTime", System.currentTimeMillis()).apply();
                        admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean("isFirstAd", false);
                                editor.putLong("previousAdTime", System.currentTimeMillis());
                                editor.apply();
                                Log.d(logTag, "Admob Interstitial Closed.");

                                isInterstitialAdVisible = false;

                                load_interstitial(mContext);

                                mActionOnAdClosedListener.ActionAfterAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                Log.d(logTag, "Admob Interstitial Failed to Show.");

                                isInterstitialAdVisible = false;

                                mActionOnAdClosedListener.ActionAfterAd();
                            }
                        });
                    } else {
                        mActionOnAdClosedListener.ActionAfterAd();
                    }

                    isAlreadyLoaded = false;
                    isAdmobLoaded = false;
                    isRequestSent = false;
                }

            }, dialog_show_time);
        } catch (Exception e) {
            Log.d(logTag, "Exception: " + e.getMessage());
            dismissWaitDialog();
        }
    }

    static void show_fb_interstitial() {
        showWaitDialog();
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissWaitDialog();
                    if (fbInterstitialAd != null) {
                        fbInterstitialAd.show();
                        isInterstitialAdVisible = true;
                    } else {
                        mActionOnAdClosedListener.ActionAfterAd();
                    }

                    isAlreadyLoaded = false;
                    isFbLoaded = false;
                    isRequestSent = false;
                }
            }, dialog_show_time);
        } catch (Exception e) {
            Log.d(logTag, "Exception: " + e.getMessage());
            dismissWaitDialog();
        }
    }

    public static void getAdIDsFromFirebase(Context your_activity_context) {

        mContext = your_activity_context;

        FirebaseApp.initializeApp(mContext);

        sharedpreferences = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(mContext.getString(R.string.admob));


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                AdsModel adIDs = dataSnapshot.getValue(AdsModel.class);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                assert adIDs != null;
                editor.putString("admob_app_id", adIDs.admob_app_id);
                editor.putString("admob_app_open", adIDs.admob_app_open);
                editor.putString("admob_interstitial", adIDs.admob_interstitial);
                editor.putString("admob_native", adIDs.admob_native);
                editor.putString("admob_banner", adIDs.admob_banner);
                editor.putInt("ad_delay", adIDs.ad_delay);
                editor.putInt("app_open_interstitial_delay", adIDs.app_open_interstitial_delay);
                editor.putString("fb_interstitial", adIDs.fb_interstitial);
                editor.putString("fb_native", adIDs.fb_native);
                editor.putString("fb_banner", adIDs.fb_banner);
                editor.putBoolean("admob_native_exit_visible", adIDs.admob_native_exit_visible);
                editor.putBoolean("fb_native_exit_visible", adIDs.fb_native_exit_visible);
                editor.putBoolean("fb_native_visible", adIDs.fb_native_visible);
                editor.putBoolean("admob_native_visible", adIDs.admob_native_visible);
                editor.putBoolean("admob_interstitial_visible", adIDs.admob_interstitial_visible);
                editor.putBoolean("fb_interstitial_visible", adIDs.fb_interstitial_visible);
                editor.putBoolean("admob_app_open_visible", adIDs.admob_app_open_visible);
                editor.putBoolean("native_adapter_visible", adIDs.native_adapter_visible);
                editor.putBoolean("fb_banner_visible", adIDs.fb_banner_visible);
                editor.apply();

                admob_interstitial_id = sharedpreferences.getString("admob_interstitial", "");
                admob_native = sharedpreferences.getString("admob_native", "");
                admob_banner = sharedpreferences.getString("admob_banner", "");
                admob_app_open = sharedpreferences.getString("admob_app_open", "");
                fb_interstitial = sharedpreferences.getString("fb_interstitial", "");
                fb_native = sharedpreferences.getString("fb_native", "");
                fb_banner = sharedpreferences.getString("fb_banner", "");
                admob_native_exit_visible = sharedpreferences.getBoolean("admob_native_exit_visible", false);
                fb_native_exit_visible = sharedpreferences.getBoolean("fb_native_exit_visible", false);
                fb_native_visible = sharedpreferences.getBoolean("fb_native_visible", false);
                admob_native_visible = sharedpreferences.getBoolean("admob_native_visible", false);
                admob_interstitial_visible = sharedpreferences.getBoolean("admob_interstitial_visible", false);
                fb_interstitial_visible = sharedpreferences.getBoolean("fb_interstitial_visible", false);
                admob_app_open_visible = sharedpreferences.getBoolean("admob_app_open_visible", false);
                native_adapter_visible = sharedpreferences.getBoolean("native_adapter_visible", false);
                fb_banner_visible = sharedpreferences.getBoolean("fb_banner_visible", false);

                try {
                    ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;

                    String admobAppID = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d("Ads_", "Admob App ID: " + admobAppID);
                    ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", sharedpreferences.getString("admob_app_id", ""));//you can replace your key APPLICATION_ID here
                    admobAppID = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d("Ads_", "Admob App ID Replaced: " + admobAppID);

                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("Ads_", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("Ads_", "Failed to load meta-data, NullPointer: " + e.getMessage());
                }


                Log.d("Ads_", "Admob_App_Open: " + sharedpreferences.getString("admob_app_open", ""));
                Log.d("Ads_", "Admob_Interstitial: " + sharedpreferences.getString("admob_interstitial", ""));
                Log.d("Ads_", "Admob_Native: " + sharedpreferences.getString("admob_native", ""));
                Log.d("Ads_", "Admob_Banner: " + sharedpreferences.getString("admob_banner", ""));
                Log.d("Ads_", "Ad_Delay: " + sharedpreferences.getInt("ad_delay", 8));
                Log.d("Ads_", "ad_network: " + sharedpreferences.getString("ad_network", ""));
                Log.d("Ads_", "fb_interstitial: " + sharedpreferences.getString("fb_interstitial", ""));
                Log.d("Ads_", "fb_native: " + sharedpreferences.getString("fb_native", ""));
                Log.d("Ads_", "fb_banner: " + sharedpreferences.getString("fb_banner", ""));
                Log.d("Ads_", "fb_native_exit_visible: " + sharedpreferences.getBoolean("fb_native_exit_visible", false));
                Log.d("Ads_", "admob_native_exit_visible: " + sharedpreferences.getBoolean("admob_native_exit_visible", false));
                Log.d("Ads_", "fb_native_visible: " + sharedpreferences.getBoolean("fb_native_visible", false));
                Log.d("Ads_", "admob_native_visible: " + sharedpreferences.getBoolean("admob_native_visible", false));
                Log.d("Ads_", "admob_interstitial_visible: " + sharedpreferences.getBoolean("admob_interstitial_visible", false));
                Log.d("Ads_", "fb_interstitial_visible: " + sharedpreferences.getBoolean("fb_interstitial_visible", false));
                Log.d("Ads_", "admob_app_open_visible: " + sharedpreferences.getBoolean("admob_app_open_visible", false));
                Log.d("Ads_", "native_adapter_visible: " + sharedpreferences.getBoolean("native_adapter_visible", false));
                Log.d("Ads_", "fb_banner_visible: " + sharedpreferences.getBoolean("fb_banner_visible", false));


                MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

                AudienceNetworkAds.initialize(mContext);


                load_interstitial(mContext);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Ads_", "Failed to read value.", error.toException());
            }
        });
    }
    public static void getIdsFromSharedPrefrences(Context mContext){
        sharedpreferences = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        admob_interstitial_id = sharedpreferences.getString("admob_interstitial", "");
        admob_native = sharedpreferences.getString("admob_native", "");
        admob_banner = sharedpreferences.getString("admob_banner", "");
        admob_app_open = sharedpreferences.getString("admob_app_open", "");
        fb_interstitial = sharedpreferences.getString("fb_interstitial", "");
        fb_native = sharedpreferences.getString("fb_native", "");
        fb_banner = sharedpreferences.getString("fb_banner", "");
        admob_native_exit_visible = sharedpreferences.getBoolean("admob_native_exit_visible", false);
        fb_native_exit_visible = sharedpreferences.getBoolean("fb_native_exit_visible", false);
        fb_native_visible = sharedpreferences.getBoolean("fb_native_visible", false);
        admob_native_visible = sharedpreferences.getBoolean("admob_native_visible", false);
        admob_interstitial_visible = sharedpreferences.getBoolean("admob_interstitial_visible", false);
        fb_interstitial_visible = sharedpreferences.getBoolean("fb_interstitial_visible", false);
        admob_app_open_visible = sharedpreferences.getBoolean("admob_app_open_visible", false);
        native_adapter_visible = sharedpreferences.getBoolean("native_adapter_visible", false);
        fb_banner_visible = sharedpreferences.getBoolean("fb_banner_visible", false);

    }
    static Dialog dialog;
    static boolean isShowing = false;

    static void showWaitDialog() {

        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);

        TextView textView = dialog.findViewById(R.id.loading_text);
        textView.setText("Loading Ad...");


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        Activity activity = (Activity) mContext;
        if (!(activity.isFinishing()) && !(activity.isDestroyed())) {
            try {
                dialog.show();
                isShowing = true;
            } catch (Exception e) {

            }
        }


    }

    public static void dismissWaitDialog() {
        if (isShowing) {
            try {
                isShowing = false;
                dialog.dismiss();

            } catch (Exception e) {
                Log.i("TAG", "hideLoadingDialog: unable to dismiss dialog. -> ", e);
            }
        }
    }

    public static boolean checkInternetConnected(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

}

