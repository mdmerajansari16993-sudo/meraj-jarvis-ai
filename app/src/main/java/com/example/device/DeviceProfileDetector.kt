package com.example.device

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class DeviceSpec(
    val brandName: String,
    val manufacturer: String,
    val modelName: String,
    val osFlavor: String, // "HyperOS / MIUI", "One UI", "ColorOS", "OxygenOS", "Stock Android"
    val androidVersion: String,
    val screenResolution: String,
    val automationLayoutProfile: String
)

object DeviceProfileDetector {

    fun detectDevice(context: Context): DeviceSpec {
        val manufacturer = Build.MANUFACTURER.uppercase(Locale.ROOT)
        val model = Build.MODEL
        val brand = Build.BRAND.uppercase(Locale.ROOT)

        val (friendlyBrand, osFlavor, layoutProfile) = when {
            manufacturer.contains("XIAOMI") || brand.contains("REDMI") || brand.contains("POCO") -> {
                Triple(
                    "Redmi / Xiaomi",
                    "Xiaomi HyperOS / MIUI",
                    "Redmi/MIUI Layout Profile: Tuned for Floating Windows, Autostart Optimization & Center Notch"
                )
            }
            manufacturer.contains("SAMSUNG") -> {
                Triple(
                    "Samsung",
                    "Samsung One UI",
                    "Samsung One UI Layout: Tuned for Edge Panels, Game Booster & Navigation Bar"
                )
            }
            manufacturer.contains("OPPO") -> {
                Triple(
                    "Oppo",
                    "Oppo ColorOS",
                    "Oppo ColorOS Profile: Tuned for Background App Freeze Guard & Smart Sidebar"
                )
            }
            manufacturer.contains("VIVO") || brand.contains("IQOO") -> {
                Triple(
                    "Vivo / iQOO",
                    "Funtouch OS / OriginOS",
                    "Vivo Funtouch Profile: Tuned for Multi-Turbo & Ultra Game Mode"
                )
            }
            manufacturer.contains("ONEPLUS") -> {
                Triple(
                    "OnePlus",
                    "OxygenOS",
                    "OnePlus OxygenOS Profile: Tuned for High-Refresh Rate Gaming & Pro Gaming Mode"
                )
            }
            manufacturer.contains("REALME") -> {
                Triple(
                    "Realme",
                    "Realme UI",
                    "Realme UI Profile: Tuned for Quick Return & Game Space Touch Injection"
                )
            }
            manufacturer.contains("GOOGLE") -> {
                Triple(
                    "Google Pixel",
                    "Stock Pixel Android",
                    "Google Pixel Profile: Pure Android Accessibility Gestures & Material You System Bars"
                )
            }
            else -> {
                val name = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
                Triple(
                    name,
                    "$name Android OS",
                    "Standard Adaptive Android Layout Profile: Universal Multi-Window Coordinates"
                )
            }
        }

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm?.defaultDisplay?.getMetrics(metrics)
        val resolution = "${metrics.widthPixels}x${metrics.heightPixels} (${metrics.densityDpi} DPI)"

        return DeviceSpec(
            brandName = friendlyBrand,
            manufacturer = Build.MANUFACTURER,
            modelName = model,
            osFlavor = osFlavor,
            androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})",
            screenResolution = resolution,
            automationLayoutProfile = layoutProfile
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(context: Context): String = withContext(Dispatchers.IO) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                ?: return@withContext "New Delhi, India (Default Zone)"

            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            var bestLocation: Location? = null

            for (provider in providers) {
                try {
                    if (locationManager.isProviderEnabled(provider)) {
                        val loc = locationManager.getLastKnownLocation(provider)
                        if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
                            bestLocation = loc
                        }
                    }
                } catch (e: SecurityException) {
                    // Ignore without permission
                }
            }

            if (bestLocation != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(bestLocation.latitude, bestLocation.longitude, 1)
                    val addr = addresses?.firstOrNull()
                    if (addr != null) {
                        val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "India"
                        val state = addr.adminArea ?: ""
                        val country = addr.countryName ?: "India"
                        return@withContext if (state.isNotBlank() && state != city) "$city, $state, $country" else "$city, $country"
                    }
                } catch (e: Exception) {
                    // Geocoder failed
                }
                return@withContext "GPS: ${String.format(Locale.ROOT, "%.3f", bestLocation.latitude)}, ${String.format(Locale.ROOT, "%.3f", bestLocation.longitude)} (India)"
            }
        } catch (e: Exception) {
            // General exception
        }
        return@withContext "New Delhi, India (Auto-Resolved)"
    }
}
