package com.yousefelsayed.gamescheap.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.activity.MainActivity

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        Toast.makeText(context,"Rec: ${p1?.action}",Toast.LENGTH_SHORT).show()
        if (p1?.action!! == Intent.ACTION_BOOT_COMPLETED || p1?.action!! == Intent.ACTION_SCREEN_ON){
            Log.d("Debug","BroadCast Rec")

            showNotification(context!!)
        }
    }

    private fun showNotification(c:Context){
        val builder = NotificationCompat.Builder(c,"666")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Test")
            .setContentText("Test")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test"
            val descriptionText = "Test"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("666", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            with(NotificationManagerCompat.from(c)) {
                // notificationId is a unique int for each notification that you must define
                notify(666, builder.build())
            }
        }
    }
}