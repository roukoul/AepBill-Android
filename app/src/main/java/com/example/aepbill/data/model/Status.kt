package com.example.aepbill.data.model

data class Status(
    val relay: Boolean,
    val relay2: Boolean,
    val relay3: Boolean,
    val relay4: Boolean,
    val current: Float,
    val time: String,
    val wifiMode: String,
    val alarmsEnabled: Boolean,
    val nextAlarm: String?,
    val anomalyCode: Int = 0 // 0=None, 1=Overload, 2=Leak, 3=Underload, 4=NTP
)
