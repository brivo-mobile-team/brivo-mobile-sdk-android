package com.demo.sample.kotlin.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.BrivoSharedPreferences
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener
import com.brivo.sdk.onair.interfaces.IOnRetrieveSDKLocallyStoredPassesListener
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.repository.BrivoSDKOnair
import com.demo.sample.kotlin.BrivoApplication
import com.demo.sample.kotlin.BrivoSampleConstants
import com.demo.sample.kotlin.R
import com.demo.sample.kotlin.databinding.ActivitySitesBinding
import com.demo.sample.kotlin.ui.adapters.SitesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.util.*


class SitesActivity : AppCompatActivity() {


    private lateinit var sites: ExpandableListView
    private lateinit var tvMagicDoor: TextView
    private lateinit var ivSettings: ImageView
    private lateinit var toolbar: Toolbar

    private lateinit var onairPasses: LinkedHashMap<String, BrivoOnairPass>
    private lateinit var sitesAdapter: SitesAdapter
    private lateinit var dialog: Dialog
    private var timer: Timer? = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupDialog()
        initializeUI()
    }

    private fun setupBinding() {
        val binding = DataBindingUtil.setContentView<ActivitySitesBinding>(
            this, R.layout.activity_sites
        )
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        sites = binding.sites
        tvMagicDoor = binding.tvMagicDoor
        ivSettings = binding.ivSettings
        toolbar = binding.toolbar
    }

    private fun setupDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        dialog = builder.create()
    }


    private fun initializeUI() {
        toolbar.title = "BrivoSDK " + BrivoSDK.getInstance().version
        setSupportActionBar(toolbar)

        sites.emptyView = findViewById(R.id.empty)
        sites.setOnChildClickListener { _, view, groupPosition, childPosition, _ ->
            startAccessPointsActivity(view.context, groupPosition, childPosition)
            false
        }

        tvMagicDoor.setOnClickListener {
            val intent = Intent(this@SitesActivity, UnlockAccessPointActivity::class.java)
            intent.putExtra(BrivoSampleConstants.IS_MAGIC_DOOR, true)
            startActivity(intent)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this@SitesActivity, RedeemPassActivity::class.java))
        }

        ivSettings.setOnClickListener {
            val dialog =
                Dialog(this@SitesActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.dialog_preferences)

            val etClientId: EditText = dialog.findViewById(R.id.etClientId)
            val etClientSecret: EditText = dialog.findViewById(R.id.etClientSecret)
            val btnSave: Button = dialog.findViewById(R.id.btnSave)

            val sharedPreferences =
                getSharedPreferences(BrivoSampleConstants.CONFIG_KEY, Context.MODE_PRIVATE)
            val initialClientId =
                sharedPreferences.getString(BrivoSampleConstants.CLIENT_ID_KEY, "")
            val initialClientSecret =
                sharedPreferences.getString(BrivoSampleConstants.CLIENT_SECRET_KEY, "")

            etClientId.setText(initialClientId)
            etClientSecret.setText(initialClientSecret)

            btnSave.setOnClickListener {
                BrivoSharedPreferences.clear()
                val clientId = etClientId.text.toString()
                val clientSecret = etClientSecret.text.toString()
                val editor = sharedPreferences.edit()
                editor.putString(BrivoSampleConstants.CLIENT_ID_KEY, clientId.trim())
                editor.putString(BrivoSampleConstants.CLIENT_SECRET_KEY, clientSecret.trim())
                editor.apply()
                BrivoApplication.instance.initializeBrivoMobileSDK()
                dialog.dismiss()
                this.recreate()
            }
            dialog.show()
        }

    }

    private fun startAccessPointsActivity(
        context: Context, groupPosition: Int, childPosition: Int
    ) {
        val passId = ArrayList(onairPasses.keys)[groupPosition]
        val item = onairPasses[passId]?.sites?.get(childPosition)
        val selectedSite = Gson().toJson(item)

        val intent = Intent(context, AccessPointsActivity::class.java)
        intent.putExtra(BrivoSampleConstants.SELECTED_SITE, selectedSite)
        intent.putExtra(BrivoSampleConstants.PASS_ID, passId)

        context.startActivity(intent)
    }

    private fun refreshPasses() {
        for (pass in onairPasses.values) {
            try {
                BrivoSDKOnair.instance?.refreshPass(pass.brivoOnairPassCredentials.tokens,
                    object : IOnRedeemPassListener {
                        override fun onSuccess(pass: BrivoOnairPass?) {
                            refreshPassesListUI()
                        }

                        override fun onFailed(error: BrivoError) {
                            Toast.makeText(
                                this@SitesActivity,
                                "Error " + error.message + " " + error.code,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } catch (e: BrivoSDKInitializationException) {
                e.printStackTrace()
            }
        }
    }

    private fun refreshPassesListUI() {
        runOnUiThread {
            try {
                BrivoSDKOnair.instance?.retrieveSDKLocallyStoredPasses(object :
                    IOnRetrieveSDKLocallyStoredPassesListener {
                    override fun onSuccess(passes: LinkedHashMap<String, BrivoOnairPass>?) {
                        onairPasses = passes ?: LinkedHashMap<String, BrivoOnairPass>()
                        tvMagicDoor.visibility =
                            if (passes?.isEmpty() == true) View.GONE else View.VISIBLE
                        if (::sitesAdapter.isInitialized.not()) {
                            sitesAdapter = SitesAdapter(
                                context = this@SitesActivity,
                                listDataGroup = ArrayList(onairPasses.keys),
                                listDataChild = onairPasses
                            )
                            sites.setAdapter(sitesAdapter)
                        } else {
                            sitesAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onFailed(error: BrivoError) {
                        Toast.makeText(this@SitesActivity, error.message, Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: BrivoSDKInitializationException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (timer == null) {
            timer = Timer()
            triggerRefreshPasses()
        }
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                triggerRefreshPasses()
            }

        }, 0, 15000)
    }

    private fun triggerRefreshPasses() {
        try {
            BrivoSDKOnair.instance?.retrieveSDKLocallyStoredPasses(object :
                IOnRetrieveSDKLocallyStoredPassesListener {
                override fun onSuccess(passes: LinkedHashMap<String, BrivoOnairPass>?) {
                    runOnUiThread {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                        onairPasses = passes ?: LinkedHashMap<String, BrivoOnairPass>()
                        refreshPasses()
                    }
                }

                override fun onFailed(error: BrivoError) {
                    runOnUiThread {
                        if (dialog.isShowing) {
                            dialog.dismiss()
                        }
                        Toast.makeText(this@SitesActivity, error.message, Toast.LENGTH_LONG).show()
                    }
                }
            })
        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }
    }


    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
    }
}
