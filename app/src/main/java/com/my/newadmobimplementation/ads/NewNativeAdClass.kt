package com.my.newadmobimplementation.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

import com.my.newadmobimplementation.NetworkCheck
import com.my.newadmobimplementation.R
import com.my.newadmobimplementation.ads.InterstitialMaster
import com.my.newadmobimplementation.ads.NativeMaster.backPressNative
import com.my.newadmobimplementation.ads.NativeMaster.nativeAdMobHashMap
import com.my.newadmobimplementation.ads.NativeMaster.nativeFbHashMap


object NewNativeAdClass {
    private val logTag = "Ads_"

    @RequiresApi(Build.VERSION_CODES.M)
    fun adNativeAdExit(
        mContext: Activity?,



        ) {
        Log.d(logTag, "exit nativeAdMobHashMap" + nativeAdMobHashMap!!.size)
        Log.d(logTag, "exit nativeFbHashMap" + nativeFbHashMap!!.size)




        if (NetworkCheck.isNetworkAvailable(mContext!!)){
            if (InterstitialMaster.admob_native_exit_visible) {

                if (nativeAdMobHashMap!!.containsKey(mContext!!::class.java.name)) {
                    val nativeAd: NativeAd? = nativeAdMobHashMap!![mContext::class.java.name]
                    Log.d(logTag, "Previous ad is loaded")
                    backPressNative = nativeAd!!
                } else {
                    Log.d(logTag, "Previous ad is reloaded Exit")
                    if (InterstitialMaster.admob_native != "" && InterstitialMaster.admob_native != "null") {
                        Log.d(logTag, "Previous ad is reloaded")
                        reloadAdExit(mContext)

                    } else if (InterstitialMaster.fb_native_exit_visible) {

                        val fb = nativeFbHashMap?.get(mContext::class.java.name)
                        if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                            backPressNative = fb!!
                        } else {
                            if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") loadFBNativeExit(
                                mContext
                            )
                        }
                    }
                }
            } else if (InterstitialMaster.fb_native_exit_visible) {

                val fb: com.facebook.ads.NativeAd? =
                    nativeFbHashMap!![mContext::class.java.name]
                if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                    backPressNative = fb!!
                } else {
                    if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {

                        loadFBNativeExit(
                            mContext
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun adNativeAd(
        mContext: Activity?,
        isMedia: Boolean,
        adContainer: CardView?,
        isMediumAd: Boolean = false,
        onfailed: () -> Unit
    ) {
        Log.d(logTag, "exit nativeAdMobHashMap" + nativeAdMobHashMap!!.size)
        Log.d(logTag, "exit nativeFbHashMap" + nativeFbHashMap!!.size)
        lateinit var defaultAdviewMedia: NativeAdView
        lateinit var defaultAdviewBanner: NativeAdView
        if (isMedia) {
            defaultAdviewMedia = if (isMediumAd) {
                mContext!!.layoutInflater.inflate(
                    R.layout.native_ad_admob_layout_large_medium,
                    null
                ) as NativeAdView
            } else {
                mContext!!.layoutInflater.inflate(
                    R.layout.native_ad_admob_layout_large_normal,
                    null
                ) as NativeAdView
            }
        } else {
            defaultAdviewBanner = if (isMediumAd) {
                mContext!!.layoutInflater.inflate(
                    R.layout.native_ad_admob_layout_banner_small,
                    null
                ) as NativeAdView
            } else {
                mContext!!.layoutInflater.inflate(
                    R.layout.native_ad_admob_layout_banner_normal,
                    null
                ) as NativeAdView
            }
        }



        if (NetworkCheck.isNetworkAvailable(mContext)){
            if (InterstitialMaster.admob_native_visible) {
                adContainer!!.visibility = View.VISIBLE
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    val nativeAd: NativeAd? = nativeAdMobHashMap!![mContext::class.java.name]
                    Log.d(logTag, "Previous ad is loaded")
                    if (isMedia) {
                        populateMediaNative(nativeAd!!, defaultAdviewMedia)
                        adContainer.addView(defaultAdviewMedia)
                    } else {
                        populateBannerNative(nativeAd!!, defaultAdviewBanner)
                        adContainer.addView(defaultAdviewBanner)
                    }
                } else {
                    Log.d(logTag, "Previous ad is reloaded")
                    if (InterstitialMaster.admob_native != "" && InterstitialMaster.admob_native != "null") {
                        if (isMedia) {
                            reloadAd(mContext, adContainer, isMedia, defaultAdviewMedia, isMediumAd)
                        } else {
                            reloadAd(mContext, adContainer, isMedia, defaultAdviewBanner, isMediumAd)
                        }
                    } else if (InterstitialMaster.fb_native_visible) {
                        val fb: com.facebook.ads.NativeAd? =
                            nativeFbHashMap!![mContext::class.java.name]
                        if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                            if (isMedia) {
                                inflateAdMedia(fb!!, mContext, isMediumAd)
                            } else {
                                inflateAdBanner(fb!!, mContext, isMediumAd)
                            }
                        } else {
                            if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
                                loadFBNative(
                                    mContext,
                                    isMedia, isMediumAd
                                )
                            }
                        }
                    } else {
                        onfailed()
                    }
                }
            } else if (InterstitialMaster.fb_native_visible) {
                adContainer?.visibility = View.VISIBLE
                if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                    val fb: com.facebook.ads.NativeAd? =
                        nativeFbHashMap!!.get(mContext::class.java.name)
                    if (isMedia) {
                        inflateAdMedia(fb!!, mContext, isMediumAd)
                    } else {
                        inflateAdBanner(fb!!, mContext, isMediumAd)
                    }
                } else {
                    if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") loadFBNative(
                        mContext,
                        isMedia, isMediumAd
                    )
                }
            } else {
                onfailed.invoke()
            }
        }else{
            onfailed.invoke()
        }
    }

    private fun reloadAd(
        mContext: Activity?,
        frameLayout: CardView,
        isMedia: Boolean,
        defaultAdview: NativeAdView,
        isMediumAd: Boolean
    ) {
        val builder: AdLoader.Builder =
            AdLoader.Builder(mContext!!, InterstitialMaster.admob_native)
        frameLayout.visibility = View.VISIBLE

        builder.forNativeAd { mNativeAd: NativeAd ->
            nativeAdMobHashMap!![mContext::class.java.name] = mNativeAd
            Log.d("Ads_", "native_ad")

            if (isMedia) {
                populateMediaNative(mNativeAd, defaultAdview)
            } else {
                populateBannerNative(mNativeAd, defaultAdview)
            }

            frameLayout.addView(defaultAdview)
        }
        val videoOptions: VideoOptions = VideoOptions.Builder()
            .setStartMuted(false)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: LoadAdError) {
//                frameLayout.setVisibility(View.GONE);
                Log.d("Ads_", "Admob Native Failed to Load.")
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }
                if (InterstitialMaster.fb_native_visible) {
                    Log.d("Ads_", "Fb Simple load on Failed.   1")
//
                    if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                        val fb: com.facebook.ads.NativeAd =
                            nativeFbHashMap!![mContext::class.java.name]!!
                        if (isMedia) {
                            Log.d("Ads_", "Fb Simple load on Failed.   isMedia")
                            inflateAdMedia(fb, mContext, isMedia)
                        } else {
                            inflateAdBanner(fb, mContext, isMedia)
                            Log.d("Ads_", "Fb Simple load on Failed.   else")
                        }
                    } else {
                        Log.d("Ads_", "Fb Simple load on Failed.   Reload")
                        if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") loadFBNative(
                            mContext,
                            isMedia, isMediumAd
                        )
                    }
                }

            }

            override fun onAdClicked() {
                super.onAdClicked()
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }

            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("Ads_", "Admob Native Loaded..")
                frameLayout.visibility = View.VISIBLE
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun reloadAdExit(
        mContext: Activity?,


        ) {
        val builder: AdLoader.Builder =
            AdLoader.Builder(mContext!!, InterstitialMaster.admob_native)


        builder.forNativeAd { mNativeAd: NativeAd ->
            nativeAdMobHashMap!![mContext::class.java.name] = mNativeAd
            Log.d("Ads_", "native_ad")
            backPressNative = mNativeAd
        }
        val videoOptions: VideoOptions = VideoOptions.Builder()
            .setStartMuted(false)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: LoadAdError) {
//                frameLayout.setVisibility(View.GONE);
                Log.d("Ads_", "Admob Native Failed to Load.")
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }


                if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                    val fb: com.facebook.ads.NativeAd? =
                        nativeFbHashMap!![mContext::class.java.name]
                    backPressNative = fb!!
                } else {
                    if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
                        Log.d(logTag, "bottom loaded: ")
                        loadFBNativeExit(mContext)
                    }
                }

            }

            override fun onAdClicked() {
                super.onAdClicked()
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }
                backPressNative = null

