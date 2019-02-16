package com.elevenetc.tracker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.location.*

class TrackerService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        showNotification()

        val locClient = LocationServices.getFusedLocationProviderClient(this)

        val request = LocationRequest()
        request.setInterval(1000)

        locClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {

            }

            override fun onLocationAvailability(p0: LocationAvailability?) {

            }
        }, null)
    }

    override fun onDestroy() {
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