package com.example.freecodecampstreak.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.freecodecampstreak.model.Root
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class FreeCodeCampRepository {
    private val client = OkHttpClient()
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getChallengeStatus(userName: String): String {
        val request = Request.Builder().url(
            "https://api.freecodecamp.org/users/get-public-profile?username=$userName"
        ).build()
        return try {
            val response = client.newCall(request).execute()
            val rootObj = gson.fromJson(response.body?.string(), Root::class.java)

            val userMap = rootObj.entities.user
            val user = userMap[userName]
            val challenges = user?.completedChallenges

            val filtered = challenges?.filter {
                val data = Instant.ofEpochMilli(it.completedDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                val today = LocalDate.now().atStartOfDay()
                data.isAfter(today)
            }
            if ((filtered?.count() ?: 0) > 0) "DONE!" else "PENDING"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}