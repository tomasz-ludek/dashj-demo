package org.dash.dashj.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import org.dashj.dashjinterface.WalletAppKitService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            val preferences = MainPreferences(this@SplashActivity)
            WalletAppKitService.init(this, MainApplication.walletConfigMap[preferences.latestConfigName]!!)

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
            finish()
        }, 2000)
    }
}
