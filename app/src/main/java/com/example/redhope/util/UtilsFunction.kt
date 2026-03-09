package com.example.redhope.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.R
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.redhope.modal.EligibilityForm
import com.example.redhope.modal.EligibilityResult

fun callPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = "tel:$phoneNumber".toUri()
    }
    context.startActivity(intent)
}

fun openNearbyBloodBanks(context: Context) {
    val uri = "geo:0,0?q=blood banks near me".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    // Safety check
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Fallback if Google Maps not installed
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "https://www.google.com/maps/search/blood+banks+near+me".toUri()
            )
        )
    }
}


fun checkEligibility(form: EligibilityForm): EligibilityResult {
    return when {
        form.age < 18 ->
            EligibilityResult.NotEligible("You must be at least 18 years old to donate.")

        form.weight < 50 ->
            EligibilityResult.NotEligible("Minimum weight required is 50 kg.")

        form.lastDonationMonthsAgo < 3 ->
            EligibilityResult.NotEligible("You must wait at least 3 months between donations.")

        form.hasIllness ->
            EligibilityResult.NotEligible("Recent illness makes you temporarily ineligible.")

        form.hasRecentSurgery ->
            EligibilityResult.NotEligible("Recent surgery makes you temporarily ineligible.")

        form.onMedication ->
            EligibilityResult.NotEligible("Some medications restrict blood donation.")

        else -> EligibilityResult.Eligible
    }
}

class CooldownWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        showNotification()

        return Result.success()
    }

    private fun showNotification() {

        val channelId = "cooldown_channel"

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        // Create channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Cooldown Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("TimeOut Finished")
            .setContentText("You can now mark yourself available for donation.")
            .setSmallIcon(com.example.redhope.R.drawable.notification)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}



