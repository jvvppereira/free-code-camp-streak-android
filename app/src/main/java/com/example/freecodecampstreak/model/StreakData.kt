package com.example.freecodecampstreak.model

data class StreakData (
    val count: Int,
    val last7Days: List<DayStatus>,
    val status: String
)