//                CustomDialog.getInstance(mContext)!!.dismissDialog()

                reloadAdExit(mContext)

            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("Ads_", "Admob Native Loaded..")

            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateMediaNative(nativeAd: NativeAd, adView: NativeAdView) {


        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.adHeadline)
        adView.bodyView = adView.findViewById(R.id.adBody)
        adView.callToActionView = adView.findViewById(R.id.adCallToAction)
        adView.iconView = adView.findViewById(R.id.adAppIcon)
        adView.mediaView = adView.findViewById<View>(R.id.adMedia) as MediaView
        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.findViewById<View>(R.id.adMedia) as MediaView).setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                if (child is ImageView) {
                    child.scaleType = ImageView.ScaleType.FIT_XY
                }
            }

            override fun onChildViewRemoved(view: View, view1: View) {}
        })
        adView.mediaView!!.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
            adView.findViewById<CardView>(R.id.adIconCard).visibility= View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
        adView.visibility = View.VISIBLE

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc: VideoController = nativeAd.mediaContent!!.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }
    }

    private fun populateBannerNative(nativeAd: NativeAd, adView: NativeAdView) {


        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.adHeadline)
        adView.bodyView = adView.findViewById(R.id.adBody)
        adView.callToActionView = adView.findViewById(R.id.adCallToAction)
        adView.iconView = adView.findViewById(R.id.adAppIcon)
        //        adView.setMediaView((MediaView) adView.findViewById(R.id.adMedia));
        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        /* ((MediaView) adView.findViewById(R.id.adMedia)).setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child instanceof ImageView){
                    ((ImageView) child).setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

            }

            @Override
            public void onChildViewRemoved(View view, View view1) {

            }
        });
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());*/

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.findViewById<CardView>(R.id.adIconCard).visibility= View.GONE
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.findViewById<CardView>(R.id.adIconCard).visibility= View.VISIBLE
            adView.iconView!!.visibility = View.VISIBLE
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
        adView.visibility = View.VISIBLE

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        /* VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }*/
    }

    // -------- FB Native Starts ---------------
    fun loadFBNative(mContext: Activity?, isMedia: Boolean, isMediumAd: Boolean) {

        try {
            if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
                val fbNativeAd = NativeAd(mContext, InterstitialMaster.fb_native)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {
                        // Native ad finished downloading all assets
                        Log.e(logTag, "FB Native ad finished downloading all assets.")
                    }

                    override fun onError(ad: Ad, adError: AdError) {
                        // Native ad failed to load
                        Log.e(logTag, "FB Native ad failed to load: " + adError.errorMessage)
                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }
                    }

                    override fun onAdLoaded(ad: Ad) {
                        // Native ad is loaded and ready to be displayed
                        Log.d(logTag, "FB Native ad is loaded and ready to be displayed!")
                        if (fbNativeAd !== ad) {
                            return
                        }
                        nativeFbHashMap!![mContext!!::class.java.name] = fbNativeAd
                        // Inflate Native Ad into Container
                        if (isMedia) {
                            inflateAdMedia(fbNativeAd, mContext, isMediumAd)
                        } else {
                            inflateAdBanner(fbNativeAd, mContext, isMediumAd)
                        }
                    }

                    override fun onAdClicked(ad: Ad) {
                        // Native ad clicked
                        Log.d(logTag, "FB Native ad clicked!")
                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }
                    }

                    override fun onLoggingImpression(ad: Ad) {
                        // Native ad impression
                        Log.d(logTag, "FB Native ad impression logged!")
                    }
                }

                // Request an ad
                fbNativeAd.loadAd(
                    fbNativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build()
                )
                //                } else {
