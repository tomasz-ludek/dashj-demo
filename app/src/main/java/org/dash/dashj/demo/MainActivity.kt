package org.dash.dashj.demo

import android.app.ActivityOptions
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_app_bar.*
import org.dash.dashj.demo.ui.addresshierarchy.AddressHierarchyFragment
import org.dash.dashj.demo.ui.blocklist.BlockListFragment
import org.dash.dashj.demo.ui.evouserlist.EvoUserListFragment
import org.dash.dashj.demo.ui.governancelist.GovernanceListFragment
import org.dash.dashj.demo.ui.masternodelist.MasternodeListFragment
import org.dash.dashj.demo.ui.peerlist.PeerListFragment
import org.dash.dashj.demo.ui.sporklist.SporkListFragment
import org.dash.dashj.demo.ui.transactionlist.TransactionListFragment
import org.dash.dashj.demo.ui.util.UtilsFragment
import org.dashj.dashjinterface.WalletAppKitService

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var headerView: View
    private lateinit var preferences: MainPreferences

    private var stopServiceWhenOnStop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        preferences = MainPreferences.getInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, PeerListFragment.newInstance())
                    .commitNow()
        }

        initDrawer()
    }

    private fun initDrawer() {
        headerView = nav_view.getHeaderView(0)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                expandWalletsView(false)
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setCheckedItem(R.id.nav_peers)
        nav_view.setNavigationItemSelectedListener(this)

        headerView.findViewById<View>(R.id.showWalletsView).setOnClickListener {
            val walletsListView = headerView.findViewById<View>(R.id.walletsListView)
            expandWalletsView(walletsListView.visibility == View.GONE)
        }

        val walletsListView = headerView.findViewById<ViewGroup>(R.id.walletsListView)
        val layoutInflater = LayoutInflater.from(this)
        for ((key, _) in MainApplication.walletConfigMap) {
            val walletActionView = layoutInflater.inflate(R.layout.wallet_item_view, walletsListView, false) as TextView
            walletsListView.addView(walletActionView)
            walletActionView.tag = key
            walletActionView.text = key
            walletActionView.setOnClickListener {
                switchWallet(it.tag.toString())
            }
        }

        headerView.findViewById<TextView>(R.id.activeWalletView).text = preferences.latestConfigName
    }

    private fun expandWalletsView(expand: Boolean) {
        val headerView = nav_view.getHeaderView(0)
        val walletsListView = headerView.findViewById<View>(R.id.walletsListView)
        val expandIconView = headerView.findViewById<View>(R.id.expandIconView)
        if (expand) {
            walletsListView.visibility = View.VISIBLE
            expandIconView.rotation = 180.0f
        } else {
            walletsListView.visibility = View.GONE
            expandIconView.rotation = 0.0f
        }
    }

    private fun switchWallet(walletName: String) {
        val sameWallet = (preferences.latestConfigName == walletName)
        if (!sameWallet) {
            preferences.latestConfigName = walletName
            stopServiceWhenOnStop = true

            val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
            val intent = SplashActivity.createIntent(this, sameWallet)
            startActivity(intent, options.toBundle())
            finish()
        } else {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onStop() {
        super.onStop()
        if (stopServiceWhenOnStop) {
            WalletAppKitService.stop(this)
        }
    }

    fun setSubTitle(title: CharSequence?) {
        toolbar.subtitle = title
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
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
        switchFragment(item.itemId)
        return true
    }

    private fun switchFragment(navItemId: Int) {
        when (navItemId) {
            R.id.nav_utils -> {
                switchFragment(UtilsFragment.newInstance())
            }
            R.id.nav_peers -> {
                switchFragment(PeerListFragment.newInstance())
            }
            R.id.nav_blocks -> {
                switchFragment(BlockListFragment.newInstance())
            }
            R.id.nav_transaction -> {
                switchFragment(TransactionListFragment.newInstance())
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
            R.id.nav_address -> {
                switchFragment(AddressHierarchyFragment.newInstance())
            }
            R.id.nav_evo_users -> {
                switchFragment(EvoUserListFragment.newInstance())
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commitNow()
    }
}
