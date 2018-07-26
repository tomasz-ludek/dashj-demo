package org.dash.dashj.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.dash.dashj.demo.ui.peerlist.PeerListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PeerListFragment.newInstance())
                    .commitNow()
        }
    }
}
