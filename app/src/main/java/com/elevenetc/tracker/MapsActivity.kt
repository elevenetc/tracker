package com.elevenetc.tracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val locManager by lazy { LocManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val handler = XH(this, mainLooper)
        locManager.getAll(handler)
    }

    fun addLocs(locs: List<Loc>) {
        locs.forEach {
            val sydney = LatLng(it.lat, it.lon)
            mMap.addMarker(MarkerOptions().position(sydney).title(it.time))

        }

        if (locs.isNotEmpty()) {
            val last = locs.last()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(last.lat, last.lon)))
        }
    }

    class XH(val act: MapsActivity, l: Looper) : Handler(l) {
        override fun handleMessage(msg: Message) {
            val locs: List<Loc> = msg.obj as List<Loc>
            act.addLocs(locs)
        }
    }
}
