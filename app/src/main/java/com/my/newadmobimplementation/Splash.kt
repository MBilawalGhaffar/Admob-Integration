package com.my.newadmobimplementation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.my.newadmobimplementation.ads.InterstitialMaster

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            InterstitialMaster.getAdIDsFromFirebase(
                this@Splash
            )
        } catch (e: Exception) {
            Log.d("Ads_", e.message.toString())
        }

        Handler(Looper.myLooper()!!).postDelayed(
            {
            startActivity(Intent(this,MainActivity::class.java ))
            finish()
        },1500)
    }
}