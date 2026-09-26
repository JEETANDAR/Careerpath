package com.aistudio.carrerpath.counseling

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class EduApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        try {
            // Explicitly set Coil's default singleton ImageLoader configured for emulator stability
            val imageLoader = newImageLoader()
            Coil.setImageLoader(imageLoader)
        } catch (e: Exception) {
            Log.e("EduApplication", "Coil init error: ${e.message}", e)
        }

        try {
            val validApiKey = "AIzaSyAJaeSc5LwartpzZ-ftltW_rxz5lRMYCtw"
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                val currentApp = FirebaseApp.getInstance()
                if (currentApp.options.apiKey.contains("remixed") || currentApp.options.apiKey.isBlank()) {
                    currentApp.delete()
                }
            }
            if (FirebaseApp.getApps(this).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(this)
                } catch (e: Exception) {
                    val options = FirebaseOptions.Builder()
                        .setProjectId("carrerpath-7460e")
                        .setApplicationId("1:121678107921:android:8143aea73fd9e5f42928b0")
                        .setApiKey(validApiKey)
                        .setStorageBucket("carrerpath-7460e.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(this, options)
                }
            }
            try {
                // Ensure app verification is active by default so real phone numbers can receive real SMS OTP
                FirebaseAuth.getInstance().firebaseAuthSettings.setAppVerificationDisabledForTesting(false)
                Log.d("EduApplication", "FirebaseAuth initialized with standard app verification enabled for real SMS.")
            } catch (e: Exception) {
                Log.w("EduApplication", "Could not configure app verification settings: ${e.message}")
            }
            Log.d("EduApplication", "Firebase initialized successfully.")
        } catch (e: Exception) {
            Log.e("EduApplication", "Firebase init error: ${e.message}", e)
        }
    }

    override fun newImageLoader(): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url.toString()
                var normalizedUrl = if (originalUrl.contains("commons.wikimedia.org/wiki/Special:Redirect/file/")) {
                    var u = originalUrl.replace("%20", "_").replace(" ", "_")
                    if (!u.contains("width=")) {
                        u = if (u.contains("?")) "$u&width=800" else "$u?width=800"
                    }
                    u
                } else {
                    originalUrl
                }

                if (normalizedUrl.contains("auto=format")) {
                    normalizedUrl = normalizedUrl.replace("auto=format", "fm=jpg")
                }
                if (normalizedUrl.contains("images.unsplash.com") && !normalizedUrl.contains("fm=jpg")) {
                    normalizedUrl = if (normalizedUrl.contains("?")) "$normalizedUrl&fm=jpg" else "$normalizedUrl?fm=jpg"
                }

                val newRequest = originalRequest.newBuilder()
                    .url(normalizedUrl)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; rv:128.0) Gecko/128.0 CareerCounselingApp/1.0 (contact: aijeetandar@gmail.com)")
                    .header("Accept", "image/jpeg,image/png")
                    .build()
                chain.proceed(newRequest)
            }
            .addNetworkInterceptor { chain ->
                val request = chain.request()
                val newRequest = request.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; rv:128.0) Gecko/128.0 CareerCounselingApp/1.0 (contact: aijeetandar@gmail.com)")
                    .header("Accept", "image/jpeg,image/png")
                    .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .allowHardware(false) // Prevents HARDWARE bitmap & Codec2 component query failure on emulators
            .allowRgb565(false)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .respectCacheHeaders(false)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache_v2"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .build()
    }
}
