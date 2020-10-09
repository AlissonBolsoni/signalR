package br.com.alissonbolsoni.signalr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

class PermissionsActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.SET_WALLPAPER,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.VIBRATE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CHANGE_WIFI_STATE
    )
    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permissions_activity)
        this.savedInstanceState = savedInstanceState

        checkPermissions()
    }

    private fun permissionsSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun checkPermissions() {
        if (isPermissionsGranted()) {
            onCreateAfterPermissions()
        } else {
            ActivityCompat.requestPermissions(
                this, permissions,
                1
            )
        }
    }

    private fun isPermissionsGranted(): Boolean {
        this.permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkPermissions()
    }

    private fun onCreateAfterPermissions() {
        if (this.savedInstanceState == null)
            permissionsSuccess()
    }

}