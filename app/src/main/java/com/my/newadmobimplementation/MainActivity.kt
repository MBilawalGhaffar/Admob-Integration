package com.my.newadmobimplementation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.my.newadmobimplementation.ads.ActionOnAdClosedListener
import com.my.newadmobimplementation.ads.InterstitialMaster

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InterstitialMaster.show_interstitial(
            this@MainActivity,
            this@MainActivity,
        ) {
            Toast.makeText(this,"Fetch Ad",Toast.LENGTH_SHORT).show()
            InterstitialMaster.load_interstitial(this)
        }
    }


}