//                    inflateAd(fbNativeAd);
//                }
            }
        } catch (ex: Exception) {
            Log.d("Ads_", "CrashLog: " + ex.message)
        }
    }

    fun loadFBNativeExit(mContext: Activity) {

        try {
            if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
//                if (fbNativeAd == null) { // new impression is required here everytime.
                val fbNativeAd = NativeAd(mContext, InterstitialMaster.fb_native)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {
                        // Native ad finished downloading all assets
                        Log.e(logTag, "FB Native ad finished downloading all assets.Exit")
                    }

                    override fun onError(ad: Ad, adError: AdError) {
                        // Native ad failed to load
                        Log.e(logTag, "FB Native ad failed to load: " + adError.errorMessage)
                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }
                        backPressNative = null
                    }

                    override fun onAdLoaded(ad: Ad) {
                        // Native ad is loaded and ready to be displayed
                        Log.d(logTag, "FB Native ad is loaded and ready to be displayed!Exit")
                        if (fbNativeAd !== ad) {
                            return
                        }
                        nativeFbHashMap!![mContext!!::class.java.name] = fbNativeAd
                        backPressNative = fbNativeAd
                        // Inflate Native Ad into Container

                    }

                    override fun onAdClicked(ad: Ad) {
                        // Native ad clicked
                        Log.d(logTag, "FB Native ad clicked!")
                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }
                        backPressNative = null

//                        CustomDialog.getInstance(mContext)!!.dismissDialog()

                        loadFBNativeExit(mContext)

                    }

                    override fun onLoggingImpression(ad: Ad) {
                        // Native ad impression
                        Log.d(logTag, "FB Native ad impression logged!Exit")
                    }
                }

                // Request an ad
                fbNativeAd.loadAd(
                    fbNativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build()
                )
                //                } else {
