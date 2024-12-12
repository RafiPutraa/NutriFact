package com.dicoding.nutrifact.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import android.util.Base64
import org.json.JSONObject

fun String.withDateFormat(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }
        val date = inputFormat.parse(this) as Date
        outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


fun isTokenExpired(token: String): Boolean {
    return try {
        val parts = token.split(".")
        if (parts.size < 3) return true

        val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
        val json = JSONObject(payload)
        val exp = json.getLong("exp")
        val currentTime = System.currentTimeMillis() / 1000
        currentTime > exp
    } catch (e: Exception) {
        true
    }
}

