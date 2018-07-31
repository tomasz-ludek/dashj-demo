package org.dash.dashj.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.dash.dashj.demo.ui.blocklist.BlockListFragment
import org.dash.dashj.demo.ui.peerlist.PeerListFragment
import org.dash.dashj.demo.ui.sporklist.SporkListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SporkListFragment.newInstance())
//                    .replace(R.id.container, PeerListFragment.newInstance())
//                    .replace(R.id.container, BlockListFragment.newInstance())
                    .commitNow()
        }
    }
}