//                    inflateAd(fbNativeAd);
//                }
            }
        } catch (ex: Exception) {
            Log.d("Ads_", "CrashLog: " + ex.message)
        }
    }


    private fun inflateAdMedia(
        nativeAd: com.facebook.ads.NativeAd,
        mContext: Activity?,
        isMediumAd: Boolean
    ) {
        nativeAd.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        // Add the Ad view into the ad container.
        val nativeAdLayout: NativeAdLayout? = mContext!!.findViewById(R.id.fbNativeAdContainer)
        nativeAdLayout?.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView = if (isMediumAd) {
            inflater.inflate(
                R.layout.native_ad_fb_layout_medium,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        } else {
            inflater.inflate(
                R.layout.native_ad_fb_layout_large_normal,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        }
        nativeAdLayout?.addView(fbNativeAdView)
        val adChoicesContainer: LinearLayout? = fbNativeAdView.findViewById(R.id.adChoicesContainer)
        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer?.removeAllViews()
        adChoicesContainer?.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdIcon =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdIcon)
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(R.id.nativeAdTitle)
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
//        val nativeAdSocialContext =
//            fbNativeAdView.findViewById<TextView>(R.id.nativeAdSocialContext)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction =
            fbNativeAdView.findViewById<Button>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
//        if (!isMediumAd){
//            nativeAdSocialContext.text = nativeAd.adSocialContext
//        }

        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }

    private fun inflateAdMediaExit(
        nativeAd: com.facebook.ads.NativeAd?,
        mContext: Activity?,
        nativeAdLayout: com.facebook.ads.NativeAdLayout?
    ) {
        nativeAd!!.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        nativeAdLayout!!.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView =
            inflater.inflate(
                R.layout.native_ad_fb_layout_large_normal,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        nativeAdLayout.addView(fbNativeAdView)
        val adChoicesContainer: LinearLayout? = fbNativeAdView.findViewById(R.id.adChoicesContainer)
        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer!!.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdIcon =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdIcon)
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(R.id.nativeAdTitle)
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
//        val nativeAdSocialContext =
//            fbNativeAdView.findViewById<TextView>(R.id.nativeAdSocialContext)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction =
            fbNativeAdView.findViewById<Button>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
//        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }

    private fun inflateAdBanner(
        nativeAd: com.facebook.ads.NativeAd,
        mContext: Activity?,
        isMediumAd: Boolean
    ) {
        nativeAd.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        // Add the Ad view into the ad container.
        val nativeAdLayout: NativeAdLayout? = mContext!!.findViewById(R.id.fbNativeAdContainer)
        nativeAdLayout!!.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView = if (isMediumAd) {
            inflater.inflate(
                R.layout.native_ad_fb_layout_banner_medium,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        } else {
            inflater.inflate(
                R.layout.native_ad_fb_layout_banner_normal,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        }
        nativeAdLayout.addView(fbNativeAdView)

        // Add the AdOptionsView
        val adChoicesContainer =
            fbNativeAdView.findViewById<LinearLayout>(R.id.adChoicesContainer)
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        /* com.facebook.ads.MediaView nativeAdIcon = fbNativeAdView.findViewById(R.id.nativeAdIcon);*/
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(
            R.id.nativeAdTitle
        )
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction = fbNativeAdView.findViewById<TextView>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, clickableViews
        )
    }

    private fun inflateAdBannerExit(
        nativeAd: com.facebook.ads.NativeAd?,
        mContext: Activity?,
        nativeAdLayout: com.facebook.ads.NativeAdLayout
    ) {
        nativeAd!!.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        // Add the Ad view into the ad container.

        nativeAdLayout!!.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView = inflater.inflate(
            R.layout.native_ad_fb_layout_banner_normal,
            nativeAdLayout,
            false
        ) as ConstraintLayout
        nativeAdLayout.addView(fbNativeAdView)

        // Add the AdOptionsView
        val adChoicesContainer =
            fbNativeAdView.findViewById<LinearLayout>(R.id.adChoicesContainer)
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        /* com.facebook.ads.MediaView nativeAdIcon = fbNativeAdView.findViewById(R.id.nativeAdIcon);*/
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(
            R.id.nativeAdTitle
        )
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction = fbNativeAdView.findViewById<TextView>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction


        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, clickableViews
        )
    } // -------- FB Native Ends ----------------


    public fun inflateAdMobNativeDialogBox(
        mNativeAd: NativeAd,
        adView: NativeAdView,
        isMedia: Boolean
    ) {

        if (isMedia) {
            populateMediaNative(mNativeAd, adView)
        } else {
            populateBannerNative(mNativeAd, adView)
        }
    }

    public fun inflateFbNativeDialogBox(
        mContext: Activity?,
        fbNative: com.facebook.ads.NativeAd?,
        binding: com.facebook.ads.NativeAdLayout,
        isMedia: Boolean
    ) {

        if (isMedia) {
            inflateAdMediaExit(fbNative!!, mContext, binding)
        } else {
            inflateAdBannerExit(fbNative!!, mContext, binding)
        }
    }


    fun adNativeAdAdapter(
        mContext: Activity?,
        adView: NativeAdView,
        isMedia: Boolean,
        nativeAdLayout: com.facebook.ads.NativeAdLayout?,
        isMediumAd:Boolean=false,

        callback: (Any?) -> Unit,


        ) {

        if (InterstitialMaster.admob_native_visible) {
            if (nativeAdMobHashMap!!.containsKey(mContext!!::class.java.name)) {
                val nativeAd: NativeAd? = nativeAdMobHashMap!![mContext::class.java.name]
                Log.d(logTag, "Previous ad is loaded")
                inflateAdMobNativeAdapter(nativeAd!!, adView, isMedia)
            } else {
                if (InterstitialMaster.admob_native != "" && InterstitialMaster.admob_native != "null") {
                    Log.d(logTag, "Previous ad is reloaded")
                    nativeAdLayout?.let { reloadAdAdapter(mContext, adView, isMedia, it, isMediumAd,callback) }
                }

                else if (InterstitialMaster.fb_native_visible) {

                    val fb = nativeFbHashMap?.get(mContext::class.java.name)
                    if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                        inflateFbNativeAdapter(mContext,fb,isMedia,isMediumAd,nativeAdLayout!!)
                    }else{
                        if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {

                            nativeAdLayout?.let {
                                loadFBNativeAdapter(
                                    mContext,
                                    isMedia, it, isMediumAd,callback
                                )
                            }
                        }
                    }
                }
            } } else if (InterstitialMaster.fb_native_visible) {
            val fb = nativeFbHashMap?.get(mContext!!::class.java.name)
            if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                inflateFbNativeAdapter(mContext,fb,isMedia,isMediumAd,nativeAdLayout!!)
            }else{
                if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") nativeAdLayout?.let {
                    loadFBNativeAdapter(
                        mContext,
                        isMedia, it, isMediumAd,callback
                    )
                }
            }


        }
    }


    private fun loadFBNativeAdapter(
        mContext: Context?,
        isMedia: Boolean,
        nativeAdLayout: NativeAdLayout,
        isMediumAd: Boolean,
        callback: (Any?) -> Unit
    ) {

        try {
            if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
//                if (fbNativeAd == null) { // new impression is required here everytime.
                val fbNativeAd = NativeAd(mContext, InterstitialMaster.fb_native)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {
                        // Native ad finished downloading all assets
                        Log.e(logTag, "FB Native ad finished downloading all assets.Adapter")
                    }

                    override fun onError(ad: Ad, adError: AdError) {
                        // Native ad failed to load
                        Log.e(logTag, "FB Native ad failed to load: " + adError.errorMessage)
                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }


                    }

                    override fun onAdLoaded(ad: Ad) {
                        // Native ad is loaded and ready to be displayed
                        Log.d(logTag, "FB Native ad is loaded and ready to be displayed!Adapter")
                        if (fbNativeAd !== ad) {
                            return
                        }
                        nativeFbHashMap!![mContext!!::class.java.name] = fbNativeAd
                        callback(fbNativeAd)
                        inflateFbNativeAdapter(mContext, ad, isMedia, isMediumAd, nativeAdLayout)
                        // Inflate Native Ad into Container

                    }

                    override fun onAdClicked(ad: Ad) {
                        // Native ad clicked

                        if (nativeFbHashMap!!.containsKey(mContext!!::class.java.name)) {
                            nativeFbHashMap!!.remove(mContext::class.java.name)
                        }
                        Log.d(logTag, "FB Native ad clicked!")

                    }

                    override fun onLoggingImpression(ad: Ad) {
                        // Native ad impression
                        Log.d(logTag, "FB Native ad impression logged!Exit")
                    }
                }

                // Request an ad
                fbNativeAd.loadAd(
                    fbNativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build()
                )
                //                } else {
