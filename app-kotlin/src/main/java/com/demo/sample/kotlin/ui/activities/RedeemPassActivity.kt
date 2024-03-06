package com.demo.sample.kotlin.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.BrivoSDK
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.model.BrivoConfiguration
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.onair.interfaces.IOnRedeemPassListener
import com.brivo.sdk.onair.model.BrivoOnairPass
import com.brivo.sdk.onair.repository.BrivoSDKOnair
import com.demo.sample.kotlin.R
import com.demo.sample.kotlin.databinding.ActivityRedeemPassBinding
import com.demo.sample.kotlin.BrivoSampleConstants

class RedeemPassActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog

    private lateinit var btnLogin: Button
    private lateinit var etEmail: EditText
    private lateinit var etToken: EditText
    private lateinit var switchRegion: SwitchCompat

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRedeemPassBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_redeem_pass
            )
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        btnLogin = binding.btnLogin
        etEmail = binding.etEmail
        etToken = binding.etToken
        switchRegion = binding.switchRegion

        initializeBrivoMobileSDK()

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        dialog = builder.create()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim { it <= ' ' }
            val token = etToken.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {
                etEmail.error = getString(R.string.redeem_pass_missing_email)
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (token.isEmpty()) {
                etToken.error = getString(R.string.redeem_pass_missing_token)
                etToken.requestFocus()
                return@setOnClickListener
            }

            try {
                redeemPass(email, token)
            } catch (e: BrivoSDKInitializationException) {
                e.printStackTrace()
            }
        }
    }

    private fun initializeBrivoMobileSDK() {
        try {
            switchRegion.isActivated = true
            switchRegion.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    val sharedPreferences =
                        getSharedPreferences(BrivoSampleConstants.CONFIG_KEY, Context.MODE_PRIVATE)
                    val storedClientId = sharedPreferences.getString(
                        BrivoSampleConstants.CLIENT_ID_KEY,
                        BrivoSampleConstants.CLIENT_ID
                    )
                    val storedClientSecret = sharedPreferences.getString(
                        BrivoSampleConstants.CLIENT_SECRET_KEY,
                        BrivoSampleConstants.CLIENT_SECRET
                    )

                    BrivoSDK.getInstance().init(
                        applicationContext, BrivoConfiguration(
                            clientId = storedClientId ?: BrivoSampleConstants.CLIENT_ID,
                            clientSecret = storedClientSecret ?: BrivoSampleConstants.CLIENT_SECRET,
                            authUrl = BrivoSampleConstants.AUTH_URL,
                            apiUrl = BrivoSampleConstants.API_URL,
                            useEURegion = true,
                            useSDKStorage = true,
                            shouldVerifyDoor = false
                        )
                    )
                } else {
                    BrivoSDK.getInstance().init(
                        applicationContext, BrivoConfiguration(
                            clientId = BrivoSampleConstants.CLIENT_ID_EU,
                            clientSecret = BrivoSampleConstants.CLIENT_SECRET_EU,
                            authUrl = BrivoSampleConstants.AUTH_URL_EU,
                            apiUrl = BrivoSampleConstants.API_URL_EU,
                            useEURegion = true,
                            useSDKStorage = true,
                            shouldVerifyDoor = false
                        )
                    )
                }
            }

        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }
    }

    @Throws(BrivoSDKInitializationException::class)
    private fun redeemPass(email: String, token: String) {
        dialog.show()
        BrivoSDKOnair.instance?.redeemPass(email, token, object : IOnRedeemPassListener {
            override fun onSuccess(pass: BrivoOnairPass?) {
                runOnUiThread {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                    Toast.makeText(
                        this@RedeemPassActivity,
                        R.string.reedem_pass_success_message,
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@RedeemPassActivity, SitesActivity::class.java))
                    finish()
                }
            }

            override fun onFailed(error: BrivoError) {
                runOnUiThread {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                    BrivoLog.e(error.message)
                    Toast.makeText(
                        this@RedeemPassActivity,
                        "Error " + error.message + " " + error.code,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
}
