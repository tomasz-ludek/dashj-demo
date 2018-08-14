package org.dash.dashj.demo

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_app_bar.*
import org.dash.dashj.demo.ui.blocklist.BlockListFragment
import org.dash.dashj.demo.ui.masternodelist.GovernanceListFragment
import org.dash.dashj.demo.ui.masternodelist.MasternodeListFragment
import org.dash.dashj.demo.ui.peerlist.PeerListFragment
import org.dash.dashj.demo.ui.sporklist.SporkListFragment
import org.dash.dashj.demo.ui.util.UtilsFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var headerView: View
    private lateinit var walletManager: WalletManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, PeerListFragment.newInstance())
                    .commitNow()
        }

        walletManager = WalletManager.getInstance()
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

        headerView.findViewById<View>(R.id.mainnetActionView).setOnClickListener {
            switchWallet(Constants.WALLET_MAINNET_NAME)
        }

        headerView.findViewById<View>(R.id.testnetActionView).setOnClickListener {
            switchWallet(Constants.WALLET_TESTNET3_NAME)
        }

        headerView.findViewById<View>(R.id.dummyTestnetActionView).setOnClickListener {
            switchWallet(Constants.WALLET_SEED_TESTNET3_NAME)
        }

        headerView.findViewById<View>(R.id.showWalletsView).setOnClickListener {
            val walletsListView = headerView.findViewById<View>(R.id.walletsListView)
            expandWalletsView(walletsListView.visibility == View.GONE)
        }

        headerView.findViewById<TextView>(R.id.activeWalletView).text = walletManager.configName
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
        val preferences = MainPreferences(this)
        preferences.latestConfigName = walletName
        walletManager.setActiveWallet(walletName, this)
        switchFragment(R.id.nav_peers)
        nav_view.setCheckedItem(R.id.nav_peers)
        headerView.findViewById<TextView>(R.id.activeWalletView).text = walletName
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
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commitNow()
    }
}
