package com.demo.sample.kotlin.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.brivo.sdk.BrivoLog
import com.brivo.sdk.BrivoSDKInitializationException
import com.brivo.sdk.access.BrivoSDKAccess
import com.brivo.sdk.ble.BrivoBLEErrorCodes
import com.brivo.sdk.ble.BrivoBLEErrors
import com.brivo.sdk.enums.AccessPointCommunicationState
import com.brivo.sdk.interfaces.IOnCommunicateWithAccessPointListener
import com.brivo.sdk.localauthentication.BrivoSDKLocalAuthentication
import com.brivo.sdk.model.BrivoError
import com.brivo.sdk.model.BrivoResult
import com.brivo.sdk.onair.model.BrivoAccessPoint
import com.demo.sample.kotlin.BrivoSampleConstants
import com.demo.sample.kotlin.R
import com.demo.sample.kotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import pl.bclogic.pulsator4droid.library.PulsatorLayout

class UnlockAccessPointActivity : AppCompatActivity() {

    private val TIMEOUT: Long = 30000
    private var isUnlocking = false
    private var isMagicDoor = false
    private lateinit var brivoAccessPoint: BrivoAccessPoint
    private lateinit var passId: String
    private var cancellationSignal: CancellationSignal? = null

    private lateinit var btnUnlock: Button
    private lateinit var ivLock: ImageView
    private lateinit var tvMessage: TextView
    private lateinit var pulsator: PulsatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        btnUnlock = binding.btnUnlock
        ivLock = binding.ivLock
        tvMessage = binding.tvMessage
        pulsator = binding.pulsator

        btnUnlock.setOnClickListener { unlockDoor() }
        ivLock.setOnClickListener { unlockDoor() }
        val intent = intent
        isMagicDoor = intent.getBooleanExtra(BrivoSampleConstants.IS_MAGIC_DOOR, false)
        if (!isMagicDoor) {
            val message = intent.getStringExtra(BrivoSampleConstants.SELECTED_ACCESS_POINT)
            passId = intent.getStringExtra(BrivoSampleConstants.PASS_ID).toString()
            brivoAccessPoint = Gson().fromJson(message, BrivoAccessPoint::class.java)
        }
        try {
            BrivoSDKLocalAuthentication.getInstance().init(
                applicationContext,
                getString(R.string.two_factor_dialog_title),
                getString(R.string.two_factor_dialog_message),
                getString(R.string.two_factor_dialog_cancel),
                ""
            )
        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (cancellationSignal?.isCanceled == false) {
                    cancellationSignal?.cancel()
                }
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        requestMultiplePermissions.launch(
            permissions
        )
        checkForPermissions()
    }

