package com.example.aepbill.data.model

data class AlarmEntry(
    val start: String,  // "HH:MM:SS"
    val end: String,    // "HH:MM:SS"
    val endDay: Int = 0, // 0 = same day, 1 = next day
    val relay: Int = 0  // 0 = Relay S, 1 = Relay A, 2 = Relay B, 3 = Relay C
)

data class ScheduleResponse(
    val schedule: Map<String, List<AlarmEntry>>
)
