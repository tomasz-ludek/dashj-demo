package org.dash.dashj.demo.ui.peerlist

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import android.util.Pair
import org.bitcoinj.core.Peer
import java.net.InetAddress
import java.util.*

class ReverseDnsLiveData : MutableLiveData<Map<InetAddress, String>>() {

    private val hostNameCache = HashMap<InetAddress, String>()

    fun fire(peerList: List<Peer>?) {
        peerList?.let {
            for (peer in it) {
                val address = peer.address.addr
                if (!hostNameCache.containsKey(address)) {
                    hostNameCache[address] = ""
                    ReverseDnsTask().execute(address)
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class ReverseDnsTask : AsyncTask<InetAddress, Void, Pair<InetAddress, String>>() {

        override fun doInBackground(vararg args: InetAddress): Pair<InetAddress, String> {
            val address = args[0]
            val canonicalHostName = address.canonicalHostName
            return Pair(address, canonicalHostName)
        }

        override fun onPostExecute(result: Pair<InetAddress, String>) {
            hostNameCache[result.first] = result.second
            value = hostNameCache
        }
    }
}