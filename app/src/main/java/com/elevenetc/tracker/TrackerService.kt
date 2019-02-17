package com.elevenetc.tracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import java.util.*


class TrackerService : Service() {

    val locManager by lazy { LocManager(this) }

    val locClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    val prefs by lazy { getSharedPreferences("locs", Context.MODE_PRIVATE) }

    val listener = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            Log.d("loc", p0.toString())
            val loc = Loc(p0.lastLocation.latitude, p0.lastLocation.longitude, getNow())

            locManager.store(loc)
        }

        private fun getNow(): String {
            val current = Date(System.currentTimeMillis())
            val cal = Calendar.getInstance()
            cal.time = current
            val min = cal.get(Calendar.MINUTE)
            val hour = cal.get(Calendar.HOUR)
            val date = cal.get(Calendar.DATE)
            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)
            val time = "$hour:$min-$date.$month.$year"
            return time
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            Log.d("loc", p0.toString())
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        showNotification()

        val request = LocationRequest()
        request.interval = 1000
        request.priority = PRIORITY_HIGH_ACCURACY
        locClient.requestLocationUpdates(request, listener, null)
    }

    override fun onDestroy() {
        locManager.stop()
        locClient.removeLocationUpdates(listener)
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun showNotification() {
        val channelId = "tracker-id"

        createNotificationChannel(channelId, "Tracker channel")

        val intent = Intent(this, TrackerActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val notification = Notification.Builder(this, channelId)
                .setContentTitle(getText(R.string.tracker_notification_title))
                .setContentText(getText(R.string.tracker_notification_content))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                //.setTicker(getText(R.string.ticker_text))
                .build()

        startForeground(100, notification)
    }


}