package org.dash.dashj.demo


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.vote_activity.*
import org.bitcoinj.masternode.owner.MasternodeControl
import org.dash.dashj.demo.ui.governancelist.GovernanceProposalData
import org.dash.dashj.demo.util.GovernanceHelper
import java.io.File

class VoteActivity : AppCompatActivity() {

    companion object {

        private const val EXTRA_HASH = "extra_hash"
        private const val EXTRA_PROPOSAL_DATA = "extra_proposal_data"

        fun createIntent(context: Context, hash: String, proposalData: String): Intent {
            val intent = Intent(context, VoteActivity::class.java)
            intent.putExtra(EXTRA_HASH, hash)
            intent.putExtra(EXTRA_PROPOSAL_DATA, proposalData)
            return intent
        }
    }

    private lateinit var proposalHash: String
    private lateinit var proposal: GovernanceProposalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vote_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        initView()
    }

    private fun initView() {
        val extras = intent.extras
        proposalHash = extras.getString(EXTRA_HASH)
        val proposalData = extras.getString(EXTRA_PROPOSAL_DATA)
        proposal = GovernanceHelper.parseProposal(proposalData)

        title = "Vote on Proposal"
        toolbar.subtitle = proposal.name

        hashView.text = "hash: $proposalHash"

        voteBtnView.setOnClickListener {
            voteOnProposal()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun voteOnProposal() {

        val ipPort = ipInputView.text.toString()
        val masternodePrivKey = masternodePrivKeyInputView.text.toString()
        val collateralTxid = collateralTxidInputView.text.toString()
        val collateralIndex = collateralIndexInputView.text.toString()
        val voteOutcome = when (voteOutcomeView.checkedRadioButtonId) {
            R.id.voteYesView -> "yes"
            R.id.voteAbstainView -> "abstain"
            R.id.voteNoView -> "no"
            else -> throw IllegalStateException()
        }

        val control = MasternodeControl(org.bitcoinj.core.Context.get(), null as File?)
        control.addConfig("masternode", ipPort, masternodePrivKey, collateralTxid, collateralIndex)

        val error = StringBuilder()
        val broadcast = control.voteAlias("masternode", proposalHash, "funding", voteOutcome, error)

        voteResultView.visibility = View.VISIBLE
        voteResultView.text = "$error\n$broadcast"
    }

}