//                    inflateAd(fbNativeAd);
//                }
            }
        } catch (ex: Exception) {
            Log.d("Ads_", "CrashLog: " + ex.message)
        }
    }

    private fun reloadAdAdapter(
        mContext: Context?,
        adView: NativeAdView,
        isMedia: Boolean,
        nativeAdLayout: NativeAdLayout,
        isMediumAd:Boolean,
        callback: (Any?) -> Unit

    ) {
        val builder: AdLoader.Builder =
            AdLoader.Builder(mContext!!, InterstitialMaster.admob_native)


        builder.forNativeAd { mNativeAd: NativeAd ->
            nativeAdMobHashMap!![mContext::class.java.name] = mNativeAd
            Log.d("Ads_", "native_ad")
            callback(mNativeAd)
            inflateAdMobNativeAdapter(mNativeAd, adView, isMedia)
        }
        val videoOptions: VideoOptions = VideoOptions.Builder()
            .setStartMuted(false)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader: AdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: LoadAdError) {
//                frameLayout.setVisibility(View.GONE);
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }
                Log.d("Ads_", "Admob Native Failed to Load.")
                val fb = nativeFbHashMap?.get(mContext::class.java.name)
                if (nativeFbHashMap!!.containsKey(mContext::class.java.name)) {
                    inflateFbNativeAdapter(mContext,fb,isMedia,true,nativeAdLayout!!)
                }else{
                    if (InterstitialMaster.fb_native != "" && InterstitialMaster.fb_native != "null") {
                        Log.d(logTag, "bottom loaded: ")
                        loadFBNativeAdapter(mContext, isMedia, nativeAdLayout, isMediumAd,callback)
                    }
                }





            }

            override fun onAdClicked() {
                super.onAdClicked()
                if (nativeAdMobHashMap!!.containsKey(mContext::class.java.name)) {
                    nativeAdMobHashMap!!.remove(mContext::class.java.name)
                }


            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("Ads_", "Admob Native Loaded..")

            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    public fun inflateAdMobNativeAdapter(
        mNativeAd: NativeAd,
        adView: com.google.android.gms.ads.nativead.NativeAdView,
        isMedia: Boolean
    ) {

        if (isMedia) {
            populateMediaNative(mNativeAd, adView)
        } else {
            populateBannerNative(mNativeAd, adView)
        }
    }

    public fun inflateFbNativeAdapter(
        mContext: Context?,
        fbNative: com.facebook.ads.NativeAd?,
        isMedia: Boolean, isMediumAd:Boolean,
        nativeAdLayout: com.facebook.ads.NativeAdLayout
    ) {
        Log.d(logTag, "inflateFbNativeDialogBox: ")
        if (isMedia) {
            inflateAdMediaAdapter(fbNative!!, mContext, isMediumAd, nativeAdLayout)
        } else {
            inflateAdBannerAdapter(fbNative!!, mContext, isMediumAd, nativeAdLayout)
        }
    }

    private fun inflateAdBannerAdapter(
        nativeAd: com.facebook.ads.NativeAd,
        mContext: Context?,
        isMediumAd: Boolean,
        nativeAdLayout: NativeAdLayout?
    ) {
        nativeAd.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        // Add the Ad view into the ad container.

        nativeAdLayout!!.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView = if (isMediumAd) {
            inflater.inflate(
                R.layout.native_ad_fb_layout_banner_medium,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        } else {
            inflater.inflate(
                R.layout.native_ad_fb_layout_banner_normal,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        }
        nativeAdLayout.addView(fbNativeAdView)

        // Add the AdOptionsView
        val adChoicesContainer =
            fbNativeAdView.findViewById<LinearLayout>(R.id.adChoicesContainer)
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        /* com.facebook.ads.MediaView nativeAdIcon = fbNativeAdView.findViewById(R.id.nativeAdIcon);*/
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(
            R.id.nativeAdTitle
        )
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction = fbNativeAdView.findViewById<TextView>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        if (!isMediumAd){
            sponsoredLabel.text = nativeAd.sponsoredTranslation
        }


        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, clickableViews
        )
    }

    private fun inflateAdMediaAdapter(
        nativeAd: com.facebook.ads.NativeAd,
        mContext: Context?,
        isMediumAd: Boolean,
        nativeAdLayout: NativeAdLayout?
    ) {
        nativeAd.unregisterView()
        val fbNativeAdView: ConstraintLayout?
        // Add the Ad view into the ad container.

        nativeAdLayout?.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(mContext)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        fbNativeAdView = if (isMediumAd) {
            inflater.inflate(
                R.layout.native_ad_fb_layout_medium,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        } else {
            inflater.inflate(
                R.layout.native_ad_fb_layout_large_normal,
                nativeAdLayout,
                false
            ) as ConstraintLayout
        }
        nativeAdLayout?.addView(fbNativeAdView)
        val adChoicesContainer: LinearLayout? = fbNativeAdView.findViewById(R.id.adChoicesContainer)
        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(mContext, nativeAd, nativeAdLayout)
        adChoicesContainer?.removeAllViews()
        adChoicesContainer?.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdIcon =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdIcon)
        val nativeAdTitle = fbNativeAdView.findViewById<TextView>(R.id.nativeAdTitle)
        val nativeAdMedia =
            fbNativeAdView.findViewById<com.facebook.ads.MediaView>(R.id.nativeAdMedia)
//        val nativeAdSocialContext =
//            fbNativeAdView.findViewById<TextView>(R.id.nativeAdSocialContext)
        val nativeAdBody = fbNativeAdView.findViewById<TextView>(R.id.nativeAdBody)
        val sponsoredLabel = fbNativeAdView.findViewById<TextView>(R.id.nativeAdSponsoredLabel)
        val nativeAdCallToAction =
            fbNativeAdView.findViewById<Button>(R.id.nativeAdCallToAction)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
//        if (!isMediumAd){
//            nativeAdSocialContext.text = nativeAd.adSocialContext
//        }
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            fbNativeAdView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }





}