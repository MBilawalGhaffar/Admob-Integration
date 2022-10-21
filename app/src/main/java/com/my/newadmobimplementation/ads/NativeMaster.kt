package com.my.newadmobimplementation.ads

import com.facebook.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

object NativeMaster {
    var mNativeAd: NativeAd? = null
    var fbNativeAd: com.facebook.ads.NativeAd? = null
    var backPressNative: Any? = Any()
    var nativeShowTime: Long = 0
 public   var fbAdView: AdView? = null

    public var nativeAdMobHashMap: HashMap<String, NativeAd>? = HashMap()
    public var nativeFbHashMap:HashMap<String, com.facebook.ads.NativeAd>?= HashMap()

}