package com.gandhasiri.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gandhasiri.app.MainActivity
import com.gandhasiri.app.R
import java.util.*
import kotlin.math.*

object TreeCalculator {

    /**
     * Estimate heartwood readiness based on girth (cm) and age (years)
     * Sandalwood heartwood typically starts forming after 15-20 years
     * and reaches harvest maturity at 25-30 years with 30+ cm girth
     */
    fun calculateHeartwoodEstimate(girthCm: Double, ageYears: Int): HeartwoodEstimate {
        val diameterCm = girthCm / Math.PI
        val estimatedHeartwoodDiameter = if (ageYears > 15) {
            (ageYears - 15) * 0.3 + (girthCm * 0.05)
        } else 0.0

        val yearsToHarvest = when {
            ageYears >= 30 && girthCm >= 94.2 -> 0 // Already mature (~30cm diameter)
            else -> {
                val targetGirth = 94.2 // ~30cm diameter
                val targetAge = 30
                val yearsForGirth = ((targetGirth - girthCm) / 3.14).toInt() // ~1cm girth per year
                val yearsForAge = maxOf(0, targetAge - ageYears)
                maxOf(yearsForGirth, yearsForAge, 0)
            }
        }

        val maturityPercent = when {
            ageYears >= 30 && girthCm >= 94.2 -> 100
            else -> ((ageYears.toDouble() / 30) * 50 + (girthCm / 94.2) * 50).toInt().coerceIn(0, 99)
        }

        val estimatedValue = calculateEstimatedValue(girthCm, ageYears)

        return HeartwoodEstimate(
            yearsToHarvest = yearsToHarvest,
            maturityPercent = maturityPercent,
            heartwoodDiameterCm = estimatedHeartwoodDiameter,
            estimatedValueINR = estimatedValue,
            canHarvest = yearsToHarvest == 0
        )
    }

    private fun calculateEstimatedValue(girthCm: Double, ageYears: Int): Long {
        // Sandalwood value depends heavily on heartwood content
        // Market rate ~₹10,000-15,000 per kg of heartwood
        val heartwoodWeightKg = if (ageYears > 15) {
            val hwDiameter = (ageYears - 15) * 0.3
            val hwVolume = Math.PI * (hwDiameter / 2).pow(2) * (ageYears * 0.05)
            hwVolume * 0.8 // density factor
        } else 0.0

        return (heartwoodWeightKg * 12000).toLong()
    }

    fun calculateGrowthRate(previousGirth: Double, currentGirth: Double, daysBetween: Int): Double {
        if (daysBetween == 0) return 0.0
        return ((currentGirth - previousGirth) / daysBetween) * 365 // annual rate
    }

    fun getHealthColor(status: String): Long = when (status) {
        "EXCELLENT" -> 0xFF4CAF50
        "GOOD" -> 0xFF8BC34A
        "FAIR" -> 0xFFFFEB3B
        "POOR" -> 0xFFFF9800
        "CRITICAL" -> 0xFFF44336
        else -> 0xFF4CAF50
    }
}

data class HeartwoodEstimate(
    val yearsToHarvest: Int,
    val maturityPercent: Int,
    val heartwoodDiameterCm: Double,
    val estimatedValueINR: Long,
    val canHarvest: Boolean
)

object NotificationHelper {
    private const val SECURITY_CHANNEL_ID = "gandhasiri_security"
    private const val GROWTH_CHANNEL_ID = "gandhasiri_growth"
    private const val SECURITY_NOTIFICATION_ID = 1001
    private const val GROWTH_NOTIFICATION_ID = 1002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val securityChannel = NotificationChannel(
                SECURITY_CHANNEL_ID,
                "Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent security alerts for your sandalwood trees"
                enableVibration(true)
                enableLights(true)
            }

            val growthChannel = NotificationChannel(
                GROWTH_CHANNEL_ID,
                "Growth Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log tree growth data"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(securityChannel)
            manager.createNotificationChannel(growthChannel)
        }
    }

    fun sendSecurityAlert(context: Context, description: String, location: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "security")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, SECURITY_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🚨 Gandha-Siri Security Alert!")
            .setContentText("Suspicious activity reported: $description")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("⚠️ ALERT: $description\n📍 Location: $location\nYour emergency contacts have been notified.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(SECURITY_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun sendGrowthReminder(context: Context, treeId: String, treeName: String) {
        val notification = NotificationCompat.Builder(context, GROWTH_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("🌿 Time to check your tree!")
            .setContentText("Record growth data for $treeName ($treeId)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(GROWTH_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}

object LegalGuide {
    data class LegalStep(
        val stepNumber: Int,
        val title: String,
        val description: String,
        val authority: String,
        val documents: List<String>
    )

    fun getHarvestingSteps(): List<LegalStep> = listOf(
        LegalStep(
            1, "Apply to Forest Department",
            "Submit Form-1 to your District Forest Officer (DFO) with proof of ownership and tree register.",
            "District Forest Officer (DFO)",
            listOf("Land ownership documents (RTC/Pahani)", "Proof of planting", "Gandha-Siri Tree Register export")
        ),
        LegalStep(
            2, "Tree Inspection",
            "Forest officials will visit your land to verify tree count, girth measurements, and health.",
            "Range Forest Officer (RFO)",
            listOf("Government-issued ID", "Land survey documents", "Tree photographs")
        ),
        LegalStep(
            3, "Obtain Transit Permit",
            "After approval, get Form-2 Transit Permit before transporting any sandalwood.",
            "Divisional Forest Officer",
            listOf("DFO approval letter", "Buyer's details", "Vehicle details for transport")
        ),
        LegalStep(
            4, "Sell at Authorized Depot",
            "In Karnataka, sandalwood must be sold at KFPCL (Karnataka Forest Products Corporation) depots.",
            "KFPCL Depot Officer",
            listOf("Transit Permit", "DFO approval", "Your Aadhaar/PAN card")
        ),
        LegalStep(
            5, "Receive Payment",
            "Payment is made at government rates. Keep all receipts for future legal compliance.",
            "KFPCL Accounts Office",
            listOf("Bank account details", "Transit permit receipt", "Weighment certificate")
        )
    )

    fun getSecurityChecklist(): List<Pair<String, String>> = listOf(
        Pair("Barbed Wire Fencing", "Install at least 1.5m high barbed wire fence around the entire farm boundary"),
        Pair("Motion-Sensor Lighting", "Install solar-powered motion lights at entry points and around trees"),
        Pair("Guard Dog Presence", "Keep dogs on property — they deter potential thieves"),
        Pair("Neighbour Watch Network", "Inform trusted neighbors and create a phone tree for alerts"),
        Pair("Tree Marking", "Paint trees with fluorescent paint and carve your land survey number"),
        Pair("Camera Surveillance", "Install CCTV or trail cameras covering tree rows"),
        Pair("Register with Police", "File a preventive complaint with local police citing your Tree Register"),
        Pair("Invisible UV Marking", "Use UV paint to mark trees for forensic identification"),
        Pair("Regular Night Patrols", "Schedule weekly night checks especially during harvest season"),
        Pair("Insurance Coverage", "Get crop insurance covering sandalwood theft and damage")
    )
}
