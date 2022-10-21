package com.my.newadmobimplementation

import android.app.Application
import com.hindi.keybaord.newhindikeyboard.interstatialAdvanceNewHindi.NativeMaster
import com.my.newadmobimplementation.ads.OpenApp

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        NativeMaster.nativeFbHashMap!!.clear()
        NativeMaster.nativeAdMobHashMap!!.clear()
        NativeMaster.backPressNative = null
        OpenApp(this@ApplicationClass)
    }
}