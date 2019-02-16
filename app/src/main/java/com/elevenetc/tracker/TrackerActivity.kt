package com.elevenetc.tracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.view.*

class TrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById(R.id.btn_start_tracking).setOnClickListener(View.OnClickListener { startTrackerService() })

        findViewById(R.id.btn_stop_tracking).setOnClickListener(View.OnClickListener { stopService() })
    }

    private fun startTrackerService() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        } else {
            startService(Intent(this, TrackerService::class.java))
        }

    }


    private fun stopService() {
        stopService(Intent(this, TrackerService::class.java))
    }
}