    private fun unlockDoor() {
        if (isUnlocking) {
            return
        }
        isUnlocking = true
        ivLock.setImageResource(R.drawable.lock)
        togglePulseAnimation(true)
        cancellationSignal = CancellationSignal()
        waitForSlowConnection(cancellationSignal)
        tvMessage.text = ""

        try {
            if (isMagicDoor) {
                BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
                    cancellationSignal,
                    object : IOnCommunicateWithAccessPointListener {
                        override fun onResult(result: BrivoResult) {
                            onUnlockAccessPointResult(result)
                        }
                    })

            } else {
                brivoAccessPoint.id?.let { accessPointId ->
                    BrivoSDKAccess.getInstance().unlockAccessPoint(passId,
                        accessPointId,
                        cancellationSignal,
                        object : IOnCommunicateWithAccessPointListener {
                            override fun onResult(result: BrivoResult) {
                                onUnlockAccessPointResult(result)
                            }
                        })
                } ?: run {
                    Toast.makeText(
                        this@UnlockAccessPointActivity,
                        "Error: missing accessPointId",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: BrivoSDKInitializationException) {
            e.printStackTrace()
        }
    }

    private fun onUnlockAccessPointResult(result: BrivoResult) {
        when (result.communicationState) {
            AccessPointCommunicationState.SUCCESS -> {
                runOnUiThread {
                    showUnlockResultSnackbar(true)
                    prepareUIForResult(cancellationSignal)
                }
            }
            AccessPointCommunicationState.FAILED -> {
                result.error?.let { onUnlockFailed(it, cancellationSignal) }
            }
            AccessPointCommunicationState.SHOULD_CONTINUE -> {
                result.shouldContinueListener?.onShouldContinue(true)
            }
            AccessPointCommunicationState.SCANNING -> BrivoLog.i("unlockAccessPoint -> scanning")
            AccessPointCommunicationState.AUTHENTICATE -> BrivoLog.i("unlockAccessPoint -> authenticate")
            AccessPointCommunicationState.CONNECTING -> BrivoLog.i("unlockAccessPoint -> connecting")
            AccessPointCommunicationState.COMMUNICATING -> BrivoLog.i("unlockAccessPoint -> communicating")
        }
    }

    private fun prepareUIForResult(cancellationSignal: CancellationSignal?) {
        isUnlocking = false
        cancellationSignal?.cancel()
        togglePulseAnimation(false)
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                BrivoLog.i("permission: ${it.key} = ${it.value}")
            }
        }

    private fun checkForPermissions() {
        Dexter.withContext(this)
            .withPermission(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {}

                override fun onPermissionDenied(response: PermissionDeniedResponse) {}

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    AlertDialog.Builder(this@UnlockAccessPointActivity)
                        .setTitle(getString(R.string.permission_dialog_title))
                        .setMessage(getString(R.string.permission_dialog_message))
                        .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                            navigateToPermissionSettings()
                        }
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { _: DialogInterface?, _: Int ->
                            Snackbar.make(
                                findViewById(R.id.layoutUnlockAccessPoint),
                                getString(R.string.permission_dialog_reason),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(android.R.string.ok) {
                                    navigateToPermissionSettings()
                                }
                                .show()
                        }
                        .show()
                }
            }).check()
    }

    private fun showUnlockResultSnackbar(isUnlockSuccessful: Boolean) {
        val snackBar: Snackbar = Snackbar.make(
            window.decorView,
            getString(if (isUnlockSuccessful) R.string.brivo_sample_door_unlocked else R.string.brivo_sample_door_unlocked_failed),
            Snackbar.LENGTH_SHORT
        )
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            if (isUnlockSuccessful) ContextCompat.getColor(
                this,
                android.R.color.holo_green_dark
            ) else ContextCompat.getColor(
                this, android.R.color.holo_red_dark
            )
        )
        snackBar.show()
        ivLock.setImageResource(if (isUnlockSuccessful) R.drawable.lock_open else R.drawable.lock)
    }

    private fun waitForSlowConnection(cancellationSignal: CancellationSignal?) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            onUnlockFailed(
                BrivoBLEErrors.getBleAuthenticationTimedOut(),
                cancellationSignal
            )
        }, TIMEOUT)
    }

    private fun togglePulseAnimation(started: Boolean) {
        pulsator.start()
        pulsator.animate()
            ?.alpha(if (started) 1.0f else 0.0f)
            ?.setDuration(300)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    pulsator.visibility = if (started) View.VISIBLE else View.GONE
                }
            })
    }

    private fun onUnlockFailed(error: BrivoError, cancellationSignal: CancellationSignal?) {
        runOnUiThread {
            when (error.code) {
                BrivoBLEErrorCodes.BLE_DISABLED_ON_DEVICE -> tvMessage.text =
                    getString(R.string.brivo_sample_please_enable_ble_to_continue)
                BrivoBLEErrorCodes.BLE_FAILED_TRANSMISSION,
                BrivoBLEErrorCodes.BLE_ACCESS_DENIED,
                BrivoBLEErrorCodes.BLE_CONNECTION_MANAGER_FAILED_TO_INITIALIZE,
                BrivoBLEErrorCodes.BLE_UNKNOWN_ERROR -> {
                    tvMessage.text = getString(R.string.brivo_sample_failed_to_unlock_door)
                    showUnlockResultSnackbar(false)
                }
                BrivoBLEErrorCodes.BLE_AUTHENTICATION_TIMED_OUT -> {
                    tvMessage.text = getString(R.string.brivo_sample_failed_to_unlock_door_timeout)
                    showUnlockResultSnackbar(false)
                }
                else -> {
                    Toast.makeText(
                        this@UnlockAccessPointActivity,
                        "Error: " + error.code + " " + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            prepareUIForResult(cancellationSignal)
        }
    }

    private fun navigateToPermissionSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}

