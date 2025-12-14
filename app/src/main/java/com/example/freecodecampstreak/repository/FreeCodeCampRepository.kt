package com.example.freecodecampstreak.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.freecodecampstreak.model.Challenge
import com.example.freecodecampstreak.model.Root
import com.example.freecodecampstreak.model.User
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class FreeCodeCampRepository {
    private val client = OkHttpClient()
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getChallengeStatus(userName: String): String {
        val user = getUserData(userName)
        val challenges = user.completedChallenges
        val streakChallenges = getStreakChallenges(challenges)
//        val streakLastWeek = getLastWeekStatus(challenges)
        val countStreak = streakChallenges.count()

        return if (wasTodayExerciseDone(streakChallenges)) "$countStreak STREAK DAYS"
        else "PENDING $countStreak"
    }

    private fun getUserData(userName: String): User {
        val request = Request.Builder().url(
            "https://api.freecodecamp.org/users/get-public-profile?username=$userName"
        ).build()
        return try {
            val response = client.newCall(request).execute()
            val rootObj = gson.fromJson(response.body?.string(), Root::class.java)

            val userMap = rootObj.entities.user
            userMap[userName]
        } catch (e: Exception) {
            "Error: ${e.message}"
        } as User
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStreakChallenges(challenges: List<Challenge>): List<Challenge> {
        if (challenges.isEmpty()) return emptyList()

        val sortedChallenges = challenges.sortedBy { it.completedDate }

        var lastStreakDate: LocalDate? = null
        val streak = mutableListOf<Challenge>()

        for (challenge in sortedChallenges.reversed()) {
            val challengeDate =
                Instant.ofEpochMilli(challenge.completedDate).atZone(ZoneId.systemDefault())
                    .toLocalDate()
            if (lastStreakDate == null) {
                streak.add(challenge)
                lastStreakDate = challengeDate
            } else {
                when (challengeDate) {
                    lastStreakDate -> {
                        continue
                    }

                    lastStreakDate.minusDays(1) -> {
                        streak.add(challenge)
                        lastStreakDate = challengeDate
                    }

                    else -> {
                        break
                    }
                }
            }
        }
        return streak.reversed()
    }

    private fun wasTodayExerciseDone(streakChallenges: List<Challenge>): Boolean {
        val today = LocalDate.now()
        val latestChallenge = streakChallenges.last()
        val latestDate =
            Instant.ofEpochMilli(latestChallenge.completedDate).atZone(ZoneId.systemDefault())
                .toLocalDate()
        return latestDate == today
    }

    private fun getLastWeekStatus(challenges: List<Challenge>): Map<String, Boolean> {
        val today = LocalDate.now()
        val zoneId = ZoneId.systemDefault()

        val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

        val map = last7Days.associate { date ->
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val hasChallenge = challenges.any { challenge ->
                val challengeDate =
                    Instant.ofEpochMilli(challenge.completedDate).atZone(zoneId).toLocalDate()
                challengeDate.isEqual(date)
            }
            dayOfWeek to hasChallenge
        }

        return map
    }
}