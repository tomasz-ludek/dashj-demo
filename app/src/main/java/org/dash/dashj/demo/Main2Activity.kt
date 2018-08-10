package org.dash.dashj.demo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import org.dash.dashj.demo.ui.blocklist.BlockListFragment
import org.dash.dashj.demo.ui.masternodelist.GovernanceListFragment
import org.dash.dashj.demo.ui.masternodelist.MasternodeListFragment
import org.dash.dashj.demo.ui.peerlist.PeerListFragment
import org.dash.dashj.demo.ui.sporklist.SporkListFragment


class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, PeerListFragment.newInstance())
                    .commitNow()
        }

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setCheckedItem(R.id.nav_peers)
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_wallet_mainnet -> {
                stopService(Intent(this, BlockchainSyncService::class.java))
                val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val edit = preferences.edit()
                edit.putBoolean("testnetmode", false)
                edit.commit()
                val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(this.baseContext, 0, Intent(intent), intent.flags))
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            R.id.nav_wallet_testnet -> {
                stopService(Intent(this, BlockchainSyncService::class.java))
                val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val edit = preferences.edit()
                edit.putBoolean("testnetmode", true)
                edit.commit()
                val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(this.baseContext, 0, Intent(intent), intent.flags))
                android.os.Process.killProcess(android.os.Process.myPid())
            }
            R.id.nav_peers -> {
                switchFragment(PeerListFragment.newInstance())
            }
            R.id.nav_blocks -> {
                switchFragment(BlockListFragment.newInstance())
            }
            R.id.nav_sporks -> {
                switchFragment(SporkListFragment.newInstance())
            }
            R.id.nav_masternodes -> {
                switchFragment(MasternodeListFragment.newInstance())
            }
            R.id.nav_governance -> {
                switchFragment(GovernanceListFragment.newInstance())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commitNow()
    }
}
