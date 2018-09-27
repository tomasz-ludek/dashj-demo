package org.dash.dashj.demo

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.activity_splash.*
import org.dashj.dashjinterface.WalletAppKitService
import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_LONG_DURATION = "EXTRA_LONG_DURATION"

        fun createIntent(context: Context, longDuration: Boolean): Intent {
            val intent = Intent(context, SplashActivity::class.java)
            intent.putExtra(EXTRA_LONG_DURATION, longDuration)
            return intent
        }
    }

    private lateinit var progressTask: ProgressTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val longDuration = intent.getBooleanExtra(EXTRA_LONG_DURATION, false)
        val duration = if (longDuration) 1000 else 20
        progressTask = ProgressTask(duration, WeakReference(progressView))
        messageView.text = "Starting ${MainPreferences.getInstance().latestConfigName}..."
    }

    override fun onStart() {
        super.onStart()
        progressTask.execute()
    }

    open class ProgressTask(private val maxProgress: Int,
                            private val progressBar: WeakReference<ProgressBar>) : AsyncTask<Void, Int, Void>() {

        override fun onPreExecute() {
            progressBar.get()?.let {
                it.max = maxProgress
                it.progress = 0
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            for (i in 0..maxProgress) {
                Thread.sleep(100)
                publishProgress(i)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            progressBar.get()?.let {
                it.progress = values[0]!!
            }
        }

        override fun onPostExecute(result: Void?) {
            progressBar.get()?.context!!.applicationContext.let {
                val latestConfigName = MainPreferences.getInstance().latestConfigName
                WalletAppKitService.init(it, MainApplication.walletConfigMap[latestConfigName]!!)

                val intent = Intent(it, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val options = ActivityOptions.makeCustomAnimation(it, android.R.anim.fade_in, android.R.anim.fade_out)
                it.startActivity(intent, options.toBundle())
            }
        }
    }
}
