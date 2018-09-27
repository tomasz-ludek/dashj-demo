package org.dash.dashj.demo.ui.evouserlist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import org.bitcoinj.core.Coin
import org.bitcoinj.core.Transaction
import org.bitcoinj.evolution.EvolutionUser
import org.dash.dashj.demo.ui.util.SingleLiveEvent
import org.dashj.dashjinterface.WalletAppKitService
import org.dashj.dashjinterface.data.DjInterfaceViewModel
import org.dashj.dashjinterface.data.EvoUsersLiveData

class EvoUserListViewModel(application: Application) : DjInterfaceViewModel(application) {

    private val _evoUsers = EvoUsersLiveData(application)
    val evoUsers: EvoUsersLiveData
        get() = _evoUsers

    private val _showMessageAction = SingleLiveEvent<Pair<String, String>>()
    val showMessageAction
        get() = _showMessageAction

    private val _showErrorMessageAction = SingleLiveEvent<Pair<String, String>>()
    val showErrorMessageAction
        get() = _showErrorMessageAction

    fun createUser() {
        djService.value?.createUser(object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(result: Transaction) {
                val message = "Transaction hash: ${result.hashAsString}"
                _showMessageAction.call(Pair("SubTxRegister", message))
            }

            override fun onFailure(ex: Exception) {
                _showErrorMessageAction.call(Pair("SubTxRegister", ex.message!!))
            }
        })
    }

    fun topUpUser(evoUser: EvolutionUser, credits: Coin) {
        djService.value?.topUpUser(evoUser, credits, object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(result: Transaction) {
                val message = "Transaction hash: ${result.hashAsString}"
                _showMessageAction.call(Pair("SubTxTopup", message))
            }

            override fun onFailure(ex: Exception) {
                _showErrorMessageAction.call(Pair("SubTxTopup", ex.message!!))
            }
        })
    }

    fun reset(evoUser: EvolutionUser) {
        djService.value?.resetUser(evoUser, object : WalletAppKitService.Result<Transaction> {

            override fun onSuccess(result: Transaction) {
                val message = "Transaction hash: ${result.hashAsString}"
                _showMessageAction.call(Pair("SubTxResetKey", message))
            }

            override fun onFailure(ex: Exception) {
                _showErrorMessageAction.call(Pair("SubTxResetKey", ex.message!!))
            }
        })
    }
}
